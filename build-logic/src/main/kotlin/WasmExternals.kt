import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Generates Kotlin externals for the Fila* C API as compiled to wasm32 (design §6).
 *
 * clang (from .emsdk, targeting wasm32) parses the headers, so types and struct layouts are the
 * real wasm ABI ones. Per C module (`modules`: interface name → c/<dir>) it emits into [mainDir]:
 * an `external interface` of every function (all numeric: pointers/bool/enums `Int`, 64-bit
 * `JsBigInt`), top-level wrappers named like the C functions (as cinterop names them) that take
 * `Boolean`/`Long`/`String?` and call [instance], the enum constants under their C names, and one
 * offsets object per struct. Into [testDir] it emits each function's wasm arity for the parity test.
 */
@CacheableTask
abstract class GenerateWasmExternals : DefaultTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val headers: ConfigurableFileCollection

    /** Generated interface name → header dir under c/, e.g. FilamentC → filament. */
    @get:Input abstract val modules: MapProperty<String, String>
    @get:Input abstract val packageName: Property<String>
    /** Interface every generated one extends (the hand-written runtime surface). */
    @get:Input abstract val baseInterface: Property<String>
    /** Expression the wrappers call through, e.g. `fila`. */
    @get:Input abstract val instance: Property<String>

    @get:Internal abstract val cDir: DirectoryProperty
    @get:Internal abstract val emsdkDir: DirectoryProperty
    @get:OutputDirectory abstract val mainDir: DirectoryProperty
    @get:OutputDirectory abstract val testDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val c = cDir.get().asFile
        val headerTexts = modules.get().mapValues { (_, dir) ->
            c.resolve("$dir/c").listFiles { f -> f.extension == "h" }!!.sorted().joinToString("\n") { it.readText() }
        }
        val work = temporaryDir
        val umbrella = work.resolve("umbrella.c").apply {
            writeText(modules.get().values.flatMap { dir ->
                c.resolve("$dir/c").listFiles { f -> f.extension == "h" }!!.sorted()
            }.joinToString("\n") { "#include \"${it.absolutePath}\"" } + "\n")
        }
        val includes = modules.get().values.map { "-I${c.resolve("$it/c").absolutePath}" }
        val ast = emcc(work, "ast.json", listOf("-Xclang", "-ast-dump=json") + includes + umbrella.absolutePath)
        val layouts = emcc(work, "layouts.txt", listOf("-Xclang", "-fdump-record-layouts-complete") + includes + umbrella.absolutePath)

        @Suppress("UNCHECKED_CAST")
        val decls = (JsonSlurper().parse(ast) as Map<String, Any?>)["inner"] as List<Map<String, Any?>>
        val typedefs = decls.filter { it["kind"] == "TypedefDecl" }.associate {
            val type = it["type"] as Map<*, *>
            it["name"] as String to (type["desugaredQualType"] ?: type["qualType"]) as String
        }
        val records = parseLayouts(layouts.readText())

        val pkg = packageName.get()
        val mainOut = mainDir.get().asFile.apply { deleteRecursively(); mkdirs() }
        val testOut = testDir.get().asFile.apply { deleteRecursively(); mkdirs() }
        val arities = StringBuilder()

        for ((iface, dir) in modules.get()) {
            val text = headerTexts.getValue(iface)
            val declared = Regex("[^A-Za-z0-9_](Fila[A-Za-z0-9]+_[A-Za-z0-9_]+)\\s*\\(").findAll(text).map { it.groupValues[1] }.toSet()
            val out = StringBuilder(header(pkg, dir))

            val functions = decls.filter { it["kind"] == "FunctionDecl" && it["name"] in declared }.sortedBy { it["name"] as String }
                .map { toFunction(it, typedefs) }
            out.append("external interface $iface : ${baseInterface.get()} {\n")
            functions.forEach { fn ->
                val retSuffix = if (fn.ret.raw == "Unit") "" else ": ${fn.ret.raw}"
                out.append("    fun _${fn.name}(${fn.params.joinToString { "${it.name}: ${it.raw}" }})$retSuffix\n")
                arities.append("    \"_${fn.name}\" to ${fn.params.size},\n")
            }
            out.append("}\n\n")

            val call = instance.get()
            functions.forEach { fn ->
                val args = fn.params.joinToString { p ->
                    when (p.kind) {
                        Kind.BOOL -> "if (${p.name}) 1 else 0"
                        Kind.I64 -> "${p.name}.toI64()"
                        Kind.STRING -> "cString(${p.name})"
                        Kind.RAW -> p.name
                    }
                }
                var body = "$call._${fn.name}($args)"
                body = when (fn.ret.kind) {
                    Kind.BOOL -> "$body != 0"
                    Kind.I64 -> "$body.toKotlinLong()"
                    Kind.STRING -> "$call.readString($body)"
                    Kind.RAW -> body
                }
                // Kotlin/JS keeps Float as a double; see normalizeF32.
                if (fn.ret.kotlin == "Float") body = "normalizeF32($body)"
                if (fn.params.any { it.kind == Kind.STRING }) body = "$call.heapScoped { $body }"
                val retSuffix = if (fn.ret.kotlin == "Unit") "" else ": ${fn.ret.kotlin}"
                out.append("fun ${fn.name}(${fn.params.joinToString { "${it.name}: ${it.kotlin}" }})$retSuffix = $body\n")
            }
            out.append("\n")

            decls.filter { it["kind"] == "EnumDecl" }.forEach { enum ->
                // C enum types are plain ints at the boundary; keep the names so ported code reads the same.
                (enum["name"] as String?)?.takeIf { Regex("enum\\s+${Regex.escape(it)}\\s*\\{").containsMatchIn(text) }
                    ?.let { out.append("typealias $it = Int\n") }
                var next = 0L
                (enum["inner"] as List<Map<String, Any?>>?).orEmpty().filter { it["kind"] == "EnumConstantDecl" }.forEach { k ->
                    val kName = k["name"] as String
                    val value = (k["inner"] as List<Map<String, Any?>>?)?.firstNotNullOfOrNull { it["value"] as String? }?.toLong() ?: next
                    next = value + 1
                    if (Regex("\\b${Regex.escape(kName)}\\b").containsMatchIn(text)) out.append("const val $kName = $value\n")
                }
            }
            out.append("\n")

            // Scalar typedefs (e.g. FilaEntity = uint32_t, FilaTextureSampler = uint64_t) keep their names too.
            decls.filter { it["kind"] == "TypedefDecl" && (it["name"] as String).startsWith("Fila") }.forEach { td ->
                val tdName = td["name"] as String
                val resolved = resolve(tdName, typedefs)
                val scalar = !resolved.startsWith("struct ") && !resolved.startsWith("enum ") && '*' !in resolved && '(' !in resolved
                if (scalar && Regex("typedef\\s+[^;{}]*\\b${Regex.escape(tdName)}\\s*;").containsMatchIn(text)) {
                    out.append("typealias $tdName = ${type(resolved, resolved, tdName).kotlin}\n")
                }
            }
            out.append("\n")

            val call2 = instance.get()
            records.filterKeys { "::" !in it && Regex("struct\\s+${Regex.escape(it)}\\s*\\{").containsMatchIn(text) }.toSortedMap().forEach { (rec, layout) ->
                out.append(structView(rec, layout, typedefs, records, call2, ""))
                out.append("\n")
            }
            mainOut.resolve("$iface.kt").writeText(out.toString())
        }

        testOut.resolve("WasmArities.kt").writeText(
            header(pkg, modules.get().values.joinToString()) +
                "/** wasm export name → parameter count, for the export parity test. */\n" +
                "val WASM_ARITIES: List<Pair<String, Int>> = listOf(\n$arities)\n",
        )
    }

    private enum class Kind { RAW, BOOL, I64, STRING }
    private class Type(val raw: String, val kotlin: String, val kind: Kind)
    private class Param(val name: String, val raw: String, val kotlin: String, val kind: Kind)
    private class Function(val name: String, val params: List<Param>, val ret: Type)

    @Suppress("UNCHECKED_CAST")
    private fun toFunction(fn: Map<String, Any?>, typedefs: Map<String, String>): Function {
        val name = fn["name"] as String
        val params = (fn["inner"] as List<Map<String, Any?>>?).orEmpty().filter { it["kind"] == "ParmVarDecl" }
            .mapIndexed { i, p ->
                val type = p["type"] as Map<*, *>
                val t = type(desugar(type, typedefs), (type["qualType"] as String).trim(), "$name param")
                Param(kotlinName(p["name"] as String? ?: "p$i"), t.raw, t.kotlin, t.kind)
            }.toMutableList()
        val cReturn = ((fn["type"] as Map<*, *>)["qualType"] as String).substringBefore("(").trim()
        val resolved = resolve(cReturn, typedefs)
        // wasm C ABI: a struct returned by value becomes a leading result pointer (sret).
        val ret = if (resolved.startsWith("struct ")) {
            params.add(0, Param("result", "Int", "Int", Kind.RAW))
            Type("Unit", "Unit", Kind.RAW)
        } else type(resolved, cReturn, "$name return")
        return Function(name, params, ret)
    }

    private fun type(resolved: String, written: String, where: String): Type {
        val raw = kotlinType(resolved, where)
        return when {
            resolved == "_Bool" || resolved == "bool" -> Type(raw, "Boolean", Kind.BOOL)
            raw == "JsBigInt" -> Type(raw, "Long", Kind.I64)
            written.replace(" ", "") == "constchar*" -> Type(raw, "String?", Kind.STRING)
            else -> Type(raw, raw, Kind.RAW)
        }
    }

    private fun emcc(work: File, output: String, args: List<String>): File {
        val out = work.resolve(output)
        val emsdk = emsdkDir.get().asFile
        val cmd = ". '${emsdk.resolve("emsdk_env.sh")}' >/dev/null 2>&1 && emcc -fsyntax-only " +
            args.joinToString(" ") { "'$it'" } + " > '$out'"
        val process = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(false).start()
        val err = process.errorStream.bufferedReader().readText()
        check(process.waitFor() == 0) { "emcc failed:\n$err" }
        return out
    }

    private class Field(val name: String, val offset: Int, val type: String)
    private class Layout(val size: Int, val fields: List<Field>)

    // A view over a struct at `ptr` in the heap, shaped like the cinterop struct type. Anonymous
    // nested structs (`struct { ... } ssct;`) become nested classes named after their field.
    private fun structView(name: String, layout: Layout, typedefs: Map<String, String>, records: Map<String, Layout>, call: String, indent: String): String {
        val out = StringBuilder("${indent}class $name(val ptr: Int) {\n")
        layout.fields.forEach { f ->
            val anonymous = f.type.removePrefix("struct ").takeIf { "::(unnamed" in it }
            if (anonymous != null && anonymous in records) {
                val nested = f.name.replaceFirstChar { it.uppercase() }
                out.append("$indent    val ${kotlinName(f.name)}: $nested get() = $nested(ptr + ${f.offset})\n")
                out.append(structView(nested, records.getValue(anonymous), typedefs, records, call, "$indent    "))
            } else {
                fieldAccessor(f, typedefs, records, call)?.let { out.append("$indent    $it\n") }
            }
        }
        out.append("$indent    companion object {\n$indent        const val SIZE = ${layout.size}\n")
        layout.fields.forEach { f -> out.append("$indent        const val ${kotlinName(f.name)} = ${f.offset}\n") }
        return out.append("$indent    }\n$indent}\n").toString()
    }

    // Typed property for one struct field, or null when the type has no view (left as an offset).
    private fun fieldAccessor(f: Field, typedefs: Map<String, String>, records: Map<String, Layout>, call: String): String? {
        val name = kotlinName(f.name)
        val at = "ptr + ${f.offset}"
        val array = Regex("^(.*)\\[(\\d+)]$").find(f.type)
        val base = resolve((array?.groupValues?.get(1) ?: f.type).trim(), typedefs)
        if (array != null) {
            val view = when (base) {
                "float" -> "F32Array"; "double" -> "F64Array"
                "int", "unsigned int" -> "I32Array"; "unsigned char", "char", "signed char" -> "U8Array"
                "_Bool", "bool" -> "BoolArray"
                else -> return null
            }
            return "val $name: $view get() = $view($at)"
        }
        if (base.startsWith("struct ")) {
            val nested = base.removePrefix("struct ").trim()
            return if (nested in records) "val $name: $nested get() = $nested($at)" else null
        }
        val (type, get, set) = when {
            '*' in base || '(' in base -> Triple("Int", "getI32", "setI32")
            base == "float" -> Triple("Float", "getF32", "setF32")
            base == "double" -> Triple("Double", "getF64", "setF64")
            base == "_Bool" || base == "bool" -> Triple("Boolean", "getBool", "setBool")
            base == "long long" || base == "unsigned long long" -> Triple("Long", "getI64", "setI64")
            base == "unsigned char" || base == "char" || base == "signed char" -> Triple("Int", "getU8", "setU8")
            base == "unsigned short" || base == "short" -> Triple("Int", "getU16", "setU16")
            base.startsWith("enum ") || base in setOf("int", "unsigned int", "long", "unsigned long") -> Triple("Int", "getI32", "setI32")
            else -> return null
        }
        return "var $name: $type get() = $call.$get($at); set(value) { $call.$set($at, value) }"
    }

    // Parses clang's record-layout dump; keeps top-level fields ("|   type name") of named Fila* structs.
    private fun parseLayouts(dump: String): Map<String, Layout> = dump.split("*** Dumping AST Record Layout").mapNotNull { block ->
        val lines = block.lines()
        val name = lines.firstNotNullOfOrNull { Regex("^\\s*0 \\| struct (Fila\\w+(::\\(unnamed at [^)]*\\))?)$").find(it)?.groupValues?.get(1) } ?: return@mapNotNull null
        val size = Regex("\\[sizeof=(\\d+)").find(block)!!.groupValues[1].toInt()
        val fields = lines.mapNotNull { Regex("^\\s*(\\d+) \\|   (\\S.*) (\\w+)$").find(it) }
            .map { Field(it.groupValues[3], it.groupValues[1].toInt(), it.groupValues[2].replace("const ", "").trim()) }
        name to Layout(size, fields)
    }.toMap()

    private fun desugar(type: Map<*, *>, typedefs: Map<String, String>): String =
        resolve((type["desugaredQualType"] ?: type["qualType"]) as String, typedefs)

    private fun resolve(type: String, typedefs: Map<String, String>): String {
        var t = type.replace("const ", "").trim()
        while (t in typedefs && typedefs[t] != t) t = typedefs.getValue(t).replace("const ", "").trim()
        return t
    }

    private fun kotlinType(c: String, where: String): String = when {
        '*' in c || '(' in c -> "Int"
        c == "void" -> "Unit"
        c == "float" -> "Float"
        c == "double" -> "Double"
        c == "long long" || c == "unsigned long long" -> "JsBigInt"
        c.startsWith("enum ") -> "Int"
        c in setOf("_Bool", "bool", "char", "signed char", "unsigned char", "short", "unsigned short",
            "int", "unsigned int", "long", "unsigned long") -> "Int"
        else -> error("GenerateWasmExternals: no wasm mapping for C type '$c' ($where)")
    }

    private fun kotlinName(name: String) = if (name in KOTLIN_KEYWORDS) "`$name`" else name

    private fun header(pkg: String, source: String) =
        "// Generated by GenerateWasmExternals from c/$source headers. Do not edit.\n" +
            "@file:Suppress(\"FunctionName\", \"ObjectPropertyName\", \"unused\")\n\npackage $pkg\n\n" +
            (if (pkg == RUNTIME_PACKAGE) "" else "import $RUNTIME_PACKAGE.*\n\n")

    private companion object {
        /** Where the hand-written runtime (heapScoped, toI64, ...) lives, in :wasm. */
        const val RUNTIME_PACKAGE = "io.github.erkko68.filament.wasm"
        val KOTLIN_KEYWORDS = setOf("as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
            "interface", "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try",
            "typealias", "typeof", "val", "var", "when", "while")
    }
}

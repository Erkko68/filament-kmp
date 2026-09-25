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
 * `JsBigInt`), the enum constants under their C names, and one offsets object per struct. Into
 * [testDir] it emits each function's wasm arity for the export parity test.
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

            out.append("external interface $iface : ${baseInterface.get()} {\n")
            decls.filter { it["kind"] == "FunctionDecl" && it["name"] in declared }.sortedBy { it["name"] as String }.forEach { fn ->
                val name = fn["name"] as String
                val params = (fn["inner"] as List<Map<String, Any?>>?).orEmpty().filter { it["kind"] == "ParmVarDecl" }
                    .mapIndexed { i, p -> kotlinName(p["name"] as String? ?: "p$i") to kotlinType(desugar(p["type"] as Map<*, *>, typedefs), "$name param") }
                    .toMutableList()
                val cReturn = resolve(((fn["type"] as Map<*, *>)["qualType"] as String).substringBefore("(").trim(), typedefs)
                // wasm C ABI: a struct returned by value becomes a leading result pointer (sret).
                val ret = if (cReturn.startsWith("struct ")) { params.add(0, "result" to "Int"); "Unit" } else kotlinType(cReturn, "$name return")
                val retSuffix = if (ret == "Unit") "" else ": $ret"
                out.append("    fun _$name(${params.joinToString { "${it.first}: ${it.second}" }})$retSuffix\n")
                arities.append("    \"_$name\" to ${params.size},\n")
            }
            out.append("}\n\n")

            decls.filter { it["kind"] == "EnumDecl" }.forEach { enum ->
                var next = 0L
                (enum["inner"] as List<Map<String, Any?>>?).orEmpty().filter { it["kind"] == "EnumConstantDecl" }.forEach { k ->
                    val kName = k["name"] as String
                    val value = (k["inner"] as List<Map<String, Any?>>?)?.firstNotNullOfOrNull { it["value"] as String? }?.toLong() ?: next
                    next = value + 1
                    if (Regex("\\b${Regex.escape(kName)}\\b").containsMatchIn(text)) out.append("const val $kName = $value\n")
                }
            }
            out.append("\n")

            records.filterKeys { Regex("struct\\s+${Regex.escape(it)}\\s*\\{").containsMatchIn(text) }.toSortedMap().forEach { (rec, layout) ->
                out.append("object $rec {\n    const val SIZE = ${layout.size}\n")
                layout.fields.forEach { (field, offset) -> out.append("    const val ${kotlinName(field)} = $offset\n") }
                out.append("}\n\n")
            }
            mainOut.resolve("$iface.kt").writeText(out.toString())
        }

        testOut.resolve("WasmArities.kt").writeText(
            header(pkg, modules.get().values.joinToString()) +
                "/** wasm export name → parameter count, for the export parity test. */\n" +
                "val WASM_ARITIES: List<Pair<String, Int>> = listOf(\n$arities)\n",
        )
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

    private class Layout(val size: Int, val fields: List<Pair<String, Int>>)

    // Parses clang's record-layout dump; keeps top-level fields ("|   type name") of named Fila* structs.
    private fun parseLayouts(dump: String): Map<String, Layout> = dump.split("*** Dumping AST Record Layout").mapNotNull { block ->
        val lines = block.lines()
        val name = lines.firstNotNullOfOrNull { Regex("^\\s*0 \\| struct (Fila\\w+)$").find(it)?.groupValues?.get(1) } ?: return@mapNotNull null
        val size = Regex("\\[sizeof=(\\d+)").find(block)!!.groupValues[1].toInt()
        val fields = lines.mapNotNull { Regex("^\\s*(\\d+) \\|   (\\S.*) (\\w+)$").find(it) }.map { it.groupValues[3] to it.groupValues[1].toInt() }
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
            "@file:Suppress(\"FunctionName\", \"ObjectPropertyName\", \"unused\")\n\npackage $pkg\n\n"

    private companion object {
        val KOTLIN_KEYWORDS = setOf("as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
            "interface", "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try",
            "typealias", "typeof", "val", "var", "when", "while")
    }
}

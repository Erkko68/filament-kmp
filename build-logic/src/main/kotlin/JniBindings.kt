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
 * Generates JNI bindings for the Fila* C API (the Android counterpart of [GenerateWasmExternals]).
 *
 * clang parses the headers; per C module (`modules`, as on wasm) it emits a C file of JNI forwarders into
 * [cDir] and a Kotlin file of top-level `external fun`s named like the C functions into [kotlinDir]. The boundary is all primitives: pointers and callbacks
 * are `Long`, enums and ≤32-bit ints `Int`, 64-bit/size_t `Long`, `const char*` `String?`. Casts use
 * the declared C type, so one output serves every ABI. A struct returned by value gets a leading
 * `result` pointer, as on wasm.
 */
@CacheableTask
abstract class GenerateJniBindings : DefaultTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val headers: ConfigurableFileCollection

    /** Generated file/class name → header dir under c/, e.g. FilamentC → filament (as on wasm). */
    @get:Input abstract val modules: MapProperty<String, String>
    @get:Input abstract val packageName: Property<String>

    @get:Internal abstract val cSourceDir: DirectoryProperty
    @get:OutputDirectory abstract val cDir: DirectoryProperty
    @get:OutputDirectory abstract val kotlinDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val c = cSourceDir.get().asFile
        val moduleHeaders = modules.get().mapValues { (_, dir) -> c.resolve("$dir/c").listFiles { f -> f.extension == "h" }!!.sorted() }
        val umbrella = temporaryDir.resolve("umbrella.c").apply {
            writeText(moduleHeaders.values.flatten().joinToString("\n") { "#include \"${it.absolutePath}\"" } + "\n")
        }
        val ast = clang(temporaryDir.resolve("ast.json"), modules.get().values.map { "-I${c.resolve("$it/c").absolutePath}" } + umbrella.absolutePath)

        @Suppress("UNCHECKED_CAST")
        val decls = (JsonSlurper().parse(ast) as Map<String, Any?>)["inner"] as List<Map<String, Any?>>
        val typedefs = decls.filter { it["kind"] == "TypedefDecl" }.associate {
            val type = it["type"] as Map<*, *>
            it["name"] as String to (type["desugaredQualType"] ?: type["qualType"]) as String
        }

        val pkg = packageName.get()
        val cOutDir = cDir.get().asFile.apply { deleteRecursively(); mkdirs() }
        val kOutDir = kotlinDir.get().asFile.apply { deleteRecursively(); mkdirs() }

        for ((file, headerFiles) in moduleHeaders) {
            val text = headerFiles.joinToString("\n") { it.readText() }
            val declared = Regex("[^A-Za-z0-9_](Fila[A-Za-z0-9]+_[A-Za-z0-9_]+)\\s*\\(").findAll(text).map { it.groupValues[1] }.toSet()
            val functions = decls.filter { it["kind"] == "FunctionDecl" && it["name"] in declared }
                .sortedBy { it["name"] as String }.map { toFunction(it, typedefs) }
            val jniPrefix = "Java_${pkg.replace('.', '_')}_${file}_"

            val cOut = StringBuilder(banner(modules.get().getValue(file)))
            cOut.append("#include <jni.h>\n#include <stddef.h>\n#include <stdint.h>\n\n")
            headerFiles.forEach { cOut.append("#include \"${it.name}\"\n") }
            cOut.append("\n")

            // Top-level functions named like the C ones (as cinterop and the wasm wrappers name them). Calling one
            // initializes this file class first, and that runs `loaded`, so libfilament-c is always in by then.
            val kOut = StringBuilder(banner(modules.get().getValue(file)))
            kOut.append("@file:JvmName(\"$file\")\n@file:Suppress(\"FunctionName\", \"unused\")\n\npackage $pkg\n\n")
            kOut.append("private val loaded = FilaJni.load()\n\n")

            functions.forEach { fn -> emitFunction(fn, jniPrefix, cOut, kOut) }
            kOut.append("\n")

            // Struct views, shaped like the wasm ones, but layouts differ per ABI (pointer/size_t width,
            // x86-32 alignment): each struct's C side reports [sizeof, offset0, size0, ...] as compiled.
            cOut.append("static inline jintArray filaLayout(JNIEnv* env, const jint* values, jsize count) {\n")
            cOut.append("    jintArray out = (*env)->NewIntArray(env, count);\n")
            cOut.append("    (*env)->SetIntArrayRegion(env, out, 0, count, values);\n    return out;\n}\n\n")
            val structOut = StringBuilder()
            @Suppress("UNCHECKED_CAST")
            decls.filter { it["kind"] == "RecordDecl" && it["completeDefinition"] == true }
                .mapNotNull { r -> (r["name"] as String?)?.let { it to r } }
                .filter { (name, _) -> Regex("struct\\s+${Regex.escape(name)}\\s*\\{").containsMatchIn(text) }
                .sortedBy { it.first }
                .forEach { (name, record) -> emitStruct(Struct(name, "struct $name", "", record, typedefs), jniPrefix, cOut, kOut, structOut, "") }
            kOut.append("\n")

            // Enum types and constants under their C names, so code ported from the cinterop actuals reads the same.
            decls.filter { it["kind"] == "EnumDecl" }.forEach { enum ->
                (enum["name"] as String?)?.takeIf { Regex("enum\\s+${Regex.escape(it)}\\s*\\{").containsMatchIn(text) }
                    ?.let { kOut.append("typealias $it = Int\n") }
                var next = 0L
                @Suppress("UNCHECKED_CAST")
                (enum["inner"] as List<Map<String, Any?>>?).orEmpty().filter { it["kind"] == "EnumConstantDecl" }.forEach { k ->
                    val kName = k["name"] as String
                    @Suppress("UNCHECKED_CAST")
                    val value = (k["inner"] as List<Map<String, Any?>>?)?.firstNotNullOfOrNull { it["value"] as String? }?.toLong() ?: next
                    next = value + 1
                    if (Regex("\\b${Regex.escape(kName)}\\b").containsMatchIn(text)) kOut.append("const val $kName = $value\n")
                }
            }
            kOut.append("\n")

            // Scalar typedefs (FilaEntity = uint32_t, FilaTextureSampler = uint64_t) keep their names too.
            decls.filter { it["kind"] == "TypedefDecl" && (it["name"] as String).startsWith("Fila") }.forEach { td ->
                val tdName = td["name"] as String
                val resolved = resolve(tdName, typedefs)
                val scalar = !resolved.startsWith("struct ") && !resolved.startsWith("enum ") && '*' !in resolved && '(' !in resolved
                if (scalar && Regex("typedef\\s+[^;{}]*\\b${Regex.escape(tdName)}\\s*;").containsMatchIn(text)) {
                    kOut.append("typealias $tdName = ${kind(resolved, resolved, tdName).kotlin}\n")
                }
            }
            kOut.append("\n").append(structOut)

            cOutDir.resolve("$file.c").writeText(cOut.toString())
            kOutDir.resolve("$file.kt").writeText(kOut.toString())
        }
    }

    private fun emitFunction(fn: Function, jniPrefix: String, cOut: StringBuilder, kOut: StringBuilder) {
        val jniParams = fn.params.joinToString("") { ", ${it.kind.jni} a_${it.name}" }
        cOut.append("JNIEXPORT ${fn.ret.jni} JNICALL $jniPrefix${fn.name.replace("_", "_1")}(JNIEnv* env, jclass cls$jniParams) {\n")
        fn.params.filter { it.kind == Kind.STRING }.forEach {
            cOut.append("    const char* s_${it.name} = a_${it.name} ? (*env)->GetStringUTFChars(env, a_${it.name}, NULL) : NULL;\n")
        }
        val args = fn.params.filter { !it.sret }.joinToString { p ->
            when (p.kind) {
                Kind.STRING -> "s_${p.name}"
                Kind.PTR -> "(${p.cType})(intptr_t) a_${p.name}"
                else -> "(${p.cType}) a_${p.name}"
            }
        }
        val call = "${fn.name}($args)"
        val releases = fn.params.filter { it.kind == Kind.STRING }.joinToString("") {
            "    if (s_${it.name}) (*env)->ReleaseStringUTFChars(env, a_${it.name}, s_${it.name});\n"
        }
        val sret = fn.params.firstOrNull { it.sret }
        when {
            sret != null -> cOut.append("    *(${sret.cType})(intptr_t) a_${sret.name} = $call;\n$releases")
            fn.ret == Kind.VOID -> cOut.append("    $call;\n$releases")
            else -> {
                val wrap = when (fn.ret) {
                    Kind.BOOL -> "$call ? JNI_TRUE : JNI_FALSE"
                    Kind.PTR -> "(jlong)(intptr_t) $call"
                    Kind.STRING -> "(*env)->NewStringUTF(env, $call)"
                    else -> "(${fn.ret.jni}) $call"
                }
                if (releases.isEmpty()) cOut.append("    return $wrap;\n")
                else cOut.append("    ${fn.ret.jni} r = $wrap;\n$releases    return r;\n")
            }
        }
        cOut.append("}\n\n")

        val retSuffix = if (fn.ret == Kind.VOID) "" else ": ${fn.ret.kotlin}"
        kOut.append("external fun ${fn.name}(${fn.params.joinToString { "${kotlinName(it.name)}: ${it.kind.kotlin}" }})$retSuffix\n")
    }

    private enum class Kind(val jni: String, val kotlin: String) {
        VOID("void", "Unit"), BOOL("jboolean", "Boolean"), INT("jint", "Int"), LONG("jlong", "Long"),
        FLOAT("jfloat", "Float"), DOUBLE("jdouble", "Double"), PTR("jlong", "Long"), STRING("jstring", "String?"),
    }
    private class Param(val name: String, val cType: String, val kind: Kind, val sret: Boolean = false)
    private class Function(val name: String, val params: List<Param>, val ret: Kind)

    /** A struct view: [cType] + [path] address it in C (`path` non-empty for anonymous nested structs). */
    private inner class Struct(val kotlinName: String, val cType: String, val path: String, record: Map<String, Any?>, typedefs: Map<String, String>) {
        val fields = mutableListOf<Field>()
        val nested = mutableListOf<Struct>()

        init {
            var anonymous: Map<String, Any?>? = null
            @Suppress("UNCHECKED_CAST")
            (record["inner"] as List<Map<String, Any?>>?).orEmpty().forEach { d ->
                when (d["kind"]) {
                    "RecordDecl" -> anonymous = d
                    "FieldDecl" -> {
                        val fname = d["name"] as String
                        val written = ((d["type"] as Map<*, *>)["qualType"] as String).replace("const ", "").trim()
                        if ("(unnamed" in written) {
                            nested += Struct(fname.replaceFirstChar { it.uppercase() }, cType, "$path$fname.", anonymous!!, typedefs)
                            fields += Field(fname, FieldKind.NESTED, nestedClass = nested.last().kotlinName)
                        } else fields += field(fname, written, typedefs, "$kotlinName.$fname")
                    }
                }
            }
        }
    }

    private enum class FieldKind { FLOAT, DOUBLE, BOOL, INT, UINT, LONG, STRUCT, NESTED, F32_ARRAY, F64_ARRAY, I32_ARRAY }
    private class Field(val name: String, val kind: FieldKind, val nestedClass: String? = null)

    private fun field(name: String, written: String, typedefs: Map<String, String>, where: String): Field {
        Regex("^(.*)\\[(\\d+)]$").find(written)?.let { m ->
            val kind = when (resolve(m.groupValues[1], typedefs)) {
                "float" -> FieldKind.F32_ARRAY
                "double" -> FieldKind.F64_ARRAY
                "int", "unsigned int" -> FieldKind.I32_ARRAY
                else -> error("GenerateJniBindings: no array view for '$written' ($where)")
            }
            return Field(name, kind)
        }
        val c = resolve(written, typedefs)
        val kind = when {
            '*' in c || '(' in c -> FieldKind.LONG
            c.startsWith("struct ") -> return Field(name, FieldKind.STRUCT, nestedClass = c.removePrefix("struct ").trim())
            c == "float" -> FieldKind.FLOAT
            c == "double" -> FieldKind.DOUBLE
            c == "_Bool" || c == "bool" -> FieldKind.BOOL
            c in setOf("long", "unsigned long", "long long", "unsigned long long") -> FieldKind.LONG
            c.startsWith("unsigned") -> FieldKind.UINT
            c.startsWith("enum ") || c in setOf("char", "signed char", "short", "int") -> FieldKind.INT
            else -> error("GenerateJniBindings: no field mapping for C type '$c' ($where)")
        }
        return Field(name, kind)
    }

    private fun emitStruct(s: Struct, jniPrefix: String, cOut: StringBuilder, decls: StringBuilder, kOut: StringBuilder, indent: String) {
        val layoutFn = "FilaLayout_${(s.cType.removePrefix("struct ") + "_" + s.path.replace('.', '_')).trimEnd('_')}"
        val base = if (s.path.isEmpty()) "0" else "offsetof(${s.cType}, ${s.path.trimEnd('.')})"
        val size = if (s.path.isEmpty()) "sizeof(${s.cType})" else "sizeof(((${s.cType}*) 0)->${s.path.trimEnd('.')})"
        cOut.append("JNIEXPORT jintArray JNICALL $jniPrefix${layoutFn.replace("_", "_1")}(JNIEnv* env, jclass cls) {\n")
        cOut.append("    const jint l[] = {\n        (jint) $size,\n")
        s.fields.forEach { f ->
            cOut.append("        (jint) (offsetof(${s.cType}, ${s.path}${f.name}) - $base), (jint) sizeof(((${s.cType}*) 0)->${s.path}${f.name}),\n")
        }
        cOut.append("    };\n    return filaLayout(env, l, sizeof(l) / sizeof(l[0]));\n}\n\n")
        decls.append("external fun $layoutFn(): IntArray\n")

        kOut.append("${indent}class ${s.kotlinName}(val ptr: Long) {\n")
        kOut.append("$indent    private val b = FilaJni.buffer(ptr, SIZE)\n")
        s.fields.forEachIndexed { i, f ->
            val off = "L[${1 + 2 * i}]"
            val sz = "L[${2 + 2 * i}]"
            val n = kotlinName(f.name)
            val line = when (f.kind) {
                FieldKind.FLOAT -> "var $n: Float get() = b.getFloat($off); set(value) { b.putFloat($off, value) }"
                FieldKind.DOUBLE -> "var $n: Double get() = b.getDouble($off); set(value) { b.putDouble($off, value) }"
                FieldKind.BOOL -> "var $n: Boolean get() = b.get($off).toInt() != 0; set(value) { b.put($off, (if (value) 1 else 0).toByte()) }"
                FieldKind.INT -> "var $n: Int get() = b.readInt($off, $sz, signed = true); set(value) { b.writeInt($off, $sz, value) }"
                FieldKind.UINT -> "var $n: Int get() = b.readInt($off, $sz, signed = false); set(value) { b.writeInt($off, $sz, value) }"
                FieldKind.LONG -> "var $n: Long get() = b.readLong($off, $sz); set(value) { b.writeLong($off, $sz, value) }"
                FieldKind.STRUCT, FieldKind.NESTED -> "val $n: ${f.nestedClass} get() = ${f.nestedClass}(ptr + $off)"
                FieldKind.F32_ARRAY -> "val $n: F32Array get() = F32Array(b, $off)"
                FieldKind.F64_ARRAY -> "val $n: F64Array get() = F64Array(b, $off)"
                FieldKind.I32_ARRAY -> "val $n: I32Array get() = I32Array(b, $off)"
            }
            kOut.append("$indent    $line\n")
        }
        s.nested.forEach { emitStruct(it, jniPrefix, cOut, decls, kOut, "$indent    ") }
        kOut.append("$indent    companion object {\n")
        kOut.append("$indent        private val L = $layoutFn()\n")
        kOut.append("$indent        val SIZE: Int get() = L[0]\n")
        kOut.append("$indent    }\n$indent}\n")
        if (indent.isEmpty()) kOut.append("\n")
    }

    @Suppress("UNCHECKED_CAST")
    private fun toFunction(fn: Map<String, Any?>, typedefs: Map<String, String>): Function {
        val name = fn["name"] as String
        val params = (fn["inner"] as List<Map<String, Any?>>?).orEmpty().filter { it["kind"] == "ParmVarDecl" }
            .mapIndexed { i, p ->
                val type = p["type"] as Map<*, *>
                val written = (type["qualType"] as String).trim()
                Param(p["name"] as String? ?: "p$i", written, kind(resolve((type["desugaredQualType"] ?: written) as String, typedefs), written, "$name param"))
            }.toMutableList()
        val cReturn = ((fn["type"] as Map<*, *>)["qualType"] as String).substringBefore("(").trim()
        val resolved = resolve(cReturn, typedefs)
        val ret = if (resolved.startsWith("struct ")) {
            params.add(0, Param("result", "$cReturn *", Kind.PTR, sret = true))
            Kind.VOID
        } else kind(resolved, cReturn, "$name return")
        return Function(name, params, ret)
    }

    private fun kind(c: String, written: String, where: String): Kind = when {
        written.replace(" ", "") == "constchar*" -> Kind.STRING
        '*' in c || '(' in c -> Kind.PTR
        c == "void" -> Kind.VOID
        c == "_Bool" || c == "bool" -> Kind.BOOL
        c == "float" -> Kind.FLOAT
        c == "double" -> Kind.DOUBLE
        // Parsed for an LP64 target, so size_t/uint64_t land here; the C cast narrows on 32-bit ABIs.
        c in setOf("long", "unsigned long", "long long", "unsigned long long") -> Kind.LONG
        c.startsWith("enum ") || c in setOf("char", "signed char", "unsigned char", "short", "unsigned short", "int", "unsigned int") -> Kind.INT
        else -> error("GenerateJniBindings: no JNI mapping for C type '$c' ($where)")
    }

    private fun resolve(type: String, typedefs: Map<String, String>): String {
        var t = type.replace("const ", "").trim()
        while (t in typedefs && typedefs[t] != t) t = typedefs.getValue(t).replace("const ", "").trim()
        return t
    }

    // Any clang can dump the AST: -ffreestanding keeps it on clang's own stdint/stddef/stdbool.
    private fun clang(out: File, args: List<String>): File {
        val cmd = listOf("clang", "-fsyntax-only", "-ffreestanding", "--target=aarch64-linux-android", "-Xclang", "-ast-dump=json") + args
        val process = ProcessBuilder(cmd).redirectOutput(out).start()
        val err = process.errorStream.bufferedReader().readText()
        check(process.waitFor() == 0) { "clang failed:\n$err" }
        return out
    }

    private fun kotlinName(name: String) = if (name in KOTLIN_KEYWORDS) "`$name`" else name

    private fun banner(source: String) =
        "// Generated by GenerateJniBindings from c/$source headers. Do not edit.\n" +
            "// Committed so building needs no header parse; regenerate with :android:generateJniBindings.\n"

    private companion object {
        val KOTLIN_KEYWORDS = setOf("as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
            "interface", "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try",
            "typealias", "typeof", "val", "var", "when", "while")
    }
}

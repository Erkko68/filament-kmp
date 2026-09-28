import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
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
 * clang parses the headers; per function it emits a C forwarder into [cDir] and an
 * `@JvmStatic external fun` into [kotlinDir]. The boundary is all primitives: pointers and callbacks
 * are `Long`, enums and ≤32-bit ints `Int`, 64-bit/size_t `Long`, `const char*` `String?`. Casts use
 * the declared C type, so one output serves every ABI. A struct returned by value gets a leading
 * `result` pointer, as on wasm.
 */
@CacheableTask
abstract class GenerateJniBindings : DefaultTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val headers: ConfigurableFileCollection

    /** Header dirs under c/, e.g. filament, gltfio. */
    @get:Input abstract val modules: ListProperty<String>
    @get:Input abstract val packageName: Property<String>
    @get:Input abstract val className: Property<String>

    @get:Internal abstract val cSourceDir: DirectoryProperty
    @get:OutputDirectory abstract val cDir: DirectoryProperty
    @get:OutputDirectory abstract val kotlinDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val c = cSourceDir.get().asFile
        val headerFiles = modules.get().flatMap { dir -> c.resolve("$dir/c").listFiles { f -> f.extension == "h" }!!.sorted() }
        val text = headerFiles.joinToString("\n") { it.readText() }
        val umbrella = temporaryDir.resolve("umbrella.c").apply {
            writeText(headerFiles.joinToString("\n") { "#include \"${it.absolutePath}\"" } + "\n")
        }
        val ast = clang(temporaryDir.resolve("ast.json"), modules.get().map { "-I${c.resolve("$it/c").absolutePath}" } + umbrella.absolutePath)

        @Suppress("UNCHECKED_CAST")
        val decls = (JsonSlurper().parse(ast) as Map<String, Any?>)["inner"] as List<Map<String, Any?>>
        val typedefs = decls.filter { it["kind"] == "TypedefDecl" }.associate {
            val type = it["type"] as Map<*, *>
            it["name"] as String to (type["desugaredQualType"] ?: type["qualType"]) as String
        }
        val declared = Regex("[^A-Za-z0-9_](Fila[A-Za-z0-9]+_[A-Za-z0-9_]+)\\s*\\(").findAll(text).map { it.groupValues[1] }.toSet()
        val functions = decls.filter { it["kind"] == "FunctionDecl" && it["name"] in declared }
            .sortedBy { it["name"] as String }.map { toFunction(it, typedefs) }

        val pkg = packageName.get()
        val cls = className.get()
        val jniPrefix = "Java_${pkg.replace('.', '_')}_${cls}_"

        val cOut = StringBuilder(banner("//"))
        cOut.append("#include <jni.h>\n#include <stdint.h>\n\n")
        headerFiles.forEach { cOut.append("#include \"${it.name}\"\n") }
        cOut.append("\n")

        val kOut = StringBuilder(banner("//"))
        kOut.append("@file:Suppress(\"FunctionName\", \"unused\")\n\npackage $pkg\n\n")
        kOut.append("object $cls {\n    init { FilaJni.load() }\n\n")

        functions.forEach { fn ->
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
            kOut.append("    @JvmStatic external fun ${fn.name}(${fn.params.joinToString { "${kotlinName(it.name)}: ${it.kind.kotlin}" }})$retSuffix\n")
        }
        kOut.append("}\n\n")

        // Enum constants under their C names, so code ported from the cinterop actuals reads the same.
        decls.filter { it["kind"] == "EnumDecl" }.forEach { enum ->
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

        cDir.get().asFile.apply { deleteRecursively(); mkdirs() }.resolve("FilaJniBindings.c").writeText(cOut.toString())
        kotlinDir.get().asFile.apply { deleteRecursively(); mkdirs() }.resolve("$cls.kt").writeText(kOut.toString())
    }

    private enum class Kind(val jni: String, val kotlin: String) {
        VOID("void", "Unit"), BOOL("jboolean", "Boolean"), INT("jint", "Int"), LONG("jlong", "Long"),
        FLOAT("jfloat", "Float"), DOUBLE("jdouble", "Double"), PTR("jlong", "Long"), STRING("jstring", "String?"),
    }
    private class Param(val name: String, val cType: String, val kind: Kind, val sret: Boolean = false)
    private class Function(val name: String, val params: List<Param>, val ret: Kind)

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

    private fun banner(comment: String) =
        "$comment Generated by GenerateJniBindings from the c/ headers. Do not edit.\n" +
            "$comment Committed so building needs no header parse; regenerate with :android:generateJniBindings.\n"

    private companion object {
        val KOTLIN_KEYWORDS = setOf("as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
            "interface", "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try",
            "typealias", "typeof", "val", "var", "when", "while")
    }
}

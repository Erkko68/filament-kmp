package buildlogic.apigen.externals

import buildlogic.apigen.ApiGenTask
import buildlogic.apigen.KotlinBindings
import buildlogic.apigen.c.CFunction
import buildlogic.apigen.c.CManifest
import buildlogic.apigen.externals.jni.JniForwarderWriter
import buildlogic.apigen.externals.wasm.WasmArityTableWriter
import buildlogic.apigen.externals.wasm.WasmExportListWriter
import buildlogic.apigen.externals.wasm.WasmTypeTableWriter
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Generates each platform's glue for the functions in [manifest], the ones the common externals bind (Native needs
 * none: `@SymbolName` links directly). Into [outputDir]:
 *
 * ```
 * jni/<Header>.c                     JNI forwarders, compiled into the desktop and Android images
 * wasm/<runtime>-exports.txt         -sEXPORTED_FUNCTIONS of each wasm runtime
 * wasm/<runtime>-types.js            bool/float/int64 tables for the runtime's `--post-js`
 * webTest/WasmArities.kt             the default runtime's export arities, for the web parity test
 * ```
 */
@CacheableTask
abstract class GenerateBindingsTask : ApiGenTask() {
    @get:InputFile @get:PathSensitive(PathSensitivity.NONE)
    abstract val manifest: RegularFileProperty

    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val out = outputDir.get().asFile.apply { deleteRecursively() }
        val headers = CManifest.parse(manifest.get().asFile.readText()).headers
        bindingFiles(headers, config.get().kotlin!!, config.get().prefix).forEach { (path, text) ->
            out.resolve(path).apply { parentFile.mkdirs() }.writeText(text)
        }
    }
}

/** The glue of the functions each of [headers] declares, by path under the output dir. */
internal fun bindingFiles(headers: Map<String, List<CFunction>>, bindings: KotlinBindings, prefix: String): Map<String, String> {
    val out = LinkedHashMap<String, String>()
    headers.forEach { (header, functions) ->
        // The externals' file, and the facade class its functions compile to on the JVM.
        val name = File(header).nameWithoutExtension
        val jvmClass = "${bindings.packages.getValue(header.substringBefore('/'))}.${name.replaceFirstChar { it.uppercase() }}Kt"
        out["jni/$name.c"] = JniForwarderWriter.write("$name.kt", jvmClass, functions, headers.keys)
    }

    // A Kotlin module is named as its C module.
    val byRuntime = headers.entries.groupBy({ bindings.wasmRuntimes[it.key.substringBefore('/')] ?: bindings.wasmRuntime }) { it.value }
        .mapValues { (_, functions) -> functions.flatten().distinctBy { it.name }.sortedBy { it.name } }
    byRuntime.forEach { (runtime, functions) ->
        out["wasm/$runtime-exports.txt"] = WasmExportListWriter.write(functions)
        out["wasm/$runtime-types.js"] = WasmTypeTableWriter.write(functions, prefix)
    }
    out["webTest/WasmArities.kt"] = WasmArityTableWriter.write(byRuntime[bindings.wasmRuntime].orEmpty(), bindings.wasmPackage)
    return out
}

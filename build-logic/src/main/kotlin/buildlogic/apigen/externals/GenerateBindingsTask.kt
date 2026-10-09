package buildlogic.apigen.externals

import buildlogic.apigen.externals.jni.JniForwarderWriter
import buildlogic.apigen.externals.wasm.WasmArityTableWriter
import buildlogic.apigen.externals.wasm.WasmExportListWriter
import buildlogic.apigen.externals.wasm.WasmTypeTableWriter
import buildlogic.apigen.ApiGenTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Generates each platform's glue from the common externals, the single source of truth for the C API
 * surface Kotlin binds (Native needs none: `@SymbolName` links directly). Into [outputDir]:
 *
 * ```
 * jni/<SourceFile>.c                 JNI forwarders, compiled into the desktop and Android images
 * wasm/<runtime>-exports.txt         -sEXPORTED_FUNCTIONS of each wasm runtime
 * wasm/<runtime>-types.js            bool/float/int64 tables for the runtime's `--post-js`
 * webTest/WasmArities.kt             the default runtime's export arities, for the web parity test
 * ```
 */
@CacheableTask
abstract class GenerateBindingsTask : ApiGenTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileCollection

    /** The C API's headers the JNI forwarders include for the prototypes. */
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val headers: ConfigurableFileCollection

    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val out = outputDir.get().asFile.apply { deleteRecursively() }
        val cDir = apiGen().cDir
        val bindings = config.get().kotlin!!
        val sources = sources.files.sortedBy { it.path }.mapNotNull(ExternalFunctionParser::parse)
        val headerPaths = headers.files.map { it.relativeTo(cDir).invariantSeparatorsPath }.sorted()
        sources.forEach { source -> out.write("jni/${source.file.nameWithoutExtension}.c", JniForwarderWriter.write(source, headerPaths)) }

        val byRuntime = sources.groupBy { bindings.wasmRuntimes[it.kotlinModule] ?: bindings.wasmRuntime }
            .mapValues { (_, files) -> files.flatMap { it.functions }.distinctBy { it.symbol }.sortedBy { it.symbol } }
        byRuntime.forEach { (runtime, functions) ->
            out.write("wasm/$runtime-exports.txt", WasmExportListWriter.write(functions))
            out.write("wasm/$runtime-types.js", WasmTypeTableWriter.write(functions, config.get().prefix))
        }
        out.write("webTest/WasmArities.kt", WasmArityTableWriter.write(byRuntime[bindings.wasmRuntime].orEmpty(), bindings.wasmPackage))
    }

    private fun File.write(path: String, text: String) = resolve(path).apply { parentFile.mkdirs() }.writeText(text)
}

package buildlogic.apigen.externals

import buildlogic.apigen.externals.jni.JniForwarderWriter
import buildlogic.apigen.externals.wasm.WasmArityTableWriter
import buildlogic.apigen.externals.wasm.WasmExportListWriter
import buildlogic.apigen.externals.wasm.WasmTypeTableWriter
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Generates each platform's glue from the common externals, the single source of truth for the Fila*
 * surface Kotlin binds (Native needs none: `@SymbolName` links directly). Into [outputDir]:
 *
 * ```
 * jni/<SourceFile>.c                 JNI forwarders, compiled into the desktop and Android images
 * wasm/<runtime>-exports.txt         -sEXPORTED_FUNCTIONS of each wasm runtime
 * wasm/<runtime>-types.js            bool/float/int64 tables for fila-globals.js
 * webTest/WasmArities.kt             filament-kmp's export arities, for :web's parity test
 * ```
 */
@CacheableTask
abstract class GenerateBindingsTask : DefaultTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileCollection

    /** Kotlin module → the wasm runtime its C module links into; others go to [DEFAULT_WASM_RUNTIME]. */
    @get:Input abstract val wasmRuntimes: MapProperty<String, String>

    /** Fila* headers the JNI forwarders include for the prototypes. */
    @get:InputFiles @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val headers: ConfigurableFileCollection

    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val out = outputDir.get().asFile.apply { deleteRecursively() }
        val sources = sources.files.sortedBy { it.path }.mapNotNull(ExternalFunctionParser::parse)
        val headerNames = headers.files.map { it.name }.sorted()

        sources.forEach { out.write("jni/${it.file.nameWithoutExtension}.c", JniForwarderWriter.write(it, headerNames)) }

        val byRuntime = sources.groupBy { wasmRuntimes.get()[it.kotlinModule] ?: DEFAULT_WASM_RUNTIME }
            .mapValues { (_, files) -> files.flatMap { it.functions }.distinctBy { it.symbol }.sortedBy { it.symbol } }
        byRuntime.forEach { (runtime, functions) ->
            out.write("wasm/$runtime-exports.txt", WasmExportListWriter.write(functions))
            out.write("wasm/$runtime-types.js", WasmTypeTableWriter.write(functions))
        }
        out.write("webTest/WasmArities.kt", WasmArityTableWriter.write(byRuntime[DEFAULT_WASM_RUNTIME].orEmpty()))
    }

    private fun File.write(path: String, text: String) = resolve(path).apply { parentFile.mkdirs() }.writeText(text)

    companion object {
        const val DEFAULT_WASM_RUNTIME = "filament-kmp"
    }
}

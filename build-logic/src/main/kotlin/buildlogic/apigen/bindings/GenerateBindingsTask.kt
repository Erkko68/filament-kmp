package buildlogic.apigen.bindings

import buildlogic.apigen.ApiGenTask
import buildlogic.apigen.c.CManifest
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

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
        bindingFiles(headers, config.get().kotlin!!, config.get().naming.prefix).forEach { (path, text) ->
            out.resolve(path).apply { parentFile.mkdirs() }.writeText(text)
        }
    }
}

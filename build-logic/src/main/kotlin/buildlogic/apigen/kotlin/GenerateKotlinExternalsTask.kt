package buildlogic.apigen.kotlin

import buildlogic.apigen.ApiGenTask
import buildlogic.apigen.KotlinBindings
import buildlogic.apigen.c.CManifest
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * Generates the Kotlin externals of each header in `api-manifest.json` into its C module's package, in the Kotlin
 * module of the same name.
 */
@DisableCachingByDefault(because = "Writes committed sources")
abstract class GenerateKotlinExternalsTask : ApiGenTask() {
    @TaskAction
    fun generate() = apiGen().generateKotlinExternals()
}

/** Each header's externals by path under the Kotlin dir. */
internal fun kotlinExternals(manifest: CManifest, bindings: KotlinBindings): Map<String, String> =
    manifest.headers.entries.associate { (header, functions) ->
        val module = header.substringBefore('/')
        val pkg = bindings.packages.getValue(module)
        "${packageDir(module, pkg)}/${File(header).nameWithoutExtension}.kt" to KotlinExternalsWriter.write(header, functions, pkg, bindings.interop)
    }

/** Where [module]'s externals in [pkg] live, under the Kotlin dir. */
internal fun packageDir(module: String, pkg: String) = "$module/src/commonMain/kotlin/${pkg.replace('.', '/')}"

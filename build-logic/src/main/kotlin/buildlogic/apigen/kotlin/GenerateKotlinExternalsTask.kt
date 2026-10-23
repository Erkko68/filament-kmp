package buildlogic.apigen.kotlin

import buildlogic.apigen.ApiGenTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

/**
 * Generates the Kotlin externals of each header in `api-manifest.json` into its C module's package, in the Kotlin
 * module of the same name.
 */
@DisableCachingByDefault(because = "Writes committed sources")
abstract class GenerateKotlinExternalsTask : ApiGenTask() {
    @TaskAction
    fun generate() = apiGen().generateKotlinExternals()
}

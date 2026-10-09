package buildlogic.apigen.kotlin

import buildlogic.apigen.ApiGenConfig
import buildlogic.apigen.ApiGenTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * Generates each C module's Kotlin externals from its headers (`c/<module>/generated` and `c/<module>/manual`) into
 * the `capi` package of the Kotlin module of the same name.
 */
@DisableCachingByDefault(because = "Writes committed sources")
abstract class GenerateKotlinExternalsTask : ApiGenTask() {
    @TaskAction
    fun generate() {
        val apiGen = apiGen()
        val kotlin = checkNotNull(apiGen.kotlinDir) { "The config has no Kotlin bindings" }
        config.get().kotlin!!.packages.forEach { (module, pkg) -> kotlin.resolve(packageDir(module, pkg)).apply { deleteRecursively(); mkdirs() } }
        kotlinExternals(apiGen.cDir, config.get()).forEach { (path, text) -> kotlin.resolve(path).writeText(text) }
    }
}

/** Each module's externals by path under the Kotlin dir, from the headers under [c]. */
internal fun kotlinExternals(c: File, config: ApiGenConfig): Map<String, String> {
    val bindings = config.kotlin!!
    val headers = { module: String -> listOf("generated", "manual").flatMap { dir -> c.resolve("$module/$dir").listFiles { f -> f.extension == "h" }.orEmpty().sortedBy { it.name } } }
    // Every module's types: one module's functions take another's enums.
    val writer = KotlinExternalsWriter(c.listFiles().orEmpty().filter { it.isDirectory }.flatMap { headers(it.name) }.map { it.readText() }, config.prefix, bindings.interop)
    return bindings.packages.flatMap { (module, pkg) ->
        headers(module).mapNotNull { header ->
            writer.write(header.relativeTo(c).invariantSeparatorsPath, header.readText(), pkg)?.let { "${packageDir(module, pkg)}/${header.nameWithoutExtension}.kt" to it }
        }
    }.toMap()
}

/** Where [module]'s externals in [pkg] live, under the Kotlin dir. */
internal fun packageDir(module: String, pkg: String) = "$module/src/commonMain/kotlin/${pkg.replace('.', '/')}"

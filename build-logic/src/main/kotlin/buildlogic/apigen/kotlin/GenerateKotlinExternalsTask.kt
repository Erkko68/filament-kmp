package buildlogic.apigen.kotlin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * Generates each C module's Kotlin externals from its headers (`c/<module>/generated` and `c/<module>/manual`) into
 * the `capi` package of the Kotlin module of the same name.
 */
@DisableCachingByDefault(because = "Writes committed sources")
abstract class GenerateKotlinExternalsTask : DefaultTask() {
    @get:Internal abstract val cDir: DirectoryProperty

    @get:Internal abstract val kotlinDir: DirectoryProperty

    /** C module → the Kotlin package its externals go to. */
    @get:Input abstract val packages: MapProperty<String, String>

    @TaskAction
    fun generate() {
        val kotlin = kotlinDir.get().asFile
        packages.get().forEach { (module, pkg) -> kotlin.resolve(packageDir(module, pkg)).apply { deleteRecursively(); mkdirs() } }
        kotlinExternals(cDir.get().asFile, packages.get()).forEach { (path, text) -> kotlin.resolve(path).writeText(text) }
    }
}

/** Each module's externals by path under the Kotlin dir, from the headers under [c]; [packages]: C module → package. */
internal fun kotlinExternals(c: File, packages: Map<String, String>): Map<String, String> {
    val headers = { module: String -> listOf("generated", "manual").flatMap { dir -> c.resolve("$module/$dir").listFiles { f -> f.extension == "h" }.orEmpty().sortedBy { it.name } } }
    // Every module's types: filamat's functions take filament's enums.
    val writer = KotlinExternalsWriter(c.listFiles().orEmpty().filter { it.isDirectory }.flatMap { headers(it.name) }.map { it.readText() })
    return packages.flatMap { (module, pkg) ->
        headers(module).mapNotNull { header ->
            writer.write(header.relativeTo(c).invariantSeparatorsPath, header.readText(), pkg)?.let { "${packageDir(module, pkg)}/${header.nameWithoutExtension}.kt" to it }
        }
    }.toMap()
}

/** Where [module]'s externals in [pkg] live, under the Kotlin dir. */
internal fun packageDir(module: String, pkg: String) = "$module/src/commonMain/kotlin/${pkg.replace('.', '/')}"

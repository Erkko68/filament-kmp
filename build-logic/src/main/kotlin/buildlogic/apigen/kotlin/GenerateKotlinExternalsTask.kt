package buildlogic.apigen.kotlin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

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
        val c = cDir.get().asFile
        val headers = { module: String -> listOf("generated", "manual").flatMap { dir -> c.resolve("$module/$dir").listFiles { f -> f.extension == "h" }.orEmpty().sortedBy { it.name } } }
        // Every module's types: filamat's functions take filament's enums.
        val writer = KotlinExternalsWriter(c.listFiles().orEmpty().filter { it.isDirectory }.flatMap { headers(it.name) }.map { it.readText() })
        packages.get().forEach { (module, pkg) ->
            val out = kotlinDir.get().asFile.resolve("$module/src/commonMain/kotlin/${pkg.replace('.', '/')}").apply { deleteRecursively(); mkdirs() }
            // ponytail: a module moving record by record lists its migrated headers; drop once filament is done.
            val migrated = c.resolve("$module/migrated.txt").takeIf { it.exists() }?.readLines()?.filter { it.isNotBlank() }?.toSet()
            headers(module).filter { migrated == null || it.parentFile.name != "generated" || it.nameWithoutExtension in migrated }.forEach { header ->
                writer.write(header.relativeTo(c).invariantSeparatorsPath, header.readText(), pkg)?.let { out.resolve("${header.nameWithoutExtension}.kt").writeText(it) }
            }
        }
    }
}

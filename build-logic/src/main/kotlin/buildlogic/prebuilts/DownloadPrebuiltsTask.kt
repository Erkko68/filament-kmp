package buildlogic.prebuilts

import buildlogic.platform.FilamentTarget
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Extracts one target's static libraries from its upstream release tarball into [outputDir].
 * The `.prebuilt-source` stamp ("version|path") re-extracts when either changes, e.g. a Windows CRT
 * variant switch, which a non-empty-dir check would miss.
 */
abstract class DownloadPrebuiltsTask : DefaultTask() {
    @get:Input abstract val filamentVersion: Property<String>
    @get:Input abstract val target: Property<FilamentTarget>
    @get:OutputDirectory abstract val outputDir: DirectoryProperty
    @get:Internal abstract val cacheDir: DirectoryProperty

    @TaskAction
    fun run() {
        val version = filamentVersion.get()
        val target = target.get()
        val (asset, path) = checkNotNull(target.release) { "${target.id} has no upstream release" }
        val outDir = outputDir.get().asFile
        val stampFile = outDir.resolve(STAMP)
        val stamp = "$version|$path"
        if (stampFile.isFile && stampFile.readText().trim() == stamp) return

        outDir.listFiles()?.forEach { it.deleteRecursively() }
        val tarball = ArchiveCache.filamentRelease(cacheDir.get().asFile, version, asset, logger)
        val count = if (path.startsWith("xcf:")) {
            TarExtraction.extractXcframeworkSlice(tarball, path.removePrefix("xcf:"), outDir)
        } else {
            TarExtraction.extractLibs(tarball, path, outDir)
        }
        check(count > 0) { "No libraries for ${target.id} in ${tarball.name} under '$path'" }
        stampFile.writeText(stamp + "\n")
        logger.lifecycle("[${target.id}] extracted $count libraries")
    }

    companion object {
        const val STAMP = ".prebuilt-source"
    }
}

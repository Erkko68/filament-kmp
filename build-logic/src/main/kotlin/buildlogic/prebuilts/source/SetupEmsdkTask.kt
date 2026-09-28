package buildlogic.prebuilts.source

import buildlogic.prebuilts.ArchiveCache
import buildlogic.prebuilts.TarExtraction
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Installs the Emscripten SDK into [installDir] (.emsdk/), pinned to the version upstream Filament
 * builds its wasm with (`emsdkVersion` in gradle.properties). CMake then uses its toolchain file directly.
 */
abstract class SetupEmsdkTask @Inject constructor(private val exec: ExecOperations) : DefaultTask() {
    @get:Input abstract val emsdkVersion: Property<String>
    @get:Internal abstract val installDir: DirectoryProperty
    @get:Internal abstract val cacheDir: DirectoryProperty

    private fun stamp() = installDir.get().asFile.resolve(".installed-version")

    @TaskAction
    fun install() {
        val version = emsdkVersion.get()
        // Checked here, not as up-to-dateness: a CI cache restores .emsdk/ without Gradle's task history.
        if (stamp().isFile && stamp().readText().trim() == version) return
        val dir = installDir.get().asFile.apply { deleteRecursively(); mkdirs() }
        TarExtraction.extractTree(ArchiveCache.emsdk(cacheDir.get().asFile, version, logger), dir)
        val emsdk = dir.resolve(if (System.getProperty("os.name").startsWith("Windows")) "emsdk.bat" else "emsdk").path
        exec.exec { commandLine(emsdk, "install", version) }
        exec.exec { commandLine(emsdk, "activate", "--embedded", version) }
        stamp().writeText(version + "\n")
    }
}

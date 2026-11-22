package buildlogic.platform

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

/**
 * Fails if the desktop library in [libraryDir] needs a newer OS than its floor, or a library that OS doesn't ship.
 * The build machine sets both silently: a newer CI image once raised the Linux library to glibc 2.43.
 */
abstract class CheckOsFloorTask @Inject constructor(private val exec: ExecOperations) : DefaultTask() {
    @get:Input abstract val target: Property<FilamentTarget>
    @get:InputDirectory @get:PathSensitive(PathSensitivity.NAME_ONLY) abstract val libraryDir: DirectoryProperty
    /** The CMake build tree; on Windows its cache names the MSVC linker, which dumps the DLL's imports. */
    @get:Internal abstract val cmakeBuildDir: DirectoryProperty

    @TaskAction
    fun check() {
        val lib = libraryDir.get().asFile.listFiles()!!
            .single { it.extension in setOf("so", "dylib", "dll") && it.name.contains("filament-c") }
        val problems = when (target.get().osFamily) {
            "linux" -> linux(lib)
            "macos" -> macos(lib)
            "windows" -> windows(lib)
            else -> error("Not a desktop target: ${target.get()}")
        }
        if (problems.isNotEmpty()) throw GradleException("${lib.name} breaks its OS floor:\n" + problems.joinToString("\n") { "  - $it" })
    }

    private fun linux(lib: File): List<String> {
        val out = run("readelf", "-d", "-V", "-W", lib.path)
        val needed = Regex("""\(NEEDED\).*\[(.+)]""").findAll(out).map { it.groupValues[1] }.toList()
        val glibc = Regex("""Name: GLIBC_([\d.]+)""").findAll(out).map { it.groupValues[1] }.distinct().toList()
        return needed.filterNot { name -> LINUX_SYSTEM_LIBS.any(name::startsWith) }.map { "needs $it, not part of a base install" } +
            glibc.filter { newer(it, LINUX_MIN_GLIBC) }.map { "needs GLIBC_$it, above the $LINUX_MIN_GLIBC floor" }
    }

    private fun macos(lib: File): List<String> {
        val minos = Regex("""minos (\S+)""").find(run("vtool", "-show-build", lib.path))?.groupValues?.get(1)
            ?: return listOf("has no LC_BUILD_VERSION")
        // otool -L: the file, the library's own install name, then one line per dependency.
        val deps = run("otool", "-L", lib.path).lines().drop(2).map { it.trim().substringBefore(" (") }.filter { it.isNotEmpty() }
        return listOfNotNull(minos.takeIf { newer(it, MACOS_DEPLOYMENT_TARGET) }?.let { "needs macOS $it, above the $MACOS_DEPLOYMENT_TARGET floor" }) +
            deps.filterNot { it.startsWith("/usr/lib/") || it.startsWith("/System/Library/") }.map { "needs $it, not part of macOS" }
    }

    private fun windows(lib: File): List<String> {
        val cache = cmakeBuildDir.get().file("CMakeCache.txt").asFile.readLines()
        val linker = cache.first { it.startsWith("CMAKE_LINKER:") }.substringAfter('=')
        val dlls = Regex("""^\s+(\S+\.dll)\s*$""", setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
            .findAll(run(linker, "/dump", "/dependents", lib.path)).map { it.groupValues[1] }
        // /MT: a dynamic CRT is missing on machines without the VC++ redistributable.
        return dlls.filter { CRT_DLL.containsMatchIn(it) }.map { "needs $it; link the CRT statically (/MT)" }.toList()
    }

    private fun run(vararg command: String): String {
        val out = ByteArrayOutputStream()
        exec.exec { commandLine(*command); standardOutput = out }
        return out.toString()
    }

    private companion object {
        /** On every glibc distro, with a GL driver. */
        val LINUX_SYSTEM_LIBS = listOf("ld-linux", "libc.so", "libm.so", "libdl.so", "libpthread.so", "librt.so", "libgcc_s.so", "libGL.so")
        val CRT_DLL = Regex("""^(vcruntime|msvcp|ucrtbase|api-ms-win-crt|concrt|vcomp)""", RegexOption.IGNORE_CASE)

        fun newer(version: String, floor: String): Boolean {
            val a = version.split('.').map(String::toInt)
            val b = floor.split('.').map(String::toInt)
            return (0 until maxOf(a.size, b.size)).map { a.getOrElse(it) { 0 } - b.getOrElse(it) { 0 } }.firstOrNull { it != 0 }?.let { it > 0 } ?: false
        }
    }
}

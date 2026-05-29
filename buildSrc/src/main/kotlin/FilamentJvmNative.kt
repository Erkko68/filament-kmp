import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import java.io.File

// jextract major version. Pinned to 22 (not the toolchain's 25) so the generated bindings
// target the JDK 22 FFM API — i.e. they use find().orElseThrow() rather than the
// JDK 23+ SymbolLookup.findOrThrow(), keeping the consumer floor at JDK 22 (release 22).
// jextract is a manual dev prerequisite — run scripts/gradle/download_jextract.py once to install it
// (the build does not auto-download it). That script pins the exact early-access build coordinates.
private const val JEXTRACT_MAJOR = "22"

/**
 * Result of wiring the JVM/Panama native build for one Kotlin module.
 *
 * @property dylibDir         build/cmake dir that holds the freshly built libfilament-c.{dylib,so,dll}
 * @property generatedJavaDir build/generated/jextract — jextract output, added to the jvm Java source set
 * @property platformArch     "{platform}-{arch}", e.g. "macos-arm64", used as the natives/ resource subdir
 * @property cmakeBuild       the task producing the dylib (depend on it from processResources)
 * @property jextract         the task producing the generated Java (depend on it from the jvm compile tasks)
 */
data class FilamentJvmNative(
    val dylibDir: Provider<Directory>,
    val generatedJavaDir: Provider<Directory>,
    val platformArch: String,
    val cmakeBuild: TaskProvider<Exec>,
    val jextract: TaskProvider<Exec>,
)

/**
 * Registers the native build for the Project Panama (FFM) JVM bindings:
 *   1. builds the combined SHARED library via c/CMakeLists.txt (-DFILAMENT_BUILD_SHARED=ON), and
 *   2. runs jextract over the C headers to generate the low-level binding classes.
 *
 * Mirrors the per-host detection in java/filament/build.gradle.kts (which drives the JNI build)
 * so the same prebuilts tree feeds both. Reusable across the kotlin JVM modules.
 *
 * @param headerDirs  C header dirs to extract (e.g. c/filament/c). All *.h within are #included
 *                    into one umbrella header passed to jextract.
 * @param includeDirs -I dirs for jextract/clang (header dirs + Filament include/).
 * @param ffmPackage  Java package for the generated bindings (e.g. io.github.erkko68.filament.ffm).
 * @param headerClassName  name of the generated top-level functions class (e.g. FilamentC).
 */
fun Project.applyFilamentJvmNative(
    headerDirs: List<File>,
    includeDirs: List<File>,
    ffmPackage: String,
    headerClassName: String,
): FilamentJvmNative {
    // ── Host platform / arch (matches java/filament/build.gradle.kts) ─────────
    val hostOs = System.getProperty("os.name")
    val platform = when {
        hostOs.startsWith("Mac", ignoreCase = true) -> "macos"
        hostOs.startsWith("Windows", ignoreCase = true) -> "windows"
        else -> "linux"
    }
    val archRaw = System.getProperty("os.arch").lowercase()
    val arch = when {
        archRaw.contains("aarch64") || archRaw.contains("arm64") -> "Arm64"
        archRaw == "x64" || archRaw.contains("amd64") || archRaw.contains("x86_64") -> "X64"
        else -> error("Unsupported filament.arch '$archRaw'. Use arm64 or x64.")
    }
    val resArch = if (arch == "Arm64") "arm64" else "x64"
    val platformArch = "$platform-$resArch"
    val prebuiltsTarget = when (platform) {
        "macos" -> if (arch == "Arm64") "macosArm64" else "macosX64"
        "linux" -> if (arch == "Arm64") "linuxArm64" else "linuxX64"
        "windows" -> "mingwX64"
        else -> error("Unsupported platform '$platform'")
    }

    val cmakePath = listOf("/opt/homebrew/bin/cmake", "/usr/local/bin/cmake")
        .firstOrNull { File(it).exists() } ?: "cmake"

    val cmakeSourceDir = rootProject.file("c")
    val cmakeBuildDir = layout.buildDirectory.dir("cmake").get().asFile
    val generatedDir = layout.buildDirectory.dir("generated/jextract").get().asFile

    val downloadPrebuilts = rootProject.tasks.named("downloadPrebuilts_$prebuiltsTarget")
    val downloadIncludes = rootProject.tasks.named("downloadIncludes")

    // ── CMake: configure + build the combined SHARED library ──────────────────
    val cmakeConfigure = tasks.register("cmakeConfigureFilamentCJvm", Exec::class.java) {
        dependsOn(downloadPrebuilts, downloadIncludes)
        doFirst { cmakeBuildDir.mkdirs() }
        workingDir(cmakeBuildDir)
        val args = mutableListOf(
            cmakePath, cmakeSourceDir.absolutePath.replace('\\', '/'),
            "-DFILAMENT_BUILD_SHARED=ON",
            "-DFILAMENT_PLATFORM=$platform",
            "-DFILAMENT_ARCH=$arch",
            "-DCMAKE_BUILD_TYPE=Release",
        )
        if (platform == "macos") {
            args += "-DCMAKE_OSX_SYSROOT=macosx"
            args += "-DCMAKE_OSX_ARCHITECTURES=${if (arch == "Arm64") "arm64" else "x86_64"}"
        }
        commandLine(args)
    }

    val cmakeBuild = tasks.register("cmakeBuildFilamentCJvm", Exec::class.java) {
        dependsOn(cmakeConfigure)
        workingDir(cmakeBuildDir)
        // No output declarations: cmakeConfigure writes CMakeCache.txt into this same dir, so
        // declaring it as an output here would let Gradle wipe the cache before the build runs.
        // CMake's own incremental build keeps rebuilds cheap. (Matches java/filament.)
        commandLine(cmakePath, "--build", ".", "--target", "filament-c-jvm", "--config", "Release")
    }

    // ── jextract: generate the bindings from the headers ─────────────────────
    // jextract is a manual dev prerequisite (not auto-downloaded). Install it once with
    // scripts/gradle/download_jextract.py; the task below fails with that hint if it's missing.
    val jextractBin = rootProject.file(
        ".gradle/jextract/jextract-$JEXTRACT_MAJOR/bin/" +
            if (platform == "windows") "jextract.bat" else "jextract",
    )

    val absHeaderDirs = headerDirs.map { it.absolutePath }
    val absIncludeDirs = includeDirs.map { it.absolutePath }
    val headerFiles = headerDirs.flatMap { dir ->
        dir.listFiles { f -> f.extension == "h" }?.toList() ?: emptyList()
    }.sortedBy { it.name }

    val jextract = tasks.register("jextractFilamentC", Exec::class.java) {
        dependsOn(downloadIncludes)
        inputs.files(headerFiles)
        outputs.dir(generatedDir)
        doFirst {
            if (!jextractBin.exists()) {
                throw org.gradle.api.GradleException(
                    "jextract $JEXTRACT_MAJOR not found at $jextractBin.\n" +
                        "It is a one-time dev prerequisite — install it with:\n" +
                        "    python3 scripts/gradle/download_jextract.py $JEXTRACT_MAJOR",
                )
            }
            generatedDir.deleteRecursively()
            generatedDir.mkdirs()
            // jextract takes a single header; build an umbrella that #includes every C header.
            val umbrella = File(cmakeBuildDir.parentFile, "filament_c_all.h")
            umbrella.parentFile.mkdirs()
            umbrella.writeText(headerFiles.joinToString("\n") { "#include \"${it.name}\"" } + "\n")
        }
        val umbrella = File(cmakeBuildDir.parentFile, "filament_c_all.h")
        val cmd = mutableListOf(
            jextractBin.absolutePath,
            "--output", generatedDir.absolutePath,
            "-t", ffmPackage,
            "--header-class-name", headerClassName,
        )
        absHeaderDirs.forEach { cmd += listOf("-I", it) }
        absIncludeDirs.forEach { cmd += listOf("-I", it) }
        cmd += umbrella.absolutePath
        commandLine(cmd)
    }

    return FilamentJvmNative(
        dylibDir = layout.buildDirectory.dir("cmake"),
        generatedJavaDir = layout.buildDirectory.dir("generated/jextract"),
        platformArch = platformArch,
        cmakeBuild = cmakeBuild,
        jextract = jextract,
    )
}

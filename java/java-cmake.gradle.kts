// Shared CMake build logic for all java/ modules.
// Applied with: apply(from = rootProject.file("java-cmake.gradle.kts"))
// The applying module must already have java-library (or kotlin("jvm")) applied.

configure<org.gradle.api.plugins.JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val hostOs = System.getProperty("os.name")
val hostPlatform = when {
    hostOs.startsWith("Mac", ignoreCase = true) -> "macos"
    hostOs.startsWith("Windows", ignoreCase = true) -> "windows"
    else -> "linux"
}
val platform = (project.findProperty("filament.platform") as String? ?: hostPlatform).lowercase().also {
    if (it !in listOf("macos", "linux", "windows"))
        error("Unsupported filament.platform '$it'. Use macos, linux, or windows.")
}

val hostArchRaw = System.getProperty("os.arch").lowercase()
val archRaw = (project.findProperty("filament.arch") as String? ?: hostArchRaw).lowercase()
val arch = when {
    archRaw.contains("aarch64") || archRaw.contains("arm64") -> "Arm64"
    archRaw == "x64" || archRaw.contains("amd64") || archRaw.contains("x86_64") -> "X64"
    else -> error("Unsupported filament.arch '$archRaw'. Use arm64 or x64.")
}

val cmakePath = listOf("/opt/homebrew/bin/cmake", "/usr/local/bin/cmake")
    .firstOrNull { File(it).exists() } ?: "cmake"

// Map JVM (host) platform/arch → Kotlin Native target name used by the prebuilts script.
val prebuiltsTarget = when (platform) {
    "macos"   -> if (arch == "Arm64") "macosArm64" else "macosX64"
    "linux"   -> if (arch == "Arm64") "linuxArm64" else "linuxX64"
    "windows" -> "mingwX64"
    else      -> error("Unsupported platform '$platform'")
}

// Prebuilt static libs are downloaded by the root-level task in build.gradle.kts.
val downloadPrebuiltsTask = rootProject.tasks.named("downloadPrebuilts_$prebuiltsTarget")

tasks.register<Exec>("cmakeConfig") {
    dependsOn(downloadPrebuiltsTask)
    val cmakeBuildDir = layout.buildDirectory.dir("cmake").get().asFile
    doFirst { cmakeBuildDir.mkdirs() }
    workingDir(cmakeBuildDir)
    // CMake parses backslashes in cache vars as escapes (FindJNI.cmake regex chokes on
    // "\h" in "C:\hostedtoolcache\..."). Pass forward slashes — CMake handles them on Windows.
    val javaHome = System.getProperty("java.home").replace('\\', '/')
    val args = mutableListOf(
        cmakePath, "../../",
        "-DFILAMENT_PLATFORM=$platform",
        "-DFILAMENT_ARCH=$arch",
        "-DCMAKE_BUILD_TYPE=Release",
        "-DJAVA_HOME=$javaHome",
    )
    if (platform == "macos") {
        val osxArch = if (arch == "Arm64") "arm64" else "x86_64"
        args += "-DCMAKE_OSX_SYSROOT=macosx"
        args += "-DCMAKE_OSX_ARCHITECTURES=$osxArch"
    }
    commandLine(args)
}

tasks.register<Exec>("cmakeBuild") {
    dependsOn("cmakeConfig")
    workingDir(layout.buildDirectory.dir("cmake").get().asFile)
    // --config Release is needed for multi-config generators (Visual Studio on Windows);
    // it's a no-op for single-config generators (Make / Ninja on macOS / Linux).
    commandLine(cmakePath, "--build", ".", "--config", "Release")
}

val resArch = if (arch == "Arm64") "arm64" else "x64"

tasks.named<org.gradle.language.jvm.tasks.ProcessResources>("processResources") {
    dependsOn("cmakeBuild")
    // Single-config generators (Make / Ninja on macOS / Linux) put libs directly in build/cmake/.
    from(layout.buildDirectory.dir("cmake")) {
        include("*.dylib", "*.so", "*.dll")
        into("natives/$platform-$resArch")
    }
    // Multi-config generators (Visual Studio on Windows) put DLLs in build/cmake/Release/.
    from(layout.buildDirectory.dir("cmake/Release")) {
        include("*.dll")
        into("natives/$platform-$resArch")
    }
    // Bundle pre-compiled natives from other platforms (used in CI publish job).
    // Pass -PjniArtifactsDir=<path> where the dir has {platform}-{arch}/{module}/ subdirs.
    val jniArtifactsDir = (project.findProperty("jniArtifactsDir") as? String)?.let { file(it) }
    if (jniArtifactsDir != null && jniArtifactsDir.exists()) {
        jniArtifactsDir.listFiles { f -> f.isDirectory }?.forEach { platformDir ->
            val moduleDir = File(platformDir, project.name)
            if (moduleDir.exists()) {
                from(moduleDir) {
                    include("*.dylib", "*.so", "*.dll")
                    into("natives/${platformDir.name}")
                }
            }
        }
    }
}

tasks.named("assemble") {
    dependsOn("cmakeBuild")
}

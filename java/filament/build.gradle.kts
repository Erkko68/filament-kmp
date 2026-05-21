plugins {
    `java-library`
    id("filament-publish")
}

// This module drives the combined CMake build at java/CMakeLists.txt — one shared lib
// (libfilament-jni) containing the JNI glue for filament + filamat + gltfio + filament-utils,
// with the Filament static archives linked exactly once. The dylib is packaged into this
// module's resources and reaches the other three java/* modules transitively at runtime.

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.annotations)
}

// ── Platform / arch ──────────────────────────────────────────────────────────
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

val prebuiltsTarget = when (platform) {
    "macos"   -> if (arch == "Arm64") "macosArm64" else "macosX64"
    "linux"   -> if (arch == "Arm64") "linuxArm64" else "linuxX64"
    "windows" -> "mingwX64"
    else      -> error("Unsupported platform '$platform'")
}

val downloadPrebuiltsTask = rootProject.tasks.named("downloadPrebuilts_$prebuiltsTarget")
val downloadIncludesTask = rootProject.tasks.named("downloadIncludes")

// ── CMake driver ─────────────────────────────────────────────────────────────
val combinedCmakeSourceDir = rootProject.file("java")

tasks.register<Exec>("cmakeConfig") {
    dependsOn(downloadPrebuiltsTask)
    dependsOn(downloadIncludesTask)
    val cmakeBuildDir = layout.buildDirectory.dir("cmake").get().asFile
    doFirst { cmakeBuildDir.mkdirs() }
    workingDir(cmakeBuildDir)
    // CMake parses backslashes in cache vars as escapes (FindJNI.cmake regex chokes on
    // "\h" in "C:\hostedtoolcache\..."). Pass forward slashes — CMake handles them on Windows.
    val javaHome = System.getProperty("java.home").replace('\\', '/')
    val args = mutableListOf(
        cmakePath, combinedCmakeSourceDir.absolutePath.replace('\\', '/'),
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
    // no-op for single-config generators (Make / Ninja on macOS / Linux).
    commandLine(cmakePath, "--build", ".", "--config", "Release")
}

// ── Package the dylib into resources ─────────────────────────────────────────
val resArch = if (arch == "Arm64") "arm64" else "x64"

tasks.named<org.gradle.language.jvm.tasks.ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("cmakeBuild")
    // Single-config generators (Make / Ninja) put libs directly in build/cmake/.
    from(layout.buildDirectory.dir("cmake")) {
        include("*.dylib", "*.so", "*.dll")
        into("natives/$platform-$resArch")
    }
    // Multi-config generators (Visual Studio) put DLLs in build/cmake/Release/.
    from(layout.buildDirectory.dir("cmake/Release")) {
        include("*.dll")
        into("natives/$platform-$resArch")
    }
    // Bundle pre-compiled natives from other platforms (used in CI publish job).
    // Pass -PjniArtifactsDir=<path> where the dir has {platform}-{arch}/ subdirs each
    // containing the combined libfilament-jni for that platform.
    val jniArtifactsDir = (project.findProperty("jniArtifactsDir") as? String)?.let { file(it) }
    if (jniArtifactsDir != null && jniArtifactsDir.exists()) {
        jniArtifactsDir.listFiles { f -> f.isDirectory }?.forEach { platformDir ->
            from(platformDir) {
                include("*.dylib", "*.so", "*.dll")
                into("natives/${platformDir.name}")
            }
        }
    }
}

tasks.named("assemble") {
    dependsOn("cmakeBuild")
}

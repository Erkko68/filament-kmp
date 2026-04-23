plugins {
    `java-library`
}

val projectGroup: String by project
group = "$projectGroup.java"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":java:filament"))
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks.register<Exec>("cmakeConfig") {
    val cmakeBuildDir = layout.buildDirectory.dir("cmake").get().asFile
    doFirst {
        if (!cmakeBuildDir.exists()) cmakeBuildDir.mkdirs()
    }
    workingDir(cmakeBuildDir)

    val hostPlatform = when {
        System.getProperty("os.name").startsWith("Mac", ignoreCase = true) -> "macos"
        System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> "windows"
        else -> "linux"
    }
    val p = (project.findProperty("filament.platform") as String? ?: hostPlatform).lowercase()
    val platform = when (p) {
        "macos", "linux", "windows" -> p
        else -> throw GradleException("Unsupported filament.platform '$p'. Use macos, linux, or windows.")
    }

    val a = (project.findProperty("filament.arch") as String? ?: "arm64").lowercase()
    val arch = when (a) {
        "arm64" -> "Arm64"
        "x64", "amd64" -> "X64"
        else -> throw GradleException("Unsupported filament.arch '$a'. Use arm64 or x64.")
    }

    val cmakePath = if (File("/opt/homebrew/bin/cmake").exists()) "/opt/homebrew/bin/cmake" else "cmake"
    val args = mutableListOf(
        cmakePath,
        "../../",
        "-DFILAMENT_PLATFORM=$platform",
        "-DFILAMENT_ARCH=$arch"
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
    val cmakePath = if (File("/opt/homebrew/bin/cmake").exists()) "/opt/homebrew/bin/cmake" else "cmake"
    commandLine(cmakePath, "--build", ".")
}

// Native library bundling
val hostPlatform = when {
    System.getProperty("os.name").startsWith("Mac", ignoreCase = true) -> "macos"
    System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> "windows"
    else -> "linux"
}
val resPlatform = (project.findProperty("filament.platform") as String? ?: hostPlatform).lowercase()
val resArch = (project.findProperty("filament.arch") as String? ?: "arm64").lowercase().let {
    if (it == "aarch64" || it == "arm64") "arm64" else "x64"
}

tasks.processResources {
    dependsOn("cmakeBuild")
    from(layout.buildDirectory.dir("cmake")) {
        include("*.dylib", "*.so", "*.dll")
        into("natives/${resPlatform}-${resArch}")
    }
}

tasks.named("assemble") {
    dependsOn("cmakeBuild")
}


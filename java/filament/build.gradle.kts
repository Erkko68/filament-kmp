plugins {
    `java-library`
}

val projectGroup: String by project
group = "$projectGroup.java"

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    val isMacos = p == "macos"

    val platform = when (p) {
        "linux", "windows", "macos" -> p
        else -> throw GradleException("Unsupported filament.platform '$p'. Use linux, windows, or macos.")
    }

    val hostArch = System.getProperty("os.arch").lowercase().let {
        if (it.contains("aarch64") || it.contains("arm64")) "arm64" else "x64"
    }
    val a = (project.findProperty("filament.arch") as String? ?: hostArch).lowercase()
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
        "-DFILAMENT_ARCH=$arch",
        "-DJAVA_HOME=${System.getProperty("java.home")}"
    )
    if (platform == "macos") {
        args += "-DCMAKE_OSX_SYSROOT=macosx"
        args += "-DCMAKE_OSX_ARCHITECTURES=arm64"
    }
    commandLine(args)
}

tasks.register<Exec>("cmakeBuild") {
    dependsOn("cmakeConfig")
    workingDir(layout.buildDirectory.dir("cmake").get().asFile)
    
    val p = (project.findProperty("filament.platform") as String? ?: hostPlatform).lowercase()

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
val hostArchDefault = System.getProperty("os.arch").lowercase().let {
    if (it.contains("aarch64") || it.contains("arm64")) "arm64" else "x64"
}
val resArch = (project.findProperty("filament.arch") as String? ?: hostArchDefault).lowercase().let {
    if (resPlatform == "macos") "arm64" else if (it == "aarch64" || it == "arm64") "arm64" else "x64"
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


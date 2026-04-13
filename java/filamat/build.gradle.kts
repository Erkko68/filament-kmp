plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":java:filament"))
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks.register<Exec>("cmakeConfig") {
    val buildDir = layout.buildDirectory.dir("cmake").get().asFile
    buildDir.mkdirs()
    workingDir(buildDir)
    
    val p = project.findProperty("filament.platform") as String? ?: "macos"
    val a = project.findProperty("filament.arch") as String? ?: "arm64"
    
    val platform = when(p) {
        "ios-simulator" -> "iosSimulator"
        "ios" -> "ios"
        "macos" -> "macos"
        else -> p
    }
    val arch = when(a) {
        "arm64" -> "Arm64"
        "x64" -> "X64"
        else -> a.replaceFirstChar { it.uppercase() }
    }
    
    val cmakePath = if (File("/opt/homebrew/bin/cmake").exists()) "/opt/homebrew/bin/cmake" else "cmake"
    commandLine(cmakePath, "../../", "-DFILAMENT_PLATFORM=$platform", "-DFILAMENT_ARCH=$arch")
}

tasks.register<Exec>("cmakeBuild") {
    dependsOn("cmakeConfig")
    workingDir(layout.buildDirectory.dir("cmake").get().asFile)
    val cmakePath = if (File("/opt/homebrew/bin/cmake").exists()) "/opt/homebrew/bin/cmake" else "cmake"
    commandLine(cmakePath, "--build", ".")
}

plugins {
    `java-library`
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":java:filament"))
    implementation(kotlin("stdlib"))
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

    val cmakeArgs = mutableListOf("-DFILAMENT_PLATFORM=$platform", "-DFILAMENT_ARCH=$arch")
    
    if (p == "ios-simulator") {
        cmakeArgs.add("-DCMAKE_OSX_SYSROOT=iphonesimulator")
        cmakeArgs.add("-DCMAKE_OSX_ARCHITECTURES=arm64")
    } else if (p == "ios") {
        cmakeArgs.add("-DCMAKE_OSX_SYSROOT=iphoneos")
        cmakeArgs.add("-DCMAKE_OSX_ARCHITECTURES=arm64")
    } else if (p == "macos") {
        cmakeArgs.add("-DCMAKE_OSX_SYSROOT=macosx")
        cmakeArgs.add("-DCMAKE_OSX_ARCHITECTURES=arm64")
    }

    val cmakePath = if (File("/opt/homebrew/bin/cmake").exists()) "/opt/homebrew/bin/cmake" else "cmake"
    commandLine(cmakePath, "../../", *cmakeArgs.toTypedArray())
}

tasks.register<Exec>("cmakeBuild") {
    dependsOn("cmakeConfig")
    workingDir(layout.buildDirectory.dir("cmake").get().asFile)
    val cmakePath = if (File("/opt/homebrew/bin/cmake").exists()) "/opt/homebrew/bin/cmake" else "cmake"
    commandLine(cmakePath, "--build", ".")
}

plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.register<Exec>("cmakeConfig") {
    val buildDir = layout.buildDirectory.dir("cmake").get().asFile
    buildDir.mkdirs()
    workingDir(buildDir)
    
    val platform = project.findProperty("filament.platform") as String? ?: "macos"
    val arch = project.findProperty("filament.arch") as String? ?: "arm64"
    
    commandLine("cmake", "../../", "-DFILAMENT_PLATFORM=$platform", "-DFILAMENT_ARCH=$arch")
}

tasks.register<Exec>("cmakeBuild") {
    dependsOn("cmakeConfig")
    workingDir(layout.buildDirectory.dir("cmake").get().asFile)
    commandLine("cmake", "--build", ".")
}

// Ensure java compile doesn't strictly depend on native build but we might want it for tests
// tasks.withType<Jar> { dependsOn("cmakeBuild") }

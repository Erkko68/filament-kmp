rootProject.name = "filament-umbrella"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}


// Java JVM bindings — standalone composite build.
includeBuild("java/") {
    dependencySubstitution {
        substitute(module("io.github.erkko68.filament.java:filament")).using(project(":filament"))
        substitute(module("io.github.erkko68.filament.java:filamat")).using(project(":filamat"))
        substitute(module("io.github.erkko68.filament.java:gltfio")).using(project(":gltfio"))
        substitute(module("io.github.erkko68.filament.java:filament-utils")).using(project(":filament-utils"))
    }
}

// JS Kotlin/JS external declarations — kept as a subproject (not a composite)
// because the Kotlin/JS plugin needs a single root for its `rootPackageJson` task
// across every `js()` target in the build (including samples consumers).
include(":js")

// Kotlin KMP modules (orchestrator layer).
include(":kotlin:filament")
include(":kotlin:filamat")
include(":kotlin:filament-utils")
include(":kotlin:gltfio")
include(":kotlin:filament-compose")

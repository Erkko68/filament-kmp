rootProject.name = "filament-kmp"

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


// All bindings live in a single Gradle build as flat subprojects.
// kotlin/* — KMP orchestrator modules (cinterop + JVM/Android/JS shims).
// java/*  — JVM bindings (filament-jni, filamat-jni, etc.).
// :js     — Kotlin/JS external declarations.
//
// js/ must remain a subproject (not a composite build) so the Kotlin/JS plugin
// can coordinate its single `rootPackageJson` across every `js()` target.

include(":kotlin:filament")
include(":kotlin:filamat")
include(":kotlin:filament-utils")
include(":kotlin:gltfio")
include(":kotlin:filament-compose")

include(":java:filament")
include(":java:filamat")
include(":java:gltfio")
include(":java:filament-utils")

include(":js")

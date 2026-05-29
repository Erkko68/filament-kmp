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


// All bindings live in a single Gradle build as flat subprojects.
// kotlin/* — KMP orchestrator modules (cinterop + JVM/Android/JS shims).
// :java:filament-ffm — the single Project Panama (FFM) JVM binding module.
// :js     — Kotlin/JS external declarations.
//
// js/ must remain a subproject (not a composite build) so the Kotlin/JS plugin
// can coordinate its single `rootPackageJson` across every `js()` target.

include(":kotlin:filament")
include(":kotlin:filamat")
include(":kotlin:filament-utils")
include(":kotlin:gltfio")
include(":kotlin:filament-compose")

// JVM/Panama (FFM) bindings: one combined libfilament-c image (filament + filamat +
// filament-utils + gltfio) plus jextract-generated bindings. All four kotlin JVM modules
// depend on it; it replaced the per-module hand-written JNI stack (java/filament*, java/gltfio).
include(":java:filament-ffm")

include(":js")

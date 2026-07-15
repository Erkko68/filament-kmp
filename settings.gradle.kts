rootProject.name = "filament-umbrella"

pluginManagement {
    // Convention plugins live in the build-logic included build (not buildSrc), so
    // editing them doesn't invalidate the whole main build's task graph.
    includeBuild("build-logic")
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
// :java   — the single Project Panama (FFM) JVM binding module.
// :web    — Kotlin/JS + Wasm external declarations.
//
// js/ must remain a subproject (not a composite build) so the Kotlin/JS plugin
// can coordinate its single `rootPackageJson` across every `js()` target.

include(":kotlin:filament")
include(":kotlin:filamat")
include(":kotlin:filament-utils")
include(":kotlin:gltfio")
include(":kotlin:filament-compose")
include(":kotlin:test-support") // test-only shared helpers (TestEnv + skip annotations)

// JVM/Panama (FFM) bindings: one combined libfilament-c image (filament + filamat +
// filament-utils + gltfio) plus jextract-generated bindings. All four kotlin JVM modules
// depend on it; it replaced the per-module hand-written JNI stack (java/filament*, java/gltfio).
// Published as the `filament-ffm` artifact (bindings only — natives live in the runtime
// modules below; artifact ids pinned via maven.artifactId in each module's gradle.properties).
include(":java")

// FFM native runtime jars (skiko-awt-runtime style): one slim natives jar per platform.
// filament-ffm's own metadata depends on all of them by default; its per-platform
// Gradle-metadata variants (os/arch attributes) narrow that to exactly one.
include(":java:runtime-macos-arm64")
include(":java:runtime-linux-x64")
include(":java:runtime-linux-arm64")
include(":java:runtime-windows-x64")

include(":web")

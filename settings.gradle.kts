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

// One build, flat subprojects: the published kotlin/* modules and the native runtimes they bind.
// :web must stay a subproject (not a composite build) so the Kotlin/JS plugin can coordinate its
// single `rootPackageJson` across every `js()` target.

include(":kotlin:filament")
include(":kotlin:filamat")
include(":kotlin:filament-utils")
include(":kotlin:gltfio")
include(":kotlin:filament-compose")
include(":kotlin:test-support") // test-only shared helpers (TestEnv + skip annotations)

// Native runtimes of the bindings: the C API (c/) built per platform.
include(":jni")     // JNI runtime (FilaJni) shared by desktop and Android
include(":desktop") // desktop loader + libfilament-c; one runtime jar per platform below
include(":desktop:runtime-macos-arm64")
include(":desktop:runtime-linux-x64")
include(":desktop:runtime-linux-arm64")
include(":desktop:runtime-windows-x64")
include(":desktop:runtime-windows-arm64")
include(":android") // libfilament-c.so per ABI
include(":web")     // filament-kmp/filamat-kmp wasm runtimes + their Kotlin loader

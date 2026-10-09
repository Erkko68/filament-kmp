import buildlogic.cmake.registerCApiBuild
import buildlogic.platform.FilamentTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("filament-publish")
}

// Web runtime: filament-kmp.{js,wasm} and filamat-kmp.{js,wasm} (c/ + CMakeLists.txt here, Emscripten), which
// export the Fila* C API, and the Kotlin side (webMain) that loads them and moves data across the boundary.
// The wasm files aren't packed in the klib (webpack never sees klib resources): apps take them from the
// GitHub release.

val cmakeBuild = registerCApiBuild("cmakeBuild", FilamentTarget.WASM) {
    targets.addAll("filament-web", "filamat-web")
    dependsOn(":setupEmsdk")
}

// FILA_WASM_PREBUILT=<dir> uses an already built runtime (CI's js/wasm jobs take the web-runtime job's)
// instead of running Emscripten.
val runtime: FileCollection = providers.environmentVariable("FILA_WASM_PREBUILT").orNull
    ?.let { files(it) }
    ?: files(cmakeBuild.flatMap { it.outputDir })

val stageFilamentWasm = tasks.register<Sync>("stageFilamentWasm") {
    from(runtime) { include("filament-kmp.js", "filament-kmp.wasm") }
    into(layout.buildDirectory.dir("filamentWasm"))
}

// filamat-kmp.{js,wasm}: the optional runtime material compiler, shipped by :kotlin:filamat.
tasks.register<Sync>("stageFilamatWasm") {
    from(runtime) { include("filamat-kmp.js", "filamat-kmp.wasm") }
    into(layout.buildDirectory.dir("filamatWasm"))
}

kotlin {
    js { browser() }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    applyDefaultHierarchyTemplate()

    sourceSets {
        webMain.dependencies {
            api("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
        }
        webTest {
            // The export parity test's arities, generated from c/api-manifest.json.
            kotlin.srcDir(files(rootProject.layout.buildDirectory.dir("generated/bindings/webTest")).builtBy(":generateBindings"))
            resources.srcDir(stageFilamentWasm)
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

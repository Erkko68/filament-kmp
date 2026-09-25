import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

// Web bindings module, the web counterpart of :java (FFM). Builds c/ with Emscripten
// (c/CMakeLists.txt, FILAMENT_PLATFORM=wasm) into filament-kmp.{js,wasm}, which exports the
// Fila* C API, and exposes it to js + wasmJs through numeric externals.
// See docs/design/web-c-api-bindings.md.

val wasmBuildDir = rootProject.file("c/build/wasm")

val buildFilamentWasm = tasks.register<Exec>("buildFilamentWasm") {
    workingDir(rootDir)
    // ponytail: no declared inputs, so this always runs; cmake --build is a fast no-op when current.
    commandLine(
        "sh", "-c",
        """
        set -e
        [ -f prebuilts/wasm/lib/.prebuilt-source ] || { echo "Missing prebuilts/wasm/lib — run scripts/dev/build-wasm-libs.sh" >&2; exit 1; }
        scripts/dev/setup-emsdk.sh >/dev/null
        . .emsdk/emsdk_env.sh >/dev/null 2>&1
        emcmake cmake -S c -B ${wasmBuildDir.relativeTo(rootDir)} -DFILAMENT_PLATFORM=wasm -DCMAKE_BUILD_TYPE=Release >/dev/null
        cmake --build ${wasmBuildDir.relativeTo(rootDir)} --target filament-web
        """.trimIndent(),
    )
}

val stageFilamentWasm = tasks.register<Sync>("stageFilamentWasm") {
    dependsOn(buildFilamentWasm)
    from(wasmBuildDir) { include("filament-kmp.js", "filament-kmp.wasm") }
    into(layout.buildDirectory.dir("filamentWasm"))
}

kotlin {
    js { browser() }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    applyDefaultHierarchyTemplate()

    sourceSets {
        webMain {
            resources.srcDir(stageFilamentWasm)
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
            }
        }
        webTest {
            resources.srcDir(stageFilamentWasm)
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

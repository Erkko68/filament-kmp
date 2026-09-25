import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("filament-publish")
}

group = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
version = project.findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"

// Web bindings module, the web counterpart of :java (FFM). Builds c/ with Emscripten
// (c/CMakeLists.txt, FILAMENT_PLATFORM=wasm) into filament-kmp.{js,wasm}, which exports the
// Fila* C API, and exposes it to js + wasmJs through numeric externals.
// See docs/design/web-c-api-bindings.md.

val wasmBuildDir = rootProject.file("c/build/wasm")

val setupEmsdk = tasks.register<Exec>("setupEmsdk") {
    workingDir(rootDir)
    commandLine("scripts/dev/setup-emsdk.sh")
}

// One interface per C module; all three live in filament-kmp.wasm. filamat gets its own wasm.
val generateWasmExternals = tasks.register<GenerateWasmExternals>("generateWasmExternals") {
    dependsOn(setupEmsdk)
    val modules = mapOf("FilamentC" to "filament", "FilamentUtilsC" to "filament-utils", "GltfioC" to "gltfio")
    this.modules.set(modules)
    headers.from(modules.values.map { rootProject.fileTree("c/$it/c") { include("*.h") } })
    packageName.set("io.github.erkko68.filament.wasm")
    baseInterface.set("FilamentModule")
    instance.set("fila")
    cDir.set(rootProject.layout.projectDirectory.dir("c"))
    emsdkDir.set(rootProject.layout.projectDirectory.dir(".emsdk"))
    // Committed output (like the old hand-written externals): compiling needs no emsdk. The web CI
    // job regenerates and fails on a diff, so a header change can't go unnoticed.
    mainDir.set(layout.projectDirectory.dir("src/webMain/generated"))
    testDir.set(layout.projectDirectory.dir("src/webTest/generated"))
}

val buildFilamentWasm = tasks.register<Exec>("buildFilamentWasm") {
    dependsOn(setupEmsdk)
    workingDir(rootDir)
    // ponytail: no declared inputs, so this always runs; cmake --build is a fast no-op when current.
    commandLine(
        // bash, not sh: emsdk_env.sh can't locate itself under dash (Ubuntu's /bin/sh).
        "bash", "-c",
        """
        set -e
        [ -f prebuilts/wasm/lib/.prebuilt-source ] || { echo "Missing prebuilts/wasm/lib — run scripts/dev/build-wasm-libs.sh" >&2; exit 1; }
        . .emsdk/emsdk_env.sh >/dev/null 2>&1
        emcmake cmake -S c -B ${wasmBuildDir.relativeTo(rootDir)} -DFILAMENT_PLATFORM=wasm -DCMAKE_BUILD_TYPE=Release >/dev/null
        cmake --build ${wasmBuildDir.relativeTo(rootDir)} --target filament-web filamat-web
        """.trimIndent(),
    )
}

val stageFilamentWasm = tasks.register<Sync>("stageFilamentWasm") {
    dependsOn(buildFilamentWasm)
    from(wasmBuildDir) { include("filament-kmp.js", "filament-kmp.wasm") }
    into(layout.buildDirectory.dir("filamentWasm"))
}

// filamat-kmp.{js,wasm}: the optional runtime material compiler, shipped by :kotlin:filamat.
val stageFilamatWasm = tasks.register<Sync>("stageFilamatWasm") {
    dependsOn(buildFilamentWasm)
    from(wasmBuildDir) { include("filamat-kmp.js", "filamat-kmp.wasm") }
    into(layout.buildDirectory.dir("filamatWasm"))
}

kotlin {
    js { browser() }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    applyDefaultHierarchyTemplate()

    sourceSets {
        webMain {
            // filament-kmp.{js,wasm} aren't packed: webpack never sees klib resources, so apps
            // take them from the GitHub release instead.
            kotlin.srcDir("src/webMain/generated")
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
            }
        }
        webTest {
            kotlin.srcDir("src/webTest/generated")
            resources.srcDir(stageFilamentWasm)
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

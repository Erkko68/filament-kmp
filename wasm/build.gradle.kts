import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

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
    cDir.set(rootProject.layout.projectDirectory.dir("c"))
    emsdkDir.set(rootProject.layout.projectDirectory.dir(".emsdk"))
    mainDir.set(layout.buildDirectory.dir("generated/wasmExternals/webMain"))
    testDir.set(layout.buildDirectory.dir("generated/wasmExternals/webTest"))
}

val buildFilamentWasm = tasks.register<Exec>("buildFilamentWasm") {
    dependsOn(setupEmsdk)
    workingDir(rootDir)
    // ponytail: no declared inputs, so this always runs; cmake --build is a fast no-op when current.
    commandLine(
        "sh", "-c",
        """
        set -e
        [ -f prebuilts/wasm/lib/.prebuilt-source ] || { echo "Missing prebuilts/wasm/lib — run scripts/dev/build-wasm-libs.sh" >&2; exit 1; }
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
            kotlin.srcDir(generateWasmExternals.flatMap { it.mainDir })
            resources.srcDir(stageFilamentWasm)
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
            }
        }
        webTest {
            kotlin.srcDir(generateWasmExternals.flatMap { it.testDir })
            resources.srcDir(stageFilamentWasm)
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

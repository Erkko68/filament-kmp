plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js {
        browser {
            binaries.executable()
            commonWebpackConfig {
                outputFileName = "webApp.js"
            }
        }
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            binaries.executable()
            commonWebpackConfig {
                outputFileName = "webApp.js"
            }
        }
    }

    // js + wasmJs share the trivial entry point (Main.kt) and the :shared dependency
    // via webMain; each target keeps its own resources (index.html differs: a plain
    // <script> for js vs a <script type="module"> for the wasm bundle).
    applyDefaultHierarchyTemplate()

    sourceSets {
        val webMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
            // filament-kmp/filamat-kmp.{js,wasm}: klib resources don't reach webpack, so serve the umbrella's build output.
            resources.srcDir(rootDir.resolve("../web/build/filamentWasm"))
            resources.srcDir(rootDir.resolve("../web/build/filamatWasm"))
        }
    }
}

tasks.matching { it.name.endsWith("ProcessResources") }.configureEach {
    dependsOn(gradle.includedBuild("filament-umbrella").task(":web:stageFilamentWasm"))
    dependsOn(gradle.includedBuild("filament-umbrella").task(":web:stageFilamatWasm"))
}

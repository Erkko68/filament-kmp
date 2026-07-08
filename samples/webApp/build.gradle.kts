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
        }
    }
}

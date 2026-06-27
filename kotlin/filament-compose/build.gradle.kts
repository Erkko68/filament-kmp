plugins {
    id("filament-kmp-module")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

filamentModule {
    xcframeworkName.set("FilamentCompose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kotlin:filament"))
            api(project(":kotlin:filament-utils"))
            api(project(":kotlin:gltfio"))
            api(project(":kotlin:filamat"))
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.ui)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":kotlin:test-support"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        // Android instrumented tests: runComposeUiTest needs a host Activity, supplied by the
        // ui-test-manifest artifact (it merges a debug AndroidManifest with a test ComponentActivity).
        // Lives only here — filament-compose is the only module that drives runComposeUiTest.
        named("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.compose.ui.test.manifest)
                // Override the stale Espresso (3.5.0) that compose ui-test drags in; the old one calls
                // an InputManager method removed in Android 14+, breaking waitForIdle() on-device.
                implementation(libs.androidx.test.espresso.core)
            }
        }
    }
}

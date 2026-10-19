// Test-only multiplatform helpers (TestEnv host/target detection, per-target skip
// annotations, frame readback) shared across every module's tests. Deliberately NOT using the
// filament-kmp-module convention plugin: this module has no cinterop / prebuilts /
// publishing — just plain Kotlin across the same targets the consumers use.
plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    android {
        namespace = "io.github.erkko68.filament.testsupport"
        compileSdk = 37
        minSdk = 24
    }
    iosArm64()
    iosSimulatorArm64()
    jvm()
    js { browser() }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs { browser() }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("test"))
            // Readback helpers and RealBackendFixture drive a real Engine.
            api(project(":kotlin:filament"))
        }
        jvmMain.dependencies {
            implementation(kotlin("test-junit"))
        }
        androidMain.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}

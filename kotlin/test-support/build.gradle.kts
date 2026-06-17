// Test-only multiplatform helpers (TestEnv host/target detection + per-target skip
// annotations) shared across every module's commonTest. Deliberately NOT using the
// filament-kmp-module convention plugin: this module has no cinterop / prebuilts /
// publishing — just plain Kotlin across the same targets the consumers use.
plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    jvm()
    js { browser() }

    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "io.github.erkko68.filament.testsupport"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}

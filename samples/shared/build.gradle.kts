import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "eric.bitria.samples.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }
    }

    jvm()

    js {
        browser {
            binaries.executable()
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { appleTarget ->
        appleTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            api(libs.compose.components.resources)
            api(libs.filament)
            api(libs.filament.compose)
            api(libs.utils)
            api(libs.gltfio)
            implementation(libs.navigation.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.uiToolingPreview)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
    }
}

compose.resources {
    packageOfResClass = "eric.bitria.samples.shared.resources"
    publicResClass = true
}

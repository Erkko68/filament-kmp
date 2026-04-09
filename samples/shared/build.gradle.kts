import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true

            val targetName = iosTarget.name
            val filamentPrebuiltDir = "${projectDir}/../../prebuilts/ios/lib/universal"

            linkerOpts("-L$filamentPrebuiltDir")

            // Link all required Filament static libraries
            linkerOpts(
                "-lfilament", "-lbackend", "-lutils", "-lmath",
                "-lgeometry_combined", "-libl-lite", "-libl",
                "-lfilamat_combined", "-lshaders", "-lfilament-iblprefilter",
                "-lcamutils", "-limage", "-limageio-lite", "-lfilabridge",
                "-lfilaflat", "-lzstd", "-lsmol-v", "-lktxreader",
                "-lpng", "-ltinyexr", "-lz", "-labseil", "-lperfetto"
            )

            // Link the C-wrapper built by the filament-kmp library
            linkerOpts("-L${projectDir}/../../c/build/$targetName", "-lfilament-c")

            // Essential frameworks for Filament on iOS
            linkerOpts(
                "-framework", "Metal",
                "-framework", "UIKit",
                "-framework", "CoreVideo",
                "-framework", "QuartzCore",
                "-framework", "CoreGraphics",
                "-framework", "Foundation"
            )
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("dev.filament.kmp:filament")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "eric.bitria.samples.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

compose.resources {
    packageOfResClass = "eric.bitria.samples.shared.resources"
}

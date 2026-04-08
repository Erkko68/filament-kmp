import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "dev.filament.kmp"
version = "0.1.0-SNAPSHOT"

val filamentAndroidVersion = "1.70.1"

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget()

    iosArm64()
    iosSimulatorArm64()

    macosArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting
        val nativeMain by getting
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.filament:filamat-android:$filamentAndroidVersion")
                implementation(project(":filament"))
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filamat") {
                defFile(project.file("src/nativeInterop/cinterop/filamat.def"))
                includeDirs(
                    project.file("../../C/filamat/c"),
                    project.file("../../filament-main/filament/include"),
                    project.file("../../filament-main/libs/utils/include")
                )
            }
        }

        binaries.all {
            val platform = target.konanTarget.family
            val libDir = when (platform) {
                Family.IOS -> "ios"
                Family.OSX -> "macos"
                Family.LINUX -> "linux"
                Family.MINGW -> "windows"
                else -> error("Unsupported platform: $platform")
            }

            linkerOpts(
                "-L${projectDir}/../../filament-prebuilts/lib/$libDir",
                "-lfilamat"
            )
        }
    }
}

android {
    namespace = "dev.filament.kmp.filamat"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}

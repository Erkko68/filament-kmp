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
        val commonMain by getting {
            dependencies {
                implementation(project(":filament"))
            }
        }
        val nativeMain by getting
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.filament:filament-utils-android:$filamentAndroidVersion")
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filament_utils") {
                defFile(project.file("src/nativeInterop/cinterop/filament-utils.def"))
                includeDirs(
                    project.file("../../C/filament-utils/c"),
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
                "-L${projectDir}/../../prebuilts/lib/$libDir",
                "-lfilament-utils"
            )
        }
    }
}

android {
    namespace = "dev.filament.kmp.utils"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}

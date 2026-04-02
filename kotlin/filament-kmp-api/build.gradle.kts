import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "dev.filament.kmp"
version = "0.1.0-SNAPSHOT"

kotlin {
    androidTarget()

    iosArm64()
    iosSimulatorArm64()

    macosArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    js(IR) {
        browser()
        binaries.library()
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {}

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        nativeMain.dependencies {}

        androidMain.dependencies {
            implementation("com.google.android.filament:filament-android:1.70.1")
        }

        jsMain.dependencies {

        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {

        compilations.getByName("main").cinterops {
            create("filament") {
                defFile(project.file("src/nativeInterop/cinterop/filament.def"))

                includeDirs(
                    project.file("../../c-wrapper/include"),
                    project.file("../../filament-prebuilts/include")
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
                "-lfilament"
            )

            when (platform) {
                Family.IOS -> {
                    linkerOpts(
                        "-framework", "Metal",
                        "-framework", "UIKit",
                        "-framework", "CoreVideo",
                        "-framework", "QuartzCore"
                    )
                }

                Family.OSX -> {
                    linkerOpts(
                        "-framework", "Metal",
                        "-framework", "Cocoa",
                        "-framework", "QuartzCore"
                    )
                }

                else -> Unit
            }
        }
    }
}

android {
    namespace = "dev.filament.kmp"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}
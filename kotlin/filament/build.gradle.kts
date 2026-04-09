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

//    js(IR) {
//        browser()
//        binaries.library()
//    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nativeMain by getting

        val androidMain by getting {
            dependencies {
                implementation("com.google.android.filament:filament-android:$filamentAndroidVersion")
            }
        }

        //val jsMain by getting
    }

    targets.withType<KotlinNativeTarget>().configureEach {

        compilations.getByName("main").cinterops {
            create("filament") {
                defFile(project.file("src/nativeInterop/cinterop/filament.def"))

                includeDirs(
                    project.file("../../c/filament/c"),
                    project.file("../../c/filamat/c"),
                    project.file("../../c/filament-utils/c"),
                    project.file("../../prebuilts/include")
                )
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.all {
            val platform = target.konanTarget.family

            // Path to prebuilt Filament libraries
            val filamentPrebuiltDir = when (platform) {
                Family.IOS -> "${projectDir}/../../prebuilts/ios/lib/universal"
                Family.OSX -> "${projectDir}/../../prebuilts/mac/lib/arm64"
                else -> ""
            }

            if (filamentPrebuiltDir.isNotEmpty()) {
                linkerOpts("-L$filamentPrebuiltDir")

                // Link all required Filament static libraries
                // Note: The order of linking can be important for static libraries
                linkerOpts(
                    "-lfilament", "-lbackend", "-lutils", "-lmath",
                    "-lgeometry_combined", "-libl-lite", "-libl",
                    "-lfilamat_combined", "-lshaders", "-lfilament-iblprefilter",
                    "-lcamutils", "-limage", "-limageio-lite", "-lfilabridge",
                    "-lfilaflat", "-lzstd", "-lsmol-v", "-lktxreader",
                    "-lpng", "-ltinyexr", "-lz", "-labseil", "-lperfetto"
                )

                // Link the C-wrapper (built from /c folder)
                // We use target-specific build directories
                linkerOpts("-L${projectDir}/../../c/build/${target.name}", "-lfilament-c")
            }

            when (platform) {
                Family.IOS -> {
                    linkerOpts(
                        "-framework", "Metal",
                        "-framework", "UIKit",
                        "-framework", "CoreVideo",
                        "-framework", "QuartzCore",
                        "-framework", "CoreGraphics",
                        "-framework", "Foundation"
                    )
                }

                Family.OSX -> {
                    linkerOpts(
                        "-framework", "Metal",
                        "-framework", "Cocoa",
                        "-framework", "QuartzCore",
                        "-framework", "CoreVideo",
                        "-framework", "AppKit",
                        "-framework", "CoreGraphics",
                        "-framework", "Foundation"
                    )
                }

                else -> Unit
            }
        }
    }

    // --- Automated Native Build Configuration ---
    targets.withType<KotlinNativeTarget>().configureEach {
        val targetName = name
        val konanTarget = konanTarget

        // Map KonanTarget to simplified Filament platform flag
        val filaPlatform = when (konanTarget) {
            org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64 -> "ios"
            org.jetbrains.kotlin.konan.target.KonanTarget.IOS_SIMULATOR_ARM64 -> "ios-simulator"
            org.jetbrains.kotlin.konan.target.KonanTarget.MACOS_ARM64 -> "macos"
            else -> ""
        }

        if (filaPlatform.isNotEmpty()) {
            val cmakeTaskName = "buildFilamentC_$targetName"
            val buildDir = project.file("../../c/build/$targetName")

            val buildFilamentC = tasks.register<Exec>(cmakeTaskName) {
                buildDir.mkdirs()
                workingDir(buildDir)

                val cmakePath = "/opt/homebrew/bin/cmake"
                commandLine("sh", "-c", "$cmakePath ../../ -DFILAMENT_PLATFORM=$filaPlatform -DCMAKE_BUILD_TYPE=Release && $cmakePath --build . --target filament-c")

                // Log what we are doing
                doFirst {
                    println("Building Filament C Wrapper for $targetName ($filaPlatform) in $buildDir")
                }
            }

            // Ensure the native build runs before compilation
            compilations.getByName("main").compileKotlinTaskProvider.configure {
                dependsOn(buildFilamentC)
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
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
                implementation("com.google.android.filament:filamat-android:$filamentAndroidVersion")
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filamat") {
                defFile(project.file("src/nativeInterop/cinterop/filamat.def"))
                includeDirs(
                    project.file("../../c/filamat/c"),
                    project.file("../../c/filament/c"),
                    project.file("../../prebuilts/include")
                )
            }
        }
    }

    // --- Automated Native Build & C-Interop Embedding Configuration ---
    targets.withType<KotlinNativeTarget>().configureEach {
        val targetName = name
        val konanTarget = konanTarget

        if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.IOS) {
            binaries.all {
                freeCompilerArgs += listOf("-Xoverride-konan-properties=apple.sdk.min.version=15.0")
            }
        }

        // 1. Setup paths
        val (filaPlatform, filaArch) = when (konanTarget) {
            org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64 -> "ios" to "arm64"
            org.jetbrains.kotlin.konan.target.KonanTarget.IOS_SIMULATOR_ARM64 -> "ios-simulator" to "arm64"
            org.jetbrains.kotlin.konan.target.KonanTarget.IOS_X64 -> "ios-simulator" to "x64"
            org.jetbrains.kotlin.konan.target.KonanTarget.MACOS_ARM64 -> "macos" to "arm64"
            org.jetbrains.kotlin.konan.target.KonanTarget.MACOS_X64 -> "macos" to "x64"
            org.jetbrains.kotlin.konan.target.KonanTarget.LINUX_X64 -> "linux" to "x64"
            org.jetbrains.kotlin.konan.target.KonanTarget.LINUX_ARM64 -> "linux" to "arm64"
            org.jetbrains.kotlin.konan.target.KonanTarget.MINGW_X64 -> "windows" to "x64"
            else -> "" to ""
        }
        val (libPrefix, libSuffix) = when (konanTarget.family) {
            org.jetbrains.kotlin.konan.target.Family.MINGW -> "" to ".lib"
            else -> "lib" to ".a"
        }
        val buildDir = project.file("../../c/build/$targetName")
        val filamentPrebuiltDir = "${projectDir}/../../prebuilts/$targetName/lib"

        // 2. Automate the C Wrapper compilation
        if (filaPlatform.isNotEmpty()) {
            val cmakeTaskName = "buildFilamatC_$targetName"
            val buildFilamatC = tasks.register<Exec>(cmakeTaskName) {
                buildDir.mkdirs()
                workingDir(buildDir)
                val cmakePath = "/opt/homebrew/bin/cmake"
                commandLine("sh", "-c", "$cmakePath ../../ -DFILAMENT_PLATFORM=$filaPlatform -DFILAMENT_ARCH=$filaArch -DCMAKE_BUILD_TYPE=Release && $cmakePath --build . --target filamat-c")
                doFirst { println("Building Filamat C Wrapper for $targetName ($filaPlatform/$filaArch) in $buildDir") }
            }

            compilations.getByName("main").compileKotlinTaskProvider.configure {
                dependsOn(buildFilamatC)
            }

            project.tasks.matching { it.name == "cinteropFilamat${targetName.replaceFirstChar { c -> c.uppercase() }}" }.configureEach {
                dependsOn(buildFilamatC)
            }
        }

        // 3. Configure cinterop to PACK the static libraries into the .klib
        compilations.getByName("main").cinterops {
            getByName("filamat") {
                if (filamentPrebuiltDir.isNotEmpty()) {
                    extraOpts("-libraryPath", filamentPrebuiltDir)
                    extraOpts("-libraryPath", buildDir.absolutePath)

                    val prebuiltLibs = if (file(filamentPrebuiltDir).exists()) {
                        fileTree(filamentPrebuiltDir) {
                            include("*.a", "*.lib")
                        }.map { it.name }
                    } else {
                        emptyList<String>()
                    }

                    val staticLibs = prebuiltLibs + "${libPrefix}filamat-c${libSuffix}"

                    staticLibs.forEach { lib ->
                        extraOpts("-staticLibrary", lib)
                    }
                }
            }
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

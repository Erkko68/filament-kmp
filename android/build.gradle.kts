import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
}

// Android JNI bindings module, the Android counterpart of :java (FFM) and :web (wasm). AGP builds
// c/CMakeLists.txt (FILAMENT_PLATFORM=android) per ABI into one libfilament-c.so: the Fila* C API
// plus generated JNI forwarders, over the upstream android-native prebuilts. Kotlin sees the C API
// through generated top-level functions per C module (FilamentC.kt & co., all primitives, like the wasm externals).

val abis = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")

android {
    namespace = "io.github.erkko68.filament.jni"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    // Upstream's pinned NDK/CMake (build/common/versions): the prebuilts' libc++ must match.
    ndkVersion = "29.0.14206865"

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk { abiFilters += abis }
        externalNativeBuild {
            cmake {
                arguments += listOf("-DFILAMENT_PLATFORM=android", "-DANDROID_STL=c++_static")
                targets += "filament-c-android"
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = rootProject.file("c/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    // The .so always builds Release: a Debug libfilament-c against Release prebuilts buys nothing.
    buildTypes {
        getByName("debug") {
            externalNativeBuild { cmake { arguments += "-DCMAKE_BUILD_TYPE=Release" } }
        }
    }
}

androidComponents.onVariants { it.sources.kotlin?.addStaticSourceDirectory("src/main/generated") }

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.android.jvmTarget.get()))
    }
}

// Prebuilts + headers must exist before CMake configures.
tasks.matching { it.name.startsWith("configureCMake") || it.name.startsWith("buildCMake") }.configureEach {
    dependsOn(abis.map { ":downloadPrebuilts_android-$it" }, ":downloadIncludes")
}

// Committed output (like :web's externals): regenerate after a C header change.
tasks.register<GenerateJniBindings>("generateJniBindings") {
    val cModules = mapOf("FilamentC" to "filament", "FilamatC" to "filamat", "FilamentUtilsC" to "filament-utils", "GltfioC" to "gltfio")
    modules.set(cModules)
    headers.from(cModules.values.map { rootProject.fileTree("c/$it/c") { include("*.h") } })
    packageName.set("io.github.erkko68.filament.jni")
    cSourceDir.set(rootProject.layout.projectDirectory.dir("c"))
    cDir.set(layout.projectDirectory.dir("src/main/cpp/generated"))
    kotlinDir.set(layout.projectDirectory.dir("src/main/generated"))
}

dependencies {
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.kotlin.testJunit)
}

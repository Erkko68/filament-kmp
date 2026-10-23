import buildlogic.cmake.CMakeBuildTask
import buildlogic.cmake.registerCApiBuild
import buildlogic.platform.FilamentTarget

plugins {
    id("com.android.library")
    id("filament-publish")
}

// Android runtime for :jni: libfilament-c.so per ABI (c/ + jni/, plus FilaAndroid's entry points), built with
// the SDK's NDK and CMake pinned to upstream's (build/common/versions) so the prebuilts' libc++ matches.
// Plain CMake tasks rather than AGP's externalNativeBuild: configuring the build never needs an SDK.

val ndkVersion = providers.gradleProperty("ndkVersion").get()
val cmakeVersion = providers.gradleProperty("androidCmakeVersion").get()
val minSdkVersion = libs.versions.android.minSdk.get().toInt()

android {
    namespace = "io.github.erkko68.filament.jni"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = minSdkVersion }
    val jvmTarget = JavaVersion.toVersion(libs.versions.android.jvmTarget.get())
    compileOptions {
        sourceCompatibility = jvmTarget
        targetCompatibility = jvmTarget
    }
}

dependencies {
    api(project(":jni"))
}

val sdk = androidComponents.sdkComponents.sdkDirectory
FilamentTarget.android.forEach { target ->
    val cmakeBuild = registerCApiBuild("cmakeBuild_${target.abi}", target) {
        val cmakeBin = sdk.map { it.dir("cmake/$cmakeVersion/bin") }
        val exe = if (System.getProperty("os.name").startsWith("Windows")) ".exe" else ""
        cmake.set(cmakeBin.map { it.file("cmake$exe").asFile.path })
        arguments.addAll("-G", "Ninja", "-DANDROID_ABI=${target.abi}", "-DANDROID_PLATFORM=android-$minSdkVersion", "-DANDROID_STL=c++_static")
        arguments.add(cmakeBin.map { "-DCMAKE_MAKE_PROGRAM=${it.file("ninja$exe").asFile.invariantSeparatorsPath}" })
        arguments.add(sdk.map { "-DCMAKE_TOOLCHAIN_FILE=${it.dir("ndk/$ndkVersion").file("build/cmake/android.toolchain.cmake").asFile.invariantSeparatorsPath}" })
        // Always Release: a Debug libfilament-c against Release prebuilts buys nothing.
        buildType.set("Release")
        targets.add("filament-c-jni")
        outputSubdir.set(target.abi)
        val ndkDir = sdk.map { it.dir("ndk/$ndkVersion").asFile }
        val cmakeDir = cmakeBin.map { it.asFile }
        val missing = "Missing NDK/CMake: sdkmanager \"ndk;$ndkVersion\" \"cmake;$cmakeVersion\""
        doFirst { check(ndkDir.get().isDirectory && cmakeDir.get().isDirectory) { missing } }
    }
    androidComponents.onVariants { it.sources.jniLibs?.addGeneratedSourceDirectory(cmakeBuild, CMakeBuildTask::outputDir) }
}

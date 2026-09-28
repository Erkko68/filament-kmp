package buildlogic.cmake

import buildlogic.platform.FilamentTarget
import buildlogic.platform.FilamentTarget.*
import org.gradle.api.file.Directory

/** CMake toolchain arguments per target. Android's come from :android, which knows the SDK's NDK. */
internal fun FilamentTarget.toolchainArguments(root: Directory): List<String> = when (this) {
    IOS_ARM64 -> ios("iphoneos")
    IOS_SIMULATOR_ARM64 -> ios("iphonesimulator")
    MACOS_ARM64 -> listOf("-DCMAKE_OSX_SYSROOT=macosx", "-DCMAKE_OSX_ARCHITECTURES=arm64")
    // The prebuilts are clang/libc++ builds; see c/cmake/Platform.cmake.
    LINUX_X64, LINUX_ARM64 -> listOf("-DCMAKE_C_COMPILER=clang", "-DCMAKE_CXX_COMPILER=clang++")
    WASM -> listOf("-DCMAKE_TOOLCHAIN_FILE=${emsdkDir(root)}/upstream/emscripten/cmake/Modules/Platform/Emscripten.cmake")
    WINDOWS_X64, WINDOWS_ARM64, ANDROID_ARM64_V8A, ANDROID_ARMEABI_V7A, ANDROID_X86_64, ANDROID_X86 -> emptyList()
}

private fun ios(sdk: String) = listOf(
    "-DCMAKE_SYSTEM_NAME=iOS",
    "-DCMAKE_OSX_SYSROOT=$sdk",
    "-DCMAKE_OSX_ARCHITECTURES=arm64",
    "-DCMAKE_OSX_DEPLOYMENT_TARGET=15.0",
)

internal fun emsdkDir(root: Directory): String = root.dir(".emsdk").asFile.invariantSeparatorsPath

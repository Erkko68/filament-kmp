package buildlogic.platform

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider

/**
 * Every native target the C API is built for, and where its Filament static libraries come from:
 * an upstream release tarball ([release] = asset suffix to path inside it, `xcf:<slice>` for an
 * xcframework slice), or, where upstream ships none, a source build (BuildFromSourceTask).
 * Libraries land in `prebuilts/<id>/lib` either way, fetched by the root `prebuilts_<id>` task.
 */
enum class FilamentTarget(val id: String, val release: Pair<String, String>?) {
    IOS_ARM64("ios-arm64", "ios" to "xcf:ios-arm64"),
    IOS_SIMULATOR_ARM64("ios-simulator-arm64", "ios" to "xcf:ios-arm64_x86_64-simulator"),
    MACOS_ARM64("macos-arm64", "mac" to "filament/lib/arm64"),
    LINUX_X64("linux-x64", "linux" to "filament/lib/x86_64"),
    LINUX_ARM64("linux-arm64", "arm-linux" to "filament/lib/aarch64"),
    // /MT (static CRT): the JVM's own msvcp140.dll conflicts with /MD.
    WINDOWS_X64("windows-x64", "windows" to "lib/x86_64/mt"),
    WINDOWS_ARM64("windows-arm64", null),
    ANDROID_ARM64_V8A("android-arm64-v8a", "android-native" to "filament/lib/arm64-v8a"),
    ANDROID_ARMEABI_V7A("android-armeabi-v7a", "android-native" to "filament/lib/armeabi-v7a"),
    ANDROID_X86_64("android-x86_64", "android-native" to "filament/lib/x86_64"),
    ANDROID_X86("android-x86", "android-native" to "filament/lib/x86"),
    WASM("wasm", null);

    val fromSource: Boolean get() = release == null

    /** The NDK ABI of an Android target. */
    val abi: String get() = id.removePrefix("android-")

    /** c/CMakeLists.txt's FILAMENT_PLATFORM: ios | ios-simulator | macos | linux | windows | android | wasm. */
    val cmakePlatform: String get() = when {
        this == IOS_SIMULATOR_ARM64 -> "ios-simulator"
        else -> id.substringBefore('-')
    }

    /** Gradle's OperatingSystemFamily of a desktop target. */
    val osFamily: String get() = id.substringBefore('-')

    /** Gradle's MachineArchitecture of a desktop target. */
    val machineArchitecture: String get() = if (id.endsWith("arm64")) "arm64" else "x86-64"

    companion object {
        val desktop = listOf(MACOS_ARM64, LINUX_X64, LINUX_ARM64, WINDOWS_X64, WINDOWS_ARM64)
        val android = listOf(ANDROID_ARM64_V8A, ANDROID_ARMEABI_V7A, ANDROID_X86_64, ANDROID_X86)
        val ios = listOf(IOS_ARM64, IOS_SIMULATOR_ARM64)

        fun byId(id: String): FilamentTarget = entries.first { it.id == id }

        /** The desktop target of the build host. */
        fun host(): FilamentTarget = desktop.firstOrNull { it.id == "${hostPlatform()}-${hostArch()}" }
            ?: error("No Filament desktop target for ${hostPlatform()}-${hostArch()} (supported: ${desktop.map { it.id }})")
    }
}

/** Name of the root task that fills `prebuilts/<id>/lib`. */
val FilamentTarget.prebuiltsTask: String get() = ":prebuilts_$id"

/**
 * Directory holding [target]'s static libraries: `prebuilts/<id>/lib`, or `$FILAMENT_PREBUILTS_DIR/<id>/lib`
 * to link a locally built Filament instead.
 */
fun Project.filamentLibDir(target: FilamentTarget): Provider<Directory> {
    val root = rootProject.layout.projectDirectory
    return providers.environmentVariable("FILAMENT_PREBUILTS_DIR")
        .map { root.dir(it).dir("${target.id}/lib") }
        .orElse(root.dir("prebuilts/${target.id}/lib"))
}

package buildlogic.platform

import java.io.File

/** The host OS: "macos" | "windows" | "linux". */
internal fun hostPlatform(): String {
    val os = System.getProperty("os.name")
    return when {
        os.startsWith("Mac", ignoreCase = true) || os.contains("Darwin", ignoreCase = true) -> "macos"
        os.startsWith("Windows", ignoreCase = true) -> "windows"
        else -> "linux"
    }
}

/** The host CPU: "arm64" | "x64". */
internal fun hostArch(): String {
    val arch = System.getProperty("os.arch").lowercase()
    return when {
        arch.contains("aarch64") || arch.contains("arm64") -> "arm64"
        arch == "x64" || arch.contains("amd64") || arch.contains("x86_64") -> "x64"
        else -> error("Unsupported host arch '$arch'. Use arm64 or x64.")
    }
}

/** Absolute path to a usable cmake, preferring Homebrew installs, else whatever is on PATH. */
internal fun resolveCmake(): String =
    listOf("/opt/homebrew/bin/cmake", "/usr/local/bin/cmake").firstOrNull { File(it).exists() } ?: "cmake"

/** [program]'s absolute path from PATH (with .exe on Windows), or null. */
internal fun findOnPath(program: String): String? {
    val name = if (hostPlatform() == "windows") "$program.exe" else program
    return System.getenv("PATH").orEmpty().split(File.pathSeparator)
        .map { File(it, name) }.firstOrNull { it.canExecute() }?.path
}

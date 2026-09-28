package buildlogic.platform

import java.io.File

/**
 * What `source emsdk_env.sh` sets, for running emcc through CMake's Emscripten toolchain file directly:
 * the SDK root, and its bundled Python/Node where it ships them (emcc needs Python 3.10+, newer than
 * macOS's system one). Call at execution time: [emsdk] exists only after `setupEmsdk`.
 */
fun emsdkEnvironment(emsdk: File): Map<String, String> {
    val env = mutableMapOf("EMSDK" to emsdk.invariantSeparatorsPath)
    fun bundled(dir: String, exe: String) = emsdk.resolve(dir).listFiles()?.map { it.resolve("bin/$exe") }?.firstOrNull { it.isFile }
    bundled("python", "python3")?.let { env["EMSDK_PYTHON"] = it.path }
    bundled("node", "node")?.let { env["EMSDK_NODE"] = it.path }
    return env
}

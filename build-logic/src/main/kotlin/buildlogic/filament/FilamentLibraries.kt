package buildlogic.filament

import buildlogic.cmake.linkCApi
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

// Filament archives each module's C API needs beyond its dependencies' (klibs carry them to the final link).
private val MODULE_ARCHIVES = mapOf(
    "filament" to listOf("filament", "backend", "utils", "geometry", "ibl-lite", "filaflat", "filabridge", "smol-v", "zstd", "meshoptimizer"),
    "filamat" to listOf("filamat", "shaders", "filabridge", "filaflat"),
    "filament-utils" to listOf("filament-iblprefilter", "camutils", "image", "imageio-lite", "ktxreader", "basis_transcoder", "stb"),
    "gltfio" to listOf("gltfio_core", "dracodec", "basis_transcoder", "mikktspace", "stb", "image", "imageio-lite", "ktxreader", "uberarchive", "uberzlib"),
)

private const val IOS_LINKER_OPTS =
    "-lc++ -lz -framework Metal -framework UIKit -framework CoreVideo -framework QuartzCore -framework CoreGraphics -framework Foundation -framework OpenGLES"

/** Packs a binding [module]'s C API and its Filament archives into the klib ([linkCApi]); nothing for other modules. */
fun KotlinNativeTarget.linkFilamentCApi(project: Project, module: String) {
    linkCApi(project, module, MODULE_ARCHIVES[module] ?: return, IOS_LINKER_OPTS)
}

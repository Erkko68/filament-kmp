package buildlogic.prebuilts.source

import buildlogic.platform.FilamentTarget
import java.io.File

/**
 * How to build Filament's static libraries for a target upstream publishes none for.
 *
 * @property hostTools   tools built natively first and imported by the cross build (matc, resgen, …)
 * @property arguments   the target's CMake arguments on top of Filament's release defaults
 * @property patches     source file → text replacements applied before configuring
 * @property install     true: take libraries from `cmake --install`; false: from the build tree
 * @property extraTargets built after the default target (e.g. filamat, off by default on wasm)
 */
class SourceBuildRecipe(
    val hostTools: List<String> = emptyList(),
    val arguments: List<String>,
    val patches: Map<String, List<Pair<String, String>>> = emptyMap(),
    val install: Boolean,
    val extraTargets: List<String> = emptyList(),
    val emscripten: Boolean = false,
)

internal fun FilamentTarget.sourceBuildRecipe(): SourceBuildRecipe = when (this) {
    FilamentTarget.WASM -> SourceBuildRecipe(
        // upstream build.sh's WEB_HOST_TOOLS
        hostTools = listOf("matc", "resgen", "cmgen", "filamesh", "uberz", "mipgen", "glslminifier"),
        arguments = listOf("-G", "Ninja", "-DWASM=1", "-DFILAMENT_BUILD_FILAMAT=ON"),
        install = false,
        extraTargets = listOf("filamat"),
        emscripten = true,
    )
    // Mirrors upstream's build/windows/build-github.bat /MT variant (hardcoded to x64 there).
    FilamentTarget.WINDOWS_ARM64 -> SourceBuildRecipe(
        arguments = listOf("-A", "ARM64", "-DUSE_STATIC_CRT=ON", "-DFILAMENT_WINDOWS_CI_BUILD=ON", "-DFILAMENT_SUPPORTS_VULKAN=ON"),
        patches = mapOf(
            // BlueGL's only 64-bit Windows trampoline is x64 MASM; use the portable C++ one on ARM64.
            "libs/bluegl/CMakeLists.txt" to listOf(
                "if(NOT IS_64_BIT)" to "if(NOT IS_64_BIT OR CMAKE_GENERATOR_PLATFORM STREQUAL \"ARM64\")",
                "if (WIN32 AND IS_64_BIT)" to "if (WIN32 AND IS_64_BIT AND NOT CMAKE_GENERATOR_PLATFORM STREQUAL \"ARM64\")",
            ),
            // Filament rejects MSYS2 shells via \$MSYSTEM, which Git Bash forwards even to MSVC builds.
            "CMakeLists.txt" to listOf("if(DEFINED ENV{MSYSTEM})" to "if(FALSE)"),
        ),
        install = true,
    )
    else -> error("$id is downloaded from upstream releases, not built from source")
}

/** The static libraries a finished build produced, from its install tree or its build tree. */
internal fun SourceBuildRecipe.collectLibraries(buildDir: File, installDir: File): List<File> = if (install) {
    installDir.resolve("lib").walk().filter { it.isFile && (it.extension == "a" || it.extension == "lib") }.toList()
} else {
    buildDir.walk().onEnter { it.name != "CMakeFiles" }.filter { it.isFile && it.extension == "a" }.toList()
}

/** uberarchive.h as built: resgen bakes this build's archive size in. */
internal fun SourceBuildRecipe.uberarchiveHeader(buildDir: File, installDir: File): File =
    if (install) installDir.resolve("include/gltfio/materials/uberarchive.h")
    else buildDir.resolve("libs/gltfio/materials/uberarchive.h")

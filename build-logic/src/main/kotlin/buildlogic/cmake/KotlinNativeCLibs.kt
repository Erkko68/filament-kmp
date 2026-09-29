package buildlogic.cmake

import buildlogic.platform.FilamentTarget
import buildlogic.platform.filamentLibDir
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget

// Filament archives each module's C API needs beyond its dependencies' (klibs carry them to the final link).
private val MODULE_ARCHIVES = mapOf(
    "filament" to listOf("filament", "backend", "utils", "geometry", "ibl-lite", "filaflat", "filabridge", "smol-v", "zstd", "meshoptimizer"),
    "filamat" to listOf("filamat", "shaders", "filabridge", "filaflat"),
    "filament-utils" to listOf("filament-iblprefilter", "camutils", "image", "imageio-lite", "ktxreader", "basis_transcoder", "stb"),
    "gltfio" to listOf("gltfio_core", "dracodec", "basis_transcoder", "mikktspace", "stb", "image", "imageio-lite", "ktxreader", "uberarchive", "uberzlib"),
)

private const val IOS_LINKER_OPTS =
    "-lc++ -lz -framework Metal -framework UIKit -framework CoreVideo -framework QuartzCore -framework CoreGraphics -framework Foundation -framework OpenGLES"

/** The Kotlin/Native target's [FilamentTarget], or null for targets without a C API build. */
val KotlinNativeTarget.filamentTarget: FilamentTarget?
    get() = when (konanTarget) {
        KonanTarget.IOS_ARM64 -> FilamentTarget.IOS_ARM64
        KonanTarget.IOS_SIMULATOR_ARM64 -> FilamentTarget.IOS_SIMULATOR_ARM64
        else -> null
    }

/**
 * Packs a binding [module]'s C API static library (from the root `cmakeBuild_<id>` task) and its Filament archives
 * into the klib through a header-less cinterop: the common externals link by `@SymbolName`, so the
 * cinterop only carries libraries and linker options to the final binary.
 */
fun KotlinNativeTarget.linkFilamentCApi(project: Project, module: String) {
    val target = filamentTarget ?: return
    if (module !in MODULE_ARCHIVES) return
    val cLibDir = project.rootProject.layout.buildDirectory.dir("filament-c/${target.id}")
    val libDir = project.filamentLibDir(target)
    val archives = listOf("$module-c") + MODULE_ARCHIVES.getValue(module)
    val defFile = project.layout.buildDirectory.file("cinterop/$name/$module.def")

    val writeDef = project.tasks.register("cinteropDef${module.capitalized()}${name.capitalized()}") {
        val text = listOf(
            "staticLibraries = ${archives.joinToString(" ") { "lib$it.a" }}",
            "libraryPaths = ${cLibDir.get().asFile.path} ${libDir.get().asFile.path}",
            "linkerOpts = $IOS_LINKER_OPTS",
        ).joinToString("\n", postfix = "\n")
        inputs.property("text", text)
        outputs.file(defFile)
        doLast { defFile.get().asFile.writeText(text) }
    }
    compilations.getByName("main").cinterops.create("filament") {
        definitionFile.set(defFile)
        project.tasks.named(interopProcessingTaskName) { dependsOn(writeDef, ":cmakeBuild_${target.id}") }
    }
}

private fun String.capitalized() = split('-').joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }

package buildlogic.apicheck

import buildlogic.cmake.registerCApiBuild
import buildlogic.cppapi.ApiModelTask
import buildlogic.platform.FilamentTarget
import buildlogic.platform.filamentLibDir
import buildlogic.platform.hostPlatform
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

private val FILAMENT_LIBRARIES = listOf("filament", "gltfio_core", "filamat", "camutils", "geometry", "filament-iblprefilter", "utils")
private val PUBLIC_HEADERS = listOf(
    "filament/*.h", "gltfio/*.h", "filamat/*.h", "camutils/*.h", "geometry/*.h", "filament-iblprefilter/*.h", "utils/EntityManager.h",
)
private val C_MODULES = listOf("filament", "filamat", "filament-utils", "gltfio")

/**
 * Registers `apiModel` ([ApiModelTask]) and, off Windows, `apiGaps` ([ApiGapsTask]) for the host, plus `apiGapsCBuild`: the C API's objects built at -O0,
 * so calls to Filament's inline methods stay calls.
 */
fun Project.registerApiGapTasks() {
    val root = layout.projectDirectory
    tasks.register<ApiModelTask>("apiModel") {
        group = "verification"
        description = "Reports the Filament C++ API surface clang sees in the public headers."
        includeDir.set(root.dir("include"))
        publicHeaders.from(root.dir("include").asFileTree.matching { include(PUBLIC_HEADERS) })
        report.set(layout.buildDirectory.file("reports/api-model.txt"))
    }
    if (hostPlatform() == "windows") return // nm can't read MSVC objects
    val target = FilamentTarget.host()
    val cBuild = registerCApiBuild("apiGapsCBuild", target) {
        description = "Builds the Fila* C API's objects without inlining, for apiGaps."
        buildType.set("Debug")
        buildDir.set(layout.buildDirectory.dir("cmake/api-gaps"))
        outputDir.set(layout.buildDirectory.dir("filament-c/api-gaps"))
        arguments.add("-DJNI_HOME=${System.getProperty("java.home").replace('\\', '/')}")
        targets.addAll(C_MODULES.map { "fila-$it" })
    }

    tasks.register<ApiGapsTask>("apiGaps") {
        group = "verification"
        description = "Reports the Filament C++ API the Fila* C API doesn't call, and Fila* functions Kotlin doesn't bind."
        filamentLibraries.from(filamentLibDir(target).map { dir -> FILAMENT_LIBRARIES.map { dir.file("lib$it.a") } })
        includeDir.set(root.dir("include"))
        publicHeaders.from(root.dir("include").asFileTree.matching { include(PUBLIC_HEADERS) })
        cApiObjects.from(cBuild.flatMap { it.buildDir }.map { it.asFileTree.matching { include("CMakeFiles/fila-*.dir/**/*.o") } })
        cApiHeaders.from(root.dir("c").asFileTree.matching { include("*/c/*.h") })
        externals.from(root.dir("kotlin").asFileTree.matching { include("*/src/commonMain/**/*.kt") })
        report.set(layout.buildDirectory.file("reports/api-gaps.txt"))
        dependsOn(cBuild)
    }
}

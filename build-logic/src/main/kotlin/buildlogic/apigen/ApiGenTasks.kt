package buildlogic.apigen

import buildlogic.apigen.c.GenerateCApiTask
import buildlogic.apigen.cpp.ApiModelTask
import buildlogic.apigen.externals.GenerateBindingsTask
import buildlogic.apigen.gaps.ApiGapsTask
import buildlogic.apigen.kotlin.GenerateKotlinExternalsTask
import buildlogic.cmake.registerCApiBuild
import buildlogic.platform.FilamentTarget
import buildlogic.platform.filamentLibDir
import buildlogic.platform.hostPlatform
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

private val FILAMENT_LIBRARIES = listOf("filament", "gltfio_core", "filamat", "camutils", "geometry", "filament-iblprefilter", "utils")
private val C_MODULES = listOf("filament", "filamat", "filament-utils", "gltfio")

/** C modules on the generated API, by the Kotlin package of their externals; the rest still use c/<module>/{c,cpp}. */
private val GENERATED_MODULES = mapOf(
    "filamat" to "io.github.erkko68.filament.filamat.capi",
    "filament-utils" to "io.github.erkko68.filament.utils.capi",
    "gltfio" to "io.github.erkko68.filament.gltfio.capi",
)

/**
 * Registers the API generator, C++ headers → Fila* C → Kotlin, one package per stage:
 *
 * ```
 * ApiHeaders   c/api-headers.txt: which headers are API, and the C module each goes to
 * cpp/         clang's AST of those headers → CppApi model         apiModel        (report)
 * c/           CppApi → c/<module>/generated                       generateCApi    (committed)
 * kotlin/      c/<module>/{generated,manual} → Kotlin externals     generateKotlinExternals (committed)
 * externals/   common Kotlin externals → JNI forwarders, wasm tables  generateBindings (build/)
 * gaps/        C++ API the C API doesn't call, C the Kotlin doesn't bind  apiGaps  (report)
 * ```
 */
fun Project.registerApiGenTasks() {
    val root = layout.projectDirectory
    tasks.register<ApiModelTask>("apiModel") {
        group = "verification"
        description = "Reports the Filament C++ API surface clang sees in the public headers."
        includeDir.set(root.dir("include"))
        publicHeaders.from(apiHeaderFiles())
        report.set(layout.buildDirectory.file("reports/api-model.txt"))
    }

    tasks.register<GenerateCApiTask>("generateCApi") {
        group = "build setup"
        description = "Generates the Fila* C API from Filament's public headers into c/<module>/generated."
        includeDir.set(root.dir("include"))
        publicHeaders.from(apiHeaderFiles())
        apiHeadersFile.set(apiHeadersFile())
        modules.set(apiHeaders().modules.keys.toList())
        cDir.set(root.dir("c"))
    }

    tasks.register<GenerateKotlinExternalsTask>("generateKotlinExternals") {
        group = "build setup"
        description = "Generates the common Kotlin externals of the generated and manual Fila* C headers."
        cDir.set(root.dir("c"))
        kotlinDir.set(root.dir("kotlin"))
        packages.set(GENERATED_MODULES)
        // Reads the headers generateCApi writes.
        mustRunAfter("generateCApi")
    }

    tasks.register<GenerateBindingsTask>("generateBindings") {
        group = "filament"
        description = "Generates the JNI forwarders and wasm export tables from the common externals."
        sources.from(root.dir("kotlin").asFileTree.matching { include("*/src/commonMain/**/*.kt") })
        headers.from(root.dir("c").asFileTree.matching { include("*/c/*.h", "*/generated/*.h", "*/manual/*.h") })
        cDir.set(root.dir("c"))
        wasmRuntimes.put("filamat", "filamat-kmp")
        outputDir.set(layout.buildDirectory.dir("generated/bindings"))
    }

    if (hostPlatform() == "windows") return // nm can't read MSVC objects
    val target = FilamentTarget.host()
    // The C API's objects at -O0, so calls to Filament's inline methods stay calls.
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
        publicHeaders.from(apiHeaderFiles())
        cApiObjects.from(cBuild.flatMap { it.buildDir }.map { it.asFileTree.matching { include("CMakeFiles/fila-*.dir/**/*.o") } })
        cApiHeaders.from(root.dir("c").asFileTree.matching { include("*/c/*.h") })
        externals.from(root.dir("kotlin").asFileTree.matching { include("*/src/commonMain/**/*.kt") })
        report.set(layout.buildDirectory.file("reports/api-gaps.txt"))
        dependsOn(cBuild)
    }
}

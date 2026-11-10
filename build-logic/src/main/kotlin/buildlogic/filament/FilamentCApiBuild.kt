package buildlogic.filament

import buildlogic.apigen.bindings.GenerateBindingsTask
import buildlogic.cmake.CMakeBuildTask
import buildlogic.cmake.cmakeBuildSlot
import buildlogic.cmake.toolchainArguments
import buildlogic.platform.FilamentTarget
import buildlogic.platform.emsdkEnvironment
import buildlogic.platform.filamentLibDir
import buildlogic.platform.prebuiltsTask
import buildlogic.platform.resolveCmake
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

/**
 * Registers a [CMakeBuildTask] building the C API (c/CMakeLists.txt) for [target] into
 * `build/filament-c/<id>`: its toolchain, Filament libraries and headers, and the generated bindings.
 * [configure] adds the consumer's own arguments and [CMakeBuildTask.targets].
 */
fun Project.registerCApiBuild(
    name: String,
    target: FilamentTarget,
    configure: CMakeBuildTask.() -> Unit,
): TaskProvider<CMakeBuildTask> = tasks.register<CMakeBuildTask>(name) {
    group = "filament"
    description = "Builds the Fila* C API for ${target.id}."
    val root = rootProject.layout.projectDirectory
    val bindings = rootProject.tasks.named<GenerateBindingsTask>("generateBindings").flatMap { it.outputDir }
    val libDir = filamentLibDir(target)

    cmake.set(resolveCmake())
    // -Pfilament.debug=true builds the C API in Debug (Filament's libraries stay Release).
    buildType.set(if (findProperty("filament.debug") == "true") "Debug" else "Release")
    sourceDir.set(root.dir("c"))
    buildDir.set(layout.buildDirectory.dir("cmake/${target.id}"))
    outputDir.set(layout.buildDirectory.dir("filament-c/${target.id}"))
    arguments.addAll(
        "-DFILAMENT_PLATFORM=${target.cmakePlatform}",
        "-DFILAMENT_INCLUDE_DIR=${root.dir("include").asFile.invariantSeparatorsPath}",
    )
    arguments.add("-DFILAMENT_LIB_DIR=${libDir.asFile.invariantSeparatorsPath}")
    arguments.add(bindings.map { "-DFILA_BINDINGS_DIR=${it.asFile.invariantSeparatorsPath}" })
    arguments.addAll(target.toolchainArguments(root))
    if (target == FilamentTarget.WASM) {
        val emsdk = root.dir(".emsdk").asFile
        environment.putAll(provider { emsdkEnvironment(emsdk) })
    }

    sources.from(
        root.dir("c"),
        root.file("jni/CMakeLists.txt"), root.dir("jni/src/main/cpp"),
        root.dir("android/src/main/cpp"),
        root.file("web/CMakeLists.txt"), root.dir("web/src/wasm"),
        bindings, libDir, root.file("include/.filament-version"),
    )
    dependsOn(target.prebuiltsTask, ":downloadIncludes", ":generateBindings")
    usesService(cmakeBuildSlot())
    configure()
}

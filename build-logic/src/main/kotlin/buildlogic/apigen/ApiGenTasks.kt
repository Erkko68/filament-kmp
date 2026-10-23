package buildlogic.apigen

import buildlogic.apigen.bindings.GenerateBindingsTask
import buildlogic.apigen.c.GenerateCApiTask
import buildlogic.apigen.cpp.ApiModelTask
import buildlogic.apigen.kotlin.GenerateKotlinExternalsTask
import buildlogic.apigen.report.ApiCoverageTask
import buildlogic.apigen.report.ApiVerifyTask
import org.gradle.api.Project
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.register

/**
 * Registers the API generator for the library [config] describes, C++ headers → C → Kotlin, one package per stage:
 *
 * ```
 * ApiGenConfig  what the generator is told about the library
 * ApiHeaders    api-headers.txt: which headers are API, and the C module each goes to
 * ApiGen        the stages, each reading what the one before wrote; no Gradle
 * cpp/          clang's AST of those headers → CppApi model         apiModel        (report)
 * c/            CppApi → c/<module>/generated; the functions those and
 *               c/<module>/manual declare → c/api-manifest.json     generateCApi    (committed)
 * kotlin/       api-manifest.json → Kotlin externals                generateKotlinExternals (committed)
 * bindings/     api-manifest.json → JNI forwarders, wasm tables     generateBindings (build/)
 * report/       what C++ the C and Kotlin APIs bind, by C function     apiCoverage (committed report)
 *               C++ API the C API's objects don't call                 apiGaps  (report; the library's build registers it)
 *               hand-written Kotlin enums send C the C++ values        apiVerify (fails)
 * ```
 *
 * The Kotlin stages are registered when the config has [ApiGenConfig.kotlin]. generateKotlinExternals runs
 * generateCApi first, so a manual header's new function reaches Kotlin in one run.
 */
fun Project.registerApiGenTasks(config: ApiGenConfig) {
    val root = layout.projectDirectory
    val apiGen = ApiGen(config, projectDir, layout.buildDirectory.dir("tmp/apiGen").get().asFile)
    fun ApiGenTask.configure() {
        this.config.set(config)
        projectDir.set(root)
    }
    // What clang reads: the whole include tree, which the API headers include, and which of it is API.
    fun ApiGenTask.readsHeaders() {
        inputs.dir(apiGen.includeDir).withPathSensitivity(PathSensitivity.RELATIVE)
        inputs.file(apiGen.apiHeadersFile).withPathSensitivity(PathSensitivity.RELATIVE)
    }

    tasks.register<ApiModelTask>("apiModel") {
        group = "verification"
        description = "Reports the ${config.name} C++ API surface clang sees in the public headers."
        configure()
        readsHeaders()
        report.set(layout.buildDirectory.file("reports/api-model.txt"))
    }

    val generateCApi = tasks.register<GenerateCApiTask>("generateCApi") {
        group = "build setup"
        description = "Generates the ${config.c.prefix}* C API from ${config.name}'s public headers into ${config.c.dir}/<module>/generated."
        configure()
        readsHeaders()
        // The functions the manual headers declare, and the bridge header the forwarders compile against.
        inputs.files(fileTree(apiGen.cDir) { include("*/manual/*.h", "*/manual/*.hpp") }).withPathSensitivity(PathSensitivity.RELATIVE)
        outputs.dirs(provider { apiGen.generatedDirs() })
        outputs.file(apiGen.manifestFile)
    }

    tasks.register<ApiCoverageTask>("apiCoverage") {
        group = "verification"
        description = "Writes ${config.c.dir}/api-coverage.txt: what of the C++ API the C and Kotlin APIs bind; warns on stale generated code."
        configure()
        // Reads what they write, and tasks run in parallel under the configuration cache.
        mustRunAfter(generateCApi)
        if (config.kotlin != null) mustRunAfter("generateKotlinExternals")
    }

    if (config.kotlin == null) return

    tasks.register<GenerateKotlinExternalsTask>("generateKotlinExternals") {
        group = "build setup"
        description = "Generates the common Kotlin externals of the ${config.c.prefix}* C functions in api-manifest.json."
        configure()
        // Of the manifest generateCApi writes, which is up to date when nothing it reads changed.
        dependsOn(generateCApi)
    }

    tasks.register<ApiVerifyTask>("apiVerify") {
        group = "verification"
        description = "Checks the hand-written Kotlin enums send C the values of ${config.name}'s."
        configure()
    }

    tasks.register<GenerateBindingsTask>("generateBindings") {
        group = "build setup"
        description = "Generates the JNI forwarders and wasm export tables of the functions in api-manifest.json."
        configure()
        // The committed manifest: every native build runs this, without clang or generateCApi.
        manifest.set(apiGen.manifestFile)
        mustRunAfter(generateCApi)
        outputDir.set(layout.buildDirectory.dir("generated/bindings"))
    }
}

package buildlogic.apigen

import buildlogic.apigen.c.GenerateCApiTask
import buildlogic.apigen.cpp.ApiModelTask
import buildlogic.apigen.externals.GenerateBindingsTask
import buildlogic.apigen.gaps.ApiCoverageTask
import buildlogic.apigen.kotlin.GenerateKotlinExternalsTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * Registers the API generator for the library [config] describes, C++ headers → C → Kotlin, one package per stage:
 *
 * ```
 * ApiGenConfig  what the generator is told about the library; ApiGen runs the cpp and c stages, without Gradle
 * ApiHeaders    api-headers.txt: which headers are API, and the C module each goes to
 * cpp/          clang's AST of those headers → CppApi model         apiModel        (report)
 * c/            CppApi → c/<module>/generated                       generateCApi    (committed)
 * kotlin/       c/<module>/{generated,manual} → Kotlin externals     generateKotlinExternals (committed)
 * externals/    common Kotlin externals → JNI forwarders, wasm tables  generateBindings (build/)
 * gaps/         what C++ the C and Kotlin APIs bind, by C function     apiCoverage (committed report)
 *               C++ API the C API's objects don't call                 apiGaps  (report; the library's build registers it)
 * ```
 *
 * The Kotlin stages are registered when the config has [ApiGenConfig.kotlin].
 */
fun Project.registerApiGenTasks(config: ApiGenConfig) {
    val root = layout.projectDirectory
    val apiGen = ApiGen(config, projectDir, layout.buildDirectory.dir("tmp/apiGen").get().asFile)
    fun ApiGenTask.configure() {
        this.config.set(config)
        projectDir.set(root)
    }

    tasks.register<ApiModelTask>("apiModel") {
        group = "verification"
        description = "Reports the ${config.name} C++ API surface clang sees in the public headers."
        configure()
        inputs.files(provider { apiGen.headerFiles() + apiGen.apiHeadersFile })
        report.set(layout.buildDirectory.file("reports/api-model.txt"))
    }

    tasks.register<GenerateCApiTask>("generateCApi") {
        group = "build setup"
        description = "Generates the ${config.prefix}* C API from ${config.name}'s public headers into ${config.cDir}/<module>/generated."
        configure()
        // Its TODOs note the functions the manual headers already write; the forwarders it compiles include the bridge header.
        inputs.files(provider { apiGen.headerFiles() + apiGen.apiHeadersFile + apiGen.manualHeaders() + apiGen.bridgeHeaders() })
        outputs.dirs(provider { apiGen.generatedDirs() })
    }

    tasks.register<ApiCoverageTask>("apiCoverage") {
        group = "verification"
        description = "Writes ${config.cDir}/api-coverage.txt: what of the C++ API the C and Kotlin APIs bind; warns on stale generated code."
        configure()
        // Reads what they write, and tasks run in parallel under the configuration cache.
        mustRunAfter("generateCApi")
        if (config.kotlin != null) mustRunAfter("generateKotlinExternals")
    }

    val kotlin = config.kotlin ?: return

    tasks.register<GenerateKotlinExternalsTask>("generateKotlinExternals") {
        group = "build setup"
        description = "Generates the common Kotlin externals of the generated and manual ${config.prefix}* C headers."
        configure()
        // Reads the headers generateCApi writes.
        mustRunAfter("generateCApi")
    }

    tasks.register<GenerateBindingsTask>("generateBindings") {
        group = "build setup"
        description = "Generates the JNI forwarders and wasm export tables from the common externals."
        configure()
        sources.from(root.dir(kotlin.dir).asFileTree.matching { include("*/src/commonMain/**/*.kt") })
        headers.from(root.dir(config.cDir).asFileTree.matching { include("*/generated/*.h", "*/manual/*.h") })
        // Reads the externals it rewrites.
        mustRunAfter("generateKotlinExternals")
        outputDir.set(layout.buildDirectory.dir("generated/bindings"))
    }
}

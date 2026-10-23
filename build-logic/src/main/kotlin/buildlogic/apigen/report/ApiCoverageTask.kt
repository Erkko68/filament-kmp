package buildlogic.apigen.report

import buildlogic.apigen.ApiGenTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

/**
 * Writes `api-coverage.txt` ([coverage]). Warns when the committed C API or Kotlin externals aren't what the generators
 * would write.
 */
@UntrackedTask(because = "A report to diff, cheap next to reading the AST")
abstract class ApiCoverageTask : ApiGenTask() {
    @TaskAction
    fun run() {
        val apiGen = apiGen()
        val api = apiGen.api()
        val c = apiGen.cApi(api)
        warnStale("C API", "generateCApi", apiGen.cFiles(c).stale())
        val manifest = apiGen.manifest()
        if (!apiGen.manifestFile.isFile || apiGen.manifestFile.readText() != manifest.json()) warnStale("C API", "generateCApi", setOf(apiGen.manifestFile.name))
        apiGen.kotlinDir?.let { warnStale("Kotlin externals", "generateKotlinExternals", apiGen.kotlinFiles(manifest).stale()) }
        val coverage = coverage(api, c.bindings, config.get().naming.prefix, apiGen.kotlinDir?.let(::kotlinApiSources))
        apiGen.coverageFile.writeText(coverage.text)
        logger.lifecycle("API coverage: ${coverage.summary}")
        logger.lifecycle("Report: ${apiGen.coverageFile.toURI()} (git diff it to see what changed)")
    }

    private fun warnStale(what: String, task: String, stale: Set<String>) {
        if (stale.isNotEmpty()) logger.warn("The committed $what is stale (${stale.size} files, e.g. ${stale.first()}): run $task")
    }
}

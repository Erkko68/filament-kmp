package buildlogic.apigen.verify

import buildlogic.apigen.ApiGenTask
import buildlogic.apigen.kotlin.KotlinDeclarations
import buildlogic.apigen.kotlin.kotlinApiSources
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

/**
 * Fails when a hand-written Kotlin enum sends C a value that isn't the C++ constant's of the same name: its ordinal,
 * or the number it's constructed with. The C++ values are the ones clang reads from the headers. Where `apiCoverage`
 * reports what's bound, this checks what's bound is right.
 */
@UntrackedTask(because = "A check, cheap next to reading the AST")
abstract class ApiVerifyTask : ApiGenTask() {
    @TaskAction
    fun run() {
        val apiGen = apiGen()
        val api = apiGen.read().skipping(apiGen.apiHeaders.skipped)
        val declarations = KotlinDeclarations(kotlinApiSources(checkNotNull(apiGen.kotlinDir) { "The config has no Kotlin bindings" }))
        var checked = 0
        val wrong = api.apiEnums(apiGen.headers).flatMap { enum ->
            val constants = enum.constants.filter { api.skipReason("${enum.name}::${it.first}") == null }
            // Kotlin names an enum as the API's alias does, and may declare the name more than once: the one with its constants.
            val kotlin = KotlinEnums.bodies(declarations.scope(api.aliasesOf(enum.name)).orEmpty()).map(KotlinEnums::values)
                .maxByOrNull { values -> constants.count { it.first in values } }?.takeIf { values -> constants.any { it.first in values } }
                ?: return@flatMap emptyList() // apiCoverage reports what Kotlin doesn't declare
            checked++
            // By bits: Kotlin spells an unsigned all-ones as -1.
            constants.filter { (name, value) -> kotlin[name]?.let { it.toLong() != value.toLong() } == true }
                .map { (name, value) -> "${enum.name}::$name is $value in C++, ${kotlin[name]} in Kotlin" }
        }
        if (wrong.isNotEmpty()) throw GradleException("Kotlin enums out of step with ${config.get().name}'s:\n" + wrong.joinToString("\n") { "  $it" })
        logger.lifecycle("API verified: $checked Kotlin enums match ${config.get().name}'s values")
    }
}

package buildlogic.apigen.report

import buildlogic.apigen.ApiGenTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

/** Fails when a hand-written Kotlin enum sends C a value that isn't the C++ constant's ([enumCheck]). */
@UntrackedTask(because = "A check, cheap next to reading the AST")
abstract class ApiVerifyTask : ApiGenTask() {
    @TaskAction
    fun run() {
        val apiGen = apiGen()
        val sources = kotlinApiSources(checkNotNull(apiGen.kotlinDir) { "The config has no Kotlin bindings" })
        val check = enumCheck(apiGen.api(), KotlinDeclarations(sources))
        val name = config.get().name
        if (check.wrong.isNotEmpty()) throw GradleException("Kotlin enums out of step with $name's:\n" + check.wrong.joinToString("\n") { "  $it" })
        logger.lifecycle("API verified: ${check.checked} Kotlin enums match $name's values")
    }
}

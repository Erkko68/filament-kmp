package buildlogic.apigen.report

import buildlogic.apigen.ApiGenTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

/**
 * Fails when a hand-written Kotlin enum sends C a value that isn't the C++ constant's ([enumCheck]), or a Kotlin class
 * mirroring a C++ struct starts a property at a value its field doesn't ([defaultsCheck]).
 */
@UntrackedTask(because = "A check, cheap next to reading the AST")
abstract class ApiVerifyTask : ApiGenTask() {
    @TaskAction
    fun run() {
        val apiGen = apiGen()
        val sources = kotlinApiSources(checkNotNull(apiGen.kotlinDir) { "The config has no Kotlin bindings" })
        val api = apiGen.api()
        val declarations = KotlinDeclarations(sources)
        val enums = enumCheck(api, declarations)
        val defaults = defaultsCheck(api, declarations, config.get().naming)
        val name = config.get().name
        val wrong = enums.wrong + defaults.wrong
        if (wrong.isNotEmpty()) throw GradleException("Kotlin out of step with $name:\n" + wrong.joinToString("\n") { "  $it" })
        logger.lifecycle("API verified: ${enums.checked} Kotlin enums and ${defaults.checked} defaults match $name's")
    }
}

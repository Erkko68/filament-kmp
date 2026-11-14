package buildlogic.apigen.report

import buildlogic.apigen.ApiGenTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

/**
 * Fails when a hand-written Kotlin enum sends C a value that isn't the C++ constant's ([enumCheck]), a Kotlin class
 * mirroring a C++ struct starts a property at a value its field doesn't ([defaultsCheck]), or a Kotlin function
 * defaults a parameter to a value its C++ method doesn't ([argumentsCheck]) or takes a null where the method takes
 * none ([nullabilityCheck]).
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
        val arguments = argumentsCheck(api, declarations)
        val nulls = nullabilityCheck(api, declarations)
        val name = config.get().name
        val wrong = enums.wrong + defaults.wrong + arguments.wrong + nulls.wrong
        if (wrong.isNotEmpty()) throw GradleException("Kotlin out of step with $name:\n" + wrong.joinToString("\n") { "  $it" })
        logger.lifecycle("API verified: ${enums.checked} Kotlin enums, ${defaults.checked} defaults and ${arguments.checked} default arguments match $name's; ${nulls.checked} parameters $name reads through take no null")
    }
}

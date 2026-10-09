package buildlogic.apigen.c

import buildlogic.apigen.ApiGenTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

/**
 * Generates the C API from the library's headers into `<module>/generated` ([CApiWriter]), then checks the headers
 * parse as C and the forwarders compile against the library's headers.
 */
@DisableCachingByDefault(because = "Writes committed sources")
abstract class GenerateCApiTask : ApiGenTask() {
    @TaskAction
    fun generate() {
        val apiGen = apiGen()
        val files = apiGen.writer().write()
        apiGen.write(files)
        apiGen.checkCompiles(files.keys.map(apiGen.cDir::resolve))
        val text = files.values.joinToString("")
        logger.lifecycle("${FUNCTION.findAll(text).count()} functions generated, ${text.split("TODO(handwritten)").size - 1} left for hand-written code")
    }

    private companion object {
        // A definition's opening line in the forwarders.
        val FUNCTION = Regex("""^\S.*\) \{$""", RegexOption.MULTILINE)
    }
}

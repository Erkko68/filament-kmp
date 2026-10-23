package buildlogic.apigen.c

import buildlogic.apigen.ApiGenTask
import buildlogic.apigen.c.Binding.Status
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

/**
 * Generates the C API from the library's headers into `<module>/generated` ([CApiWriter]), then checks the headers
 * parse as C and the forwarders compile against the library's headers, and writes `api-manifest.json` ([CManifest]).
 */
@DisableCachingByDefault(because = "Writes committed sources")
abstract class GenerateCApiTask : ApiGenTask() {
    @TaskAction
    fun generate() {
        val bindings = apiGen().generateCApi().bindings
        logger.lifecycle("${bindings.count { it.status == Status.GENERATED }} functions generated, ${bindings.count { it.status == Status.TODO }} left for hand-written code")
    }
}

package buildlogic.apigen.cpp

import buildlogic.apigen.ApiGenTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

/** Writes the C++ API's surface to [report] ([modelReport]). */
@DisableCachingByDefault(because = "A local report")
abstract class ApiModelTask : ApiGenTask() {
    @get:OutputFile abstract val report: RegularFileProperty

    @TaskAction
    fun run() {
        val model = modelReport(apiGen().model())
        val file = report.get().asFile
        file.writeText(model.text)
        logger.lifecycle(model.summary)
        logger.lifecycle("Report: ${file.toURI()}")
    }
}

package buildlogic.apigen.gaps

import buildlogic.apigen.ApiGenTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Reports the library's C++ API the C API doesn't call, into [report]. The C++ side is symbols: what clang sees in
 * the headers plus what the [libraries] define, minus every symbol the C API's -O0 objects mention.
 */
@DisableCachingByDefault(because = "A local report, cheap next to the C API build it needs")
abstract class ApiGapsTask @Inject constructor(private val exec: ExecOperations) : ApiGenTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val libraries: ConfigurableFileCollection

    /** Built without inlining, so every inline method the C API calls leaves a symbol. */
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val cApiObjects: ConfigurableFileCollection

    @get:OutputFile abstract val report: RegularFileProperty

    @TaskAction
    fun run() {
        val nm = SymbolReader(exec)
        // The API headers' exported classes make up the API; everything else in the libraries is internal.
        val headers = apiGen().read().headerApi()
        val demangler = Demangler(exec)
        val headerMethods = demangler.demangle(headers.methods)
        val undeclared = CppApiGaps.undeclared(nm.read(libraries.files), demangler.demangle(headers.declared), headers.publicClasses)
        logger.lifecycle("${headerMethods.size} public methods in the headers, ${undeclared.size} more only in the libraries")
        undeclared.forEach { logger.info("  only in the libraries: $it") }
        val sections = mapOf(
            "C++ methods the ${config.get().prefix}* C API never calls" to
                CppApiGaps.find(headerMethods, undeclared, nm.read(cApiObjects.files), headers.publicClasses),
        )
        val file = report.get().asFile
        file.writeText(sections.entries.joinToString("\n") { (title, gaps) -> "## $title\n" + gaps.joinToString("") { "$it\n" } })
        sections.forEach { (title, gaps) -> logger.lifecycle("$title: ${gaps.size}") }
        logger.lifecycle("Report: ${file.toURI()}")
    }
}

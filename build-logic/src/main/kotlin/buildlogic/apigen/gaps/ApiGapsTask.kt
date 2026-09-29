package buildlogic.apigen.gaps

import buildlogic.apigen.cpp.ClangAstDump
import buildlogic.apigen.cpp.CppApiReader
import buildlogic.apigen.relativeHeaders
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Reports the Filament API nothing binds yet, along the C++ → Fila* C → Kotlin chain, into [report]. The C++ side
 * is symbols: what clang sees in the headers plus what the Filament libraries define, minus every symbol the
 * C API's -O0 objects mention.
 */
@DisableCachingByDefault(because = "A local report, cheap next to the C API build it needs")
abstract class ApiGapsTask @Inject constructor(private val exec: ExecOperations) : DefaultTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val filamentLibraries: ConfigurableFileCollection

    /** Headers whose `*_PUBLIC` classes make up the API; everything else in the libraries is internal. */
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val publicHeaders: ConfigurableFileCollection

    @get:Internal abstract val includeDir: DirectoryProperty

    /** Built without inlining, so every inline method the C API calls leaves a symbol. */
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val cApiObjects: ConfigurableFileCollection

    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val cApiHeaders: ConfigurableFileCollection

    /** commonMain sources declaring the `@ExternalSymbolName` externals. */
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val externals: ConfigurableFileCollection

    @get:OutputFile abstract val report: RegularFileProperty

    @TaskAction
    fun run() {
        val nm = SymbolReader(exec)
        val include = includeDir.get().asFile
        val headers = CppApiReader(ClangAstDump(exec, temporaryDir), temporaryDir)
            .read(include, relativeHeaders(publicHeaders.files, include)).headerApi()
        val demangler = Demangler(exec)
        val headerMethods = demangler.demangle(headers.methods)
        val undeclared = CppApiGaps.undeclared(nm.read(filamentLibraries.files), demangler.demangle(headers.declared), headers.publicClasses)
        logger.lifecycle("${headerMethods.size} public methods in the headers, ${undeclared.size} more only in the libraries")
        undeclared.forEach { logger.info("  only in the libraries: $it") }
        val sections = mapOf(
            "C++ methods the Fila* C API never calls" to
                CppApiGaps.find(headerMethods, undeclared, nm.read(cApiObjects.files), headers.publicClasses),
            "Fila* functions without a common external" to CApiGaps.find(cApiHeaders.files, externals.files),
        )
        val file = report.get().asFile
        file.writeText(sections.entries.joinToString("\n") { (title, gaps) -> "## $title\n" + gaps.joinToString("") { "$it\n" } })
        sections.forEach { (title, gaps) -> logger.lifecycle("$title: ${gaps.size}") }
        logger.lifecycle("Report: ${file.toURI()}")
    }
}

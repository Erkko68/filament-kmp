package buildlogic.apigen.c

import buildlogic.apigen.ApiHeaders
import buildlogic.apigen.cpp.ClangAstDump
import buildlogic.apigen.cpp.CppApiReader
import buildlogic.apigen.relativeHeaders
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

/**
 * Generates the Fila* C API from Filament's public headers into `c/<module>/generated` ([CApiWriter]), then
 * checks the headers parse as C and the forwarders compile against Filament's headers.
 */
@DisableCachingByDefault(because = "Writes committed sources")
abstract class GenerateCApiTask @Inject constructor(private val exec: ExecOperations) : DefaultTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val publicHeaders: ConfigurableFileCollection

    @get:Internal abstract val includeDir: DirectoryProperty

    /** `c/api-headers.txt`: which headers, and which module each one's API goes to. */
    @get:InputFile @get:PathSensitive(PathSensitivity.NONE)
    abstract val apiHeadersFile: RegularFileProperty

    @get:Input abstract val modules: ListProperty<String>

    @get:Internal abstract val cDir: DirectoryProperty

    @get:OutputDirectories
    val outputDirs get() = modules.get().map { cDir.dir("$it/generated") }

    @TaskAction
    fun generate() {
        val include = includeDir.get().asFile
        val headers = relativeHeaders(publicHeaders.files, include)
        val apiHeaders = ApiHeaders.parse(apiHeadersFile.get().asFile.readText())
        val api = CppApiReader(ClangAstDump(exec, temporaryDir), temporaryDir).read(include, headers).skipping(apiHeaders.skipped)
        api.unknownSkips().takeIf { it.isNotEmpty() }?.let { throw GradleException("api-headers.txt skips unknown declarations: $it") }
        val files = CApiWriter(api, apiHeaders, headers).write()
        val c = cDir.get().asFile
        modules.get().forEach { c.resolve("$it/generated").deleteRecursively() }
        files.forEach { (path, text) -> c.resolve(path).apply { parentFile.mkdirs() }.writeText(text) }

        val generated = files.keys.map(c::resolve)
        compile("headers.c", generated.filter { it.extension == "h" }, "clang", "-x", "c", "-std=c11")
        compile("forwarders.cpp", generated.filter { it.extension == "cpp" }, "clang++", "-std=c++20", "-I", include.path)
        val text = files.values.joinToString("")
        logger.lifecycle("${FUNCTION.findAll(text).count()} functions generated, ${text.split("TODO(handwritten)").size - 1} left for hand-written code")
    }

    private fun compile(unit: String, sources: List<File>, vararg command: String) {
        val file = temporaryDir.resolve(unit).apply { writeText(sources.joinToString("") { "#include \"${it.path}\"\n" }) }
        val output = ByteArrayOutputStream()
        val result = exec.exec {
            commandLine(*command, "-fsyntax-only", file.path)
            errorOutput = output
            isIgnoreExitValue = true
        }
        if (result.exitValue != 0) throw GradleException("Generated C API doesn't compile ($unit):\n$output")
    }

    private companion object {
        // A definition's opening line in the forwarders.
        val FUNCTION = Regex("""^\S.*\) \{$""", RegexOption.MULTILINE)
    }
}

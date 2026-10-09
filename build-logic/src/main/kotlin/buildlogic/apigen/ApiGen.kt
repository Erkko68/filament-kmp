package buildlogic.apigen

import buildlogic.apigen.c.CApiWriter
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppApiReader
import buildlogic.apigen.cpp.clang
import java.io.File

/**
 * The generator for one library, laid out as [ApiGenConfig] says under [projectDir]. Nothing here is Gradle's: the
 * tasks and the tests both drive it. [workDir] holds scratch files.
 */
internal class ApiGen(val config: ApiGenConfig, projectDir: File, workDir: File) {
    // Absolute: clang reports a declaration's file as the include path spells it.
    private val workDir = workDir.absoluteFile
    val includeDir: File = projectDir.absoluteFile.resolve(config.includeDir)
    val cDir: File = projectDir.absoluteFile.resolve(config.cDir)
    val apiHeadersFile: File = cDir.resolve("api-headers.txt")
    val apiHeaders by lazy { ApiHeaders.parse(apiHeadersFile.readText()) }
    val headers by lazy { apiHeaders.headers(includeDir) }

    fun headerFiles() = headers.map(includeDir::resolve)

    fun generatedDirs() = apiHeaders.modules.keys.map { cDir.resolve("$it/generated") }

    /** Hand-written headers: what they declare, the generator leaves alone. */
    fun manualHeaders() = apiHeaders.modules.keys.flatMap { cDir.resolve("$it/manual").listFiles { f -> f.extension == "h" }.orEmpty().toList() }

    /** The hand-written C++ headers the forwarders are compiled against. */
    fun bridgeHeaders() = apiHeaders.modules.keys.flatMap { cDir.resolve("$it/manual").listFiles { f -> f.extension == "hpp" }.orEmpty().toList() }

    fun read(): CppApi = CppApiReader(config, this.workDir.apply { mkdirs() }).read(includeDir, headers)

    /** The [CApiWriter] of the API headers; [manualHeaders] say which functions are hand-written. */
    fun writer(): CApiWriter {
        val api = read().skipping(apiHeaders.skipped)
        val unknown = api.unknownSkips().map { entry ->
            entry + api.overloads(entry.substringBefore("\\(")).ifEmpty { null }?.joinToString(prefix = " (has: ", postfix = ")").orEmpty()
        }
        check(unknown.isEmpty()) { "api-headers.txt skips unknown declarations: ${unknown.joinToString()}" }
        val function = Regex("""\b(${Regex.escape(config.prefix)}\w+)\s*\(""")
        val manual = manualHeaders().flatMap { function.findAll(it.readText()).map { m -> m.groupValues[1] } }.toSet()
        return CApiWriter(api, config, apiHeaders, headers, manual)
    }

    /** Replaces the generated dirs with [files] (text by path under [cDir]). */
    fun write(files: Map<String, String>) {
        generatedDirs().forEach { it.deleteRecursively() }
        files.forEach { (path, text) -> cDir.resolve(path).apply { parentFile.mkdirs() }.writeText(text) }
    }

    /** The paths under [cDir] where the generated dirs aren't what [files] says: changed, missing or left over. */
    fun stale(files: Map<String, String>) = stale(cDir, files, generatedDirs())

    /** Checks the generated headers among [files] parse as C, and the forwarders compile against the library's headers. */
    fun checkCompiles(files: Collection<File>) {
        compile("headers.c", files.filter { it.extension == "h" }, listOf("clang", "-x", "c", "-std=c11"))
        compile("forwarders.cpp", files.filter { it.extension == "cpp" }, listOf("clang++", "-std=${config.std}") + config.clangArgs + listOf("-I", includeDir.path))
    }

    private fun compile(unit: String, sources: List<File>, command: List<String>) {
        val file = workDir.apply { mkdirs() }.resolve(unit).apply { writeText(sources.joinToString("") { "#include \"${it.absolutePath}\"\n" }) }
        val (exit, errors) = clang(command + listOf("-fsyntax-only", file.path), workDir)
        check(exit == 0) { "Generated C API doesn't compile ($unit):\n$errors" }
    }
}

/** The paths under [root] where [dirs] aren't what [files] (text by path under [root]) says: changed, missing or left over. */
internal fun stale(root: File, files: Map<String, String>, dirs: List<File>): Set<String> {
    val onDisk = dirs.flatMap { dir -> dir.walkTopDown().filter { it.isFile }.map { it.relativeTo(root).invariantSeparatorsPath } }
    return files.filter { (path, text) -> root.resolve(path).let { !it.isFile || it.readText() != text } }.keys + (onDisk - files.keys)
}

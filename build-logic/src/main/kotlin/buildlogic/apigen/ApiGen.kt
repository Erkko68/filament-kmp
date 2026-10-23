package buildlogic.apigen

import buildlogic.apigen.c.CApi
import buildlogic.apigen.c.CApiWriter
import buildlogic.apigen.c.CManifest
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppApiReader
import buildlogic.apigen.cpp.clang
import buildlogic.apigen.kotlin.kotlinExternals
import buildlogic.apigen.kotlin.packageDir
import java.io.File

/**
 * The generator for one library, laid out as [ApiGenConfig] says under [projectDir]: each stage reads what the one
 * before wrote. Nothing here is Gradle's: the tasks run the stages. [workDir] holds scratch files.
 */
internal class ApiGen(val config: ApiGenConfig, projectDir: File, workDir: File) {
    // Absolute: clang reports a declaration's file as the include path spells it.
    private val workDir = workDir.absoluteFile
    val includeDir: File = projectDir.absoluteFile.resolve(config.sources.includeDir)
    val cDir: File = projectDir.absoluteFile.resolve(config.c.dir)
    val apiHeadersFile: File = cDir.resolve("api-headers.txt")
    val manifestFile: File = cDir.resolve("api-manifest.json")
    val coverageFile: File = cDir.resolve("api-coverage.txt")
    /** The Kotlin modules' dir, or null for a C API alone. */
    val kotlinDir: File? = config.kotlin?.let { projectDir.absoluteFile.resolve(it.dir) }
    val apiHeaders by lazy {
        ApiHeaders.parse(apiHeadersFile.readText()).also { headers ->
            config.kotlin?.let { check(it.packages.keys == headers.modules.keys) { "packages must list the api-headers.txt modules ${headers.modules.keys}" } }
        }
    }
    val headers by lazy { apiHeaders.headers(includeDir) }

    fun generatedDirs() = apiHeaders.modules.keys.map { cDir.resolve("$it/generated") }

    /** Hand-written headers: what they declare, the generator leaves alone. */
    private fun manualHeaders() = apiHeaders.modules.keys.flatMap { cDir.resolve("$it/manual").listFiles { f -> f.extension == "h" }.orEmpty().toList() }

    /** Everything clang reads in the API headers, nothing skipped. */
    fun model(): CppApi = CppApiReader(config.sources, workDir.apply { mkdirs() }).read(includeDir, headers).scoped(headers)

    /** The API: the [model] less `api-headers.txt`'s skips, each of which must name something. */
    fun api(): CppApi {
        val api = model().scoped(headers, apiHeaders.skipped)
        val unknown = api.unknownSkips().map { entry ->
            entry + api.overloads(entry.substringBefore("\\(")).ifEmpty { null }?.joinToString(prefix = " (has: ", postfix = ")").orEmpty()
        }
        check(unknown.isEmpty()) { "api-headers.txt skips unknown declarations: ${unknown.joinToString()}" }
        return api
    }

    /** The c stage: [api] in C, less the functions the [manualHeaders] declare. */
    fun cApi(api: CppApi = api()): CApi {
        val function = Regex("""\b(${Regex.escape(config.c.prefix)}\w+)\s*\(""")
        val manual = manualHeaders().flatMap { function.findAll(it.readText()).map { m -> m.groupValues[1] } }.toSet()
        return CApiWriter(api, config, apiHeaders, manual).write()
    }

    /** [CApi.files] on disk, in place of the generated dirs. */
    fun cFiles(c: CApi) = GeneratedFiles(cDir, generatedDirs(), c.files)

    /** Writes the C API and the manifest of what its headers declare, once it compiles. */
    fun generateCApi(): CApi = cApi().also { c ->
        cFiles(c).write()
        checkCompiles(c.files.keys.map(cDir::resolve))
        manifestFile.writeText(manifest().json())
    }

    /** The functions the C headers on disk declare, generated and hand-written. */
    fun manifest() = CManifest.read((generatedDirs().flatMap { it.listFiles { f -> f.extension == "h" }.orEmpty().toList() } + manualHeaders()).sortedBy { it.path }, cDir, config.c.prefix, workDir)

    /** The kotlin stage: the externals of [manifest], in place of each module's package of them. */
    fun kotlinFiles(manifest: CManifest): GeneratedFiles {
        val kotlin = checkNotNull(kotlinDir) { "The config has no Kotlin bindings" }
        val bindings = config.kotlin!!
        return GeneratedFiles(kotlin, bindings.packages.map { (module, pkg) -> kotlin.resolve(packageDir(module, pkg)) }, kotlinExternals(manifest, bindings))
    }

    /** Writes the externals of the committed manifest. */
    fun generateKotlinExternals() = kotlinFiles(CManifest.parse(manifestFile.readText())).write()

    /**
     * Checks the generated headers among [files] parse as C, and the forwarders compile against the library's headers,
     * a module at a time as the build compiles them: one can't lean on another's includes.
     */
    fun checkCompiles(files: Collection<File>) {
        compile("headers.c", files.filter { it.extension == "h" }, listOf("clang", "-x", "c", "-std=c11"))
        files.filter { it.extension == "cpp" }.groupBy { it.parentFile.parentFile.name }.forEach { (module, forwarders) ->
            compile("$module.cpp", forwarders, listOf("clang++", "-std=${config.sources.std}") + config.sources.clangArgs + listOf("-I", includeDir.path))
        }
    }

    private fun compile(unit: String, sources: List<File>, command: List<String>) {
        val file = workDir.apply { mkdirs() }.resolve(unit).apply { writeText(sources.joinToString("") { "#include \"${it.absolutePath}\"\n" }) }
        val (exit, errors) = clang(command + listOf("-fsyntax-only", file.path), workDir)
        check(exit == 0) { "Generated C API doesn't compile ($unit):\n$errors" }
    }
}

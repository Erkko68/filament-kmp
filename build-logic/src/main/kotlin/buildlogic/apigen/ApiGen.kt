package buildlogic.apigen

import buildlogic.apigen.bindings.bindingFiles
import buildlogic.apigen.c.CApi
import buildlogic.apigen.c.BROKEN
import buildlogic.apigen.c.CApiWriter
import buildlogic.apigen.c.CManifest
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppApiReader
import buildlogic.apigen.cpp.clang
import buildlogic.apigen.cpp.modelReport as reportModel
import buildlogic.apigen.kotlin.kotlinExternals
import buildlogic.apigen.kotlin.packageDir
import buildlogic.apigen.report.coverage as reportCoverage
import buildlogic.apigen.report.kotlinApiSources
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

    /**
     * The c stage: [api] in C, less the functions the [manualHeaders] declare. The [broken] ones, whose forwarder the
     * compiler rejected, become TODOs: by default the ones the generated headers on disk say were.
     */
    fun cApi(api: CppApi = api(), broken: Set<String> = brokenOnDisk()): CApi {
        val function = Regex("""\b(${Regex.escape(config.c.prefix)}\w+)\s*\(""")
        val manual = manualHeaders().flatMap { function.findAll(it.readText()).map { m -> m.groupValues[1] } }.toSet()
        return CApiWriter(api, config, apiHeaders, manual, broken).write()
    }

    private fun brokenOnDisk() = generatedDirs().flatMap { it.listFiles { f -> f.extension == "h" }.orEmpty().toList() }
        .flatMap { BROKEN_NOTE.findAll(it.readText()).map { m -> m.groupValues[1] } }.toSet()

    /** [CApi.files] on disk, in place of the generated dirs. */
    fun cFiles(c: CApi) = GeneratedFiles(cDir, generatedDirs(), c.files)

    /**
     * Writes the C API and the manifest of what its headers declare, once it compiles. The compiler has the last
     * word on what C++ allows: a forwarder it rejects is written again as a TODO.
     */
    fun generateCApi(): CApi {
        val api = api()
        var c = cApi(api, emptySet()).also { cFiles(it).write() }
        val broken = checkCompiles(c.files.keys.map(cDir::resolve))
        if (broken.isNotEmpty()) {
            println("${broken.size} forwarders don't compile, left as TODO(handwritten):\n" + broken.entries.joinToString("\n") { (name, error) -> "  $name: $error" })
            c = cApi(api, broken.keys).also { cFiles(it).write() }
            check(checkCompiles(c.files.keys.map(cDir::resolve)).isEmpty()) { "Generated C API still doesn't compile without ${broken.keys}" }
        }
        manifestFile.writeText(manifest().json())
        return c
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

    /** The bindings stage: writes each platform's glue of the committed manifest under [outDir], replacing it. */
    fun generateBindings(outDir: File) {
        val bindings = checkNotNull(config.kotlin) { "The config has no Kotlin bindings" }
        GeneratedFiles(outDir, listOf(outDir), bindingFiles(CManifest.parse(manifestFile.readText()).headers, bindings, config.c.prefix)).write()
    }

    /** The report stages: `api-coverage.txt`'s content for [api] and its C API [c], and the C++ model's report. */
    fun coverage(api: CppApi = api(), c: CApi = cApi(api)) = reportCoverage(api, c.bindings, config.c.prefix, kotlinDir?.let(::kotlinApiSources))
    fun modelReport() = reportModel(model())

    /**
     * Checks the generated headers among [files] parse as C, and the forwarders compile against the library's headers,
     * a module at a time as the build compiles them: one can't lean on another's includes. Returns the forwarders that
     * don't, each with the compiler's error; fails on an error outside one, and on a warning: clang's default ones are
     * dangling pointers and the like.
     */
    fun checkCompiles(files: Collection<File>): Map<String, String> {
        compile("headers.c", files.filter { it.extension == "h" }, listOf("clang", "-x", "c", "-std=c11", "-Werror"))?.let { error("Generated C headers don't parse:\n$it") }
        val broken = LinkedHashMap<String, String>()
        files.filter { it.extension == "cpp" }.groupBy { it.parentFile.parentFile.name }.forEach { (module, forwarders) ->
            val command = listOf("clang++", "-std=${config.sources.std}", "-Werror", "-ferror-limit=0") + config.sources.clangArgs + listOf("-I", includeDir.path)
            val errors = compile("$module.cpp", forwarders, command) ?: return@forEach
            val paths = forwarders.associateBy { it.path }
            // An error and the notes after it: the first place among them in a forwarder names the one to blame.
            errors.split(Regex("""(?m)^(?=.*: (fatal )?error: )""")).filter { ": error: " in it || ": fatal error: " in it }.forEach { diagnostic ->
                val (file, line) = DIAGNOSTIC.findAll(diagnostic).firstNotNullOfOrNull { m -> paths[m.groupValues[1]]?.let { it to m.groupValues[2].toInt() } }
                    ?: error("Generated C API doesn't compile ($module.cpp):\n$errors")
                val signature = file.readLines().take(line).last { it.endsWith(" {") && !it[0].isWhitespace() }
                broken.putIfAbsent(Regex("""(\w+)\(""").find(signature)!!.groupValues[1], diagnostic.lineSequence().first().substringAfter("error: "))
            }
        }
        return broken
    }

    /** clang's errors for a unit including [sources], or null when it compiles. */
    private fun compile(unit: String, sources: List<File>, command: List<String>): String? {
        val file = workDir.apply { mkdirs() }.resolve(unit).apply { writeText(sources.joinToString("") { "#include \"${it.absolutePath}\"\n" }) }
        val (exit, errors) = clang(command + listOf("-fsyntax-only", file.path), workDir)
        return errors.takeIf { exit != 0 }
    }

    private companion object {
        val BROKEN_NOTE = Regex("""// TODO\(handwritten\) (\w+): .*\n//     ${Regex.escape(BROKEN)}""")
        val DIAGNOSTIC = Regex("""(?m)^(.+?):(\d+):\d+: """)
    }
}

package buildlogic.apigen

import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import java.nio.file.FileSystems
import java.nio.file.Paths

/** `c/api-headers.txt`: globs under Filament's include dir, grouped by the C module their API is generated into. */
internal class ApiHeaders(val modules: Map<String, List<String>>) {
    val globs get() = modules.values.flatten().filterNot { it.startsWith("!") }

    /** `!glob` lines: headers a section's globs match that declare no API. */
    val excludes get() = modules.values.flatten().filter { it.startsWith("!") }.map { it.drop(1) }

    /** The module whose globs match [header] (relative to the include dir), or null for a non-API header. */
    fun moduleOf(header: String): String? {
        val path = Paths.get(header)
        return modules.entries.firstOrNull { (_, globs) -> globs.any { !it.startsWith("!") && FileSystems.getDefault().getPathMatcher("glob:$it").matches(path) } }?.key
    }

    companion object {
        private val SECTION = Regex("""\[(.+)]""")

        fun parse(text: String): ApiHeaders {
            val modules = LinkedHashMap<String, MutableList<String>>()
            var module: String? = null
            text.lineSequence().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }.forEach { line ->
                SECTION.matchEntire(line)?.let { module = it.groupValues[1]; modules[module!!] = ArrayList() }
                    ?: modules.getValue(checkNotNull(module) { "api-headers.txt: '$line' is outside a [module] section" }).add(line)
            }
            return ApiHeaders(modules)
        }
    }
}

/** `c/api-headers.txt`, parsed at configuration time. */
internal fun Project.apiHeaders(): ApiHeaders =
    ApiHeaders.parse(providers.fileContents(apiHeadersFile()).asText.get())

internal fun Project.apiHeadersFile(): RegularFile = layout.projectDirectory.file("c/api-headers.txt")

/** The headers [apiHeaders] selects under `include/`. */
internal fun Project.apiHeaderFiles(): FileTree =
    layout.projectDirectory.dir("include").asFileTree.matching { apiHeaders().let { include(it.globs); exclude(it.excludes) } }

/** [files] as paths relative to [includeDir], the form [buildlogic.apigen.cpp.CppApiReader] and [ApiHeaders] take. */
internal fun relativeHeaders(files: Collection<java.io.File>, includeDir: java.io.File) =
    files.map { it.relativeTo(includeDir).invariantSeparatorsPath }.toSortedSet()

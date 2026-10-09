package buildlogic.apigen

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Paths

/**
 * `api-headers.txt`: globs under the library's include dir, grouped by the C module their API is generated into, and
 * the `-regex` declarations left out of it ([skipped]).
 */
internal class ApiHeaders(val modules: Map<String, List<String>>, val skipped: Set<String> = emptySet()) {
    /** The module whose globs match [header] (relative to the include dir), or null for a non-API header. */
    fun moduleOf(header: String): String? = modules.entries.firstOrNull { (_, globs) -> globs.any { !it.startsWith("!") && matches(it, header) } }?.key

    /** The headers under [includeDir] the globs select, less the `!glob` ones, as paths relative to it. */
    fun headers(includeDir: File): Set<String> {
        val (excludes, globs) = modules.values.flatten().partition { it.startsWith("!") }
        // A plain path needs no walk of the library's sources.
        val (patterns, paths) = globs.partition { g -> g.any { it in "*?[{" } }
        val walked = if (patterns.isEmpty()) emptySequence() else
            includeDir.walkTopDown().filter { it.isFile }.map { it.relativeTo(includeDir).invariantSeparatorsPath }.filter { h -> patterns.any { matches(it, h) } }
        return (paths.filter { includeDir.resolve(it).isFile } + walked).filterNot { h -> excludes.any { matches(it.drop(1), h) } }.toSortedSet()
    }

    private fun matches(glob: String, header: String) = FileSystems.getDefault().getPathMatcher("glob:$glob").matches(Paths.get(header))

    companion object {
        private val SECTION = Regex("""\[(.+)]""")

        fun parse(text: String): ApiHeaders {
            val modules = LinkedHashMap<String, MutableList<String>>()
            val skipped = LinkedHashSet<String>()
            var module: String? = null
            text.lineSequence().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }.forEach { line ->
                if (line.startsWith("-")) { skipped += line.drop(1); return@forEach }
                SECTION.matchEntire(line)?.let { module = it.groupValues[1]; modules[module!!] = ArrayList() }
                    ?: modules.getValue(checkNotNull(module) { "api-headers.txt: '$line' is outside a [module] section" }).add(line)
            }
            return ApiHeaders(modules, skipped)
        }
    }
}

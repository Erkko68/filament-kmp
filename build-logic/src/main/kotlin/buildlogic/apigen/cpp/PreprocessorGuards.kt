package buildlogic.apigen.cpp

import java.io.File

/**
 * The `#if` conditions a header declares things under, e.g. Engine's `#if UTILS_HAS_THREADING` API. The host's AST
 * sees what they hide on other targets; the C API guards those forwarders with the same condition.
 */
internal class PreprocessorGuards(private val includeDir: File) {
    private val headers = HashMap<String, List<String?>>()

    /** The conditions around [line] of [header], joined with `&&`, or null outside any `#if`. */
    fun at(header: String, line: Int): String? = headers.getOrPut(header) { scan(includeDir.resolve(header).readLines()) }.getOrNull(line - 1)

    private fun scan(lines: List<String>): List<String?> {
        // null for #ifdef/#ifndef: include guards and feature tests, which the C API includes the same way.
        val open = ArrayList<String?>()
        return lines.map { raw ->
            val line = raw.substringBefore("//").trim()
            val text = if (line.startsWith("#")) line.drop(1).trimStart() else ""
            val directive = text.substringBefore(' ')
            val condition = text.removePrefix(directive).trim()
            when (directive) {
                "if" -> open += condition
                "ifdef", "ifndef" -> open += null
                // ponytail: #elif takes only its own condition, not the earlier branches' negation.
                "elif" -> open[open.lastIndex] = condition
                "else" -> open[open.lastIndex] = open.last()?.let { "!($it)" }
                "endif" -> open.removeAt(open.lastIndex)
            }
            open.filterNotNull().let { c -> if (c.size == 1) c[0] else c.joinToString(" && ") { "($it)" }.ifEmpty { null } }
        }
    }
}

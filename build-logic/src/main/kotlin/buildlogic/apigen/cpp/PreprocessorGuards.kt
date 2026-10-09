package buildlogic.apigen.cpp

import java.io.File

/**
 * The `#if` conditions a header declares things under, e.g. `#ifdef JPH_ENABLE_ASSERTS` validation. The AST
 * sees what they hide in other configurations; the C API guards those forwarders with the same condition.
 */
internal class PreprocessorGuards(private val includeDir: File) {
    private val headers = HashMap<String, List<String?>>()

    /** The conditions around [line] of [header], joined with `&&`, or null outside any `#if`. */
    fun at(header: String, line: Int): String? = headers.getOrPut(header) { scan(includeDir.resolve(header).readLines()) }.getOrNull(line - 1)

    private fun scan(lines: List<String>): List<String?> {
        val open = ArrayList<String?>()
        return lines.mapIndexed { i, raw ->
            val line = raw.substringBefore("//").trim()
            val text = if (line.startsWith("#")) line.drop(1).trimStart() else ""
            val directive = text.substringBefore(' ')
            val condition = text.removePrefix(directive).trim()
            when (directive) {
                "if" -> open += condition
                "ifdef" -> open += "defined($condition)"
                // An include guard (`#ifndef X` then `#define X`) is no configuration's condition.
                "ifndef" -> open += "!defined($condition)".takeUnless {
                    open.isEmpty() && lines.drop(i + 1).firstOrNull { it.isNotBlank() }?.replace(Regex("\\s+"), "") == "#define$condition"
                }
                // ponytail: #elif takes only its own condition, not the earlier branches' negation.
                "elif" -> open[open.lastIndex] = condition
                "else" -> open[open.lastIndex] = open.last()?.let { "!($it)" }
                "endif" -> open.removeAt(open.lastIndex)
            }
            open.filterNotNull().let { c -> if (c.size == 1) c[0] else c.joinToString(" && ") { "($it)" }.ifEmpty { null } }
        }
    }
}

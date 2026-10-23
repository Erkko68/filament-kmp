package buildlogic.apigen.cpp

import java.io.File

/**
 * The `#if` conditions a header declares things under, e.g. `#ifndef NDEBUG` validation. The AST
 * sees what they hide in other configurations; the C API guards those forwarders with the same condition.
 */
internal class PreprocessorGuards(private val includeDir: File) {
    private val headers = HashMap<String, List<String?>>()

    /** The conditions around [line] of [header], joined with `&&`, or null outside any `#if`. */
    fun at(header: String, line: Int): String? = headers.getOrPut(header) { scan(includeDir.resolve(header).readLines()) }.getOrNull(line - 1)

    private fun scan(lines: List<String>): List<String?> {
        val open = ArrayList<Branch>()
        return lines.mapIndexed { i, raw ->
            val line = raw.substringBefore("//").trim()
            val text = if (line.startsWith("#")) line.drop(1).trimStart() else ""
            val directive = text.substringBefore(' ')
            val condition = text.removePrefix(directive).trim()
            when (directive) {
                "if" -> open += Branch(condition)
                "ifdef" -> open += Branch("defined($condition)")
                // An include guard (`#ifndef X` then `#define X`) is no configuration's condition.
                "ifndef" -> open += Branch("!defined($condition)".takeUnless {
                    open.isEmpty() && lines.drop(i + 1).firstOrNull { it.isNotBlank() }?.replace(Regex("\\s+"), "") == "#define$condition"
                })
                "elif" -> open[open.lastIndex] = open.last().next(condition)
                "else" -> open[open.lastIndex] = open.last().next(null)
                "endif" -> open.removeAt(open.lastIndex)
            }
            open.mapNotNull { it.condition }.let { c -> if (c.size == 1) c[0] else c.joinToString(" && ") { "($it)" }.ifEmpty { null } }
        }
    }

    /** A branch of an open `#if`: taken when its [own] condition holds (none for `#else`) and the [earlier] ones' don't. */
    private class Branch(val own: String?, val earlier: List<String> = emptyList()) {
        val condition = (earlier.map { "!($it)" } + listOfNotNull(own)).joinToString(" && ").ifEmpty { null }

        fun next(own: String?) = Branch(own, earlier + listOfNotNull(this.own))
    }
}

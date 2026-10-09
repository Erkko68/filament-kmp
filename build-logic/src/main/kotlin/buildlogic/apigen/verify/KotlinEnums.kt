package buildlogic.apigen.verify

import java.math.BigInteger

/** Reads the entries of hand-written Kotlin enums, as far as checking them against C++'s needs. */
internal object KotlinEnums {
    private val ANNOTATIONS = Regex("""^(?:@\w+(?:\([^)]*\))?\s*)+""")
    private val ENTRY = Regex("""^(\w+)\s*(?:\((.*)\))?""", RegexOption.DOT_MATCHES_ALL)
    private val NUMBER = Regex("""(-?)(?:0[xX]([0-9a-fA-F_]+)|(\d[\d_]*))[uUL]*""")

    /** Each `{ … }` body in [scope], which joins the bodies of every declaration of a name. */
    fun bodies(scope: String): List<String> {
        val bodies = ArrayList<String>()
        var depth = 0
        var open = 0
        scope.forEachIndexed { i, c ->
            if (c == '{' && depth++ == 0) open = i
            if (c == '}' && --depth == 0) bodies += scope.substring(open + 1, i)
        }
        return bodies
    }

    /**
     * What the wrappers pass C for each entry of an enum's [body]: the number it's constructed with when every entry
     * has one (`POSITION(0)`), else its ordinal.
     */
    fun values(body: String): Map<String, BigInteger> {
        // ponytail: brackets inside string arguments aren't told apart; no enum has any.
        val entries = split(body, ',', stopAt = ';').map { ANNOTATIONS.replace(it.trim(), "") }.filter { it.isNotEmpty() }
            .mapNotNull { ENTRY.find(it) }.map { it.groupValues[1] to split(it.groupValues[2], ',').firstOrNull()?.trim()?.let(::number) }
        val explicit = entries.isNotEmpty() && entries.all { it.second != null }
        return entries.mapIndexed { i, (name, value) -> name to if (explicit) value!! else i.toBigInteger() }.toMap()
    }

    private fun number(text: String) = NUMBER.matchEntire(text)?.destructured?.let { (minus, hex, decimal) ->
        (if (hex.isNotEmpty()) BigInteger(hex.replace("_", ""), 16) else BigInteger(decimal.replace("_", ""))).let { if (minus.isEmpty()) it else -it }
    }

    /** [text] cut at each [separator] outside brackets, up to a [stopAt] outside them. */
    private fun split(text: String, separator: Char, stopAt: Char? = null): List<String> {
        val parts = ArrayList<String>()
        var depth = 0
        var start = 0
        for ((i, c) in text.withIndex()) when {
            c in "({[" -> depth++
            c in ")}]" -> depth--
            depth == 0 && c == stopAt -> return parts + text.substring(start, i)
            depth == 0 && c == separator -> { parts += text.substring(start, i); start = i + 1 }
        }
        return parts + text.substring(start)
    }
}

package buildlogic.apigen.gaps

/** The hand-written Kotlin API ([sources], comments aside), searched for what it declares of the C++ one. */
internal class KotlinDeclarations(private val sources: String) {
    /** The bodies of the Kotlin declarations any of the C++ [names] maps to; null when Kotlin declares none. */
    fun scope(names: Collection<String>): String? = names.mapNotNull(::scope).ifEmpty { null }?.joinToString("\n")

    /** Whether [scope] declares the C++ [qualified] name's last segment. */
    fun status(scope: String, qualified: String) =
        if (Regex("""\b${qualified.substringAfterLast("::")}\b""").containsMatchIn(scope)) "declared" else "undeclared"

    /** Each segment's declarations inside the previous one's (namespaces have none), or anywhere when the owner doesn't nest it. */
    private fun scope(qualified: String): String? {
        val segments = qualified.split("::")
        var scope = sources
        segments.forEachIndexed { i, segment ->
            val nested = bodies(scope, segment)
            scope = when {
                nested.isNotEmpty() -> nested
                i < segments.lastIndex -> scope
                else -> bodies(sources, segment).ifEmpty { return null }
            }
        }
        return scope
    }

    /** The brace-matched bodies of every declaration of [name] in [text]. */
    private fun bodies(text: String, name: String) = Regex("""\b(?:class|object|interface)\s+$name\b""").findAll(text).joinToString("\n") { m ->
        // The body's brace follows the header: constructor parameters aside, no other declaration starts before it.
        val header = StringBuilder()
        var parens = 0
        var open = m.range.last + 1
        while (open < text.length && (parens > 0 || text[open] != '{')) {
            when (text[open]) {
                '(' -> parens++
                ')' -> parens--
                else -> if (parens == 0) header.append(text[open])
            }
            open++
        }
        if (open == text.length || '}' in header || NEXT_DECLARATION.containsMatchIn(header)) return@joinToString ""
        var depth = 0
        for (i in open until text.length) when (text[i]) {
            '{' -> depth++
            '}' -> if (--depth == 0) return@joinToString text.substring(open, i + 1)
        }
        ""
    }

    private companion object {
        val NEXT_DECLARATION = Regex("""\b(?:class|object|interface|fun|val|var|typealias)\b""")
    }
}

package buildlogic.apigen.report

import java.io.File

/** The hand-written Kotlin API ([sources], comments aside), searched for what it declares of the C++ one. */
internal class KotlinDeclarations(private val sources: String) {
    /** The `{ … }` bodies of the Kotlin declarations any of the C++ [names] maps to; null when Kotlin declares none. */
    fun scope(names: Collection<String>): List<String>? = names.mapNotNull(::scope).flatten().ifEmpty { null }

    /** Whether [scope] declares the C++ [qualified] name's last segment. */
    fun status(scope: List<String>, qualified: String) =
        Regex("""\b${qualified.substringAfterLast("::")}\b""").let { name -> if (scope.any(name::containsMatchIn)) "declared" else "undeclared" }

    /**
     * The defaults the Kotlin class the C++ [qualified] name maps to gives its properties, as Kotlin spells each, by
     * name: its primary constructor's and its body's, a nested class's aside. Null when Kotlin declares no such class.
     */
    fun defaults(qualified: String): Map<String, String>? = find(qualified, ::defaults)?.first()

    private fun scope(qualified: String): List<String>? = find(qualified, ::bodies)

    /**
     * What [declarations] finds of the last segment in the bodies of the ones before it (namespaces have none), each
     * inside the previous one's, or anywhere when the owner doesn't nest it.
     */
    private fun <T> find(qualified: String, declarations: (String, String) -> List<T>): List<T>? {
        val segments = qualified.split("::")
        var scope = listOf(sources)
        for (segment in segments.dropLast(1)) scope = scope.flatMap { bodies(it, segment) }.ifEmpty { scope }
        return scope.flatMap { declarations(it, segments.last()) }.ifEmpty { declarations(sources, segments.last()) }.ifEmpty { null }
    }

    /** The property defaults of each class [name] in [text]: its primary constructor's parameters, then its body's lines. */
    private fun defaults(text: String, name: String) = Regex("""\bclass\s+$name\b""").findAll(text).map { m ->
        val defaults = LinkedHashMap<String, String>()
        var i = m.range.last + 1
        while (i < text.length) {
            val c = text[i]
            val header = text.substring(m.range.last + 1, i).trim()
            when {
                // The primary constructor's: right after the name, its type parameters, or `constructor`.
                c == '(' -> {
                    val close = closing(text, i)
                    if (header.isEmpty() || header.endsWith(">") || header.endsWith("constructor")) split(text.substring(i + 1, close), ',')
                        .forEach { PROPERTY.matchEntire(it.trim())?.let { p -> defaults[p.groupValues[1]] = p.groupValues[2].trim() } }
                    i = close
                }
                c == '{' -> {
                    var depth = 0
                    // Its own lines: the ones a nested declaration's braces don't enclose.
                    text.substring(i + 1, closing(text, i)).lines().forEach { line ->
                        if (depth == 0) PROPERTY.matchEntire(line.trim())?.let { p -> defaults.putIfAbsent(p.groupValues[1], p.groupValues[2].trim()) }
                        depth += line.count { it == '{' } - line.count { it == '}' }
                    }
                    break
                }
                // No body: the declaration ends with its line.
                c == '}' || (c == '\n' && text.getOrNull((i until text.length).firstOrNull { !text[it].isWhitespace() } ?: text.length) !in BODY_OR_SUPERTYPE) -> break
            }
            i++
        }
        defaults
    }.toList()

    /** The index of the bracket closing the one at [open]. */
    private fun closing(text: String, open: Int): Int {
        // ponytail: brackets in string literals count; none of the classes this reads has one.
        var depth = 0
        for (i in open until text.length) when (text[i]) {
            '(', '{', '[' -> depth++
            ')', '}', ']' -> if (--depth == 0) return i
        }
        return text.length
    }

    /** The brace-matched bodies of every declaration of [name] in [text]. */
    private fun bodies(text: String, name: String) = Regex("""\b(?:class|object|interface)\s+$name\b""").findAll(text).mapNotNull { m ->
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
        if (open == text.length || '}' in header || NEXT_DECLARATION.containsMatchIn(header)) return@mapNotNull null
        var depth = 0
        for (i in open until text.length) when (text[i]) {
            '{' -> depth++
            '}' -> if (--depth == 0) return@mapNotNull text.substring(open, i + 1)
        }
        null
    }.toList()

    private companion object {
        val NEXT_DECLARATION = Regex("""\b(?:class|object|interface|fun|val|var|typealias)\b""")
        val BODY_OR_SUPERTYPE = setOf('{', ':')
        /** A property with a default: annotations and modifiers, `var` or `val`, the name, any type, `=` and the default. */
        val PROPERTY = Regex("""(?:@\w+(?:\([^)]*\))?\s+)*(?:(?:private|internal|public|protected|override|open|lateinit|const)\s+)*(?:var|val)\s+(\w+)\s*(?::[^=]*)?=(?!=)\s*(.+)""", RegexOption.DOT_MATCHES_ALL)
    }
}

/** [text] cut at each [separator] outside brackets, up to a [stopAt] outside them. */
internal fun split(text: String, separator: Char, stopAt: Char? = null): List<String> {
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

private val MAIN = Regex("""/src/\w*Main/""")
private val COMMENT = Regex("""//[^\n]*|/\*[\s\S]*?\*/""")

/** The hand-written Kotlin API under [kotlin]: main sources outside capi, comments aside (tests call externals directly). */
internal fun kotlinApiSources(kotlin: File) =
    kotlin.walkTopDown().filter { f -> f.extension == "kt" && f.invariantSeparatorsPath.let { MAIN.containsMatchIn(it) && "/capi/" !in it } }
        .sortedBy { it.path }.joinToString("\n") { COMMENT.replace(it.readText(), "") }

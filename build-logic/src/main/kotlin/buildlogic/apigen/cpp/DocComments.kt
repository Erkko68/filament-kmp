package buildlogic.apigen.cpp

import java.io.File

/**
 * Declarations' documentation comments as their headers spell them, less the comment markers: the text and its
 * Doxygen commands, which a C comment keeps.
 */
internal class DocComments(private val includeDir: File) {
    private val headers = HashMap<String, ByteArray>()

    /** The comment clang attached to [node], declared in [header] (relative to the include dir), or null for none. */
    fun of(node: Map<*, *>, header: String?): String? {
        val range = node.children().firstOrNull { it["kind"] == "FullComment" }?.get("range") as? Map<*, *> ?: return null
        val source = headers.getOrPut(header ?: return null) { includeDir.resolve(header).readBytes() }
        // Byte offsets, both inclusive: from after the opening marker to the last character before any `*/`.
        val (begin, end) = listOf("begin", "end").map { ((range[it] as? Map<*, *>)?.get("offset") as? Number)?.toInt() ?: return null }
        if (begin > end || end >= source.size) return null
        val lines = String(source, begin, end - begin + 1, Charsets.UTF_8).lines().mapIndexed { i, line ->
            (if (i == 0) line.removePrefix(" ") else MARKER.replaceFirst(line, "")).trimEnd()
        }
        return lines.dropWhile { it.isEmpty() }.dropLastWhile { it.isEmpty() }.joinToString("\n").ifEmpty { null }
    }

    private companion object {
        /** What a comment's line after the first opens with: indentation, then ` * ` or `/// `. */
        val MARKER = Regex("""^[ \t]*(?://[/!]?<?|\*)? ?""")
    }
}

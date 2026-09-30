package buildlogic.apigen.cpp

import java.io.File
import java.util.IdentityHashMap

/** Where a declaration is: its [header] relative to the include dir (null outside it), and its [line]. */
internal class Location(val header: String?, val line: Int)

/**
 * The location of each declaration in a clang JSON document. Clang prints a location's file and line only when they
 * differ from the previous location printed, so this replays the document in print order.
 */
internal object DeclarationFiles {
    /** Declaration node → its location. */
    fun of(document: Map<*, *>, includeDir: File): Map<Map<*, *>, Location> {
        val prefix = includeDir.absolutePath + File.separator
        val files = IdentityHashMap<Map<*, *>, Location>()
        var current: String? = null
        var line = 0
        fun walk(value: Any?) {
            when (value) {
                is Map<*, *> -> {
                    (value["file"] as? String)?.let { current = it }
                    (value["line"] as? Number)?.let { line = it.toInt() }
                    for ((key, child) in value) {
                        // The includer's file, not a change of the current one.
                        if (key == "includedFrom") continue
                        walk(child)
                        if (key == "loc") files[value] = Location(current?.takeIf { it.startsWith(prefix) }?.removePrefix(prefix)?.replace(File.separatorChar, '/'), line)
                    }
                }
                is List<*> -> value.forEach(::walk)
            }
        }
        walk(document)
        return files
    }
}

package buildlogic.apigen.cpp

import java.io.File
import java.util.IdentityHashMap

/**
 * The header each declaration in a clang JSON document comes from. Clang prints a location's file only when it
 * differs from the previous location printed, so this replays the document in print order.
 */
internal object DeclarationFiles {
    /** Declaration node → its header relative to [includeDir], or null outside it. */
    fun of(document: Map<*, *>, includeDir: File): Map<Map<*, *>, String?> {
        val prefix = includeDir.absolutePath + File.separator
        val files = IdentityHashMap<Map<*, *>, String?>()
        var current: String? = null
        fun walk(value: Any?) {
            when (value) {
                is Map<*, *> -> {
                    (value["file"] as? String)?.let { current = it }
                    for ((key, child) in value) {
                        // The includer's file, not a change of the current one.
                        if (key == "includedFrom") continue
                        walk(child)
                        if (key == "loc") files[value] = current?.takeIf { it.startsWith(prefix) }?.removePrefix(prefix)?.replace(File.separatorChar, '/')
                    }
                }
                is List<*> -> value.forEach(::walk)
            }
        }
        walk(document)
        return files
    }
}

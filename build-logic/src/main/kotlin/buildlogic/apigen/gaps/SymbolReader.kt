package buildlogic.apigen.gaps

import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File

/** One global symbol, demangled: nm's [type] letter (`T` defined code, `U` undefined, ...) and its [name]. */
internal class Symbol(val type: Char, val name: String)

/** Reads the global symbols of object files and archives with the host's `nm`. */
internal class SymbolReader(private val exec: ExecOperations) {
    fun read(files: Collection<File>): List<Symbol> {
        if (files.isEmpty()) return emptyList()
        val out = ByteArrayOutputStream()
        exec.exec {
            commandLine(listOf("nm", "-gC") + files.map { it.path })
            standardOutput = out
        }
        return out.toString().lineSequence()
            .mapNotNull { LINE.matchEntire(it) }
            .map { Symbol(it.groupValues[1].single(), it.groupValues[2]) }
            .toList()
    }

    private companion object {
        // "<address> T name", or "<padding> U name" for undefined symbols.
        val LINE = Regex("""\s*[0-9a-fA-F]*\s+([A-Za-z])\s+(.+)""")
    }
}

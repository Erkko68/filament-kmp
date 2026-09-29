package buildlogic.apigen.gaps

import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream

/** Demangles C++ symbols with the host's `c++filt`, the same demangler `nm -C` uses. */
internal class Demangler(private val exec: ExecOperations) {
    fun demangle(symbols: Collection<String>): List<String> {
        if (symbols.isEmpty()) return emptyList()
        val out = ByteArrayOutputStream()
        exec.exec {
            commandLine("c++filt")
            standardInput = symbols.joinToString("\n").byteInputStream()
            standardOutput = out
        }
        return out.toString().lines().filter { it.isNotEmpty() }
    }
}

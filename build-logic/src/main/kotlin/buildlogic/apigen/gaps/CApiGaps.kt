package buildlogic.apigen.gaps

import buildlogic.apigen.externals.ExternalFunctionParser
import java.io.File

/** Fila* functions the C headers declare but no common `@ExternalSymbolName` external binds. */
internal object CApiGaps {
    private val FUNCTION = Regex("""\b(Fila\w+_\w+)\s*\(""")

    fun find(headers: Collection<File>, sources: Collection<File>): Set<String> {
        val bound = sources.mapNotNull(ExternalFunctionParser::parse)
            .flatMap { it.functions }
            .mapTo(HashSet()) { it.symbol }
        return headers.asSequence()
            .flatMap { header -> FUNCTION.findAll(header.readText()).map { it.groupValues[1] } }
            .filterNot { it in bound }
            .toSortedSet()
    }
}

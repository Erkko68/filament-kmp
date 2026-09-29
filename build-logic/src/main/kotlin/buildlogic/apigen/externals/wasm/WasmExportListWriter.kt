package buildlogic.apigen.externals.wasm

import buildlogic.apigen.externals.ExternalFunction

/** Writes a wasm runtime's `-sEXPORTED_FUNCTIONS` file: every external's `_symbol`, plus the allocator. */
object WasmExportListWriter {
    fun write(functions: List<ExternalFunction>): String =
        (functions.map { "_${it.symbol}" } + "_malloc" + "_free").joinToString("\n", postfix = "\n")
}

package buildlogic.apigen.externals.wasm

import buildlogic.apigen.c.CFunction

/** Writes a wasm runtime's `-sEXPORTED_FUNCTIONS` file: every external's `_symbol`, plus the allocator. */
object WasmExportListWriter {
    fun write(functions: List<CFunction>): String =
        (functions.map { "_${it.name}" } + "_malloc" + "_free").joinToString("\n", postfix = "\n")
}

package buildlogic.apigen.externals.wasm

import buildlogic.apigen.externals.ExternalFunction

/**
 * Writes the `--post-js` tables web/src/wasm/fila-globals.js reads to adapt exports for the js target:
 * which return a C bool (wasm hands back 0/1), which return a float (js keeps it as a double), and
 * which take a 64-bit int (js passes a Kotlin Long object, wasm wants a BigInt).
 */
object WasmTypeTableWriter {
    fun write(functions: List<ExternalFunction>): String {
        fun table(name: String, filter: (ExternalFunction) -> Boolean) =
            "Module['$name'] = [${functions.filter(filter).joinToString(",") { "'${it.symbol}'" }}];\n"
        return table("filaBoolExports") { it.returnType == "Boolean" } +
            table("filaF32Exports") { it.returnType == "Float" } +
            table("filaI64Exports") { fn -> fn.params.any { it.type == "Long" } }
    }
}

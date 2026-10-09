package buildlogic.apigen.externals.wasm

import buildlogic.apigen.c.CFunction

/**
 * Writes the `--post-js` tables (`<prefix>BoolExports`, …) the runtime's globals script reads to adapt exports for the js target:
 * which return a C bool (wasm hands back 0/1), which return a float (js keeps it as a double), and
 * which take a 64-bit int (js passes a Kotlin Long object, wasm wants a BigInt).
 */
object WasmTypeTableWriter {
    fun write(functions: List<CFunction>, prefix: String): String {
        val p = prefix.lowercase()
        fun table(name: String, filter: (CFunction) -> Boolean) =
            "Module['$name'] = [${functions.filter(filter).joinToString(",") { "'${it.name}'" }}];\n"
        return table("${p}BoolExports") { it.returns == "bool" } +
            table("${p}F32Exports") { it.returns == "f32" } +
            table("${p}I64Exports") { fn -> fn.params.any { it.second == "i64" } }
    }
}

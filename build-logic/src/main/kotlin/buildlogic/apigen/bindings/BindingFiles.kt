package buildlogic.apigen.bindings

import buildlogic.apigen.KotlinBindings
import buildlogic.apigen.bindings.jni.JniForwarderWriter
import buildlogic.apigen.bindings.wasm.WasmArityTableWriter
import buildlogic.apigen.bindings.wasm.WasmExportListWriter
import buildlogic.apigen.bindings.wasm.WasmTypeTableWriter
import buildlogic.apigen.c.CFunction
import java.io.File

/** The glue of the functions each of [headers] declares, by path under the output dir. */
internal fun bindingFiles(headers: Map<String, List<CFunction>>, bindings: KotlinBindings, prefix: String): Map<String, String> {
    val out = LinkedHashMap<String, String>()
    headers.forEach { (header, functions) ->
        // The externals' file, and the facade class its functions compile to on the JVM.
        val name = File(header).nameWithoutExtension
        val jvmClass = "${bindings.packages.getValue(header.substringBefore('/'))}.${name.replaceFirstChar { it.uppercase() }}Kt"
        out["jni/$name.c"] = JniForwarderWriter.write("$name.kt", jvmClass, functions, headers.keys)
    }

    val runtimeOf = bindings.wasmRuntimes.flatMap { (runtime, modules) -> modules.map { it to runtime } }.toMap()
    check(runtimeOf.keys == bindings.packages.keys) { "wasmRuntimes must cover the modules ${bindings.packages.keys}, once each" }
    val byRuntime = headers.entries.groupBy({ runtimeOf.getValue(it.key.substringBefore('/')) }) { it.value }
        .mapValues { (_, functions) -> functions.flatten().distinctBy { it.name }.sortedBy { it.name } }
    byRuntime.forEach { (runtime, functions) ->
        out["wasm/$runtime-exports.txt"] = WasmExportListWriter.write(functions)
        out["wasm/$runtime-types.js"] = WasmTypeTableWriter.write(functions, prefix)
    }
    out["webTest/WasmArities.kt"] = WasmArityTableWriter.write(byRuntime, bindings.wasmPackage)
    return out
}

package buildlogic.apigen.c

import buildlogic.apigen.cpp.forEachAstDocument
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.File

/**
 * One C function as it crosses the ABI: each of [params] by name, and [returns], as `ptr`, `i32`, `i64`, `f32`, `f64`,
 * `bool` or `void`.
 */
class CFunction(val name: String, val returns: String, val params: List<Pair<String, String>>)

/**
 * `api-manifest.json`: the functions each C header declares, generated and hand-written alike, by the header's path
 * under `c/`. The Kotlin stages read it in place of the headers.
 */
class CManifest(val headers: Map<String, List<CFunction>>) {
    /** One function per line, so a library bump's diff reads by function. */
    fun json() = headers.entries.joinToString(",\n", "{\n", "\n}\n") { (header, functions) ->
        functions.joinToString(",\n", "${JsonOutput.toJson(header)}: [\n", "\n]") {
            JsonOutput.toJson(mapOf("name" to it.name, "returns" to it.returns, "params" to it.params.map { (name, abi) -> listOf(name, abi) }))
        }
    }

    companion object {
        private val ABI = mapOf(
            "void" to "void", "_Bool" to "bool", "float" to "f32", "double" to "f64",
            "int32_t" to "i32", "uint32_t" to "i32", "int64_t" to "i64", "uint64_t" to "i64",
        )

        fun parse(text: String) = CManifest((JsonSlurper().parseText(text) as Map<*, *>).entries.associate { (header, functions) ->
            header as String to (functions as List<*>).map { f ->
                f as Map<*, *>
                CFunction(f["name"] as String, f["returns"] as String, (f["params"] as List<*>).map { p -> (p as List<*>).let { it[0] as String to it[1] as String } })
            }
        })

        /** What clang sees [headers] (under [cDir]) declare with a [prefix]ed name. */
        internal fun read(headers: List<File>, cDir: File, prefix: String, workDir: File): CManifest {
            val unit = workDir.apply { mkdirs() }.resolve("manifest.c").apply { writeText(headers.joinToString("") { "#include \"${it.absolutePath}\"\n" }) }
            val typedefs = HashMap<String, String>()
            val functions = ArrayList<Pair<String, Map<*, *>>>()
            forEachAstDocument(listOf("clang", "-x", "c", "-std=c11"), unit, prefix, workDir) { node ->
                val name = node["name"] as? String ?: return@forEachAstDocument
                when (node["kind"]) {
                    "TypedefDecl" -> typedefs[name] = qualType(node)
                    // A filtered dump names each document's file.
                    "FunctionDecl" -> if (name.startsWith(prefix)) functions += File((node["loc"] as Map<*, *>)["file"] as String).relativeTo(cDir).invariantSeparatorsPath to node
                }
            }
            fun abi(c: String, where: String): String {
                if ('*' in c) return "ptr"
                val base = c.removePrefix("const ").trim()
                return ABI[base] ?: "i32".takeIf { base.startsWith("enum ") } ?: typedefs[base]?.let { abi(it, where) } ?: error("No ABI type for C type '$c' ($where)")
            }
            return CManifest(functions.groupBy({ it.first }) { (header, node) ->
                val name = node["name"] as String
                val where = "$name in $header"
                val params = (node["inner"] as? List<*>).orEmpty().map { it as Map<*, *> }.filter { it["kind"] == "ParmVarDecl" }
                CFunction(name, abi(qualType(node).substringBefore('('), where), params.mapIndexed { i, p -> (p["name"] as? String ?: "arg$i") to abi(qualType(p), where) })
            }.toSortedMap())
        }

        private fun qualType(node: Map<*, *>) = ((node["type"] as Map<*, *>)["qualType"] as String).trim()
    }
}

package buildlogic.capigen

import buildlogic.cppapi.CppApi
import buildlogic.cppapi.CppEnum
import buildlogic.cppapi.CppMethod
import buildlogic.cppapi.CppRecord

/**
 * Writes the C API for [api]'s exported classes: per module a `Types.h` (handles, enums, math mirrors), an
 * `Includes.hpp` of [headers], and per top-level class a header and its C++ forwarders. What can't be bridged
 * becomes a `TODO(handwritten)` comment where its declaration would be.
 */
internal class CApiWriter(private val api: CppApi, private val headers: List<String>) {
    private val bridges = CBridges(api)
    private val surface = api.surface()
    private val functionNames = HashSet<String>()

    /** Generated file text by path under `c/`. */
    fun write(): Map<String, String> {
        val files = LinkedHashMap<String, String>()
        val classes = api.records.values.filter { it.name in surface && it.exported }.groupBy { topLevel(it.name) }
        for ((top, records) in classes) {
            val declarations = StringBuilder()
            val definitions = StringBuilder()
            records.forEach { record(it, declarations, definitions) }
            if (declarations.isEmpty()) continue
            val dir = "${moduleOf(top)}/generated"
            val name = CNames.type(top)
            files["$dir/$name.h"] = header(name, "#include \"Types.h\"", declarations.toString())
            files["$dir/$name.cpp"] = "$BANNER\n#include \"Includes.hpp\"\n#include \"$name.h\"\n\nextern \"C\" {\n\n$definitions} // extern \"C\"\n"
        }
        // Types last: the forwarders decide which math types need mirrors.
        MODULES.forEach { module ->
            files["$module/generated/Types.h"] = types(module)
            files["$module/generated/Includes.hpp"] = "$BANNER\n#pragma once\n\n#include <bit>\n\n" + headers.joinToString("") { "#include <$it>\n" }
        }
        return files
    }

    private fun record(record: CppRecord, fileDeclarations: StringBuilder, definitions: StringBuilder) {
        val declarations = StringBuilder()
        val self = CNames.type(record.name)
        val handle = bridges.isHandle(record.name) && !record.template
        if (handle && record.defaultConstructible && record.destructible) {
            function("${self}_create", "$self* ${self}_create(void)", "return reinterpret_cast<$self*>(new ${record.name}());", declarations, definitions)
            function("${self}_destroy", "void ${self}_destroy($self* self)", "delete reinterpret_cast<${record.name}*>(self);", declarations, definitions)
        }
        // C can't upcast: a base's functions take the base's handle.
        val bases = record.bases.mapNotNull { api.records[it] }.filter { base ->
            handle && bridges.isHandle(base.name) && !base.template && base.methods.any { it.isPublic && it.isApi && !it.isDeprecated }
        }
        bases.map { it.name }.forEach { base ->
            val name = "${self}_as${CNames.type(base).removePrefix("Fila")}"
            function(name, "${CNames.type(base)}* $name($self* self)", "return reinterpret_cast<${CNames.type(base)}*>(static_cast<$base*>(reinterpret_cast<${record.name}*>(self)));", declarations, definitions)
        }
        // Deprecated API isn't bound at all.
        val methods = record.methods.filter { it.isPublic && it.isApi && !it.isDeprecated }
            // A const overload and its non-const twin take the same C arguments; the non-const one covers both.
            .groupBy { m -> m.name to m.params.map { it.type.spelling } }.values.map { twins -> twins.firstOrNull { !it.isConst } ?: twins.first() }
        methods.groupBy { it.name }.values.forEach { overloads ->
            overloads.zip(suffixes(overloads)).forEach { (method, suffix) ->
                method(method, CNames.function(record.name, method.name, suffix), declarations, definitions)
            }
        }
        if (declarations.isNotEmpty()) fileDeclarations.appendLine("// ${record.name}").append(declarations).appendLine()
    }

    private fun method(method: CppMethod, name: String, declarations: StringBuilder, definitions: StringBuilder) {
        try {
            if (method.mangled == null) throw Unsupported("member of a class template")
            if (!bridges.isHandle(method.owner)) throw Unsupported("member of a value struct")
            val returns = bridges.of(method.returns)
            val params = method.params.mapIndexed { i, p -> p.name.ifEmpty { "arg$i" }.let { if (it == "self") "self_" else it } to bridges.of(p.type) }
            val const = if (method.isConst) "const " else ""
            val self = if (method.isStatic) null else "$const${CNames.type(method.owner)}* self"
            val target = if (method.isStatic) "${method.owner}::" else "reinterpret_cast<$const${method.owner}*>(self)->"
            val call = "$target${method.name}(${params.joinToString { (n, b) -> b.toCpp(n) }})"
            val cParams = (listOfNotNull(self) + params.map { (n, b) -> "${b.c} $n" }).joinToString().ifEmpty { "void" }
            val body = if (returns.c == "void") "$call;" else "return ${returns.toC(call)};"
            function(name, "${returns.c} $name($cParams)", body, declarations, definitions)
        } catch (e: Unsupported) {
            declarations.appendLine("// TODO(handwritten) $name: ${signature(method)}\n//     ${e.message}")
        }
    }

    private fun function(name: String, signature: String, body: String, declarations: StringBuilder, definitions: StringBuilder) {
        if (!functionNames.add(name)) throw Unsupported("$name is already generated for another overload")
        declarations.appendLine("$signature;")
        definitions.appendLine("$signature {\n    $body\n}\n")
    }

    /** Overloads take the short type names of the parameters after the ones they all share. */
    private fun suffixes(overloads: List<CppMethod>): List<String> {
        if (overloads.size == 1) return listOf("")
        val names = overloads.map { m -> m.params.map { shortName(it.type.spelling) } }
        val shared = (0 until names.minOf { it.size }).takeWhile { i -> names.all { it[i] == names[0][i] } }.size
        val tails = names.map { it.drop(shared).joinToString("_") }
        return if (tails.toSet().size == tails.size) tails else names.map { it.joinToString("_") }
    }

    private fun shortName(spelling: String) = shape(spelling).let { s ->
        s.base.substringBefore('<').substringAfterLast("::").replace(' ', '_') + "Ptr".repeat(maxOf(0, s.indirection.size - 1))
    }

    private fun types(module: String): String {
        val body = StringBuilder()
        if (module == "filament") {
            body.appendLine("typedef int32_t FilaEntity;\n")
            bridges.mathTypes.forEach { body.appendLine(mathMirror(it)) }
            body.appendLine()
        }
        surface.filter { it in api.records && moduleOf(it) == module && bridges.isHandle(it) }.sorted()
            .forEach { body.appendLine("typedef struct ${CNames.type(it)} ${CNames.type(it)};") }
        surface.mapNotNull { api.enums[it] }.filter { moduleOf(it.name) == module }.sortedBy { it.name }
            .forEach { body.appendLine().append(enum(it)) }
        val include = if (module == "filament") "#include <stdbool.h>\n#include <stddef.h>\n#include <stdint.h>"
            else "#include \"../../filament/generated/Types.h\""
        return header("${module}_Types", include, body.toString())
    }

    private fun enum(enum: CppEnum): String {
        val name = CNames.type(enum.name)
        // C enumerators are ints; wider enums become their underlying type and macros.
        return if (enum.constants.all { it.second.bitLength() < 32 }) {
            "// ${enum.name}\ntypedef enum $name {\n" +
                enum.constants.joinToString("") { (c, v) -> "    ${CNames.enumConstant(enum.name, c)} = $v,\n" } + "} $name;\n"
        } else {
            "// ${enum.name}\ntypedef ${enum.underlying ?: "uint64_t"} $name;\n" +
                enum.constants.joinToString("") { (c, v) -> "#define ${CNames.enumConstant(enum.name, c)} (($name)${v}ULL)\n" }
        }
    }

    private fun header(name: String, include: String, body: String): String {
        val guard = "FILA_GENERATED_${name.uppercase().replace('-', '_')}_H"
        return "$BANNER\n#ifndef $guard\n#define $guard\n\n$include\n\n#ifdef __cplusplus\nextern \"C\" {\n#endif\n\n" +
            "$body\n#ifdef __cplusplus\n}\n#endif\n\n#endif // $guard\n"
    }

    private fun topLevel(record: String) =
        generateSequence(record) { it.substringBeforeLast("::", "").takeIf { parent -> parent in api.records } }.last()

    private fun signature(m: CppMethod) = (if (m.isStatic) "static " else "") + "${m.returns.spelling} ${m.owner}::${m.name}(" +
        m.params.joinToString { "${it.type.spelling} ${it.name}".trim() } + ")" + if (m.isConst) " const" else ""

    companion object {
        val MODULES = listOf("filament", "filamat", "filament-utils", "gltfio")
        private const val BANNER = "// Generated by generateCApi from Filament's public headers; do not edit."
    }
}

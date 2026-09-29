package buildlogic.apigen.c

import buildlogic.apigen.ApiHeaders
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppEnum
import buildlogic.apigen.cpp.CppField
import buildlogic.apigen.cpp.CppMethod
import buildlogic.apigen.cpp.CppParam
import buildlogic.apigen.cpp.CppRecord

/**
 * Writes the C API for what [headers] declare: per module a `Types.h` (handles, enums, math mirrors) and an
 * `Includes.hpp` of [headers], and per top-level class or namespace a header and its C++ forwarders. What can't be
 * bridged becomes a `TODO(handwritten)` comment where its declaration would be.
 */
internal class CApiWriter(private val api: CppApi, private val apiHeaders: ApiHeaders, private val headers: Set<String>) {
    private val bridges = CBridges(api)
    private val surface = api.surface(headers)
    private val baseModule = apiHeaders.modules.keys.first()
    private val functionNames = HashSet<String>()
    private val files = LinkedHashMap<String, Section>()

    /** One generated header and its forwarders. */
    private class Section(val module: String) {
        val declarations = StringBuilder()
        val definitions = StringBuilder()
    }

    /** Generated file text by path under `c/`. */
    fun write(): Map<String, String> {
        api.apiRecords(headers).groupBy { topLevel(it.name) }.forEach { (top, records) ->
            val section = section(moduleOf(api.records.getValue(top).header), CNames.type(top))
            records.forEach { record(it, section) }
        }
        api.apiFunctions(headers).groupBy { it.owner }.forEach { (namespace, functions) ->
            // filament's own functions would otherwise land in a bare "Fila.h".
            val section = section(moduleOf(functions.first().header), CNames.type(namespace).takeIf { it != "Fila" } ?: "FilaFilament")
            section.declarations.appendLine("// $namespace")
            overloads(functions, section) { CNames.function(namespace, it.name, suffix = "") }
            section.declarations.appendLine()
        }
        val out = LinkedHashMap<String, String>()
        files.filterValues { it.declarations.isNotBlank() }.forEach { (name, section) ->
            val dir = "${section.module}/generated"
            out["$dir/$name.h"] = header(name, "#include \"Types.h\"", section.declarations.toString())
            out["$dir/$name.cpp"] = "$BANNER\n#include \"Includes.hpp\"\n#include \"$name.h\"\n\nextern \"C\" {\n\n${section.definitions}} // extern \"C\"\n"
        }
        // Types last: the forwarders decide which math types need mirrors.
        apiHeaders.modules.keys.forEach { module ->
            out["$module/generated/Types.h"] = types(module)
            out["$module/generated/Includes.hpp"] = includes(module)
        }
        return out
    }

    private fun section(module: String, name: String) = files.getOrPut(name) { Section(module) }

    private fun record(record: CppRecord, file: Section) {
        val section = Section(file.module)
        val self = CNames.type(record.name)
        // Constructing is only worth it for something to call: filamat::Enums, say, only holds static templates.
        val instanceMethods = record.methods.any { it.isPublic && it.isApi && !it.isStatic && !it.isDeprecated }
        val cpp = bridges.cpp(record.name)
        if (!bridges.uninstantiated(record) && record.allocatable && (instanceMethods || bridges.isValue(record.name))) {
            record.constructors.zip(suffixes(record.constructors)).forEach { (params, suffix) ->
                val name = CNames.function(record.name, "create", suffix)
                emit(name, "$cpp(${spelled(params)})", section) {
                    val bridged = bridge(params)
                    "$self* $name(${cParams(null, bridged)})" to "return fila::c(new $cpp(${args(bridged)}));"
                }
            }
            if (record.constructors.isNotEmpty() && record.destructible) {
                emit("${self}_destroy", "~${record.name}()", section) {
                    "void ${self}_destroy($self* self)" to "delete fila::cpp(self);"
                }
            }
        }
        if (!bridges.uninstantiated(record)) {
            // C can't upcast: a base's functions take the base's handle.
            record.bases.mapNotNull { api.records[it] }.filter { it !in bridges.twins(record) }.filter { base ->
                !bridges.uninstantiated(base) && base.methods.any { it.isPublic && it.isApi && !it.isDeprecated }
            }.forEach { base ->
                val baseType = CNames.type(base.name)
                val name = "${self}_as${baseType.removePrefix("Fila")}"
                val baseCpp = bridges.cpp(base.name)
                emit(name, "static_cast<$baseCpp*>", section) {
                    "$baseType* $name($self* self)" to "return fila::c(static_cast<$baseCpp*>(fila::cpp(self)));"
                }
            }
        }
        // Deprecated API isn't bound at all, nor overloads only literals can call.
        val methods = (record.methods + bridges.twins(record).flatMap { it.methods }).filter { m -> m.isPublic && m.isApi && !m.isDeprecated && m.params.none { it.type.decl in LITERAL_ONLY } }
            // A const overload and its non-const twin take the same C arguments; the non-const one covers both.
            .groupBy { m -> m.name to m.params.map { it.type.spelling } }.values.map { twins -> twins.firstOrNull { !it.isConst } ?: twins.first() }
        overloads(methods, section) { CNames.function(record.name, it.name, suffix = "") }
        if (bridges.isValue(record.name) && !bridges.uninstantiated(record)) {
            (bridges.twins(record).flatMap { it.fields } + record.fields).filter { it.isPublic && !it.isDeprecated }.forEach { field(record, it, section) }
        }
        if (section.declarations.isEmpty()) return
        file.declarations.appendLine("// ${record.name}").append(section.declarations).appendLine()
        file.definitions.append(section.definitions)
    }

    /** Emits [functions], suffixing overloads of one name with the types that tell them apart. */
    private fun overloads(functions: List<CppMethod>, section: Section, baseName: (CppMethod) -> String) {
        functions.groupBy { it.name }.values.forEach { group ->
            group.zip(suffixes(group.map { it.params })).forEach { (function, suffix) ->
                val name = baseName(function) + if (suffix.isEmpty()) "" else "_$suffix"
                emit(name, signature(function), section) { forwarder(function, name) }
            }
        }
    }

    private fun forwarder(method: CppMethod, name: String): Pair<String, String> {
        if (method.mangled == null && !bridges.instantiated(method.owner)) throw Unsupported("member of a class template")
        val returns = bridges.result(method.returns)
        val params = bridge(method.params)
        val const = if (method.isConst) "const " else ""
        val self = if (method.isStatic) null else "$const${CNames.type(method.owner)}* self"
        val target = if (method.isStatic) "${bridges.cpp(method.owner)}::" else "fila::cpp(self)->"
        val call = "$target${method.name}(${args(params)})"
        return when {
            returns.out -> "void $name(${cParams(self, params, out = "${returns.c}* out")})" to returns.store(call, "out")
            returns.c == "void" -> "void $name(${cParams(self, params)})" to "$call;"
            else -> "${returns.c} $name(${cParams(self, params, trailing(returns))})" to "return ${returns.convert(call)};"
        }
    }

    /** A value struct's field: a getter, and a setter unless the struct would keep the caller's pointer. */
    private fun field(record: CppRecord, field: CppField, section: Section) {
        val self = CNames.type(record.name)
        val cpp = "${field.type.spelling} ${record.name}::${field.name}"
        val accessor = field.name.replaceFirstChar(Char::uppercaseChar)
        // A method of the same name (Box::getCenter) already reads it.
        val getter = "${self}_get$accessor"
        if (getter !in functionNames) emit(getter, cpp, section) {
            val result = bridges.result(field.type, lvalue = true)
            val read = "fila::cpp(self)->${field.name}"
            if (result.out) "void $getter(const $self* self, ${result.c}* out)" to result.store(read, "out")
            else "${result.c} $getter(${cParams("const $self* self", emptyList(), trailing(result))})" to "return ${result.convert(read)};"
        }
        val setter = "${self}_set$accessor"
        if (setter !in functionNames) emit(setter, cpp, section) {
            if (bridges.borrowsPointer(field.type)) throw Unsupported("${field.type.spelling}: the struct would keep the caller's pointer")
            val param = bridges.param(field.type)
            "void $setter(${cParams("$self* self", listOf("value" to param))})" to "fila::cpp(self)->${field.name} = ${param.convert("value")};"
        }
    }

    /** Writes one function, or a `TODO(handwritten)` naming [cpp] when [build] can't bridge it. */
    private fun emit(name: String, cpp: String, section: Section, build: () -> Pair<String, String>) {
        try {
            val (signature, body) = build()
            if (!functionNames.add(name)) throw Unsupported("$name is already generated for another overload")
            section.declarations.appendLine("$signature;")
            section.definitions.appendLine("$signature {\n    $body\n}\n")
        } catch (e: Unsupported) {
            section.declarations.appendLine("// TODO(handwritten) $name: $cpp\n//     ${e.message}")
        }
    }

    private fun bridge(params: List<CppParam>) =
        params.mapIndexed { i, p -> p.name.ifEmpty { "arg$i" }.let { if (it in RESERVED) "${it}_" else it } to bridges.param(p.type) }

    private fun cParams(self: String?, params: List<Pair<String, CBridge>>, out: String? = null) =
        (listOfNotNull(self) + params.flatMap { (n, b) -> listOf("${b.c} $n") + b.extra.map { (c, suffix) -> "$c $n$suffix" } } + listOfNotNull(out)).joinToString().ifEmpty { "void" }

    private fun trailing(result: CBridge) = result.extra.joinToString { (c, name) -> "$c $name" }.ifEmpty { null }

    private fun args(params: List<Pair<String, CBridge>>) = params.joinToString { (n, b) -> b.convert(n) }

    /** Overloads take the short type names of the parameters after the ones they all share. */
    private fun suffixes(overloads: List<List<CppParam>>): List<String> {
        if (overloads.size <= 1) return overloads.map { "" }
        val names = overloads.map { params -> params.map { shortName(it.type.spelling) } }
        val shared = (0 until names.minOf { it.size }).takeWhile { i -> names.all { it[i] == names[0][i] } }.size
        val tails = names.map { it.drop(shared).joinToString("_") }
        return if (tails.toSet().size == tails.size) tails else names.map { it.joinToString("_") }
    }

    private fun shortName(spelling: String) = shape(spelling).let { s ->
        s.base.substringBefore('<').substringAfterLast("::").replace(' ', '_') + "Ptr".repeat(maxOf(0, s.indirection.size - 1))
    }

    private fun types(module: String): String {
        val body = StringBuilder()
        if (module == baseModule) {
            body.appendLine("typedef int32_t FilaEntity;\n")
            bridges.mathTypes.forEach { body.appendLine(mathMirror(it)) }
            body.appendLine()
        }
        moduleRecords(module).forEach { body.appendLine("typedef struct ${CNames.type(it.name)} ${CNames.type(it.name)};") }
        surface.mapNotNull { api.enums[it] }.filter { moduleOf(it.header) == module }.sortedBy { it.name }
            .forEach { body.appendLine().append(enum(it)) }
        // Callbacks take the handles and enums above.
        if (module == baseModule) bridges.callbackTypes.values.joinTo(body, "\n", prefix = "\n", postfix = "\n")
        val include = if (module == baseModule) "#include <stdbool.h>\n#include <stddef.h>\n#include <stdint.h>"
            else "#include \"../../$baseModule/generated/Types.h\""
        return header("${module}_Types", include, body.toString())
    }

    /** The C++ headers, and the `fila::cpp`/`fila::c` overloads between the module's C types and C++'s. */
    private fun includes(module: String): String {
        val text = StringBuilder("$BANNER\n#pragma once\n\n")
        if (module == baseModule) text.append("#include <bit>\n#include <string_view>\n\n").append(headers.joinToString("") { "#include <$it>\n" })
        else text.append("#include \"../../$baseModule/generated/Includes.hpp\"\n")
        text.append("#include \"Types.h\"\n\nnamespace fila {\n\n")
        if (module == baseModule) {
            text.append(PRELUDE)
            bridges.mathTypes.forEach { text.appendLine("FILA_TYPE(${CNames.type(it)}, $it)") }
        }
        moduleRecords(module).filter { it.accessible && !bridges.uninstantiated(it) }
            .forEach { text.appendLine("FILA_TYPE(${CNames.type(it.name)}, ${bridges.cpp(it.name)})") }
        return text.append("\n} // namespace fila\n").toString()
    }

    private fun moduleRecords(module: String) = surface.mapNotNull { api.records[it] }.filter { moduleOf(it.header) == module }.sortedBy { it.name }

    private fun enum(enum: CppEnum): String {
        val name = CNames.type(enum.name)
        // C enumerators are ints; wider enums become a fixed-width integer and macros.
        val wide = bridges.wideEnumType(enum)
        return if (wide == null) {
            "// ${enum.name}\ntypedef enum $name {\n" +
                enum.constants.joinToString("") { (c, v) -> "    ${CNames.enumConstant(enum.name, c)} = $v,\n" } + "} $name;\n"
        } else {
            "// ${enum.name}\ntypedef $wide $name;\n" +
                enum.constants.joinToString("") { (c, v) -> "#define ${CNames.enumConstant(enum.name, c)} (($name)${v}ULL)\n" }
        }
    }

    private fun header(name: String, include: String, body: String): String {
        val guard = "FILA_GENERATED_${name.uppercase().replace('-', '_')}_H"
        return "$BANNER\n#ifndef $guard\n#define $guard\n\n$include\n\n#ifdef __cplusplus\nextern \"C\" {\n#endif\n\n" +
            "$body\n#ifdef __cplusplus\n}\n#endif\n\n#endif // $guard\n"
    }

    /** API headers go to their section's module; the types they use from elsewhere, to the first. */
    private fun moduleOf(header: String?) = header?.let(apiHeaders::moduleOf) ?: baseModule

    private fun topLevel(record: String) =
        generateSequence(record) { it.substringBeforeLast("::", "").takeIf { parent -> parent in api.records } }.last()

    private fun spelled(params: List<CppParam>) = params.joinToString { "${it.type.spelling} ${it.name}".trim() }

    private fun signature(m: CppMethod) = (if (m.isStatic) "static " else "") + "${m.returns.spelling} ${m.owner}::${m.name}(" +
        spelled(m.params) + ")" + if (m.isConst) " const" else ""

    private companion object {
        const val BANNER = "// Generated by generateCApi from Filament's public headers; do not edit."
        val PRELUDE = """
            |// C and C++ types naming one object (a handle, a math mirror): cpp() and c() convert pointers between them.
            |#define FILA_TYPE(C, ...) \
            |    inline __VA_ARGS__* cpp(C* p) { return reinterpret_cast<__VA_ARGS__*>(p); } \
            |    inline const __VA_ARGS__* cpp(const C* p) { return reinterpret_cast<const __VA_ARGS__*>(p); } \
            |    inline C* c(__VA_ARGS__* p) { return reinterpret_cast<C*>(p); } \
            |    inline const C* c(const __VA_ARGS__* p) { return reinterpret_cast<const C*>(p); }
            |
            |// C's array as the FixedCapacityVector<T> the callee takes; element(i) makes each T.
            |template<typename F>
            |struct Items {
            |    uint32_t count;
            |    F element;
            |    template<typename T>
            |    operator utils::FixedCapacityVector<T>() const {
            |        auto v = utils::FixedCapacityVector<T>::with_capacity(count);
            |        for (uint32_t i = 0; i < count; i++) v.push_back(T(element(i)));
            |        return v;
            |    }
            |};
            |
            |template<typename F>
            |Items<F> items(uint32_t count, F element) { return { count, element }; }
            |
            |// Stores up to capacity of items into C's array; returns how many there are.
            |template<typename V, typename F>
            |uint32_t copy(const V& items, uint32_t capacity, F store) {
            |    for (uint32_t i = 0; i < capacity && i < items.size(); i++) store(items[i], i);
            |    return uint32_t(items.size());
            |}
            |
            |// StaticString only has a literal constructor. Everything taking one copies it (builderMakeName).
            |inline utils::StaticString staticString(const char* s) {
            |    static_assert(sizeof(utils::StaticString) == sizeof(std::string_view));
            |    return std::bit_cast<utils::StaticString>(std::string_view(s));
            |}
            |
            |""".trimMargin()
        // Names the forwarders declare themselves.
        val RESERVED = setOf("self", "out")
    }
}

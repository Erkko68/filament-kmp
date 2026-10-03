package buildlogic.apigen.c

import buildlogic.apigen.ApiHeaders
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppEnum
import buildlogic.apigen.cpp.CppField
import buildlogic.apigen.cpp.CppMethod
import buildlogic.apigen.cpp.CppParam
import buildlogic.apigen.cpp.CppRecord
import buildlogic.apigen.cpp.CppType

/**
 * Writes the C API for what [headers] declare: per module a `Types.h` (handles, enums, math mirrors) and an
 * `Includes.hpp` of [headers], and per top-level class or namespace a header and its C++ forwarders. What can't be
 * bridged becomes a `TODO(handwritten)` comment where its declaration would be; for one [manual] writes by hand, a note it's done.
 */
internal class CApiWriter(private val api: CppApi, private val apiHeaders: ApiHeaders, private val headers: Set<String>, private val manual: Set<String> = emptySet()) {
    private val bridges = CBridges(api)
    private val rules = BindingRules(api, bridges)
    private val surface = api.surface(headers)
    private val baseModule = apiHeaders.modules.keys.first()
    private val functionNames = HashSet<String>()
    /** The enums C declares and the public constants of the records it binds: what the Kotlin API declares by hand. */
    val enums get() = surface.mapNotNull { api.enums[it] }.filter { api.skipReason(it.name) == null }.sortedBy { it.name }
    fun skipped(name: String) = api.skipReason(name) != null
    /** The names code outside spells [name] by: Texture::InternalFormat for backend::TextureFormat. */
    fun aliasesOf(name: String): Set<String> = generateSequence(setOf(name)) { names ->
        (names + api.aliases.filterValues { it.decl in names }.keys).takeIf { it.size > names.size }
    }.last()
    val constants get() = api.publicConstants.filter { it.substringBeforeLast("::") in surface && !api.skipsConstant(it) }.sorted()
    /** After [write]: each C function the C++ API calls for, bound or not. */
    val bindings = ArrayList<Binding>()

    /** C function [c] for the C++ [cpp]; [gap] is null once bound (generated or in manual/), else `todo: …` or `skipped: …`. */
    class Binding(val c: String, val cpp: String, val gap: String?)
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
        check(bridges.unusedFunctionInstantiations.isEmpty()) { "FUNCTION_INSTANTIATIONS lists unknown templates: ${bridges.unusedFunctionInstantiations}" }
        val out = LinkedHashMap<String, String>()
        files.filterValues { it.declarations.isNotBlank() }.forEach { (name, section) ->
            val dir = "${section.module}/generated"
            out["$dir/$name.h"] = header(name, "#include \"Types.h\"", section.declarations.toString())
            // Only TODOs and skip notes: nothing to forward.
            if (section.definitions.isNotEmpty()) out["$dir/$name.cpp"] = "$BANNER\n#include \"Includes.hpp\"\n#include \"$name.h\"\n\nextern \"C\" {\n\n${section.definitions}} // extern \"C\"\n"
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
        val cpp = bridges.cpp(record.name)
        if (rules.creates(record)) {
            val (skipped, constructors) = record.constructors.partition { api.skipReason(it) != null }
            skipped.forEach {
                section.declarations.appendLine("// skipped ${record.name}(${spelled(it)})" + reason(api.skipReason(it), ""))
                bindings += Binding(CNames.function(record.name, "create"), "${record.name}(${spelled(it)})", "skipped: ${api.skipReason(it)}")
            }
            constructors.zip(suffixes(constructors.map { ctor -> ctor.map { it.type.spelling } })).forEach { (params, suffix) ->
                val name = CNames.function(record.name, "create", suffix)
                emit(name, "$cpp(${spelled(params)})", section) {
                    val bridged = bridge(params)
                    "$self* $name(${cParams(null, bridged)})" to "return fila::c(new $cpp(${args(bridged)}));"
                }
            }
        }
        if (rules.destroys(record)) {
            emit("${self}_destroy", "~${record.name}()", section) {
                "void ${self}_destroy($self* self)" to "delete fila::cpp(self);"
            }
        }
        rules.upcasts(record).forEach { base ->
            val baseType = CNames.type(base.name)
            val name = "${self}_as${baseType.removePrefix("Fila")}"
            val baseCpp = bridges.cpp(base.name)
            emit(name, "${record.name} as ${base.name}", section) {
                "$baseType* $name($self* self)" to "return fila::c(static_cast<$baseCpp*>(fila::cpp(self)));"
            }
        }
        val (skipped, methods) = rules.methods(record).partition { api.skipReason(it, record.name) != null }
        skipped.forEach {
            section.declarations.appendLine("// skipped ${signature(it)}" + reason(api.skipReason(it, record.name), "${record.name}::${it.name}"))
            bindings += Binding(CNames.function(record.name, it.name), signature(it), "skipped: ${api.skipReason(it, record.name)}")
        }
        overloads(methods, section) { CNames.function(record.name, it.name, suffix = "") }
        rules.fields(record).forEach { f ->
            api.skipReason(f, record.name)?.let {
                section.declarations.appendLine("// skipped ${record.name}::${f.name}" + reason(it, "${record.name}::${f.name}"))
                bindings += Binding("${CNames.type(record.name)}_get${f.name.replaceFirstChar(Char::uppercaseChar)}", "${f.type.spelling} ${record.name}::${f.name}", "skipped: $it")
            } ?: field(record, f, section)
        }
        if (section.declarations.isEmpty()) return
        file.declarations.appendLine("// ${record.name}").append(section.declarations).appendLine()
        file.definitions.append(section.definitions)
    }

    /** A skip note's reason, unless it's the member's own entry. */
    private fun reason(reason: String?, member: String) = reason?.takeIf { it != member }?.let { ": $it" } ?: ""

    /** A function, or a template's instantiation with the [templateArguments] its call spells. */
    private class Overload(val method: CppMethod, val template: CppMethod?, val templateArguments: List<String> = emptyList()) {
        /** The types telling it apart: its parameters', and a template's result when only that depends on the arguments. */
        val types = method.params.map { it.type.spelling } +
            listOfNotNull(method.returns.spelling.takeIf { template?.returns?.kind == CppType.Kind.TEMPLATE_PARAMETER })
    }

    /** Emits [functions], suffixing overloads of one name with the types that tell them apart. */
    private fun overloads(functions: List<CppMethod>, section: Section, baseName: (CppMethod) -> String) {
        functions.groupBy { it.name }.values.forEach { all ->
            val (templates, plain) = all.partition { it.isTemplate }
            val (listed, unlisted) = templates.partition { bridges.functionInstantiations(it) != null }
            val group = plain.map { Overload(it, null) } +
                listed.flatMap { t -> bridges.functionInstantiations(t)!!.map { (m, arguments) -> Overload(m, t, arguments) } }
            group.zip(suffixes(group.map { it.types })).forEach { (overload, suffix) ->
                val name = baseName(overload.method) + if (suffix.isEmpty()) "" else "_$suffix"
                emit(name, signature(overload.method), section, overload.method.guard) { forwarder(overload.method, name, overload.templateArguments) }
            }
            unlisted.forEach {
                section.declarations.appendLine("// TODO(handwritten) ${baseName(it)}: template ${signature(it)}\n//     function template: CBridges.FUNCTION_INSTANTIATIONS lists no instantiations")
                bindings += Binding(baseName(it), "template ${signature(it)}", "todo: FUNCTION_INSTANTIATIONS lists no instantiations")
            }
        }
    }

    private fun forwarder(method: CppMethod, name: String, templateArguments: List<String> = emptyList()): Pair<String, String> {
        if (api.records[method.owner]?.let(bridges::uninstantiated) == true) throw Unsupported("member of a class template")
        val returns = bridges.result(method.returns)
        val params = bridge(method.params)
        val const = if (method.isConst) "const " else ""
        val self = if (method.isStatic) null else "$const${CNames.type(method.owner)}* self"
        val target = if (method.isStatic) "${bridges.cpp(method.owner)}::" else "fila::cpp(self)->"
        val explicit = if (templateArguments.isEmpty()) "" else templateArguments.joinToString(prefix = "<", postfix = ">")
        val call = "$target${method.name}$explicit(${args(params)})"
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
        if (rules.getterOnly(record, field)) {
            section.declarations.appendLine("// no $setter: ${record.name} is only a result, and C would have to keep the pointer")
            bindings += Binding(setter, cpp, "skipped: ${record.name} is only a result, and C would have to keep the pointer")
            return
        }
        if (setter !in functionNames) emit(setter, cpp, section) {
            if (bridges.borrowsPointer(field.type)) throw Unsupported("${field.type.spelling}: the struct would keep the caller's pointer")
            val param = bridges.param(field.type)
            "void $setter(${cParams("$self* self", listOf("value" to param))})" to param.assign("fila::cpp(self)->${field.name}", "value")
        }
    }

    /**
     * Writes one function, or a `TODO(handwritten)` naming [cpp] when [build] can't bridge it. A [guard]ed one exists on
     * every target, and panics where the header's condition hides the C++ it calls.
     */
    private fun emit(name: String, cpp: String, section: Section, guard: String? = null, build: () -> Pair<String, String>) {
        try {
            val (signature, body) = build()
            if (!functionNames.add(name)) throw Unsupported("$name is already generated for another overload")
            section.declarations.appendLine("$signature;")
            val guarded = if (guard == null) "    $body" else "#if $guard\n    $body\n#else\n    fila::unavailable(\"$name\");\n#endif"
            section.definitions.appendLine("$signature {\n$guarded\n}\n")
            bindings += Binding(name, cpp, null)
        } catch (e: Unsupported) {
            // By hand as itself, or as one function per case (`_aux_float2`, … for a std::variant).
            val handwritten = manual.filter { it == name || it.startsWith("${name}_") }
            val note = if (handwritten.isNotEmpty()) "handwritten in manual/" else "TODO(handwritten)"
            section.declarations.appendLine("// $note $name: $cpp\n//     ${e.message}")
            if (handwritten.isEmpty()) bindings += Binding(name, cpp, "todo: ${e.message}") else handwritten.forEach { bindings += Binding(it, cpp, null) }
        }
    }

    private fun bridge(params: List<CppParam>) =
        params.mapIndexed { i, p -> p.name.ifEmpty { "arg$i" }.let { if (it in RESERVED) "${it}_" else it } to bridges.param(p.type) }

    private fun cParams(self: String?, params: List<Pair<String, CBridge>>, out: String? = null) =
        (listOfNotNull(self) + params.flatMap { (n, b) -> listOf("${b.c} $n") + b.extra.map { (c, suffix) -> "$c $n$suffix" } } + listOfNotNull(out)).joinToString().ifEmpty { "void" }

    private fun trailing(result: CBridge) = result.extra.joinToString { (c, name) -> "$c $name" }.ifEmpty { null }

    private fun args(params: List<Pair<String, CBridge>>) = params.joinToString { (n, b) -> b.convert(n) }

    /** Overloads take the short type names of the parameters after the ones they all share. */
    private fun suffixes(overloads: List<List<String>>): List<String> {
        if (overloads.size <= 1) return overloads.map { "" }
        val names = overloads.map { types -> types.map(::shortName) }
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

    /** The C++ headers, and the `fila::cpp`/`fila::c` overloads between the module's C types and C++'s (`FILA_TYPE`, from manual/FilaBridge.hpp). */
    private fun includes(module: String): String {
        val text = StringBuilder("$BANNER\n#pragma once\n\n")
        if (module == baseModule) text.append("#include \"../manual/FilaBridge.hpp\"\n\n").append(headers.joinToString("") { "#include <$it>\n" })
        else text.append("#include \"../../$baseModule/generated/Includes.hpp\"\n")
        text.append("#include \"Types.h\"\n\nnamespace fila {\n\n")
        if (module == baseModule) bridges.mathTypes.forEach { text.appendLine("FILA_TYPE(${CNames.type(it)}, $it)") }
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
        // Names the forwarders declare themselves.
        val RESERVED = setOf("self", "out")
    }
}

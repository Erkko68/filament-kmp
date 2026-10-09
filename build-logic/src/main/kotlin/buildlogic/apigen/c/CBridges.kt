package buildlogic.apigen.c

import buildlogic.apigen.ApiGenConfig
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppEnum
import buildlogic.apigen.cpp.CppMethod
import buildlogic.apigen.cpp.CppRecord
import buildlogic.apigen.cpp.CppType
import buildlogic.apigen.cpp.CppType.Kind

/**
 * How a C++ value crosses into C: its C spelling, and the expression converting it to the other side. [out]: a
 * result C returns through a trailing `out` pointer ([CAbi]), which [store] writes given the value and the pointer.
 */
class CBridge(
    val c: String,
    val convert: (String) -> String,
    val out: Boolean = false,
    val store: (String, String) -> String = { v, out -> "*$out = ${convert(v)};" },
    /** A parameter assigned to a field: the statement given the field and the C argument. */
    val assign: (String, String) -> String = { field, v -> "$field = ${convert(v)};" },
    /**
     * A parameter's C parameters after the first, as (type, name suffix): one C++ argument C passes in pieces. A
     * result's trailing parameters, as (type, name).
     */
    val extra: List<Pair<String, String>> = emptyList(),
    /** A typedef its C spelling needs, by C name: a callback's function pointer type. */
    val typedef: Pair<String, String>? = null,
)

private fun isArray(type: CppType) = type.spelling.trim().endsWith("]")

/** Why a declaration stays hand-written; the generator leaves a comment saying so instead of code. */
class Unsupported(reason: String) : Exception(reason)

/** Maps the model's types onto C, recording the math types the module's mirror structs must cover. */
internal class CBridges(private val api: CppApi, private val config: ApiGenConfig, private val names: CNames) {
    private val h = config.helpers
    val mathTypes = sortedSetOf<String>()
    /** Function pointer typedefs the forwarders use, by C name. */
    val callbackTypes = sortedMapOf<String, String>()

    /** [type] as a parameter: [CBridge.convert] turns the C argument into C++'s. */
    fun param(type: CppType) = of(type, result = false)

    /** [type] as a return value: [CBridge.convert] turns C++'s result into C's. [lvalue]: it outlives the call (a field). */
    fun result(type: CppType, lvalue: Boolean = false) = of(type, result = true, lvalue)

    /**
     * A record with public fields, or private ones C can create and copy (camutils' Bookmark). C holds it by pointer
     * like any other, but copies it in and out like a value.
     */
    fun isValue(record: String): Boolean = api.records.getValue(record).let { r ->
        r.fields.any { it.isPublic } || (r.fields.isNotEmpty() && creatable(r) && r.copyable) || twins(r).any { isValue(it.name) }
    }

    /** Bases with [record]'s C name: C sees one type, so it gets theirs. */
    fun twins(record: CppRecord) = record.bases.mapNotNull { api.records[it] }.filter { names.type(it.name) == names.type(record.name) }

    /** A class template C binds, or a record nested in one: C binds the instantiation the library exports. */
    fun instantiated(name: String) = instantiations(name).isNotEmpty()

    /** A template [record] C doesn't bind. */
    fun uninstantiated(record: CppRecord) = record.template && !instantiated(record.name)

    /** [ApiGenConfig.functionInstantiations] entries no template matched: stale after an upstream rename. */
    val unusedFunctionInstantiations = config.functionInstantiations.keys.toMutableSet()

    /**
     * The instantiations of the function template [method] C binds, each with the template arguments its call spells;
     * null when the table lists none.
     */
    fun functionInstantiations(method: CppMethod): List<Pair<CppMethod, List<String>>>? {
        val key = "${method.owner}::${method.name}"
        val instantiations = config.functionInstantiations[key] ?: return null
        unusedFunctionInstantiations -= key
        val parameters = method.templateParameters!!
        return instantiations.map { arguments ->
            check(parameters.containsAll(arguments.keys)) { "$key has no template parameter ${arguments.keys - parameters.toSet()}" }
            method.instantiate(arguments.mapValues { (_, a) -> argumentType(a) }) to parameters.takeWhile { it in arguments }.map { arguments.getValue(it) }
        }
    }

    /** A template argument as the model would resolve it; math types are external, as the dump skips them. */
    private fun argumentType(spelling: String) = when {
        CAbi.isBuiltin(spelling) -> CppType(spelling, null, Kind.BUILTIN)
        spelling in api.records || spelling in api.enums -> CppType(spelling, spelling, Kind.DECLARED)
        else -> CppType(spelling, spelling, Kind.EXTERNAL)
    }

    /** [decl] as C++ spells it: an instantiated template takes its arguments. */
    fun cpp(decl: String): String {
        var qualified = ""
        return decl.split("::").joinToString("::") { segment ->
            qualified = if (qualified.isEmpty()) segment else "$qualified::$segment"
            config.instantiations[qualified]?.let { "$segment<${it.values.joinToString()}>" } ?: segment
        }
    }

    /** [spelling], written in [scope], with the template parameters of the instantiations around it replaced. */
    private fun substitute(spelling: String, scope: String) = instantiations(scope).flatMap { config.instantiations.getValue(it).entries }
        .fold(spelling) { s, (parameter, argument) -> s.replace(Regex("\\b$parameter\\b"), argument) }

    private fun instantiations(name: String) = name.split("::").runningReduce { a, b -> "$a::$b" }.filter { it in config.instantiations }

    /** A record C can create, so it has one to copy a result into. */
    fun creatable(record: CppRecord) = !uninstantiated(record) && record.allocatable && record.destructible && record.constructors.isNotEmpty()

    /** A pointer to plain data (`const char*`, `void*`): a struct keeping it would outlive the caller's buffer. */
    fun borrowsPointer(type: CppType): Boolean = when {
        isArray(type) -> borrowsPointer(type.args.single())
        type.decl in api.aliases -> borrowsPointer(api.aliases.getValue(type.decl!!))
        else -> (type.kind == Kind.BUILTIN && shape(type.spelling).indirection.isNotEmpty()) || type.decl == "std::string_view" || type.decl in config.views
    }

    /** The integer typedef of an enum too wide for a C enum (whose enumerators are ints), or null. */
    fun wideEnumType(enum: CppEnum) = if (enum.constants.all { it.second.bitLength() < 32 }) null else CAbi.byValue(enum.underlying ?: "uint64_t")

    private fun of(type: CppType, result: Boolean, lvalue: Boolean = false): CBridge {
        val shape = shape(type.spelling)
        if (shape.indirection == listOf("*", "*")) return pointers(type, shape, result)
        if (shape.indirection.size > 1) throw Unsupported("${type.spelling}: pointer to pointer")
        val indirection = shape.indirection.firstOrNull()
        var target = type
        var alias: String? = null
        var lastAlias: String? = null
        while (!isArray(target) && target.decl in api.aliases) {
            alias = alias ?: target.decl
            lastAlias = target.decl
            target = api.aliases.getValue(target.decl!!)
            if (shape(target.spelling).indirection.isNotEmpty()) throw Unsupported("${type.spelling}: alias of a pointer")
        }
        if (isArray(target)) return sequence(target, DirectBridges(config, names, shape.const, indirection, result, lvalue))
        var decl = target.decl
        if (target.kind == Kind.TEMPLATE_PARAMETER) {
            val argument = config.instantiations[decl!!.substringBeforeLast("::")]?.get(decl.substringAfterLast("::"))
                ?: throw Unsupported("${type.spelling}: template parameter")
            return DirectBridges(config, names, shape.const, indirection, result, lvalue).builtin(argument)
        }
        decl = decl?.let { config.custom.canonical(it, target) { spelling -> substitute(spelling, lastAlias.orEmpty()) } }
        val bridge = DirectBridges(config, names, shape.const, indirection, result, lvalue)
        if (target.kind == Kind.FUNCTION && lastAlias != null && indirection != "&") return functionPointer(lastAlias, target, indirection == "*")
        decl?.let { config.custom.bridge(it, target, alias, bridge) }?.let { custom ->
            custom.typedef?.let { callbackTypes += it }
            return custom
        }
        return when {
            decl in config.allSequences -> sequence(target, bridge)
            decl == "std::optional" -> optional(target, bridge)
            decl == "std::function" && lastAlias != null -> function(lastAlias, target.args.single(), bridge)
            indirection == "&&" -> throw Unsupported("${type.spelling}: rvalue reference")
            target.kind == Kind.BUILTIN -> bridge.builtin(shape(target.spelling).base)
            target.kind == Kind.FUNCTION -> throw Unsupported("${type.spelling}: function type")
            decl == null -> throw Unsupported("${type.spelling}: ${target.kind.name.lowercase()}")
            decl in api.enums -> bridge.enum(cpp(decl!!), wideEnumType(api.enums.getValue(decl)))
            decl in config.scalars -> bridge.scalar(decl!!, config.scalars.getValue(decl))
            decl in config.allStrings -> bridge.string(decl!!)
            decl in config.mirrors -> bridge.math(decl!!).also { mathTypes += decl }
            decl in api.records && !api.records.getValue(decl).accessible -> throw Unsupported("${type.spelling}: not accessible")
            decl in api.records -> api.records.getValue(decl!!).let { bridge.record(cpp(decl), isValue(decl), creatable(it) && it.defaultConstructible) }
            else -> throw Unsupported("${type.spelling}: $decl")
        }
    }

    /**
     * A sequence ([ApiGenConfig.sequences], std::vector, std::array) or array. C passes an array and its count; `items` converts it to whichever
     * the callee takes. A result fills C's array up to its capacity and returns how many there are. Math elements are
     * contiguous mirrors; value records, the handles C created to copy into.
     */
    private fun sequence(type: CppType, bridge: DirectBridges): CBridge {
        if (!bridge.byValue && !bridge.result && !bridge.const && type.decl == "std::array") return updated(type)
        if (!bridge.byValue) throw Unsupported("${type.spelling}: by non-const reference")
        // std::array's second argument is its size.
        val element = type.args.first()
        val mirror = generateSequence(element) { t -> t.decl?.let(api.aliases::get) }.last().decl in config.mirrors &&
            shape(element.spelling).indirection.isEmpty()
        // A view's elements outlive it, as an lvalue's do.
        val lvalue = bridge.lvalue || bridge.indirection == "&" || type.decl in config.views
        val e = if (bridge.result) result(element, lvalue) else param(element)
        if (e.extra.isNotEmpty()) throw Unsupported("${type.spelling}: elements C passes in pieces")
        if (!bridge.result) {
            val items = if (mirror || e.c.endsWith("*")) "${e.c}${if (mirror) "" else " const*"}" else "const ${e.c}*"
            val item = { n: String -> if (mirror) "($n + i)" else "$n[i]" }
            val convert = { n: String -> "$h::items(${n}Count, [&](uint32_t i) { return ${e.convert(item(n))}; })" }
            return CBridge(items, convert, extra = listOf("uint32_t" to "Count"),
                assign = { field, v -> if (isArray(type)) "$h::assign($field, ${convert(v)});" else "$field = ${convert(v)};" })
        }
        val handles = e.out && element.decl in api.records
        val store = if (handles) e.store("x", "out[i]") else "out[i] = ${e.convert("x")};"
        return CBridge(
            "uint32_t",
            { call -> "$h::copy($call, outCapacity, [&](auto& x, uint32_t i) { $store })" },
            extra = listOf((if (handles) "${e.c}* const*" else "${e.c}*") to "out", "uint32_t" to "outCapacity"),
        )
    }

    /** A std::array the callee changes (by pointer or reference): C's array goes in, and gets the changes back. */
    private fun updated(type: CppType): CBridge {
        val element = type.args.first()
        val (p, r) = param(element) to result(element)
        if (listOf(p, r).any { it.out || it.extra.isNotEmpty() || it.c.endsWith("*") }) throw Unsupported("${type.spelling}: elements C passes by pointer")
        return CBridge("${p.c}*", { n ->
            "$h::updated(${n}Count, [&](uint32_t i) { return ${p.convert("$n[i]")}; }, [&](auto x, uint32_t i) { $n[i] = ${r.convert("x")}; })"
        }, extra = listOf("uint32_t" to "Count"))
    }

    /**
     * An array of handles or strings, or a handle C++ writes back (`destroy(T**)`): C spells it with its own names,
     * which point at the same objects.
     */
    private fun pointers(type: CppType, shape: Shape, result: Boolean): CBridge {
        val spelling = cSpelling(type.spelling)
        if (type.kind == Kind.BUILTIN) {
            CAbi.checkPointee(shape.base)
            return CBridge(spelling, { it })
        }
        val record = type.decl?.let(api.records::get)?.takeIf { it.accessible && !uninstantiated(it) }
            ?: throw Unsupported("${type.spelling}: pointer to pointer")
        val base = Regex("""(?<![\w:])${Regex.escape(shape.base)}(?![\w:])""")
        val c = base.replaceFirst(spelling, names.type(record.name))
        val cpp = base.replaceFirst(spelling, cpp(record.name))
        return CBridge(c, { "reinterpret_cast<${if (result) c else cpp}>($it)" })
    }

    /**
     * A std::optional crosses as a nullable pointer: a parameter's NULL is nullopt; a result fills `out` and returns
     * whether there was a value.
     */
    private fun optional(type: CppType, bridge: DirectBridges): CBridge {
        if (!bridge.byValue) throw Unsupported("${type.spelling}: by non-const reference")
        val value = if (bridge.result) result(type.args.single(), bridge.lvalue) else param(type.args.single())
        if (value.extra.isNotEmpty() || value.c.endsWith("*")) throw Unsupported("${type.spelling}: C passes its value by pointer")
        if (!bridge.result) return CBridge("const ${value.c}*", { n -> "$h::optional($n, [&](auto v) { return ${value.convert("v")}; })" })
        return CBridge("bool", { call -> "$h::present($call, [&](auto& v) { ${value.store("v", "out")} })" }, extra = listOf("${value.c}*" to "out"))
    }

    /**
     * A `std::function` alias C passes as a function pointer of the same shape, declared under the alias's name: its
     * arguments cross as results do. NULL is an empty function.
     */
    private fun function(alias: String, signature: CppType, bridge: DirectBridges): CBridge {
        if (bridge.result || !bridge.byValue) throw Unsupported("$alias by reference or as a result")
        val returns = signature.args.first()
        if (returns.spelling != "void") throw Unsupported("$alias: returns a value")
        val args = signature.args.drop(1).map { result(it) }
        if (args.any { it.out || it.extra.isNotEmpty() }) throw Unsupported("$alias: arguments C takes in pieces")
        val name = names.type(alias)
        callbackTypes[name] = "typedef void (*$name)(${args.joinToString { it.c }.ifEmpty { "void" }});"
        val params = args.indices.joinToString { "auto a$it" }
        return CBridge(name, { n ->
            "$n ? ${cpp(alias)}([=]($params) { $n(${args.withIndex().joinToString { (i, a) -> a.convert("a$i") }}); }) : nullptr"
        })
    }

    /**
     * A function pointer alias, or a pointer to a function type alias ([pointer]), whose parameters are all C types: C
     * declares the same pointer type under the alias's name.
     */
    private fun functionPointer(alias: String, type: CppType, pointer: Boolean): CBridge {
        if (!pointer && "(*" !in NULLABILITY.replace(type.spelling, "").replace(" ", "")) throw Unsupported("${type.spelling}: function type")
        if (type.args.any { !CAbi.isBuiltin(shape(it.spelling).base) }) throw Unsupported("${type.spelling}: takes C++ types")
        val name = names.type(alias)
        val params = type.args.drop(1).joinToString { cSpelling(it.spelling) }.ifEmpty { "void" }
        callbackTypes[name] = "typedef ${cSpelling(type.args.first().spelling)} (*$name)($params);"
        return CBridge(name, { it })
    }
}

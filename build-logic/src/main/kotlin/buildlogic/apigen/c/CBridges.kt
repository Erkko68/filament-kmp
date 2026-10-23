package buildlogic.apigen.c

import buildlogic.apigen.Bridges
import buildlogic.apigen.RefCounting
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppEnum
import buildlogic.apigen.cpp.CppMethod
import buildlogic.apigen.cpp.CppRecord
import buildlogic.apigen.cpp.CppType
import buildlogic.apigen.cpp.CppType.Kind

/** Maps the model's types onto C, recording the math types the module's mirror structs must cover. */
internal class CBridges(
    private val api: CppApi, private val config: Bridges, private val names: CNames, private val templates: Templates, private val records: Records,
) {
    private val h = names.helpers
    private val refs = config.refs
    val mathTypes = sortedSetOf<String>()
    /** Function pointer typedefs the forwarders use, by C name. */
    val callbackTypes = sortedMapOf<String, String>()

    /** [type] as a parameter: [CBridge.convert] turns the C argument into C++'s. */
    fun param(type: CppType) = of(type, result = false)

    /**
     * [type] as the value a field setter stores. The field keeps it past the call, so a C string it would only borrow
     * is interned (`Bridge.hpp`'s `intern`); any other borrowed pointer can't be kept.
     */
    fun fieldParam(type: CppType): CBridge {
        val param = param(type)
        if (!records.borrowsPointer(type)) return param
        if (shape(param.c).let { it.base != "char" || it.indirection != listOf("*") }) throw Unsupported("${type.spelling}: the struct would keep the caller's pointer")
        return CBridge("const char*", param.convert, assign = { field, v -> "$field = $v ? decltype($field)($h::intern($v)) : decltype($field)();" })
    }

    /** [type] as a return value: [CBridge.convert] turns C++'s result into C's. [lvalue]: it outlives the call (a field). */
    fun result(type: CppType, lvalue: Boolean = false) = of(type, result = true, lvalue)

    /** The integer typedef of an enum too wide for a C enum (whose enumerators are ints), or null. */
    fun wideEnumType(enum: CppEnum) = if (enum.constants.all { it.second.bitLength() < 32 }) null else CAbi.byValue(enum.underlying ?: "uint64_t")

    private fun of(type: CppType, result: Boolean, lvalue: Boolean = false): CBridge {
        val shape = shape(type.spelling)
        if (shape.indirection == listOf("*", "*")) return pointers(type, shape, result)
        if (shape.indirection.size > 1) throw Unsupported("${type.spelling}: pointer to pointer")
        var indirection = shape.indirection.firstOrNull()
        var const = shape.const
        var target = type
        var alias: String? = null
        var lastAlias: String? = null
        // An alias brings its own const and indirection: Vec3Arg is a `const Vec3`, Mat44Arg a `const Mat44 &`.
        while (!isArray(target) && target.decl in api.aliases) {
            alias = alias ?: target.decl
            lastAlias = target.decl
            target = api.aliases.getValue(target.decl!!)
            val aliased = shape(target.spelling)
            const = const || aliased.const
            if (aliased.indirection.isEmpty()) continue
            if (indirection != null || aliased.indirection.size > 1) throw Unsupported("${type.spelling}: alias of a pointer")
            indirection = aliased.indirection.single()
        }
        val bridge = DirectBridges(config, names, const, indirection, result, lvalue)
        if (isArray(target)) return sequence(target, bridge)
        if (target.kind == Kind.TEMPLATE_PARAMETER) {
            val argument = templates.argument(target.decl!!) ?: throw Unsupported("${type.spelling}: template parameter")
            return bridge.builtin(argument)
        }
        if (target.kind == Kind.FUNCTION && lastAlias != null && indirection != "&") return functionPointer(lastAlias, target, indirection == "*")
        val decl = target.decl?.let { config.custom.canonical(it, target) { spelling -> templates.substitute(spelling, lastAlias.orEmpty()) } }
        decl?.let { config.custom.bridge(it, target, alias, bridge) }?.let { custom ->
            custom.typedef?.let { callbackTypes += it }
            return custom
        }
        return when {
            decl in config.allSequences -> sequence(target, bridge)
            decl == "std::optional" -> optional(target, bridge)
            decl == "std::function" && lastAlias != null -> function(lastAlias, target.args.single(), bridge)
            refs != null && decl in refs.pointers -> ref(target, bridge)
            decl in config.results -> fallible(target, bridge)
            indirection == "&&" -> throw Unsupported("${type.spelling}: rvalue reference")
            target.kind == Kind.BUILTIN -> bridge.builtin(shape(target.spelling).base)
            target.kind == Kind.FUNCTION -> throw Unsupported("${type.spelling}: function type")
            decl == null -> throw Unsupported("${type.spelling}: ${target.kind.name.lowercase()}")
            decl in api.enums -> bridge.enum(templates.cpp(decl), wideEnumType(api.enums.getValue(decl)))
            decl in config.scalars -> bridge.scalar(decl, config.scalars.getValue(decl))
            decl in config.allStrings -> bridge.string(decl)
            decl in config.mirrors -> bridge.math(decl).also { mathTypes += decl }
            decl in api.records -> bridge.record(templates.cpp(handle(target).name), records.isValue(decl), api.records.getValue(decl).let { records.creatable(it) && it.defaultConstructible && it.copyable })
            else -> throw Unsupported("${type.spelling}: $decl")
        }
    }

    /** The record [type] names, when C has a handle for it. */
    private fun handle(type: CppType): CppRecord {
        val record = type.decl?.let(api.records::get) ?: throw Unsupported("${type.spelling}: ${type.kind.name.lowercase()}")
        if (!record.accessible) throw Unsupported("${type.spelling}: not accessible")
        if (templates.uninstantiated(record)) throw Unsupported("${type.spelling}: class template")
        return record
    }

    /**
     * A reference-counting pointer ([RefCounting.pointers]) crosses as the pointer it holds. C++ takes its own
     * reference to a parameter. A result by value hands C a reference to release (`_release`); one that outlives the
     * call (a field, a `const Ref&`) is borrowed.
     */
    private fun ref(type: CppType, bridge: DirectBridges): CBridge {
        if (!bridge.byValue) throw Unsupported("${type.spelling}: by pointer or non-const reference")
        val const = if (refs!!.pointers.getValue(type.decl!!)) "const " else ""
        val c = "$const${names.type(handle(type.args.single()).name)}*"
        return when {
            !bridge.result -> CBridge(c, { "$h::cpp($it)" })
            bridge.indirection == null && !bridge.lvalue -> CBridge(c, { "$h::c($h::retain($it))" })
            else -> CBridge(c, { "$h::c(($it).${refs.get}())" })
        }
    }

    /**
     * A result type ([Bridges.results]) is its value, or NULL (false, for a value C copies out) when it holds an
     * error, whose message is then copied into `outError` up to its capacity, NUL-terminated.
     */
    private fun fallible(type: CppType, bridge: DirectBridges): CBridge {
        if (!bridge.result || bridge.indirection != null) throw Unsupported("${type.spelling}: only as a result by value")
        val value = result(type.args.single())
        if (value.extra.isNotEmpty()) throw Unsupported("${type.spelling}: C takes its value in pieces")
        val error = listOf("char*" to "outError", "uint32_t" to "outErrorCapacity")
        if (value.out) return CBridge("bool", { call -> "$h::result($call, outError, outErrorCapacity, [&](auto& v) { ${value.store("v", "out")} return true; })" },
            extra = listOf("${value.c}*" to "out") + error)
        if (!value.c.endsWith("*")) throw Unsupported("${type.spelling}: no value says it failed")
        return CBridge(value.c, { call -> "$h::result($call, outError, outErrorCapacity, [&](auto& v) { return ${value.convert("v")}; })" }, extra = error)
    }

    /**
     * A sequence ([Bridges.sequences], std::vector, std::array) or array. C passes an array and its count;
     * `items` converts it to whichever the callee takes. A result fills C's array up to its capacity and returns how
     * many there are. Math elements are contiguous mirrors; value records, the handles C created to copy into.
     */
    private fun sequence(type: CppType, bridge: DirectBridges): CBridge {
        if (!bridge.byValue && !bridge.result && !bridge.const && type.decl == "std::array") return updated(type)
        if (!bridge.byValue) throw Unsupported("${type.spelling}: by non-const reference")
        // std::array's second argument is its size.
        val element = type.args.first()
        val resolved = generateSequence(element) { t -> t.decl?.let(api.aliases::get) }.last().decl
        val mirror = resolved in config.mirrors &&
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
        val handles = e.out && !mirror && resolved in api.records
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
        val record = type.decl?.let(api.records::get)?.takeIf { it.accessible && !templates.uninstantiated(it) }
            ?: throw Unsupported("${type.spelling}: pointer to pointer")
        val base = Regex("""(?<![\w:])${Regex.escape(shape.base)}(?![\w:])""")
        val c = base.replaceFirst(spelling, names.type(record.name))
        val cpp = base.replaceFirst(spelling, templates.cpp(record.name))
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
            "$n ? ${templates.cpp(alias)}([=]($params) { $n(${args.withIndex().joinToString { (i, a) -> a.convert("a$i") }}); }) : nullptr"
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

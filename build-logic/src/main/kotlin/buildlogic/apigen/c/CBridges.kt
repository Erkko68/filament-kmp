package buildlogic.apigen.c

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
internal class CBridge(
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
)

/** A C++ type C holds as the scalar [c]: [toCpp] builds it from a C value, [toC] reads one back. */
internal class Scalar(val c: String, val toCpp: (String) -> String, val toC: (String) -> String)

private const val STEADY = "std::chrono::steady_clock"

/** Durations and time points are nanoseconds (since the steady clock's epoch); a tribool is 0, 1, or 2 for indeterminate. */
private val SCALARS = mapOf(
    "std::chrono::nanoseconds" to Scalar("int64_t", { "std::chrono::nanoseconds($it)" }, { "($it).count()" }),
    "$STEADY::time_point" to Scalar(
        "int64_t",
        { "$STEADY::time_point(std::chrono::duration_cast<$STEADY::duration>(std::chrono::nanoseconds($it)))" },
        { "std::chrono::duration_cast<std::chrono::nanoseconds>(($it).time_since_epoch()).count()" },
    ),
    "utils::Entity::Type" to Scalar("uint32_t", { it }, { it }),
    "utils::bitset32" to Scalar("uint32_t", { "utils::bitset32($it)" }, { "($it).getValue()" }),
    "utils::tribool" to Scalar(
        "int32_t",
        { "utils::tribool(static_cast<utils::tribool::Value>($it))" },
        { "[](utils::tribool t) { return t.is_indeterminate() ? 2 : int32_t(t.is_true()); }($it)" },
    ),
)

/** The class templates' instantiations the libraries export, by template: C binds these, named without the arguments. */
private val INSTANTIATIONS = mapOf(
    "filament::camutils::Manipulator" to mapOf("FLOAT" to "float"),
    "filament::camutils::Bookmark" to mapOf("FLOAT" to "float"),
)
private fun math(vararg names: String) = names.map { "filament::math::$it" }
private val CONSTANT_TYPES = listOf("int32_t", "float", "bool")
// MaterialInstance's is_supported_parameter_t; the library exports getParameter for all but the bools.
private val READABLE_PARAMETER_TYPES = listOf("float", "int32_t", "uint32_t") +
    math("int2", "int3", "int4", "uint2", "uint3", "uint4", "float2", "float3", "float4", "mat3f", "mat4f")
private val PARAMETER_TYPES = READABLE_PARAMETER_TYPES + listOf("bool") + math("bool2", "bool3", "bool4")

private fun each(parameter: String, types: List<String>) = types.map { mapOf(parameter to it) }

/**
 * The function templates' instantiations C binds, by template: each maps template parameters to arguments; ones
 * left out take their defaults. C names them by the types that tell them apart, like overloads.
 */
private val FUNCTION_INSTANTIATIONS = mapOf(
    "filamat::MaterialBuilder::constant" to each("T", CONSTANT_TYPES),
    "filament::Material::Builder::constant" to each("T", CONSTANT_TYPES),
    "filament::Material::setDefaultParameter" to each("T", PARAMETER_TYPES),
    "filament::MaterialInstance::setParameter" to each("T", PARAMETER_TYPES),
    "filament::MaterialInstance::getParameter" to each("T", READABLE_PARAMETER_TYPES),
    "filament::MaterialInstance::setConstant" to each("T", CONSTANT_TYPES),
    "filament::MaterialInstance::getConstant" to each("T", CONSTANT_TYPES),
    "filament::RenderableManager::computeAABB" to math("float4", "half4", "float3", "half3")
        .flatMap { v -> listOf("uint16_t", "uint32_t").map { mapOf("VECTOR" to v, "INDEX" to it) } },
    "filament::geometry::TangentSpaceMesh::getAux" to each("T", math("float2", "float3", "float4", "ushort3", "ushort4")),
    "ktxreader::Ktx1Reader::toCompressedFilamentEnum" to each("T", listOf("filament::backend::CompressedPixelDataType")),
    // ColorConversion::ACCURATE, the default.
    "filament::Color::toLinear" to listOf(emptyMap()),
    "filament::Color::toSRGB" to listOf(emptyMap()),
)

private val MATH_VECTOR = Regex("filament::math::vec([234])")

private const val BACKEND = "filament::backend"
private const val PIXEL_BUFFER = "$BACKEND::PixelBufferDescriptor"
private val UPLOADS = setOf("$BACKEND::BufferDescriptor", PIXEL_BUFFER)
private val PIXEL_LAYOUT = listOf(
    "FilaPixelDataFormat" to "Format", "FilaPixelDataType" to "Type", "uint32_t" to "Alignment",
    "uint32_t" to "Left", "uint32_t" to "Top", "uint32_t" to "Stride",
)
// BufferDescriptor::Callback; size_t is exact here, Filament calls it.
private val BUFFER_CALLBACK = "FilaBufferDescriptorCallback" to "typedef void (*FilaBufferDescriptorCallback)(void* buffer, size_t size, void* user);"
private val USER_CALLBACK = "FilaCallback" to "typedef void (*FilaCallback)(void* user);"
private val ARG_CALLBACK = "FilaArgCallback" to "typedef void (*FilaArgCallback)(void* arg, void* user);"

private const val STATIC_STRING = "utils::StaticString"
private const val VECTOR = "utils::FixedCapacityVector"
private const val SLICE = "utils::Slice"
private val SEQUENCES = setOf(VECTOR, SLICE, "std::array", "std::vector")

private fun isArray(type: CppType) = type.spelling.trim().endsWith("]")

private val STRINGS = setOf("std::string_view", "std::string", "utils::CString", "utils::ImmutableCString", STATIC_STRING)

/** String types only literals convert to; overloads taking `const char*` cover them. */
internal val LITERAL_ONLY = setOf("filament::MaterialInstance::StringLiteral")

/** Why a declaration stays hand-written; the generator leaves a comment saying so instead of code. */
internal class Unsupported(reason: String) : Exception(reason)

/** Maps the model's types onto C, recording the math types the module's mirror structs must cover. */
internal class CBridges(private val api: CppApi) {
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

    /** Bases with [record]'s C name (backend::Viewport under filament::Viewport): C sees one type, so it gets theirs. */
    fun twins(record: CppRecord) = record.bases.mapNotNull { api.records[it] }.filter { CNames.type(it.name) == CNames.type(record.name) }

    /** A class template C binds, or a record nested in one: C binds the instantiation the library exports. */
    fun instantiated(name: String) = instantiations(name).isNotEmpty()

    /** A template [record] C doesn't bind. */
    fun uninstantiated(record: CppRecord) = record.template && !instantiated(record.name)

    /** [FUNCTION_INSTANTIATIONS] entries no template matched: stale after an upstream rename. */
    val unusedFunctionInstantiations = FUNCTION_INSTANTIATIONS.keys.toMutableSet()

    /**
     * The instantiations of the function template [method] C binds, each with the template arguments its call spells;
     * null when the table lists none.
     */
    fun functionInstantiations(method: CppMethod): List<Pair<CppMethod, List<String>>>? {
        val key = "${method.owner}::${method.name}"
        val instantiations = FUNCTION_INSTANTIATIONS[key] ?: return null
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
            INSTANTIATIONS[qualified]?.let { "$segment<${it.values.joinToString()}>" } ?: segment
        }
    }

    /** [spelling], written in [scope], with the template parameters of the instantiations around it replaced. */
    private fun substitute(spelling: String, scope: String) = instantiations(scope).flatMap { INSTANTIATIONS.getValue(it).entries }
        .fold(spelling) { s, (parameter, argument) -> s.replace(Regex("\\b$parameter\\b"), argument) }

    private fun instantiations(name: String) = name.split("::").runningReduce { a, b -> "$a::$b" }.filter { it in INSTANTIATIONS }

    /** A record C can create, so it has one to copy a result into. */
    fun creatable(record: CppRecord) = !uninstantiated(record) && record.allocatable && record.destructible && record.constructors.isNotEmpty()

    /** A pointer to plain data (`const char*`, `void*`): a struct keeping it would outlive the caller's buffer. */
    fun borrowsPointer(type: CppType): Boolean = when {
        isArray(type) -> borrowsPointer(type.args.single())
        type.decl in api.aliases -> borrowsPointer(api.aliases.getValue(type.decl!!))
        else -> (type.kind == Kind.BUILTIN && shape(type.spelling).indirection.isNotEmpty()) || type.decl == "std::string_view" || type.decl == SLICE
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
        if (isArray(target)) return sequence(target, Bridge(shape.const, indirection, result, lvalue))
        var decl = target.decl
        if (target.kind == Kind.TEMPLATE_PARAMETER) {
            val argument = INSTANTIATIONS[decl!!.substringBeforeLast("::")]?.get(decl.substringAfterLast("::"))
                ?: throw Unsupported("${type.spelling}: template parameter")
            return Bridge(shape.const, indirection, result, lvalue).builtin(argument)
        }
        // A math template's instantiation is one of the mirrored typedefs: vec3<float> is float3.
        MATH_VECTOR.matchEntire(decl.orEmpty())?.let { vector ->
            val element = substitute(target.spelling.substringAfter('<').substringBeforeLast('>').trim(), lastAlias.orEmpty())
            decl = "filament::math::$element${vector.groupValues[1]}"
        }
        val bridge = Bridge(shape.const, indirection, result, lvalue)
        return when {
            target.kind == Kind.FUNCTION && lastAlias != null && indirection != "&" -> functionPointer(lastAlias, target.spelling, indirection == "*")
            decl in UPLOADS -> bridge.upload(decl!!, pixels = decl == PIXEL_BUFFER).also { callbackTypes += BUFFER_CALLBACK }
            decl in SEQUENCES -> sequence(target, bridge)
            decl == "std::optional" -> optional(target, bridge)
            decl == "std::function" && lastAlias != null -> function(lastAlias, target.args.single(), bridge)
            decl == "utils::Invocable" -> bridge.invocable(target.spelling).let { (b, typedef) -> callbackTypes += typedef; b }
            indirection == "&&" -> throw Unsupported("${type.spelling}: rvalue reference")
            target.kind == Kind.BUILTIN -> bridge.builtin(shape(target.spelling).base)
            target.kind == Kind.FUNCTION -> throw Unsupported("${type.spelling}: function type")
            decl == null -> throw Unsupported("${type.spelling}: ${target.kind.name.lowercase()}")
            decl in api.enums -> bridge.enum(cpp(decl!!), wideEnumType(api.enums.getValue(decl)))
            decl in SCALARS -> bridge.scalar(decl, SCALARS.getValue(decl))
            decl in STRINGS -> bridge.string(decl)
            decl == "utils::Entity" -> bridge.entity()
            decl == "utils::EntityInstance" && alias != null -> bridge.instance(alias)
            decl!!.startsWith("filament::math::") && mathMirror(decl) != null -> bridge.math(decl).also { mathTypes += decl }
            decl in api.records && !api.records.getValue(decl).accessible -> throw Unsupported("${type.spelling}: not accessible")
            decl in api.records -> api.records.getValue(decl).let { bridge.record(cpp(decl), isValue(decl), creatable(it) && it.defaultConstructible) }
            else -> throw Unsupported("${type.spelling}: $decl")
        }
    }

    /**
     * A FixedCapacityVector, Slice, std::array or array. C passes an array and its count; `fila::items` converts it to whichever
     * the callee takes. A result fills C's array up to its capacity and returns how many there are. Math elements are
     * contiguous mirrors; value records, the handles C created to copy into.
     */
    private fun sequence(type: CppType, bridge: Bridge): CBridge {
        if (!bridge.byValue && !bridge.result && !bridge.const && type.decl == "std::array") return updated(type)
        if (!bridge.byValue) throw Unsupported("${type.spelling}: by non-const reference")
        // std::array's second argument is its size.
        val element = type.args.first()
        val mirror = generateSequence(element) { t -> t.decl?.let(api.aliases::get) }.last().decl.orEmpty().startsWith("filament::math::") &&
            shape(element.spelling).indirection.isEmpty()
        // A Slice views elements that outlive it, as an lvalue's do.
        val lvalue = bridge.lvalue || bridge.indirection == "&" || type.decl == SLICE
        val e = if (bridge.result) result(element, lvalue) else param(element)
        if (e.extra.isNotEmpty()) throw Unsupported("${type.spelling}: elements C passes in pieces")
        if (!bridge.result) {
            val items = if (mirror || e.c.endsWith("*")) "${e.c}${if (mirror) "" else " const*"}" else "const ${e.c}*"
            val item = { n: String -> if (mirror) "($n + i)" else "$n[i]" }
            val convert = { n: String -> "fila::items(${n}Count, [&](uint32_t i) { return ${e.convert(item(n))}; })" }
            return CBridge(items, convert, extra = listOf("uint32_t" to "Count"),
                assign = { field, v -> if (isArray(type)) "fila::assign($field, ${convert(v)});" else "$field = ${convert(v)};" })
        }
        val handles = e.out && element.decl in api.records
        val store = if (handles) e.store("x", "out[i]") else "out[i] = ${e.convert("x")};"
        return CBridge(
            "uint32_t",
            { call -> "fila::copy($call, outCapacity, [&](auto& x, uint32_t i) { $store })" },
            extra = listOf((if (handles) "${e.c}* const*" else "${e.c}*") to "out", "uint32_t" to "outCapacity"),
        )
    }

    /** A std::array the callee changes (by pointer or reference): C's array goes in, and gets the changes back. */
    private fun updated(type: CppType): CBridge {
        val element = type.args.first()
        val (p, r) = param(element) to result(element)
        if (listOf(p, r).any { it.out || it.extra.isNotEmpty() || it.c.endsWith("*") }) throw Unsupported("${type.spelling}: elements C passes by pointer")
        return CBridge("${p.c}*", { n ->
            "fila::updated(${n}Count, [&](uint32_t i) { return ${p.convert("$n[i]")}; }, [&](auto x, uint32_t i) { $n[i] = ${r.convert("x")}; })"
        }, extra = listOf("uint32_t" to "Count"))
    }

    /**
     * An array of handles or strings, or a handle C++ writes back (`destroy(T**)`): C spells it with its own names,
     * which point at the same objects.
     */
    private fun pointers(type: CppType, shape: Shape, result: Boolean): CBridge {
        // `const Material *const *` as C writes it: `const Material* const*`.
        val spelling = NULLABILITY.replace(type.spelling, "").replace(Regex("""\s*\*"""), "*").replace(Regex("""\*(?=\w)"""), "* ").trim()
        if (type.kind == Kind.BUILTIN) {
            CAbi.checkPointee(shape.base)
            return CBridge(spelling, { it })
        }
        val record = type.decl?.let(api.records::get)?.takeIf { it.accessible && !uninstantiated(it) }
            ?: throw Unsupported("${type.spelling}: pointer to pointer")
        val base = Regex("""(?<![\w:])${Regex.escape(shape.base)}(?![\w:])""")
        val c = base.replaceFirst(spelling, CNames.type(record.name))
        val cpp = base.replaceFirst(spelling, cpp(record.name))
        return CBridge(c, { "reinterpret_cast<${if (result) c else cpp}>($it)" })
    }

    /**
     * A std::optional crosses as a nullable pointer: a parameter's NULL is nullopt; a result fills `out` and returns
     * whether there was a value.
     */
    private fun optional(type: CppType, bridge: Bridge): CBridge {
        if (!bridge.byValue) throw Unsupported("${type.spelling}: by non-const reference")
        val value = if (bridge.result) result(type.args.single(), bridge.lvalue) else param(type.args.single())
        if (value.extra.isNotEmpty() || value.c.endsWith("*")) throw Unsupported("${type.spelling}: C passes its value by pointer")
        if (!bridge.result) return CBridge("const ${value.c}*", { n -> "fila::optional($n, [&](auto v) { return ${value.convert("v")}; })" })
        return CBridge("bool", { call -> "fila::present($call, [&](auto& v) { ${value.store("v", "out")} })" }, extra = listOf("${value.c}*" to "out"))
    }

    /**
     * A `std::function` alias C passes as a function pointer of the same shape, declared under the alias's name: its
     * arguments cross as results do. NULL is an empty function. Filament's take their user data as an argument.
     */
    private fun function(alias: String, signature: CppType, bridge: Bridge): CBridge {
        if (bridge.result || !bridge.byValue) throw Unsupported("$alias by reference or as a result")
        val returns = signature.args.first()
        if (returns.spelling != "void") throw Unsupported("$alias: returns a value")
        val args = signature.args.drop(1).map { result(it) }
        if (args.any { it.out || it.extra.isNotEmpty() }) throw Unsupported("$alias: arguments C takes in pieces")
        val name = CNames.type(alias)
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
    private fun functionPointer(alias: String, spelling: String, pointer: Boolean): CBridge {
        var bare = NULLABILITY.replace(spelling, "").replace(Regex("\\s+"), " ").trim()
        if (pointer && "(*)" !in bare) bare = bare.replaceFirst("(", "(*)(")
        if ("(*)" !in bare) throw Unsupported("$spelling: function type")
        val params = bare.substringAfter("(*)").trim().removeSurrounding("(", ")")
        if (!CAbi.isBuiltin(bare.substringBefore("(*)").trim()) || params.split(',').any { !CAbi.isBuiltin(shape(it).base) }) {
            throw Unsupported("$spelling: takes C++ types")
        }
        val name = CNames.type(alias)
        callbackTypes[name] = "typedef ${bare.replaceFirst("(*)", "(*$name)")};"
        return CBridge(name, { it })
    }

    private class Bridge(val const: Boolean, val indirection: String?, val result: Boolean, val lvalue: Boolean) {
        val byValue = indirection == null || (indirection == "&" && const)
        val c = if (const) "const " else ""

        /** A buffer C lends Filament: Kotlin's `Upload` fields, and a pixel buffer's layout between size and callback. */
        fun upload(decl: String, pixels: Boolean): CBridge {
            if (result || !(byValue || indirection == "&&")) throw Unsupported("$decl result")
            val layout = if (pixels) PIXEL_LAYOUT else emptyList()
            return CBridge(
                "void*",
                { n ->
                    val args = layout.joinToString("") { (c, suffix) -> ", " + if (c.startsWith("Fila")) "static_cast<$BACKEND::${c.removePrefix("Fila")}>($n$suffix)" else "$n$suffix" }
                    "$decl($n, ${n}Size$args, ${n}Callback, ${n}User)"
                },
                extra = listOf("uint32_t" to "Size") + layout + listOf(BUFFER_CALLBACK.first to "Callback", "void*" to "User"),
            )
        }

        /**
         * A C++ callable C passes as a function pointer and its user data: `void (*)(void* user)`, or
         * `void (*)(void* arg, void* user)` for one pointer argument; NULL is an empty one. Returns it with its typedef.
         */
        fun invocable(spelling: String): Pair<CBridge, Pair<String, String>> {
            if (result) throw Unsupported("$spelling result")
            val signature = spelling.substringAfter('<').substringBeforeLast('>')
            val arg = signature.substringAfter('(').substringBeforeLast(')').trim().takeIf { it.isNotEmpty() && it != "void" }
            if (signature.substringBefore('(').trim() != "void") throw Unsupported("$spelling: returns a value")
            if (arg != null && (',' in arg || shape(arg).indirection != listOf("*"))) throw Unsupported("$spelling: C callbacks take at most one pointer")
            val (name, typedef) = if (arg == null) USER_CALLBACK else ARG_CALLBACK
            return CBridge(
                name,
                { n -> "fila::callable($n, " + (if (arg == null) "[=] { $n(${n}User); }" else "[=](auto* arg) { $n((void*) arg, ${n}User); }") + ")" },
                extra = listOf("void*" to "User"),
            ) to (name to typedef)
        }

        fun scalar(decl: String, scalar: Scalar): CBridge {
            if (!byValue) throw Unsupported("$decl by pointer")
            return CBridge(scalar.c, if (result) scalar.toC else scalar.toCpp, out = result && CAbi.returnsThroughPointer(scalar.c))
        }

        /** Strings cross as NUL-terminated `const char*`: copied in, and out only when the C++ string outlives the call. */
        fun string(decl: String): CBridge {
            if (!byValue) throw Unsupported("$decl by pointer")
            return when {
                // Only literals make a StaticString; fila::staticString makes one because everything taking it copies it.
                !result -> CBridge("const char*", { if (decl == STATIC_STRING) "fila::staticString($it)" else "$decl($it)" })
                // ponytail: assumes the view is NUL-terminated (literals, CString storage); an out length if one isn't.
                decl == "std::string_view" -> CBridge("const char*", { "($it).data()" })
                // A temporary: copied into C's buffer, not NUL-terminated; returns its length.
                indirection == null && !lvalue -> CBridge(
                    "uint32_t",
                    { call -> "fila::copy($call, outCapacity, [&](char x, uint32_t i) { out[i] = x; })" },
                    extra = listOf("char*" to "out", "uint32_t" to "outCapacity"),
                )
                else -> CBridge("const char*", { "($it).c_str()" })
            }
        }

        fun builtin(base: String): CBridge {
            if (byValue) {
                val cType = CAbi.byValue(base)
                return CBridge(cType, if (cType == base) { v -> v } else cast(cType, base), out = result && CAbi.returnsThroughPointer(cType))
            }
            CAbi.checkPointee(base)
            return when {
                indirection == "*" -> CBridge("$c$base*", { it })
                result -> CBridge("$base*", { "&$it" })
                else -> CBridge("$base*", { "*$it" })
            }
        }

        fun enum(decl: String, wideType: String?): CBridge {
            if (!byValue) throw Unsupported("$decl by pointer")
            val name = CNames.type(decl)
            return CBridge(name, cast(name, decl), out = result && wideType != null && CAbi.returnsThroughPointer(wideType))
        }

        fun entity() = when {
            !byValue -> pointer("FilaEntity", "utils::Entity")
            result -> CBridge("FilaEntity", { "utils::Entity::smuggle($it)" })
            else -> CBridge("FilaEntity", { "utils::Entity::import($it)" })
        }

        fun instance(alias: String): CBridge {
            if (!byValue) throw Unsupported("$alias by pointer")
            return CBridge("uint32_t", if (result) { v -> "$v.asValue()" } else { v -> "$alias($v)" })
        }

        /** Structs never cross by value: parameters come by `const` pointer, results go out through one. */
        fun math(decl: String): CBridge {
            val name = CNames.type(decl)
            return when {
                !byValue -> handle(name)
                result -> CBridge(name, { "std::bit_cast<$name>($it)" }, out = true)
                else -> CBridge("const $name*", { "std::bit_cast<$decl>(*$it)" })
            }
        }

        /** A value struct taken or returned by value is copied: in from a `const` pointer, out into one C created. */
        fun record(decl: String, value: Boolean, copyable: Boolean): CBridge {
            val name = CNames.type(decl)
            return when {
                !value && indirection == null -> throw Unsupported("$decl by value")
                !value || !byValue -> handle(name)
                !result -> CBridge("const $name*", { "*fila::cpp($it)" })
                // C can't create one to copy into; a reference's outlives the call, so C borrows it.
                !copyable && indirection == "&" -> handle(name)
                !copyable -> throw Unsupported("$decl result: C can't create one to copy it into")
                else -> CBridge(name, { it }, out = true, store = { v, out -> "*fila::cpp($out) = $v;" })
            }
        }

        /** Casts to C's [cType] for a result, to C++'s [cpp] for a parameter. */
        private fun cast(cType: String, cpp: String) = if (result) { v: String -> "static_cast<$cType>($v)" } else { v -> "static_cast<$cpp>($v)" }

        /** C passes a pointer whichever of `*` or `&` C++ takes. */
        private fun pointer(cName: String, cppName: String): CBridge {
            val cType = "$c$cName*"
            val address = if (indirection == "*") "" else "&"
            return if (result) CBridge(cType, { "reinterpret_cast<$cType>($address$it)" })
            else CBridge(cType, { (if (indirection == "*") "" else "*") + "reinterpret_cast<$c$cppName*>($it)" })
        }

        /** A pointer to a record's handle or a math mirror, converted by the `FILA_TYPE` overloads. */
        private fun handle(cName: String): CBridge {
            val ref = indirection != "*"
            return if (result) CBridge("$c$cName*", { "fila::c(${if (ref) "&" else ""}$it)" })
            else CBridge("$c$cName*", { "${if (ref) "*" else ""}fila::cpp($it)" })
        }
    }
}

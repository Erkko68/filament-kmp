package buildlogic.apigen.c

import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppEnum
import buildlogic.apigen.cpp.CppRecord
import buildlogic.apigen.cpp.CppType
import buildlogic.apigen.cpp.CppType.Kind

/**
 * How a C++ value crosses into C: its C spelling, and the expression converting it to the other side. [out]: a
 * result C returns through a trailing `out` pointer ([CAbi]), which [store] writes.
 */
internal class CBridge(
    val c: String,
    val convert: (String) -> String,
    val out: Boolean = false,
    val store: (String) -> String = { "*out = ${convert(it)};" },
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
    "utils::bitset32" to Scalar("uint32_t", { "utils::bitset32($it)" }, { "($it).getValue()" }),
    "utils::tribool" to Scalar(
        "int32_t",
        { "utils::tribool(static_cast<utils::tribool::Value>($it))" },
        { "[](utils::tribool t) { return t.is_indeterminate() ? 2 : int32_t(t.is_true()); }($it)" },
    ),
)

private val STRINGS = setOf("std::string_view", "std::string", "utils::CString", "utils::ImmutableCString")

/** Why a declaration stays hand-written; the generator leaves a comment saying so instead of code. */
internal class Unsupported(reason: String) : Exception(reason)

/** Maps the model's types onto C, recording the math types the module's mirror structs must cover. */
internal class CBridges(private val api: CppApi) {
    val mathTypes = sortedSetOf<String>()

    /** [type] as a parameter: [CBridge.convert] turns the C argument into C++'s. */
    fun param(type: CppType) = of(type, result = false)

    /** [type] as a return value: [CBridge.convert] turns C++'s result into C's. [lvalue]: it outlives the call (a field). */
    fun result(type: CppType, lvalue: Boolean = false) = of(type, result = true, lvalue)

    /** A record with public fields. C holds it by pointer like any other, but copies it in and out like a value. */
    fun isValue(record: String) = api.records.getValue(record).fields.any { it.isPublic }

    /** A record C can create, so it has one to copy a result into. */
    fun creatable(record: CppRecord) = !record.template && record.allocatable && record.destructible && record.constructors.isNotEmpty()

    /** A pointer to plain data (`const char*`, `void*`): a struct keeping it would outlive the caller's buffer. */
    fun borrowsPointer(type: CppType) =
        (type.kind == Kind.BUILTIN && shape(type.spelling).indirection.isNotEmpty()) || type.decl == "std::string_view"

    /** The integer typedef of an enum too wide for a C enum (whose enumerators are ints), or null. */
    fun wideEnumType(enum: CppEnum) = if (enum.constants.all { it.second.bitLength() < 32 }) null else CAbi.byValue(enum.underlying ?: "uint64_t")

    private fun of(type: CppType, result: Boolean, lvalue: Boolean = false): CBridge {
        val shape = shape(type.spelling)
        if (shape.indirection.size > 1) throw Unsupported("${type.spelling}: pointer to pointer")
        val indirection = shape.indirection.firstOrNull()
        if (indirection == "&&") throw Unsupported("${type.spelling}: rvalue reference")
        var target = type
        var alias: String? = null
        while (target.decl in api.aliases) {
            alias = alias ?: target.decl
            target = api.aliases.getValue(target.decl!!)
            if (shape(target.spelling).indirection.isNotEmpty()) throw Unsupported("${type.spelling}: alias of a pointer")
        }
        if ('[' in type.spelling || '[' in target.spelling) throw Unsupported("${type.spelling}: array")
        val decl = target.decl
        val bridge = Bridge(shape.const, indirection, result, lvalue)
        return when {
            target.kind == Kind.BUILTIN -> bridge.builtin(shape(target.spelling).base)
            target.kind == Kind.FUNCTION -> throw Unsupported("${type.spelling}: function type")
            decl == null -> throw Unsupported("${type.spelling}: ${target.kind.name.lowercase()}")
            decl in api.enums -> bridge.enum(decl, wideEnumType(api.enums.getValue(decl)))
            decl in SCALARS -> bridge.scalar(decl, SCALARS.getValue(decl))
            decl in STRINGS -> bridge.string(decl)
            decl == "utils::Entity" -> bridge.entity()
            decl == "utils::EntityInstance" && alias != null -> bridge.instance(alias)
            decl.startsWith("filament::math::") && mathMirror(decl) != null -> bridge.math(decl).also { mathTypes += decl }
            decl in api.records && !api.records.getValue(decl).accessible -> throw Unsupported("${type.spelling}: not accessible")
            decl in api.records -> api.records.getValue(decl).let { bridge.record(decl, isValue(decl), creatable(it) && it.defaultConstructible) }
            else -> throw Unsupported("${type.spelling}: $decl")
        }
    }

    private class Bridge(val const: Boolean, val indirection: String?, val result: Boolean, val lvalue: Boolean) {
        val byValue = indirection == null || (indirection == "&" && const)
        val c = if (const) "const " else ""

        fun scalar(decl: String, scalar: Scalar): CBridge {
            if (!byValue) throw Unsupported("$decl by pointer")
            return CBridge(scalar.c, if (result) scalar.toC else scalar.toCpp, out = result && CAbi.returnsThroughPointer(scalar.c))
        }

        /** Strings cross as NUL-terminated `const char*`: copied in, and out only when the C++ string outlives the call. */
        fun string(decl: String): CBridge {
            if (!byValue) throw Unsupported("$decl by pointer")
            return when {
                !result -> CBridge("const char*", { "$decl($it)" })
                // ponytail: assumes the view is NUL-terminated (literals, CString storage); an out length if one isn't.
                decl == "std::string_view" -> CBridge("const char*", { "($it).data()" })
                indirection == null && !lvalue -> throw Unsupported("$decl result: C would point into a destroyed temporary")
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
                !byValue -> pointer(name, decl)
                result -> CBridge(name, { "std::bit_cast<$name>($it)" }, out = true)
                else -> CBridge("const $name*", { "std::bit_cast<$decl>(*$it)" })
            }
        }

        /** A value struct taken or returned by value is copied: in from a `const` pointer, out into one C created. */
        fun record(decl: String, value: Boolean, copyable: Boolean): CBridge {
            val name = CNames.type(decl)
            return when {
                !value && indirection == null -> throw Unsupported("$decl by value")
                !value || !byValue -> pointer(name, decl)
                !result -> CBridge("const $name*", { "*reinterpret_cast<const $decl*>($it)" })
                !copyable -> throw Unsupported("$decl result: C can't create one to copy it into")
                else -> CBridge(name, { it }, out = true, store = { "*reinterpret_cast<$decl*>(out) = $it;" })
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
    }
}

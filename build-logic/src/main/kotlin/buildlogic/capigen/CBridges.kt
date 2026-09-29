package buildlogic.capigen

import buildlogic.cppapi.CppApi
import buildlogic.cppapi.CppType
import buildlogic.cppapi.CppType.Kind

/** How a C++ type crosses into C: its C spelling, and the expressions converting a value each way. */
internal class CBridge(val c: String, val toCpp: (String) -> String, val toC: (String) -> String)

/** Why a declaration stays hand-written; the generator leaves a comment saying so instead of code. */
internal class Unsupported(reason: String) : Exception(reason)

/** A spelling taken apart: `const char * _Nonnull` is [base] `char`, [const], [indirection] `*`. */
internal class Shape(val base: String, val const: Boolean, val indirection: List<String>)

internal fun shape(spelling: String): Shape {
    var s = NULLABILITY.replace(spelling, "").trim()
    val indirection = ArrayList<String>()
    var const = false
    while (true) {
        s = s.trim()
        when {
            s.endsWith("&&") -> { indirection.add(0, "&&"); s = s.dropLast(2) }
            s.endsWith("*") || s.endsWith("&") -> { indirection.add(0, s.takeLast(1)); s = s.dropLast(1) }
            // After a `*`, a trailing const is the pointee's (`char const *`); before one, the pointer's own.
            TRAILING_CONST.containsMatchIn(s) -> { const = const || indirection.isNotEmpty(); s = s.dropLast(5) }
            else -> break
        }
    }
    if (s.startsWith("const ")) { const = true; s = s.removePrefix("const ") }
    return Shape(s.trim(), const, indirection)
}

private val TRAILING_CONST = Regex("""(^|\W)const$""")
private val NULLABILITY = Regex("""\b_(Nonnull|Nullable|Null_unspecified)\b""")

/** Maps the model's types onto C, recording the math types the module's mirror structs must cover. */
internal class CBridges(private val api: CppApi) {
    val mathTypes = sortedSetOf<String>()

    fun of(type: CppType): CBridge {
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
        val decl = target.decl
        val bridge = Bridge(shape.const, indirection)
        return when {
            target.kind == Kind.BUILTIN -> bridge.builtin(shape(target.spelling).base)
            target.kind == Kind.FUNCTION -> throw Unsupported("${type.spelling}: function type")
            decl == null -> throw Unsupported("${type.spelling}: ${target.kind.name.lowercase()}")
            decl in api.enums -> bridge.enum(decl)
            decl == "utils::Entity" -> bridge.entity()
            decl == "utils::EntityInstance" && alias != null -> bridge.instance(alias)
            decl.startsWith("filament::math::") && mathMirror(decl) != null -> bridge.math(decl).also { mathTypes += decl }
            decl in api.records && isHandle(decl) -> bridge.handle(decl)
            decl in api.records -> throw Unsupported("${type.spelling}: value struct")
            else -> throw Unsupported("${type.spelling}: $decl")
        }
    }

    /** A record C only ever holds a pointer to: no public fields to mirror. */
    fun isHandle(record: String) = api.records.getValue(record).fields.none { it.isPublic }

    private class Bridge(val const: Boolean, val indirection: String?) {
        val byValue = indirection == null || (indirection == "&" && const)
        val c = if (const) "const " else ""

        fun builtin(base: String) = when {
            byValue -> CBridge(base, { it }, { it })
            indirection == "*" -> CBridge("$c$base*", { it }, { it })
            else -> CBridge("$base*", { "*$it" }, { "&$it" })
        }

        fun enum(decl: String): CBridge {
            if (!byValue) throw Unsupported("$decl by pointer")
            val name = CNames.type(decl)
            return CBridge(name, { "static_cast<$decl>($it)" }, { "static_cast<$name>($it)" })
        }

        fun entity() = when {
            byValue -> CBridge("FilaEntity", { "utils::Entity::import($it)" }, { "utils::Entity::smuggle($it)" })
            else -> pointer("FilaEntity", "utils::Entity")
        }

        fun instance(alias: String): CBridge {
            if (!byValue) throw Unsupported("$alias by pointer")
            return CBridge("uint32_t", { "$alias($it)" }, { "$it.asValue()" })
        }

        fun math(decl: String): CBridge {
            val name = CNames.type(decl)
            return if (byValue) CBridge(name, { "std::bit_cast<$decl>($it)" }, { "std::bit_cast<$name>($it)" }) else pointer(name, decl)
        }

        fun handle(decl: String): CBridge {
            if (indirection == null) throw Unsupported("$decl by value")
            return pointer(CNames.type(decl), decl)
        }

        /** C passes a pointer whichever of `*` or `&` C++ takes. */
        private fun pointer(cName: String, cppName: String): CBridge {
            val cType = "$c$cName*"
            val cast = { value: String -> "reinterpret_cast<$c$cppName*>($value)" }
            return if (indirection == "*") CBridge(cType, cast, { "reinterpret_cast<$cType>($it)" })
            else CBridge(cType, { "*${cast(it)}" }, { "reinterpret_cast<$cType>(&$it)" })
        }
    }
}

private val VECTOR = Regex("(float|double|int|uint|short|ushort|bool|byte|ubyte)([234])")
private val MATRIX = Regex("mat([234])(f?)")
private val ELEMENT = mapOf(
    "float" to "float", "double" to "double", "int" to "int32_t", "uint" to "uint32_t", "short" to "int16_t",
    "ushort" to "uint16_t", "bool" to "bool", "byte" to "int8_t", "ubyte" to "uint8_t",
)

/** The C struct layout-compatible with a `filament::math` type, or null for the ones C can't mirror (half). */
internal fun mathMirror(decl: String): String? {
    val name = decl.removePrefix("filament::math::")
    val (element, count) = VECTOR.matchEntire(name)?.let { ELEMENT.getValue(it.groupValues[1]) to it.groupValues[2].toInt() }
        ?: MATRIX.matchEntire(name)?.let { (if (it.groupValues[2] == "f") "float" else "double") to it.groupValues[1].toInt().let { n -> n * n } }
        ?: when (name) { "quatf" -> "float" to 4; "quat" -> "double" to 4; else -> return null }
    val cName = CNames.type(decl)
    return "typedef struct $cName { $element v[$count]; } $cName;"
}

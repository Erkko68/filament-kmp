package buildlogic.apigen.c

import buildlogic.apigen.ApiGenConfig
import buildlogic.apigen.Scalar

/**
 * The bridges of a value C passes as it is, given how C++ takes it: [const], [indirection] (`*`, `&`, `&&` or none),
 * as a parameter or a [result], and whether it outlives the call ([lvalue]). [CBridges] wraps these for sequences,
 * optionals and functions.
 */
class DirectBridges internal constructor(
    private val config: ApiGenConfig, private val names: CNames,
    val const: Boolean, val indirection: String?, val result: Boolean, val lvalue: Boolean,
) {
    private val h = config.helpers
    val byValue = indirection == null || (indirection == "&" && const)
    val c = if (const) "const " else ""

    fun scalar(decl: String, scalar: Scalar): CBridge {
        if (byValue) return CBridge(scalar.c, if (result) scalar::toC else scalar::toCpp, out = result && CAbi.returnsThroughPointer(scalar.c))
        if (!scalar.layout) throw Unsupported("$decl by pointer")
        return pointer(scalar.c, decl)
    }

    /** Strings cross as NUL-terminated `const char*`: copied in, and out only when the C++ string outlives the call. */
    fun string(decl: String): CBridge {
        if (!byValue) throw Unsupported("$decl by pointer")
        return when {
            !result -> CBridge("const char*", { "${config.allStrings.getValue(decl)}($it)" })
            // ponytail: assumes the view is NUL-terminated (literals, string storage); an out length if one isn't.
            decl == "std::string_view" -> CBridge("const char*", { "($it).data()" })
            // A temporary: copied into C's buffer, not NUL-terminated; returns its length.
            indirection == null && !lvalue -> CBridge(
                "uint32_t",
                { call -> "$h::copy($call, outCapacity, [&](char x, uint32_t i) { out[i] = x; })" },
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
        val name = names.type(decl)
        return CBridge(name, cast(name, decl), out = result && wideType != null && CAbi.returnsThroughPointer(wideType))
    }

    /** Structs never cross by value: parameters come by `const` pointer, results go out through one. */
    fun math(decl: String): CBridge {
        val name = names.type(decl)
        return when {
            !byValue -> handle(name)
            result -> CBridge(name, { "std::bit_cast<$name>($it)" }, out = true)
            else -> CBridge("const $name*", { "std::bit_cast<$decl>(*$it)" })
        }
    }

    /** A value struct taken or returned by value is copied: in from a `const` pointer, out into one C created. */
    fun record(decl: String, value: Boolean, copyable: Boolean): CBridge {
        val name = names.type(decl)
        return when {
            !value && indirection == null -> throw Unsupported("$decl by value")
            !value || !byValue -> handle(name)
            !result -> CBridge("const $name*", { "*$h::cpp($it)" })
            // C can't create one to copy into; a reference's outlives the call, so C borrows it.
            !copyable && indirection == "&" -> handle(name)
            !copyable -> throw Unsupported("$decl result: C can't create one to copy it into")
            else -> CBridge(name, { it }, out = true, store = { v, out -> "*$h::cpp($out) = $v;" })
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

    /** A pointer to a record's handle or a math mirror, converted by the `cpp`/`c` overloads. */
    private fun handle(cName: String): CBridge {
        val ref = indirection != "*"
        return if (result) CBridge("$c$cName*", { "$h::c(${if (ref) "&" else ""}$it)" })
        else CBridge("$c$cName*", { "${if (ref) "*" else ""}$h::cpp($it)" })
    }
}

package buildlogic.apigen.c

import buildlogic.apigen.cpp.CppType

/**
 * The bridges of a value C passes as it is, given how C++ takes it: [const], [indirection] (`*`, `&`, `&&` or none),
 * as a parameter or a [result], and whether it outlives the call ([lvalue]). [CBridges] wraps these for sequences,
 * optionals and functions.
 */
internal class DirectBridges(val const: Boolean, val indirection: String?, val result: Boolean, val lvalue: Boolean) {
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
    fun invocable(type: CppType): Pair<CBridge, Pair<String, String>> {
        if (result) throw Unsupported("${type.spelling} result")
        val signature = type.args.single()
        val arg = signature.args.drop(1).singleOrNull()
        if (signature.args.first().spelling != "void") throw Unsupported("${type.spelling}: returns a value")
        if (signature.args.size > 2 || (arg != null && shape(arg.spelling).indirection != listOf("*"))) throw Unsupported("${type.spelling}: C callbacks take at most one pointer")
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

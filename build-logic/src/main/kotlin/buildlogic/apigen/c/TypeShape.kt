package buildlogic.apigen.c

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
/** Annotations that don't change what C passes: nullability, `__restrict`. */
internal val NULLABILITY = Regex("""\b(_Nonnull|_Nullable|_Null_unspecified|__restrict)\b""")

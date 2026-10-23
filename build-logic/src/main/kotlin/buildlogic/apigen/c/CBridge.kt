package buildlogic.apigen.c

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

/** Why a declaration stays hand-written; the generator leaves a comment saying so instead of code. */
class Unsupported(reason: String) : Exception(reason)

package buildlogic.apigen

import buildlogic.apigen.c.CBridge
import buildlogic.apigen.c.DirectBridges
import buildlogic.apigen.cpp.CppType
import java.io.Serializable

/**
 * What the generator is told about one C++ library; everything else it reads from the headers. The tables are what to
 * touch when a version bump adds a scalar, template or string type.
 */
data class ApiGenConfig(
    /** The library as prose and file names spell it: `Filament`. */
    val name: String,
    /** The dir the library's headers are included from, relative to the project. */
    val includeDir: String,
    /** Holds `api-headers.txt`, `api-manifest.json`, `api-coverage.txt` and, per module, `<module>/generated` and `<module>/manual`. */
    val cDir: String = "c",

    val std: String = "c++20",
    /** Further clang arguments: the defines and include paths the headers need. */
    val clangArgs: List<String> = emptyList(),
    /** Headers every translation unit includes first, relative to [includeDir]. */
    val prelude: List<String> = emptyList(),
    /**
     * clang `-ast-dump-filter`s covering the API: `ns::` dumps a namespace whole, `ns::Prefix` the declarations in it
     * whose names start so.
     */
    val astFilters: List<String>,
    /** Namespaces nested in a filter's that are left unparsed (huge, and not API). */
    val skippedNamespaces: Set<String> = emptySet(),

    /** What every C name starts with: `Fila`. */
    val prefix: String,
    /** Namespaces C names leave out, as every name would repeat them. */
    val droppedNamespaces: Set<String> = emptySet(),
    /** A regex of what field names start with that their accessors drop (`^m(?=[A-Z])` for `mPosition`), or empty. */
    val memberPrefix: String = "",
    /** The verbs of a field's accessors, named as the library names methods. */
    val getter: String = "Get",
    val setter: String = "Set",

    /** The C++ namespace of the helpers the forwarders call (the generated `Bridge.hpp`), and of the handle casts. */
    val helpers: String,
    /**
     * A hand-written header the forwarders include in place of `Bridge.hpp` (which it includes), relative to the first
     * module's `generated/`: the helpers only the library can write, see [refs], [results] and `Bridge.hpp`'s hooks.
     */
    val bridgeHeader: String? = null,
    /** C declarations the first module's `Types.h` opens with: what [scalars] and [custom] bridges name. */
    val typedefs: List<String> = emptyList(),

    /** Types C holds as a scalar (IDs, layers), by qualified name. */
    val scalars: Map<String, Scalar> = emptyMap(),
    /** Math types C mirrors as a struct of their storage, by qualified name. */
    val mirrors: Map<String, Mirror> = emptyMap(),
    /** The library's containers C passes as an array and its count, besides `std::vector` and `std::array`. */
    val sequences: Set<String> = emptySet(),
    /** The [sequences] that only view their elements (a span): a result's outlive the call, and a struct can't keep one. */
    val views: Set<String> = emptySet(),
    /** The library's string types, each with the spelling that constructs one from a `const char*`. */
    val strings: Map<String, String> = emptyMap(),
    val refs: RefCounting? = null,
    /** Value-or-error types: [bridgeHeader] declares `result(r, error, capacity, convert)` for them. */
    val results: Set<String> = emptySet(),
    /** String types only literals convert to; overloads taking `const char*` cover them. */
    val literalOnly: Set<String> = emptySet(),
    /** The class templates' instantiations C binds, by template, named without the arguments: parameter → argument. */
    val instantiations: Map<String, Map<String, String>> = emptyMap(),
    /**
     * The function templates' instantiations C binds, by template: each maps template parameters to arguments; ones
     * left out take their defaults. C names them by the types that tell them apart, like overloads.
     */
    val functionInstantiations: Map<String, List<Map<String, String>>> = emptyMap(),
    /** The bridges no table expresses. */
    val custom: CustomBridges = NoCustomBridges,
    /** The Kotlin stages, or null for a C API alone. */
    val kotlin: KotlinBindings? = null,
) : Serializable {
    internal val allSequences get() = sequences + STD_SEQUENCES
    internal val allStrings get() = STD_STRINGS + strings

    private companion object {
        val STD_SEQUENCES = setOf("std::array", "std::vector")
        val STD_STRINGS = mapOf("std::string_view" to "std::string_view", "std::string" to "std::string")
    }
}

/**
 * The Kotlin externals of the C API, and the JNI and wasm glue made from them. [dir] holds a Kotlin module per C module,
 * named as it is, relative to the project.
 */
data class KotlinBindings(
    /** The package of each C module's generated externals; the modules are `api-headers.txt`'s sections. */
    val packages: Map<String, String>,
    /** The package declaring `ExternalSymbolName` and `NativePointer`. */
    val interop: String,
    /** The wasm runtime the exports go to, and the package of its `WASM_ARITIES` parity table. */
    val wasmRuntime: String,
    val wasmPackage: String,
    /** The Kotlin modules whose wasm exports go to a runtime of their own. */
    val wasmRuntimes: Map<String, String> = emptyMap(),
    val dir: String = "kotlin",
) : Serializable

/**
 * A C++ type C holds as the scalar [c]: [toCpp] builds it from a C value, [toC] reads one back, each an expression
 * with `{}` for the value. [layout]: it's nothing but that scalar, so a pointer to one is a pointer to the other.
 */
data class Scalar(val c: String, val toCpp: String, val toC: String, val layout: Boolean = false) : Serializable {
    internal fun toCpp(value: String) = toCpp.replace("{}", value)
    internal fun toC(value: String) = toC.replace("{}", value)
}

/**
 * A math type's storage: [count] of [element]. [aligned]: C++ aligns it as a SIMD register, which C's mirror isn't,
 * so C++ can't use C's storage in place.
 */
data class Mirror(val element: String, val count: Int, val aligned: Boolean = false) : Serializable

/**
 * Reference counting. [pointers]: the smart pointers C holds as the pointer inside, each with whether it points to
 * const; [get] reads that pointer. [targets]: what a class derives from to be counted, by [addRef] and [release].
 * [ApiGenConfig.bridgeHeader] declares `retain(p)`, which adds a reference to a pointer or a smart pointer's.
 */
data class RefCounting(
    val pointers: Map<String, Boolean>,
    val targets: Set<String>,
    val get: String,
    val addRef: String,
    val release: String,
) : Serializable

/**
 * The bridges only one library has, which no table of [ApiGenConfig] expresses: the generator asks before its own. An
 * `object` implements it, so a config holding one stays a task input.
 */
interface CustomBridges : Serializable {
    /**
     * [decl] as the tables name it, for a template the library also names by typedef: [type] is what resolved to it,
     * [substitute] replaces the template parameters of the instantiations around it in a spelling.
     */
    fun canonical(decl: String, type: CppType, substitute: (String) -> String): String = decl

    /**
     * The bridge of a value whose type resolved to [decl], spelled through [alias] (the first one) if any, as [direct]
     * says C++ takes it; null for the generator's own.
     */
    fun bridge(decl: String, type: CppType, alias: String?, direct: DirectBridges): CBridge? = null
}

data object NoCustomBridges : CustomBridges

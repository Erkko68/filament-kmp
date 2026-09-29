package buildlogic.apigen.c

/**
 * The C types a Fila* signature takes and returns by value (docs/bindings.md, "Declaring a binding"). Kotlin binds
 * each export with one type on every target and nothing adapts widths in between, so:
 * - widths are fixed: `size_t` is 64-bit on the JVM and iOS but 32-bit on wasm32; sizes and counts cross as `uint32_t`;
 * - 8- and 16-bit integers widen to 32: Apple arm64 packs stack arguments by natural size, Kotlin passes an `Int`;
 * - 64-bit integers return through an out-pointer: the js target can't take an i64 result.
 */
internal object CAbi {
    /** The C type standing in for the builtin [cpp] by value. */
    fun byValue(cpp: String): String {
        val fixed = FIXED[cpp] ?: throw Unsupported("$cpp: no fixed-width C type")
        return WIDENED[fixed] ?: fixed
    }

    /** Pointers keep the C++ spelling, so its width must be the same on every target. */
    fun checkPointee(cpp: String) {
        if (cpp !in FIXED) throw Unsupported("$cpp*: no C type")
        if (cpp in TARGET_WIDTH) throw Unsupported("$cpp*: its width differs across targets")
    }

    fun returnsThroughPointer(c: String) = c == "int64_t" || c == "uint64_t"

    private val TARGET_WIDTH = setOf(
        "long", "long int", "unsigned long", "unsigned long int", "size_t", "std::size_t", "ssize_t", "ptrdiff_t",
        "std::ptrdiff_t", "intptr_t", "uintptr_t",
    )

    private val FIXED = mapOf(
        "void" to "void", "bool" to "bool", "float" to "float", "double" to "double",
        "char" to "int8_t", "signed char" to "int8_t", "int8_t" to "int8_t",
        "unsigned char" to "uint8_t", "uint8_t" to "uint8_t",
        "short" to "int16_t", "short int" to "int16_t", "int16_t" to "int16_t",
        "unsigned short" to "uint16_t", "unsigned short int" to "uint16_t", "uint16_t" to "uint16_t",
        "int" to "int32_t", "signed" to "int32_t", "signed int" to "int32_t", "int32_t" to "int32_t",
        "unsigned" to "uint32_t", "unsigned int" to "uint32_t", "uint32_t" to "uint32_t",
        "long" to "int64_t", "long int" to "int64_t", "long long" to "int64_t", "long long int" to "int64_t", "int64_t" to "int64_t",
        "unsigned long" to "uint64_t", "unsigned long int" to "uint64_t", "unsigned long long" to "uint64_t",
        "unsigned long long int" to "uint64_t", "uint64_t" to "uint64_t",
        // Sizes and counts fit 32 bits; a pointer-sized integer holds a native handle, 64-bit where one can be.
        "size_t" to "uint32_t", "std::size_t" to "uint32_t", "ssize_t" to "int32_t", "ptrdiff_t" to "int32_t",
        "std::ptrdiff_t" to "int32_t", "intptr_t" to "int64_t", "uintptr_t" to "uint64_t",
    )

    private val WIDENED = mapOf("int8_t" to "int32_t", "int16_t" to "int32_t", "uint8_t" to "uint32_t", "uint16_t" to "uint32_t")
}

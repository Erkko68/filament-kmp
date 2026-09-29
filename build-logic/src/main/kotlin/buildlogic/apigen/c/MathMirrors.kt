package buildlogic.apigen.c

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

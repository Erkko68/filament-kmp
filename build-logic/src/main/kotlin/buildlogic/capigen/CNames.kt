package buildlogic.capigen

/** C names for C++ declarations: `Fila` + the PascalCase path, minus the namespaces every name would repeat. */
internal object CNames {
    private val DROPPED = setOf("filament", "backend", "math")
    private val WORD_BREAK = Regex("([a-z0-9])([A-Z])")

    fun type(qualified: String) = "Fila" + qualified.split("::").filter { it !in DROPPED }
        .joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }

    fun function(owner: String, method: String, suffix: String = "") =
        "${type(owner)}_$method" + if (suffix.isEmpty()) "" else "_$suffix"

    fun enumConstant(enum: String, constant: String) = "${upperSnake(type(enum))}_${upperSnake(constant)}"

    private fun upperSnake(name: String) = WORD_BREAK.replace(name, "$1_$2").uppercase()
}


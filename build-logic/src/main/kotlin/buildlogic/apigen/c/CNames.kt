package buildlogic.apigen.c

import buildlogic.apigen.ApiGenConfig

/** C names for C++ declarations: the prefix + the PascalCase path, minus the namespaces every name would repeat. */
internal class CNames(private val config: ApiGenConfig) {
    val prefix = config.prefix
    private val memberPrefix = Regex(config.memberPrefix.ifEmpty { "$^" })

    /** Template arguments aren't part of the name: C binds one instantiation of a template. */
    fun type(qualified: String) = prefix + qualified.split("::").filter { it !in config.droppedNamespaces }
        .joinToString("") { it.substringBefore('<').replaceFirstChar(Char::uppercaseChar) }

    /** `operator()` is `invoke`, as Kotlin calls it. */
    fun function(owner: String, method: String, suffix: String = "") =
        "${type(owner)}_${if (method == "operator()") "invoke" else method}" + if (suffix.isEmpty()) "" else "_$suffix"

    /** A field's accessors, named as the library names methods: `mPosition` is read by `GetPosition`. */
    fun getter(owner: String, field: String) = accessor(owner, config.getter, field)
    fun setter(owner: String, field: String) = accessor(owner, config.setter, field)

    private fun accessor(owner: String, verb: String, field: String) =
        "${type(owner)}_$verb${memberPrefix.replace(field, "").replaceFirstChar(Char::uppercaseChar)}"

    fun enumConstant(enum: String, constant: String) = "${upperSnake(type(enum))}_${upperSnake(constant)}"

    private fun upperSnake(name: String) = WORD_BREAK.replace(name, "$1_$2").uppercase()

    private companion object {
        val WORD_BREAK = Regex("([a-z0-9])([A-Z])")
    }
}

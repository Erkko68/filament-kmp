package buildlogic.apigen.externals

import java.io.File

/** Reads the `@ExternalSymbolName` externals out of Kotlin sources (a regex over the declarations). */
object ExternalFunctionParser {
    private val DECLARATION = Regex(
        """@ExternalSymbolName\("(\w+)"\)\s*(?:private\s+|internal\s+)?external\s+fun\s+(\w+)\s*\(([^)]*)\)(?:\s*:\s*(\w+))?""",
    )
    private val PACKAGE = Regex("""^package\s+([\w.]+)""", RegexOption.MULTILINE)
    private val JVM_NAME = Regex("""@file:JvmName\("(\w+)"\)""")

    /** [file]'s externals, or null when it declares none. Its Kotlin module is the dir above `src/`. */
    fun parse(file: File): ExternalSource? {
        val text = file.readText()
        val functions = DECLARATION.findAll(text).map { m ->
            val (symbol, name, params, returnType) = m.destructured
            ExternalFunction(symbol, name, parseParams(params, "$name in ${file.name}"), returnType.ifEmpty { "Unit" })
        }.toList()
        if (functions.isEmpty()) return null

        val pkg = PACKAGE.find(text)?.groupValues?.get(1).orEmpty()
        val facade = JVM_NAME.find(text)?.groupValues?.get(1)
            ?: (file.nameWithoutExtension.replaceFirstChar { it.uppercase() } + "Kt")
        val module = file.invariantSeparatorsPath.substringBefore("/src/").substringAfterLast('/')
        return ExternalSource(file, module, if (pkg.isEmpty()) facade else "$pkg.$facade", functions)
    }

    private fun parseParams(params: String, where: String): List<ExternalFunction.Param> =
        params.split(',').map { it.trim() }.filter { it.isNotEmpty() }.map {
            val parts = it.split(':').map(String::trim)
            check(parts.size == 2) { "Can't parse parameter '$it' of $where" }
            ExternalFunction.Param(parts[0].removeSurrounding("`"), parts[1])
        }
}

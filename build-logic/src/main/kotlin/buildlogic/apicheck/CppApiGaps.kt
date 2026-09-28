package buildlogic.apicheck

/**
 * Methods of Filament's public classes the C API's objects never mention: the headers' public methods, inline
 * ones included, plus whatever the libraries define that the headers don't declare (template instances), so a
 * header-walk miss can't hide a gap.
 */
internal object CppApiGaps {
    private val TEMPLATE_ARGS = Regex("<[^<>]*>")
    private val NOT_API = listOf("::operator", "::~", "{", "$", "(anonymous")

    fun find(headerMethods: List<String>, undeclared: Set<String>, cApi: List<Symbol>, publicClasses: Set<String>): Set<String> {
        val used = cApi.mapTo(HashSet()) { normalize(it.name) }
        return (headerMethods.asSequence().map(::normalize) + undeclared)
            .filter { it !in used && isPublicMethod(it, publicClasses) }
            .toSortedSet(compareBy({ qualifiedName(it) }, { it }))
    }

    /** Methods of public classes the libraries define but the headers don't declare (demangled [declared]). */
    fun undeclared(filament: List<Symbol>, declared: List<String>, publicClasses: Set<String>): Set<String> {
        val seen = declared.mapTo(HashSet(), ::normalize)
        return filament.asSequence()
            .filter { it.type == 'T' }
            .map { normalize(it.name) }
            .filter { it !in seen && isPublicMethod(it, publicClasses) }
            .toSortedSet()
    }

    // Folds a const overload into its non-const twin: binding either one covers both.
    private fun normalize(symbol: String) = symbol.removeSuffix(" const")

    private fun isPublicMethod(signature: String, publicClasses: Set<String>): Boolean {
        if (NOT_API.any { it in signature }) return false
        val name = qualifiedName(signature)
        val owner = name.substringBeforeLast("::")
        return owner in publicClasses && name.substringAfterLast("::") != owner.substringAfterLast("::") // constructors
    }

    // Template instances demangle with their return type first: keep the last word before the parameters.
    private fun qualifiedName(signature: String): String =
        generateSequence(signature) { s -> TEMPLATE_ARGS.replace(s, "").takeIf { it != s } }.last()
            .substringBefore('(').substringAfterLast(' ')
}

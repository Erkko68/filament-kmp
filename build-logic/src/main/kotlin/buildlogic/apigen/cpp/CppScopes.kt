package buildlogic.apigen.cpp

import buildlogic.apigen.cpp.CppType.Kind

/**
 * The names declared so far, for resolving what headers spell unqualified. Headers declare before use, so
 * registering while walking and resolving on the spot sees what the compiler saw.
 */
internal class CppScopes {
    private enum class Decl { NAMESPACE, TYPE, VARIABLE, TEMPLATE_PARAMETER }

    private val names = HashMap<String, Decl>()
    private val aliasTargets = HashMap<String, String>()
    private val usingNamespaces = HashMap<String, MutableList<String>>()
    private val usingNames = HashMap<String, MutableMap<String, String>>()
    private val bases = HashMap<String, List<String>>()
    private val partial = ArrayList<String>()

    fun namespace(name: String) { names[name] = Decl.NAMESPACE }
    /** A namespace the dump covers only in part: names nothing declares are guessed to live there. */
    fun partialNamespace(name: String) { namespace(name); partial += name }
    fun bases(record: String, spelled: List<String>) { bases[record] = spelled.mapNotNull { lookup(it.substringBefore('<'), record) } }
    fun type(name: String) { names[name] = Decl.TYPE }
    fun variable(name: String) { names[name] = Decl.VARIABLE }
    fun templateParameter(name: String) { names[name] = Decl.TEMPLATE_PARAMETER }
    fun alias(name: String, target: String?) { type(name); target?.let { aliasTargets[name] = it } }
    fun usingNamespace(scope: String, spelled: String) {
        lookup(spelled, scope)?.let { usingNamespaces.getOrPut(scope, ::ArrayList) += it }
    }
    fun usingName(scope: String, qualified: String) {
        usingNames.getOrPut(scope, ::HashMap)[qualified.substringAfterLast("::")] = qualified
    }

    fun resolve(spelling: String, scope: String): CppType {
        val base = baseName(spelling) ?: return CppType(spelling, null, Kind.FUNCTION)
        if (isBuiltin(base)) return CppType(spelling, null, Kind.BUILTIN)
        val decl = lookup(base, scope) ?: return CppType(spelling, null, Kind.UNRESOLVED)
        val kind = when (names[decl]) {
            null -> Kind.EXTERNAL
            Decl.TEMPLATE_PARAMETER -> Kind.TEMPLATE_PARAMETER
            else -> Kind.DECLARED
        }
        return CppType(spelling, decl, kind)
    }

    /** The qualified name [spelled] refers to from [scope]; under a namespace nothing dumped declares it, a guess. */
    fun lookup(spelled: String, scope: String): String? {
        if (spelled.startsWith("::")) return member("", spelled.removePrefix("::"))
        val head = spelled.substringBefore("::")
        val rest = spelled.substringAfter("::", "")
        for (enclosing in enclosing(scope)) {
            val candidates = listOfNotNull(qualify(enclosing, head), usingNames[enclosing]?.get(head)) +
                usingNamespaces[enclosing].orEmpty().map { qualify(it, head) } +
                inherited(enclosing).map { qualify(it, head) }
            val found = candidates.firstOrNull { it in names } ?: continue
            return if (rest.isEmpty()) found else member(found, rest)
        }
        // Clang compiled the header, so the name exists, in a namespace the dump skipped or a global using-directive
        // pulled in. ponytail: the partial namespace sharing most of the scope wins; a name two of them declare needs decl ids.
        return partial.maxByOrNull { shared(it, scope) }?.let { qualify(it, spelled) }
    }

    private fun shared(a: String, b: String) = a.split("::").zip(b.split("::")).takeWhile { (x, y) -> x == y }.size

    private fun inherited(record: String): List<String> = bases[record].orEmpty().flatMap { listOf(it) + inherited(it) }

    private fun member(scope: String, rest: String): String? {
        val target = aliasTargets[scope] ?: scope
        val qualified = qualify(target, rest)
        return qualified.takeIf { it in names || names[target] == Decl.NAMESPACE || target.isEmpty() }
    }

    private fun enclosing(scope: String) = generateSequence(scope) { s -> if (s.isEmpty()) null else s.substringBeforeLast("::", "") }

    private fun qualify(scope: String, name: String) = if (scope.isEmpty()) name else "$scope::$name"

    private companion object {
        val QUALIFIERS = Regex("""\b(const|volatile|struct|class|enum|typename|_Nonnull|_Nullable|_Null_unspecified)\b|[*&]|\[\d*]""")
        val TEMPLATE_ARGS = Regex("<[^<>]*>")
        val BUILTIN_WORDS = setOf("void", "bool", "char", "short", "int", "long", "float", "double", "signed", "unsigned")
        val BUILTIN_TYPEDEFS = Regex("""u?int(8|16|32|64|ptr)_t|s?size_t|ptrdiff_t|nullptr_t|std::(size_t|nullptr_t|ptrdiff_t)""")

        /** The name a type spelling is built on, or null for a function type. */
        fun baseName(spelling: String): String? {
            val bare = generateSequence(spelling) { s -> TEMPLATE_ARGS.replace(s, "").takeIf { it != s } }.last()
            if ('(' in bare) return null
            return QUALIFIERS.replace(bare, " ").trim().replace(Regex("\\s+"), " ")
        }

        fun isBuiltin(base: String) = base.split(' ').all { it in BUILTIN_WORDS } || BUILTIN_TYPEDEFS.matches(base)
    }
}

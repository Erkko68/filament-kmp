package buildlogic.apicheck

import java.io.File

/**
 * What Filament's public headers declare: the qualified names of the classes exported with `*_PUBLIC` (a
 * visibility attribute) and their public nested classes, and the mangled names of their public methods,
 * header-inline ones included. [declared] adds their non-public ones: libraries export those too.
 * Class templates are named without their arguments (`camutils::Manipulator`).
 */
internal class HeaderApi(val publicClasses: Set<String>, val methods: Set<String>, val declared: Set<String>)

/** Walks clang's AST of [headers]; nothing here parses C++ itself. */
internal class HeaderApiReader(private val ast: ClangAstDump, private val workDir: File) {
    private val classes = HashSet<String>()
    private val methods = HashSet<String>()
    private val declared = HashSet<String>()

    fun read(includeDir: File, headers: Collection<File>): HeaderApi {
        val unit = workDir.resolve("headers.cpp")
        unit.writeText(headers.map { it.relativeTo(includeDir).invariantSeparatorsPath }.sorted().joinToString("") { "#include <$it>\n" })
        // A filter dumps the outermost declarations it matches, so each document sits in the filter's namespace.
        FILTERS.forEach { filter ->
            ast.forEachDeclaration(unit, includeDir, filter, SKIPPED) { visit(it, filter.substringBeforeLast("::"), exported = false) }
        }
        return HeaderApi(classes, methods, declared)
    }

    private fun visit(node: Map<*, *>, scope: String, exported: Boolean) {
        val name = node["name"] as? String ?: return
        when (node["kind"]) {
            "NamespaceDecl" -> node.children().forEach { visit(it, "$scope::$name", exported = false) }
            "ClassTemplateDecl" -> node.children().forEach { visit(it, scope, exported) }
            "CXXRecordDecl" -> if (node["completeDefinition"] == true) visitClass(node, "$scope::$name", exported)
        }
    }

    private fun visitClass(node: Map<*, *>, qualified: String, exported: Boolean) {
        val children = node.children()
        val public = exported || children.any { it["kind"] == "VisibilityAttr" }
        if (public) classes += qualified
        var access = if (node["tagUsed"] == "class") "private" else "public"
        for (child in children) {
            when (child["kind"]) {
                "AccessSpecDecl" -> access = child["access"] as String
                "CXXRecordDecl", "ClassTemplateDecl" -> visit(child, qualified, exported = public && access == "public")
                "CXXMethodDecl" -> {
                    val mangled = child["mangledName"] as? String ?: continue
                    if (!public) continue
                    declared += mangled
                    if (access == "public" && isApi(child)) methods += mangled
                }
            }
        }
    }

    private fun isApi(method: Map<*, *>) = method["isImplicit"] != true && method["explicitlyDeleted"] != true
        && !(method["name"] as String).startsWith("operator")

    @Suppress("UNCHECKED_CAST")
    private fun Map<*, *>.children() = (this["inner"] as? List<Map<*, *>>).orEmpty()

    private companion object {
        val FILTERS = listOf("filament::", "filamat::", "utils::EntityManager")
        // Math templates and the backend are huge and have no public classes of their own.
        val SKIPPED = setOf("math", "backend")
    }
}

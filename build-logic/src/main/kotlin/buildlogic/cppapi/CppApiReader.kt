package buildlogic.cppapi

import java.io.File
import java.math.BigInteger

/** Builds a [CppApi] from clang's AST of [headers]; nothing here parses C++ itself. */
internal class CppApiReader(private val ast: ClangAstDump, private val workDir: File) {
    private val scopes = CppScopes()
    private val records = LinkedHashMap<String, CppRecord>()
    private val enums = LinkedHashMap<String, CppEnum>()
    private val aliases = LinkedHashMap<String, CppType>()
    private val constants = HashMap<String, CppValue>()
    private val values = CppValues(scopes, constants)

    fun read(includeDir: File, headers: Collection<File>): CppApi {
        val unit = workDir.resolve("headers.cpp")
        unit.writeText(headers.map { it.relativeTo(includeDir).invariantSeparatorsPath }.sorted().joinToString("") { "#include <$it>\n" })
        scopes.namespace("std")
        // A filter dumps the outermost declarations it matches, so each document sits in the filter's namespace.
        FILTERS.forEach { filter ->
            val namespace = filter.substringBeforeLast("::")
            if (filter.endsWith("::")) scopes.namespace(namespace) else scopes.partialNamespace(namespace)
            SKIPPED.forEach { scopes.partialNamespace("$namespace::$it") }
            ast.forEachDeclaration(unit, includeDir, filter, SKIPPED) { visit(it, namespace, exported = false) }
        }
        return CppApi(records, enums, aliases, constants)
    }

    private fun visit(node: Map<*, *>, scope: String, exported: Boolean) {
        val name = node["name"] as? String
        when (node["kind"]) {
            "NamespaceDecl" -> if (name != null) {
                scopes.namespace("$scope::$name")
                node.children().forEach { visit(it, "$scope::$name", exported = false) }
            }
            "ClassTemplateDecl" -> node.children().forEach { child ->
                if (child["kind"] == "TemplateTypeParmDecl") (child["name"] as? String)?.let { scopes.templateParameter("$scope::$name::$it") }
                visit(child, scope, exported)
            }
            "CXXRecordDecl" -> if (name != null && node["isImplicit"] != true) {
                scopes.type("$scope::$name")
                if (node["completeDefinition"] == true) visitRecord(node, "$scope::$name", exported)
            }
            "EnumDecl" -> if (name != null) visitEnum(node, "$scope::$name")
            "TypeAliasDecl", "TypedefDecl" -> if (name != null) {
                val target = scopes.resolve(spelledType(node), scope)
                aliases["$scope::$name"] = target
                scopes.alias("$scope::$name", target.decl)
            }
            "VarDecl" -> if (name != null) {
                scopes.variable("$scope::$name")
                values.declared(node["id"] as String, "$scope::$name")
                initializer(node)?.let { constants["$scope::$name"] = values.of(it, scope) }
            }
            "UsingDirectiveDecl" -> scopes.usingNamespace(scope, (node["nominatedNamespace"] as Map<*, *>)["name"] as String)
            "UsingDecl" -> name?.let { scopes.usingName(scope, it) }
        }
    }

    private fun visitRecord(node: Map<*, *>, qualified: String, exported: Boolean) {
        val children = node.children()
        @Suppress("UNCHECKED_CAST")
        scopes.bases(qualified, (node["bases"] as? List<Map<*, *>>).orEmpty().map(::spelledType))
        val public = exported || children.any { it["kind"] == "VisibilityAttr" }
        val methods = ArrayList<CppMethod>()
        val fields = ArrayList<CppField>()
        var access = if (node["tagUsed"] == "class") "private" else "public"
        for (child in children) {
            val isPublic = access == "public"
            when (child["kind"]) {
                "AccessSpecDecl" -> access = child["access"] as String
                "CXXMethodDecl" -> methods += method(child, qualified, isPublic)
                "FieldDecl" -> (child["name"] as? String)?.let { name ->
                    val type = scopes.resolve(spelledType(child), qualified)
                    fields += CppField(name, type, initializer(child)?.let { values.of(it, qualified) }, isPublic)
                }
                "CXXRecordDecl", "ClassTemplateDecl" -> visit(child, qualified, exported = public && isPublic)
                else -> visit(child, qualified, exported = false)
            }
        }
        records[qualified] = CppRecord(qualified, public, methods, fields)
    }

    private fun method(node: Map<*, *>, owner: String, isPublic: Boolean): CppMethod {
        val name = node["name"] as String
        val (returns, qualifiers) = splitSignature(spelledType(node))
        val params = node.children().filter { it["kind"] == "ParmVarDecl" }.map { param ->
            CppParam(
                param["name"] as? String ?: "",
                scopes.resolve(spelledType(param), owner),
                initializer(param)?.let { values.of(it, owner) },
            )
        }
        return CppMethod(
            owner, name, node["mangledName"] as? String, scopes.resolve(returns, owner), params,
            isStatic = node["storageClass"] == "static",
            isConst = qualifiers.split(' ').contains("const"),
            isPublic = isPublic,
            isApi = node["isImplicit"] != true && node["explicitlyDeleted"] != true && !name.startsWith("operator"),
        )
    }

    private fun visitEnum(node: Map<*, *>, qualified: String) {
        scopes.type(qualified)
        var next = BigInteger.ZERO
        val constants = node.children().filter { it["kind"] == "EnumConstantDecl" }.map { constant ->
            val explicit = constant.children().firstOrNull { it["kind"] == "ConstantExpr" }?.get("value") as? String
            val value = explicit?.let(::BigInteger) ?: next
            next = value + BigInteger.ONE
            constant["name"] as String to value
        }
        val underlying = (node["fixedUnderlyingType"] as? Map<*, *>)?.get("qualType") as? String
        enums[qualified] = CppEnum(qualified, underlying, constants)
    }

    /** A declaration's initializer: its one child that isn't a comment or an attribute. */
    private fun initializer(node: Map<*, *>): Map<*, *>? {
        if (node["init"] == null && node["hasInClassInitializer"] != true) return null
        return node.children().firstOrNull { child -> NOT_EXPRESSIONS.none { (child["kind"] as String).endsWith(it) } }
    }

    private companion object {
        // utils first: filament's headers use utils::EntityManager, and names resolve as they're declared.
        val FILTERS = listOf("utils::EntityManager", "filament::", "filamat::")
        // Math templates are huge and declare no API of their own.
        val SKIPPED = setOf("math")
        val NOT_EXPRESSIONS = listOf("Comment", "Attr", "Decl")

        /** Splits a method type like `Mode (int) const noexcept` into its return type and trailing qualifiers. */
        fun splitSignature(type: String): Pair<String, String> {
            val close = type.lastIndexOf(')')
            var depth = 0
            for (i in close downTo 0) {
                when (type[i]) { ')' -> depth++; '(' -> depth-- }
                if (depth == 0) return type.substring(0, i).trim() to type.substring(close + 1).trim()
            }
            error("Not a function type: $type")
        }
    }
}

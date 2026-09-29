package buildlogic.apigen.cpp

import java.io.File
import java.math.BigInteger

/** Builds a [CppApi] from clang's AST of [headers]; nothing here parses C++ itself. */
internal class CppApiReader(private val ast: ClangAstDump, private val workDir: File) {
    private val scopes = CppScopes()
    private val records = LinkedHashMap<String, CppRecord>()
    private val enums = LinkedHashMap<String, CppEnum>()
    private val aliases = LinkedHashMap<String, CppType>()
    private val constants = HashMap<String, CppValue>()
    private val functions = ArrayList<CppMethod>()
    private val values = CppValues(scopes, constants)
    private val seenHeaders = HashSet<String>()
    private var headerOf: Map<Map<*, *>, String?> = emptyMap()

    /** Reads [headers] (paths relative to [includeDir]); each must declare something the filters dump. */
    fun read(includeDir: File, headers: Collection<String>): CppApi {
        val unit = workDir.resolve("headers.cpp")
        unit.writeText(headers.sorted().joinToString("") { "#include <$it>\n" })
        scopes.namespace("std")
        // Every filter's namespace up front: image's bundles name filament::math before the filament:: dump runs.
        FILTERS.forEach { filter ->
            val namespace = filter.substringBeforeLast("::", "")
            if (filter.endsWith("::")) scopes.namespace(namespace) else if (namespace.isNotEmpty()) scopes.partialNamespace(namespace)
            SKIPPED.forEach { scopes.partialNamespace(qualify(namespace, it)) }
        }
        // A filter dumps the outermost declarations it matches, so each document sits in the filter's namespace.
        FILTERS.forEach { filter ->
            val namespace = filter.substringBeforeLast("::", "")
            ast.forEachDeclaration(unit, includeDir, filter, SKIPPED) { document ->
                headerOf = DeclarationFiles.of(document, includeDir)
                visit(document, namespace, exported = false, accessible = true)
            }
        }
        val missed = headers.filterNot { it in seenHeaders }
        check(missed.isEmpty()) { "No AST filter dumps the declarations of ${missed.joinToString()}; add one to CppApiReader.FILTERS" }
        return CppApi(records, enums, aliases, constants, functions)
    }

    /**
     * [exported]: `*_PUBLIC` or publicly nested in an exported class, what the libraries' symbols follow.
     * [accessible]: nameable from outside, at namespace scope or publicly nested.
     */
    private fun visit(node: Map<*, *>, scope: String, exported: Boolean, accessible: Boolean, template: Boolean = false) {
        headerOf[node]?.let(seenHeaders::add)
        val kind = node["kind"]
        if (kind == "UsingDirectiveDecl") return scopes.usingNamespace(scope, (node["nominatedNamespace"] as Map<*, *>)["name"] as String)
        val name = node["name"] as? String ?: return
        if (kind == "UsingDecl") return scopes.usingName(scope, name)
        val qualified = qualify(scope, name)
        when (kind) {
            "NamespaceDecl" -> {
                scopes.namespace(qualified)
                node.children().forEach { visit(it, qualified, exported = false, accessible = true) }
            }
            "ClassTemplateDecl" -> node.children().forEach { child ->
                if (child["kind"] == "TemplateTypeParmDecl") (child["name"] as? String)?.let { scopes.templateParameter("$qualified::$it") }
                visit(child, scope, exported, accessible, template = true)
            }
            "CXXRecordDecl" -> if (node["isImplicit"] != true) {
                scopes.type(qualified)
                if (node["completeDefinition"] == true) visitRecord(node, qualified, exported, accessible, template)
            }
            // A redeclaration repeats one already read.
            "FunctionDecl" -> if (node["previousDecl"] == null) functions += method(node, scope, isPublic = true, free = true)
            "EnumDecl" -> visitEnum(node, qualified)
            "TypeAliasDecl", "TypedefDecl" -> {
                val target = scopes.resolve(spelledType(node), scope)
                aliases[qualified] = target
                scopes.alias(qualified, target.decl)
            }
            "VarDecl" -> {
                scopes.variable(qualified)
                values.declared(node["id"] as String, qualified)
                initializer(node)?.let { constants[qualified] = values.of(it, scope) }
            }
        }
    }

    private fun visitRecord(node: Map<*, *>, qualified: String, exported: Boolean, accessible: Boolean, template: Boolean) {
        val children = node.children()
        @Suppress("UNCHECKED_CAST")
        val bases = (node["bases"] as? List<Map<*, *>>).orEmpty()
        scopes.bases(qualified, bases.map(::spelledType))
        val publicBases = bases.filter { it["access"] == "public" }.mapNotNull { scopes.lookup(spelledType(it).substringBefore('<'), qualified) }
        val public = exported || children.any { it["kind"] == "VisibilityAttr" }
        val methods = ArrayList<CppMethod>()
        val fields = ArrayList<CppField>()
        val dd = node["definitionData"] as? Map<*, *>
        val abstract = dd?.get("isAbstract") == true
        val constructors = ArrayList<List<CppParam>>()
        if (!abstract && (dd?.get("defaultCtor") as? Map<*, *>)?.get("needsImplicit") == true) constructors += emptyList<CppParam>()
        var destructible = true
        // A declared move constructor deletes the implicit copy.
        var copyable = !abstract && ((dd?.get("moveCtor") as? Map<*, *>)?.get("userDeclared") != true ||
            (dd?.get("copyCtor") as? Map<*, *>)?.get("userDeclared") == true)
        var allocatable = publicBases.all { records[it]?.allocatable != false }
        var access = if (node["tagUsed"] == "class") "private" else "public"
        for (child in children) {
            val isPublic = access == "public"
            when (child["kind"]) {
                "AccessSpecDecl" -> access = child["access"] as String
                "CXXMethodDecl" -> {
                    methods += method(child, qualified, isPublic)
                    if (child["name"] == "operator new" && (child["explicitlyDeleted"] == true || !isPublic)) allocatable = false
                }
                "CXXConstructorDecl" -> {
                    val params = method(child, qualified, isPublic).params
                    val usable = isPublic && child["explicitlyDeleted"] != true
                    val self = params.singleOrNull()?.type?.takeIf { it.decl == qualified }
                    if (self != null && "&&" !in self.spelling && !usable) copyable = false
                    // Copies and moves take the record itself; C holds handles, never copies. Implicit ones count: an
                    // aggregate's default constructor is declared once a header uses it.
                    if (!abstract && usable && self == null) constructors += params
                }
                "CXXDestructorDecl" -> destructible = isPublic && child["explicitlyDeleted"] != true
                "FieldDecl" -> (child["name"] as? String)?.let { name ->
                    val type = scopes.resolve(spelledType(child), qualified)
                    val deprecated = child.children().any { it["kind"] == "DeprecatedAttr" }
                    fields += CppField(name, type, initializer(child)?.let { values.of(it, qualified) }, isPublic, deprecated)
                }
                // `class Frustum getFrustum()` declares Frustum in the namespace, though clang lists it here.
                "CXXRecordDecl", "ClassTemplateDecl" -> if (child["parentDeclContextId"] == null)
                    visit(child, qualified, exported = public && isPublic, accessible = accessible && isPublic, template = template)
                else -> visit(child, qualified, exported = false, accessible = accessible && isPublic)
            }
        }
        records[qualified] = CppRecord(
            qualified, headerOf[node], public, accessible, template, publicBases, methods, fields, constructors, destructible, allocatable, copyable,
        )
    }

    /** A method, or with [free] a function in namespace [owner]. */
    private fun method(node: Map<*, *>, owner: String, isPublic: Boolean, free: Boolean = false): CppMethod {
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
            owner, name, headerOf[node], node["mangledName"] as? String, scopes.resolve(returns, owner), params,
            isStatic = free || node["storageClass"] == "static",
            isConst = qualifiers.split(' ').contains("const"),
            isPublic = isPublic,
            isDeprecated = node.children().any { it["kind"] == "DeprecatedAttr" },
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
        enums[qualified] = CppEnum(qualified, headerOf[node], underlying, constants)
    }

    /** A declaration's initializer: its one child that isn't a comment or an attribute. */
    private fun initializer(node: Map<*, *>): Map<*, *>? {
        if (node["init"] == null && node["hasInClassInitializer"] != true) return null
        return node.children().firstOrNull { child -> NOT_EXPRESSIONS.none { (child["kind"] as String).endsWith(it) } }
    }

    private fun qualify(scope: String, name: String) = if (scope.isEmpty()) name else "$scope::$name"

    private companion object {
        // Dependencies first (names resolve as they're declared): filament uses utils::EntityManager, ktxreader uses
        // image's bundles and filament.
        val FILTERS = listOf("utils::EntityManager", "image::Ktx", "filament::", "filamat::", "ktxreader::", "IBLPrefilterContext")
        // Math templates are huge and declare no API of their own.
        val SKIPPED = setOf("math")
        val NOT_EXPRESSIONS = listOf("Comment", "Attr", "Decl")
        val NOEXCEPT_EXPR = Regex("""noexcept\([^()]*\)\s*$""")

        /** Splits a method type like `Mode (int) const noexcept` into its return type and trailing qualifiers. */
        fun splitSignature(spelled: String): Pair<String, String> {
            val type = NOEXCEPT_EXPR.replace(spelled, "noexcept")
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

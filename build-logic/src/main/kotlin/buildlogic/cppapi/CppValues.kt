package buildlogic.cppapi

import buildlogic.cppapi.CppValue.Aggregate
import buildlogic.cppapi.CppValue.EnumConstant
import buildlogic.cppapi.CppValue.Literal
import buildlogic.cppapi.CppValue.Named
import buildlogic.cppapi.CppValue.Unsupported

/** Folds clang's initializer expressions into [CppValue]s; anything it can't fold stays [Unsupported]. */
internal class CppValues(private val scopes: CppScopes, private val constants: Map<String, CppValue>) {
    // Clang's ids name declarations exactly, but only within one dump; other dumps' constants resolve by name.
    private val variables = HashMap<String, String>()

    fun declared(id: String, qualified: String) { variables[id] = qualified }

    fun of(expr: Map<*, *>, scope: String): CppValue {
        val kind = expr["kind"] as String
        val inner = expr.children()
        return when (kind) {
            in TRANSPARENT -> inner.firstOrNull()?.let { of(it, scope) } ?: Unsupported(kind)
            "ConstantExpr" -> (expr["value"] as? String)?.let(::Literal) ?: of(inner.single(), scope)
            "IntegerLiteral", "StringLiteral" -> Literal(expr["value"] as String)
            // Clang prints a float's exact binary value (0.200000003); the shortest round trip is what was written.
            "FloatingLiteral" -> (expr["value"] as String).let { Literal(if (typeOf(expr) == "float") it.toFloat().toString() else it.toDouble().toString()) }
            "CXXBoolLiteralExpr" -> Literal(expr["value"].toString())
            "CXXNullPtrLiteralExpr", "GNUNullExpr" -> Literal("nullptr")
            "UnaryOperator" -> unary(expr["opcode"] as String, of(inner.single(), scope))
            "BinaryOperator" -> binary(expr["opcode"] as String, of(inner[0], scope), of(inner[1], scope))
            "DeclRefExpr" -> reference(expr["referencedDecl"] as Map<*, *>, scope)
            "InitListExpr" -> Aggregate(inner.map { of(it, scope) })
            // A list-initialized object keeps its elements; any other construction converts its single argument.
            "CXXConstructExpr", "CXXTemporaryObjectExpr" ->
                if (expr["list"] == true || inner.size != 1) Aggregate(inner.map { of(it, scope) }) else of(inner.single(), scope)
            "CallExpr" -> callee(inner.first()).let { if (it.startsWith("__builtin_huge_val")) Literal("inf") else Unsupported("call $it") }
            else -> Unsupported(kind)
        }
    }

    private fun callee(expr: Map<*, *>): String =
        (expr["referencedDecl"] as? Map<*, *>)?.get("name") as? String
            ?: (expr["referencedMemberDecl"] as? String)?.let { "member" }
            ?: expr.children().firstOrNull()?.let(::callee) ?: expr["kind"] as String

    private fun reference(decl: Map<*, *>, scope: String): CppValue {
        val name = decl["name"] as String
        return when (decl["kind"]) {
            "EnumConstantDecl" -> EnumConstant(typeOf(decl).removePrefix("const "), name)
            "VarDecl" -> (variables[decl["id"]] ?: scopes.lookup(name, scope)).let { Named(it ?: name, constants[it]) }
            else -> Unsupported("reference to ${decl["kind"]}")
        }
    }

    private fun unary(opcode: String, operand: CppValue) = when {
        operand !is Literal -> Unsupported("unary $opcode")
        opcode == "-" -> Literal(if (operand.text.startsWith("-")) operand.text.drop(1) else "-${operand.text}")
        opcode == "+" -> operand
        else -> Unsupported("unary $opcode")
    }

    private fun binary(opcode: String, left: CppValue, right: CppValue): CppValue {
        val a = ((left as? Named)?.value ?: left) as? Literal
        val b = ((right as? Named)?.value ?: right) as? Literal
        val x = a?.text?.toDoubleOrNull()
        val y = b?.text?.toDoubleOrNull()
        if (x == null || y == null) return Unsupported("binary $opcode")
        val result = when (opcode) {
            "+" -> x + y; "-" -> x - y; "*" -> x * y; "/" -> x / y
            else -> return Unsupported("binary $opcode")
        }
        val integral = listOf(a.text, b.text).none { '.' in it || 'e' in it }
        return Literal(if (integral && opcode != "/") result.toLong().toString() else result.toString())
    }

    private companion object {
        val TRANSPARENT = setOf(
            "ImplicitCastExpr", "ExprWithCleanups", "MaterializeTemporaryExpr", "CXXBindTemporaryExpr", "ParenExpr",
            "CXXFunctionalCastExpr", "CStyleCastExpr", "CXXStaticCastExpr",
        )
    }
}

internal fun typeOf(node: Map<*, *>): String {
    val type = node["type"] as Map<*, *>
    return (type["desugaredQualType"] ?: type["qualType"]) as String
}

internal fun spelledType(node: Map<*, *>) = (node["type"] as Map<*, *>)["qualType"] as String

@Suppress("UNCHECKED_CAST")
internal fun Map<*, *>.children() = (this["inner"] as? List<Map<*, *>>).orEmpty()

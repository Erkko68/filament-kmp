package buildlogic.cppapi

import java.math.BigInteger

/**
 * Filament's public headers as clang sees them: every record, enum, alias and constant in the dumped namespaces,
 * keyed by qualified name. [surface] narrows it to the API: the exported classes and every type their public
 * members reach.
 */
class CppApi(
    val records: Map<String, CppRecord>,
    val enums: Map<String, CppEnum>,
    val aliases: Map<String, CppType>,
    val constants: Map<String, CppValue>,
) {
    fun surface(): Set<String> {
        val seen = LinkedHashSet<String>()
        val queue = ArrayDeque(records.values.filter { it.exported }.map { it.name })
        while (queue.isNotEmpty()) {
            val name = queue.removeFirst()
            if (!seen.add(name)) continue
            records[name]?.let { record ->
                val methods = record.methods.filter { it.isPublic && it.isApi && !it.isDeprecated }
                (methods.flatMap { m -> m.params.map { it.type } + m.returns } + record.fields.filter { it.isPublic }.map { it.type })
                    .mapNotNullTo(queue) { it.decl }
            }
            aliases[name]?.decl?.let(queue::add)
        }
        return seen
    }
}

/** A type as the header spells it, and the declaration its base name resolves to (see [Kind]). */
class CppType(val spelling: String, val decl: String?, val kind: Kind) {
    enum class Kind { BUILTIN, DECLARED, EXTERNAL, FUNCTION, TEMPLATE_PARAMETER, UNRESOLVED }

    override fun toString() = when (kind) {
        Kind.DECLARED, Kind.TEMPLATE_PARAMETER -> "$spelling{$decl}"
        Kind.EXTERNAL -> "$spelling{ext:$decl}"
        Kind.UNRESOLVED -> "$spelling{?}"
        Kind.BUILTIN, Kind.FUNCTION -> spelling
    }
}

/**
 * [exported]: `*_PUBLIC`, or publicly nested in an exported class. [template]: a class template, named without its
 * arguments. [defaultConstructible]/[destructible]: publicly, so C can `new` and `delete` it.
 */
class CppRecord(
    val name: String,
    val exported: Boolean,
    val template: Boolean,
    val bases: List<String>,
    val methods: List<CppMethod>,
    val fields: List<CppField>,
    val defaultConstructible: Boolean,
    val destructible: Boolean,
)

/** [mangled] is null for members of class templates. [isApi]: written by hand, not deleted, not an operator. */
class CppMethod(
    val owner: String,
    val name: String,
    val mangled: String?,
    val returns: CppType,
    val params: List<CppParam>,
    val isStatic: Boolean,
    val isConst: Boolean,
    val isPublic: Boolean,
    val isDeprecated: Boolean,
    val isApi: Boolean,
) {
    override fun toString() = (if (isStatic) "static " else "") + "$returns $owner::$name(${params.joinToString()})" +
        (if (isConst) " const" else "")
}

class CppParam(val name: String, val type: CppType, val default: CppValue?) {
    override fun toString() = "$type $name" + (default?.let { " = $it" } ?: "")
}

class CppField(val name: String, val type: CppType, val default: CppValue?, val isPublic: Boolean)

class CppEnum(val name: String, val underlying: String?, val constants: List<Pair<String, BigInteger>>)

/** A default argument or field initializer, folded as far as the AST allows. */
sealed interface CppValue {
    /** A number, bool, string or `nullptr`, spelled as clang prints it. */
    data class Literal(val text: String) : CppValue { override fun toString() = text }
    data class EnumConstant(val enum: String, val constant: String) : CppValue { override fun toString() = "$enum::$constant" }
    /** A named constant, with its value when it's one of [CppApi.constants]. */
    data class Named(val name: String, val value: CppValue?) : CppValue { override fun toString() = "$name(=$value)" }
    /** A braced or constructor initializer; no elements means value-initialized (zero). */
    data class Aggregate(val elements: List<CppValue>) : CppValue { override fun toString() = elements.joinToString(prefix = "{", postfix = "}") }
    data class Unsupported(val kind: String) : CppValue { override fun toString() = "<$kind>" }
}

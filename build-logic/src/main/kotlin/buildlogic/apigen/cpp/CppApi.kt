package buildlogic.apigen.cpp

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
    /** The [constants] code outside can name: at namespace scope or public in a record. */
    val publicConstants: Set<String>,
    /** Namespace-level functions. */
    val functions: List<CppMethod>,
    /**
     * Regexes of the qualified names left out of the API; a name goes with everything nested in it, so `Record::.*`
     * keeps the record but none of its members. `Record::method\(Type, …\)` names one overload (by its parameters'
     * declared types).
     */
    val skipped: Set<String> = emptySet(),
) {
    fun skipping(names: Set<String>) = CppApi(records, enums, aliases, constants, publicConstants, functions, names)

    private val patterns = skipped.map(::Regex)
    private val skips = HashMap<String, String>()

    /** The [skipped] entry matching [name] itself, or null. */
    private fun skip(name: String) = skips.getOrPut(name) { patterns.firstOrNull { it.matches(name) }?.pattern.orEmpty() }.ifEmpty { null }

    /** The [skipped] entry that leaves out [name] (a record, member or function), or null. */
    fun skipReason(name: String): String? = generateSequence(name) { it.substringBeforeLast("::", "").ifEmpty { null } }.firstNotNullOfOrNull(::skip)

    /** Why [method] of [owner] is left out: it's skipped, or its signature uses a skipped record. */
    fun skipReason(method: CppMethod, owner: String = method.owner): String? =
        skipReason("$owner::${method.name}") ?: skip(overload(method, owner)) ?: usesSkipped(method.params.map { it.type } + method.returns)

    fun skipReason(field: CppField, owner: String): String? = skipReason("$owner::${field.name}") ?: usesSkipped(listOf(field.type))

    /** Why a constructor taking [params] is left out: its parameters use a skipped record. */
    fun skipReason(params: List<CppParam>): String? = usesSkipped(params.map { it.type })

    private fun usesSkipped(types: List<CppType>) = types.flatMap { it.withArgs() }.mapNotNull { it.decl }
        // Through aliases too: Ktx1Reader takes image::KtxInfo as ktxreader::KtxInfo.
        .map { decl -> generateSequence(decl) { aliases[it]?.decl }.last() }
        .firstNotNullOfOrNull { decl -> decl.takeIf { it in records }?.let(::skipReason)?.let { "uses $decl" } }

    private fun overload(method: CppMethod, owner: String = method.owner) =
        "$owner::${method.name}(${method.params.joinToString(", ") { parameter(it.type) }})"

    // A by-value parameter's const isn't part of the signature.
    private fun parameter(type: CppType) = type.decl ?: type.spelling.let { if ('*' in it || '&' in it) it else it.removePrefix("const ") }

    /** Everything a [skipped] entry can name, as it spells it. */
    private val names by lazy {
        (records.keys + enums.keys + publicConstants +
            records.values.flatMap { r -> (r.methods.map { it.name } + r.fields.map { it.name }).map { "${r.name}::$it" } } +
            enums.values.flatMap { e -> e.constants.map { "${e.name}::${it.first}" } } +
            functions.map { "${it.owner}::${it.name}" } + (records.values.flatMap { it.methods } + functions).map { overload(it) }).toSet()
    }

    /** [skipped] entries that name nothing, stale after an upstream rename or removal. */
    fun unknownSkips() = patterns.filter { names.none(it::matches) }.map { it.pattern }

    /** The overloads of [name] as [skipped] entries spell them. */
    fun overloads(name: String) = names.filter { it.startsWith("$name(") }

    /** The records API [headers] declare that code outside can name: what gets bound. */
    fun apiRecords(headers: Set<String>) = records.values.filter { it.accessible && it.header in headers && skipReason(it.name) == null }

    fun apiFunctions(headers: Set<String>) = functions.filter { it.header in headers && it.isApi && !it.isDeprecated && skipReason(it) == null }

    /** The enums [surface] reaches: Kotlin declares their values by hand. */
    fun apiEnums(headers: Set<String>) = surface(headers).mapNotNull { enums[it] }.filter { skipReason(it.name) == null }.sortedBy { it.name }

    /** The public constants of the records [surface] reaches. */
    fun apiConstants(headers: Set<String>) = surface(headers).let { surface ->
        publicConstants.filter { it.substringBeforeLast("::") in surface && skipReason(it) == null }.sorted()
    }

    /** The names code outside spells [name] by: Texture::InternalFormat for backend::TextureFormat. */
    fun aliasesOf(name: String): Set<String> = generateSequence(setOf(name)) { names ->
        (names + aliases.filterValues { it.decl in names }.keys).takeIf { it.size > names.size }
    }.last()

    /** [apiRecords] and [apiFunctions], plus every type their public members reach. */
    fun surface(headers: Set<String>): Set<String> {
        val seen = LinkedHashSet<String>()
        val queue = ArrayDeque(apiRecords(headers).map { it.name })
        apiFunctions(headers).flatMapTo(queue) { f -> (f.params.map { it.type } + f.returns).flatMap { it.withArgs() }.mapNotNull { it.decl } }
        while (queue.isNotEmpty()) {
            val name = queue.removeFirst()
            if (!seen.add(name)) continue
            records[name]?.let { record ->
                val methods = record.methods.filter { it.isPublic && it.isApi && !it.isDeprecated && skipReason(it, name) == null }
                val fields = record.fields.filter { it.isPublic && skipReason(it, name) == null }
                (methods.flatMap { m -> m.params.map { it.type } + m.returns } + fields.map { it.type })
                    .flatMap { it.withArgs() }.mapNotNullTo(queue) { it.decl }
            }
            aliases[name]?.withArgs()?.mapNotNullTo(queue) { it.decl }
        }
        return seen
    }
}

/**
 * A type as the header spells it, the declaration its base name resolves to (see [Kind]), and its template arguments
 * ([args]; a function type's are its return and parameter types, an array's its element).
 */
class CppType(val spelling: String, val decl: String?, val kind: Kind, val args: List<CppType> = emptyList()) {
    enum class Kind { BUILTIN, DECLARED, EXTERNAL, FUNCTION, TEMPLATE_PARAMETER, UNRESOLVED }

    /** This type and, recursively, its [args]. */
    fun withArgs(): List<CppType> = listOf(this) + args.flatMap { it.withArgs() }

    override fun toString() = when (kind) {
        Kind.DECLARED, Kind.TEMPLATE_PARAMETER -> "$spelling{$decl}"
        Kind.EXTERNAL -> "$spelling{ext:$decl}"
        Kind.UNRESOLVED -> "$spelling{?}"
        Kind.BUILTIN, Kind.FUNCTION -> spelling
    }
}

/**
 * [header]: relative to the include dir, null outside it. [exported]: `*_PUBLIC`, or publicly nested in an exported
 * class. [accessible]: nameable from outside the class. [template]: a class template or nested in one, named without the arguments.
 * [constructors]: the public ones' parameters, copies and moves aside. [destructible]: publicly. [allocatable]: no
 * base deletes `operator new` (Filament's handle classes do: only the Engine creates them). [copyable]: no copy
 * constructor is deleted or hidden, nor implicitly deleted by a declared move (deletion by members goes unseen).
 * [declaredDestructor]: declares a public one, so factory-made instances are deleted through it (MaterialProvider,
 * SurfaceOrientation).
 */
class CppRecord(
    val name: String,
    val header: String?,
    val exported: Boolean,
    val accessible: Boolean,
    val template: Boolean,
    val bases: List<String>,
    val methods: List<CppMethod>,
    val fields: List<CppField>,
    val constructors: List<List<CppParam>>,
    val destructible: Boolean,
    val allocatable: Boolean,
    val copyable: Boolean,
    val declaredDestructor: Boolean = false,
) {
    val defaultConstructible get() = constructors.any { ctor -> ctor.all { it.default != null } }
}

/**
 * A method, or a namespace-level function ([owner] is the namespace, [isStatic] set). [mangled] is null for members
 * of class templates. [isApi]: written by hand, not deleted, not an operator but `()`. [isOverride]: `override` or `final`.
 */
class CppMethod(
    val owner: String,
    val name: String,
    val header: String?,
    val mangled: String?,
    val returns: CppType,
    val params: List<CppParam>,
    val isStatic: Boolean,
    val isConst: Boolean,
    val isPublic: Boolean,
    val isDeprecated: Boolean,
    val isApi: Boolean,
    val isOverride: Boolean = false,
    /** A function template's named parameters, in order; null for a plain function. */
    val templateParameters: List<String>? = null,
    /** The header's `#if` condition around the declaration (e.g. `UTILS_HAS_THREADING`), or null. */
    val guard: String? = null,
) {
    val isTemplate get() = templateParameters != null

    /** This template with [arguments] (by parameter name) in place of its parameters. */
    fun instantiate(arguments: Map<String, CppType>): CppMethod {
        val substitute = { t: CppType ->
            arguments[t.decl?.removePrefix("$owner::$name::")]?.takeIf { t.kind == CppType.Kind.TEMPLATE_PARAMETER }
                ?.let { a -> CppType(t.spelling.replace(Regex("\\b${t.decl!!.substringAfterLast("::")}\\b"), a.spelling), a.decl, a.kind, a.args) } ?: t
        }
        return CppMethod(owner, name, header, mangled, substitute(returns), params.map { CppParam(it.name, substitute(it.type), it.default) },
            isStatic, isConst, isPublic, isDeprecated, isApi, isOverride, guard = guard)
    }

    override fun toString() = (if (isStatic) "static " else "") + "$returns $owner::$name(${params.joinToString()})" +
        (if (isConst) " const" else "")
}

class CppParam(val name: String, val type: CppType, val default: CppValue?) {
    override fun toString() = "$type $name" + (default?.let { " = $it" } ?: "")
}

class CppField(val name: String, val type: CppType, val default: CppValue?, val isPublic: Boolean, val isDeprecated: Boolean)

class CppEnum(val name: String, val header: String?, val underlying: String?, val constants: List<Pair<String, BigInteger>>)

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

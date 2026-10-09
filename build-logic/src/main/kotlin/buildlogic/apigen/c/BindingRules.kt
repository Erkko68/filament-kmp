package buildlogic.apigen.c

import buildlogic.apigen.ApiGenConfig
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppField
import buildlogic.apigen.cpp.CppMethod
import buildlogic.apigen.cpp.CppRecord

/**
 * What C binds of a record: `_create`/`_destroy`, upcasts, methods and field accessors. [CApiWriter] writes them.
 * [bound]: the records it writes them for, the API headers'.
 */
internal class BindingRules(private val api: CppApi, private val config: ApiGenConfig, private val bridges: CBridges, private val bound: Set<String>) {
    /** Records the API's functions and constructors take. */
    private val parameterTypes = (api.functions + api.records.values.flatMap { r -> r.methods.filter { it.isPublic && it.isApi } }).map { it.params }
        .plus(api.records.values.flatMap { it.constructors })
        .flatMap { params -> params.flatMap { it.type.withArgs() }.mapNotNull { it.decl } }.toSet()

    /**
     * Records the API reads: taken by value, `const&`, `const*` or `&&`, and the records their fields hold. The others
     * are only results.
     */
    private val inputRecords = run {
        val params = (api.functions + api.records.values.flatMap { it.methods }).flatMap { it.params } + api.records.values.flatMap { it.constructors.flatten() }
        val taken = params.map { it.type }
            .filter { t -> shape(t.spelling).let { it.const || it.indirection.lastOrNull().let { i -> i == null || i == "&&" } } }
            .mapNotNullTo(HashSet()) { it.decl }
        generateSequence(taken) { seen -> (seen + seen.flatMap { api.records[it]?.fields.orEmpty().mapNotNull { f -> f.type.decl } }).toHashSet().takeIf { it.size > seen.size } }.last()
    }

    /** `_create` per public constructor: worth it for something to call or something taking it, not a holder of static helpers. */
    fun creates(record: CppRecord) = !bridges.uninstantiated(record) && record.allocatable &&
        (record.methods.any { it.isPublic && it.isApi && !it.isStatic && !it.isDeprecated } || bridges.isValue(record.name) || record.name in parameterTypes)

    /**
     * `_destroy`: for what C creates, and factory-made instances deleted through a declared destructor. Not what
     * counts its references: C releases the one it holds.
     */
    fun destroys(record: CppRecord) = creates(record) && !bridges.refCounted(record) && (record.constructors.isNotEmpty() || record.declaredDestructor) && record.destructible

    /**
     * The bases C gets an `_as<Base>` upcast to, however far up (JobSystemThreadPool as a JobSystem): C can't upcast,
     * and a base's functions take the base's handle.
     */
    fun upcasts(record: CppRecord): List<CppRecord> = if (bridges.uninstantiated(record)) emptyList() else
        ancestors(record).distinct().filter { base ->
            base !in bridges.twins(record) && !bridges.uninstantiated(base) && base.methods.any { it.isPublic && it.isApi && !it.isDeprecated }
        }

    private fun ancestors(record: CppRecord): List<CppRecord> = record.bases.mapNotNull { api.records[it] }.flatMap { listOf(it) + ancestors(it) }

    /**
     * The methods C binds, its twins' too. Not deprecated ones, nor overloads only literals call; a const overload's
     * non-const twin takes the same C arguments and covers both. Nor overrides of a [bound] upcast base's methods, nor a
     * `(name, nameLength, …)` overload of a `(name, …)` one: C calls those through the base and the C string.
     */
    fun methods(record: CppRecord): List<CppMethod> {
        val all = (record.methods + bridges.twins(record).flatMap { it.methods })
            .filter { m -> m.isPublic && m.isApi && !m.isDeprecated && m.params.none { it.type.decl in config.literalOnly } }
        val bases = upcasts(record).filter { it.name in bound }
        val signatures = all.mapTo(HashSet()) { m -> m.name to m.params.map { passed(it.type.spelling) } }
        return all.filterNot { m -> m.isOverride && bases.any { b -> b.methods.any { it.name == m.name } } || lengthOverload(m, signatures) }
            .groupBy { m -> m.name to m.params.map { it.type.spelling } }.values.map { twins -> twins.firstOrNull { !it.isConst } ?: twins.first() }
    }

    /** A `size_t …Length` right after a `char*` that an overload without it covers (not an array's `size`). */
    private fun lengthOverload(m: CppMethod, signatures: Set<Pair<String, List<String>>>) = m.params.zipWithNext().withIndex().any { (i, p) ->
        shape(p.first.type.spelling).let { it.base == "char" && it.indirection == listOf("*") } && p.second.name.endsWith("Length") &&
            m.name to m.params.filterIndexed { j, _ -> j != i + 1 }.map { passed(it.type.spelling) } in signatures
    }

    /** What a parameter passes: a by-value one's top-level `const` and nullability aside. */
    private fun passed(spelling: String) = shape(spelling).let { if (it.indirection.isEmpty()) it.base else cSpelling(spelling) }

    /** The fields C gets a getter and setter for: a value record's public ones, its twins' first. */
    fun fields(record: CppRecord): List<CppField> = if (!bridges.isValue(record.name) || bridges.uninstantiated(record)) emptyList() else
        (bridges.twins(record).flatMap { it.fields } + record.fields).filter { it.isPublic && !it.isDeprecated }

    /** A pointer field of a record that's only a result: no setter, as C would have to keep the pointer. */
    fun getterOnly(record: CppRecord, field: CppField) = bridges.borrowsPointer(field.type) && record.name !in inputRecords
}

package buildlogic.apigen.c

import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppField
import buildlogic.apigen.cpp.CppRecord

/** What C binds of a record: `_create`/`_destroy`, upcasts, methods and field accessors. [CApiWriter] writes them. */
internal class BindingRules(private val api: CppApi, private val bridges: CBridges) {
    /** Records the API's functions and constructors take (IBLPrefilterContext, only passed to its filters). */
    private val parameterTypes = (api.functions + api.records.values.flatMap { r -> r.methods.filter { it.isPublic && it.isApi } }).map { it.params }
        .plus(api.records.values.flatMap { it.constructors })
        .flatMap { params -> params.flatMap { it.type.withArgs() }.mapNotNull { it.decl } }.toSet()

    /**
     * Records the API reads: taken by value, `const&`, `const*` or `&&`, and the records their fields hold. The others
     * are only results (Engine::FeatureFlag).
     */
    private val inputRecords = run {
        val params = (api.functions + api.records.values.flatMap { it.methods }).flatMap { it.params } + api.records.values.flatMap { it.constructors.flatten() }
        val taken = params.map { it.type }
            .filter { t -> shape(t.spelling).let { it.const || it.indirection.lastOrNull().let { i -> i == null || i == "&&" } } }
            .mapNotNullTo(HashSet()) { it.decl }
        generateSequence(taken) { seen -> (seen + seen.flatMap { api.records[it]?.fields.orEmpty().mapNotNull { f -> f.type.decl } }).toHashSet().takeIf { it.size > seen.size } }.last()
    }

    /** `_create` per public constructor: worth it for something to call or something taking it, not Color's static helpers. */
    fun creates(record: CppRecord) = !bridges.uninstantiated(record) && record.allocatable &&
        (record.methods.any { it.isPublic && it.isApi && !it.isStatic && !it.isDeprecated } || bridges.isValue(record.name) || record.name in parameterTypes)

    /** `_destroy`: for what C creates, and factory-made instances deleted through a declared destructor. */
    fun destroys(record: CppRecord) = creates(record) && (record.constructors.isNotEmpty() || record.declaredDestructor) && record.destructible

    /** The bases C gets an `_as<Base>` upcast to: C can't upcast, and a base's functions take the base's handle. */
    fun upcasts(record: CppRecord): List<CppRecord> = if (bridges.uninstantiated(record)) emptyList() else
        record.bases.mapNotNull { api.records[it] }.filter { base ->
            base !in bridges.twins(record) && !bridges.uninstantiated(base) && base.methods.any { it.isPublic && it.isApi && !it.isDeprecated }
        }

    /**
     * The methods C binds, its twins' too. Not deprecated ones, nor overloads only literals call; a const overload's
     * non-const twin takes the same C arguments and covers both.
     */
    fun methods(record: CppRecord) = (record.methods + bridges.twins(record).flatMap { it.methods })
        .filter { m -> m.isPublic && m.isApi && !m.isDeprecated && m.params.none { it.type.decl in LITERAL_ONLY } }
        .groupBy { m -> m.name to m.params.map { it.type.spelling } }.values.map { twins -> twins.firstOrNull { !it.isConst } ?: twins.first() }

    /** The fields C gets a getter and setter for: a value record's public ones, its twins' first. */
    fun fields(record: CppRecord): List<CppField> = if (!bridges.isValue(record.name) || bridges.uninstantiated(record)) emptyList() else
        (bridges.twins(record).flatMap { it.fields } + record.fields).filter { it.isPublic && !it.isDeprecated }

    /** A pointer field of a record that's only a result: no setter, as C would have to keep the pointer. */
    fun getterOnly(record: CppRecord, field: CppField) = bridges.borrowsPointer(field.type) && record.name !in inputRecords
}

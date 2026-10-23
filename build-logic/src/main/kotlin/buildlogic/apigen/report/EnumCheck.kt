package buildlogic.apigen.report

import buildlogic.apigen.cpp.CppApi

/** How many Kotlin enums were checked, and each constant whose value differs from C++'s. */
internal class EnumCheck(val checked: Int, val wrong: List<String>)

/**
 * Checks each Kotlin enum of [api]'s against C++: what it sends C, its ordinal or the number it's constructed with,
 * must be the C++ constant's of the same name, as clang reads it from the headers. Where `apiCoverage` reports what's
 * bound, this checks what's bound is right.
 */
internal fun enumCheck(api: CppApi, declarations: KotlinDeclarations): EnumCheck {
    var checked = 0
    val wrong = api.apiEnums.flatMap { enum ->
        val constants = enum.constants.filter { api.skipReason("${enum.name}::${it.first}") == null }
        // Kotlin names an enum as the API's alias does, and may declare the name more than once: the one with its constants.
        val kotlin = KotlinEnums.bodies(declarations.scope(api.aliasesOf(enum.name)).orEmpty()).map(KotlinEnums::values)
            .maxByOrNull { values -> constants.count { it.first in values } }?.takeIf { values -> constants.any { it.first in values } }
            ?: return@flatMap emptyList() // apiCoverage reports what Kotlin doesn't declare
        checked++
        // By bits: Kotlin spells an unsigned all-ones as -1.
        constants.filter { (name, value) -> kotlin[name]?.let { it.toLong() != value.toLong() } == true }
            .map { (name, value) -> "${enum.name}::$name is $value in C++, ${kotlin[name]} in Kotlin" }
    }
    return EnumCheck(checked, wrong)
}

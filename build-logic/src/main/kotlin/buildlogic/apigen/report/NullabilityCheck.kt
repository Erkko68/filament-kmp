package buildlogic.apigen.report

import buildlogic.apigen.c.shape
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppParam

/**
 * Checks the nullable parameters of the hand-written Kotlin functions against C++'s: a `T?` parameter of a function
 * named as a C++ method of the class it mirrors, itself named as a parameter every overload of that method takes by
 * reference or as a `_Nonnull` pointer, would pass a null C++ reads through (the forwarder fails the call).
 */
internal fun nullabilityCheck(api: CppApi, declarations: KotlinDeclarations): Check {
    var checked = 0
    val wrong = api.surface.mapNotNull { api.records[it] }.flatMap { record ->
        record.methods.filter { it.isPublic && it.isApi && !it.isDeprecated }.groupBy { it.name }.flatMap { (name, overloads) ->
            val nonNull = overloads.flatMap { it.params }.groupBy { it.name }.filterValues { it.all(::nonNull) }
            declarations.parameters(record.name, name).flatten().filter { it.name in nonNull }.mapNotNull { p ->
                checked++
                "${record.name}::$name(${p.name}) is ${nonNull.getValue(p.name).first().type.spelling} in C++, ${p.type} in Kotlin".takeIf { p.type.endsWith("?") }
            }
        }
    }
    return Check(checked, wrong.distinct())
}

private fun nonNull(param: CppParam) = shape(param.type.spelling).indirection.lastOrNull() == "&" || param.type.spelling.trim().endsWith("_Nonnull")

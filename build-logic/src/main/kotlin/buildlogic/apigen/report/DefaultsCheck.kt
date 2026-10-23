package buildlogic.apigen.report

import buildlogic.apigen.Naming
import buildlogic.apigen.cpp.CppApi
import buildlogic.apigen.cpp.CppValue
import java.math.BigDecimal

/**
 * Checks the defaults of the hand-written Kotlin classes mirroring C++ structs (options, configs) against C++'s field
 * initializers: a property named as the field ([Naming.memberPrefix] aside) must start with the same value. Only what
 * both sides spell comparably is compared: numbers (at a float's precision), booleans, null, infinity, enum constants
 * and named constants by name, an aggregate's elements one by one; not what a constructor makes of its arguments.
 */
internal fun defaultsCheck(api: CppApi, declarations: KotlinDeclarations, naming: Naming): Check {
    val prefix = Regex(naming.memberPrefix.ifEmpty { "$^" })
    var checked = 0
    val wrong = api.surface.mapNotNull { api.records[it] }.flatMap { record ->
        val defaults = declarations.defaults(record.name) ?: return@flatMap emptyList()
        record.fields.filter { it.isPublic && !it.isDeprecated && it.default != null }.mapNotNull { field ->
            val name = prefix.replace(field.name, "").let { if (it == field.name) it else it.replaceFirstChar(Char::lowercaseChar) }
            val kotlin = defaults[name] ?: return@mapNotNull null
            val same = matches(field.default!!, kotlin) ?: return@mapNotNull null
            checked++
            "${record.name}::${field.name} is ${field.default} in C++, $kotlin in Kotlin".takeIf { !same }
        }
    }
    return Check(checked, wrong)
}

/** Whether [kotlin] spells the value of [cpp]; null when the two can't be compared. */
private fun matches(cpp: CppValue, kotlin: String): Boolean? = when (cpp) {
    is CppValue.Literal -> literal(cpp.text, kotlin)
    is CppValue.EnumConstant -> constant(kotlin)?.let { it == cpp.constant }
    // Kotlin names the constant, or spells its value.
    is CppValue.Named -> constant(kotlin)?.takeIf { it == cpp.name.substringAfterLast("::") }?.let { true } ?: cpp.value?.let { matches(it, kotlin) }
    is CppValue.Aggregate -> elements(kotlin)?.let { elements ->
        // C++ value-initializes the elements it doesn't list.
        if (cpp.elements.size > elements.size) false
        else elements.mapIndexed { i, e -> if (i < cpp.elements.size) matches(cpp.elements[i], e) else literal("0", e) }
            .takeIf { null !in it }?.all { it == true }
    }
    is CppValue.Constructed, is CppValue.Unsupported -> null
}

private fun literal(cpp: String, kotlin: String): Boolean? = when (cpp) {
    "true", "false" -> kotlin.takeIf { it == "true" || it == "false" }?.let { it == cpp }
    "nullptr" -> true.takeIf { kotlin == "null" }
    "inf", "-inf" -> INFINITY.matchEntire(kotlin)?.let { (it.groupValues[1] == "NEGATIVE") == cpp.startsWith("-") }
    else -> number(cpp)?.let { c -> number(kotlin)?.let { k -> equal(c, k) } }
}

/**
 * Integers exactly, or as the same 32 or 64 bits (Kotlin spells an unsigned all-ones -1); a number either side writes
 * with a fraction, at a float's precision.
 */
private fun equal(a: BigDecimal, b: BigDecimal): Boolean {
    if (a.scale() > 0 || b.scale() > 0) return a.toFloat() == b.toFloat()
    val (x, y) = a.toBigInteger() to b.toBigInteger()
    val bits = maxOf(x.bitLength(), y.bitLength())
    return x == y || bits <= 32 && x.toInt() == y.toInt() || bits <= 64 && x.toLong() == y.toLong()
}

/** A number literal of either language: `0.5f`, `-1`, `0xFF`, `33_333_333L`, `1.0E-5`. */
private fun number(text: String): BigDecimal? {
    val s = text.replace("_", "").replace("'", "")
    val digits = s.removePrefix("-").removePrefix("+")
    val value = when {
        digits.startsWith("0x", ignoreCase = true) -> digits.drop(2).trimEnd('u', 'U', 'l', 'L').toBigIntegerOrNull(16)?.toBigDecimal()
        digits.startsWith("0b", ignoreCase = true) -> digits.drop(2).trimEnd('u', 'U', 'l', 'L').toBigIntegerOrNull(2)?.toBigDecimal()
        else -> digits.trimEnd('f', 'F', 'u', 'U', 'l', 'L').toBigDecimalOrNull()
    } ?: return null
    return if (s.startsWith("-")) value.negate() else value
}

/** The constant `Type.NAME` names, or null for something else. */
private fun constant(kotlin: String) = CONSTANT.matchEntire(kotlin)?.groupValues?.get(1)

/** An array's elements as Kotlin spells them: `floatArrayOf(…)`, or `FloatArray(n)`'s zeros. */
private fun elements(kotlin: String): List<String>? {
    ARRAY_OF.matchEntire(kotlin)?.let { m -> return split(m.groupValues[1], ',').map { it.trim() }.filter { it.isNotEmpty() } }
    return ARRAY.matchEntire(kotlin)?.groupValues?.get(1)?.toIntOrNull()?.let { n -> List(n) { "0" } }
}

private val INFINITY = Regex("""(?:Float|Double)\.(POSITIVE|NEGATIVE)_INFINITY""")
private val CONSTANT = Regex("""(?:[A-Z]\w*\.)+([A-Za-z_]\w*)""")
private val ARRAY_OF = Regex("""(?:float|double|int|long|short|byte|boolean|u?int|u?long)?[aA]rrayOf\((.*)\)""", RegexOption.DOT_MATCHES_ALL)
private val ARRAY = Regex("""(?:Float|Double|Int|Long|Short|Byte|Boolean)Array\((\d+)\)""")

package io.github.erkko68.filament.interop

import io.github.erkko68.filament.InternalFilamentApi

/**
 * Values that [count] elements of [components] values each take in an array of [elementBytes]-sized values,
 * laid out [strideBytes] apart (0: tightly packed).
 */
@InternalFilamentApi
fun strided(count: Int, components: Int, strideBytes: Int, elementBytes: Int): Int = when {
    count <= 0 -> 0
    strideBytes == 0 -> count * components
    else -> (count - 1) * strideBytes / elementBytes + components
}

/**
 * The arrays of a C mesh builder that reads them only in build(), when the vertex and triangle counts are
 * known whatever order they were set in: records each array's length and checks it covers them.
 */
@InternalFilamentApi
class MeshInputs {
    var vertices = 0
    var triangles = 0
    private val checks = mutableListOf<() -> String?>()

    /** A per-vertex array of [components] values each, [strideBytes] apart. */
    fun vertexArray(name: String, length: Int, components: Int, strideBytes: Int, elementBytes: Int = 4) {
        checks += { shortfall(name, length, strided(vertices, components, strideBytes, elementBytes)) }
    }

    /** Triangle indices, 3 per triangle. */
    fun triangleArray(length: Int) {
        checks += { shortfall("triangles", length, triangles * 3) }
    }

    /** The first array too short for the counts, described, or null. */
    fun shortfall(): String? = checks.firstNotNullOfOrNull { it() }

    private fun shortfall(name: String, length: Int, needed: Int) =
        if (length < needed) "$name holds $length values but $needed are needed" else null
}

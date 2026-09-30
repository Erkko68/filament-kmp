package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * An axis-aligned box represented by its min and max coordinates.
 *
 * Vectors are `float3` arrays and matrices column-major `mat3f`/`mat4f` arrays, as in the rest of the API.
 */
class Aabb(
    /** min coordinates */
    var min: FloatArray = FloatArray(3) { Float.MAX_VALUE },
    /** max coordinates */
    var max: FloatArray = FloatArray(3) { -Float.MAX_VALUE },
) {
    /** The center of the box: (max + min) / 2. */
    fun center(): FloatArray = useNative { a -> floats(3) { FilaAabb_center(a, it) } }

    /** The half-extent of the box: (max - min) / 2. */
    fun extent(): FloatArray = useNative { a -> floats(3) { FilaAabb_extent(a, it) } }

    /** Whether the box is empty, i.e. its volume is null or negative. */
    fun isEmpty(): Boolean = useNative { FilaAabb_isEmpty(it) }

    /** The 8 corners of a box. */
    class Corners(val vertices: Array<FloatArray>) {
        val size: Int get() = vertices.size
        operator fun get(i: Int): FloatArray = vertices[i]
    }

    /** The 8 corner vertices of the box. */
    fun getCorners(): Corners = useNative { a ->
        withHandle({ FilaAabbCorners_create() }, { FilaAabbCorners_destroy(it) }) { c ->
            FilaAabb_getCorners(a, c)
            val xyz = floats(8 * 3) { FilaAabbCorners_getVertices(c, it, 8) }
            Corners(Array(8) { xyz.copyOfRange(it * 3, it * 3 + 3) })
        }
    }

    /** The maximum signed distance from [p] to the box: negative if the box contains [p]. */
    fun contains(p: FloatArray): Float = useNative { a -> p.usePinned { FilaAabb_contains(a, it) } }

    /** The bounding box of this box transformed by the affine transform [m] (`mat4f`). */
    fun transform(m: FloatArray): Aabb = useNative { a -> aabb { out -> m.usePinned { FilaAabb_transform_mat4f(a, it, out) } } }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Aabb && min.contentEquals(other.min) && max.contentEquals(other.max))

    override fun hashCode(): Int = 31 * min.contentHashCode() + max.contentHashCode()

    override fun toString(): String = "Aabb(min=${min.contentToString()}, max=${max.contentToString()})"

    /** A native copy of this box for the duration of [block]. */
    internal fun <T> useNative(block: (NativePointer) -> T): T = withHandle({ FilaAabb_create() }, { FilaAabb_destroy(it) }) { a ->
        interopScope {
            FilaAabb_setMin(a, toInterop(min))
            FilaAabb_setMax(a, toInterop(max))
        }
        block(a)
    }

    companion object {
        /** The bounding box of [box] transformed by the linear transform [m] (`mat3f`) and the translation [t]. */
        fun transform(m: FloatArray, t: FloatArray, box: Aabb): Aabb =
            box.useNative { a -> aabb { out -> interopScope { FilaAabb_transform_mat3f_float3_Aabb(toInterop(m), toInterop(t), a, out) } } }
    }
}

/** The Aabb [fill] writes into a native one (for other modules' functions returning an Aabb). */
@InternalFilamentApi
fun aabb(fill: (NativePointer) -> Unit): Aabb = withHandle({ FilaAabb_create() }, { FilaAabb_destroy(it) }) { a ->
    fill(a)
    Aabb(floats(3) { FilaAabb_getMin(a, it) }, floats(3) { FilaAabb_getMax(a, it) })
}

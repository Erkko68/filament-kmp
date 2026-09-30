package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * An axis-aligned 3D box represented by its center and half-extent.
 *
 * Vectors are `float3` arrays and matrices column-major `mat3f`/`mat4f` arrays, as in the rest of the API.
 */
class Box(
    /** Center of the 3D box */
    var center: FloatArray = FloatArray(3),
    /** Half extent from the center on all 3 axes */
    var halfExtent: FloatArray = FloatArray(3),
) {
    /** Whether the box is empty, i.e. its extents are zero. */
    fun isEmpty(): Boolean = useNative { FilaBox_isEmpty(it) }

    /** The lowest coordinates corner of the box: center - halfExtent. */
    fun getMin(): FloatArray = useNative { b -> floats(3) { FilaBox_getMin(b, it) } }

    /** The largest coordinates corner of the box: center + halfExtent. */
    fun getMax(): FloatArray = useNative { b -> floats(3) { FilaBox_getMax(b, it) } }

    /** Initializes the box from its [min] and [max] corners. @return This box. */
    fun set(min: FloatArray, max: FloatArray): Box = useNative { b ->
        interopScope { FilaBox_set(b, toInterop(min), toInterop(max)) }
        load(b)
    }

    /** Grows this box to the bounding box of the union of this and [box]. @return This box. */
    fun unionSelf(box: Box): Box = useNative { b -> box.useNative { FilaBox_unionSelf(b, it) }; load(b) }

    /** A box centered in [tr] with the same extent as this one. */
    fun translateTo(tr: FloatArray): Box = useNative { b -> box { out -> tr.usePinned { FilaBox_translateTo(b, it, out) } } }

    /** The smallest sphere containing this box: its center (xyz) and radius (w). */
    fun getBoundingSphere(): FloatArray = useNative { b -> floats(4) { FilaBox_getBoundingSphere(b, it) } }

    override fun equals(other: Any?): Boolean =
        this === other || (other is Box && center.contentEquals(other.center) && halfExtent.contentEquals(other.halfExtent))

    override fun hashCode(): Int = 31 * center.contentHashCode() + halfExtent.contentHashCode()

    override fun toString(): String = "Box(center=${center.contentToString()}, halfExtent=${halfExtent.contentToString()})"

    /** A native copy of this box for the duration of [block]. */
    internal fun <T> useNative(block: (NativePointer) -> T): T = withHandle({ FilaBox_create() }, { FilaBox_destroy(it) }) { b ->
        interopScope {
            FilaBox_setCenter(b, toInterop(center))
            FilaBox_setHalfExtent(b, toInterop(halfExtent))
        }
        block(b)
    }

    private fun load(b: NativePointer) = apply {
        center = floats(3) { FilaBox_getCenter(b, it) }
        halfExtent = floats(3) { FilaBox_getHalfExtent(b, it) }
    }

    companion object {
        /** The bounding box of [box] transformed by the linear transform [m] (`mat3f`) and the translation [t]. */
        fun transform(m: FloatArray, t: FloatArray, box: Box): Box =
            box.useNative { b -> box { out -> interopScope { FilaBox_transform(toInterop(m), toInterop(t), b, out) } } }
    }
}

/** The bounding box of [box] transformed by the rigid transform [m] (`mat4f`). */
fun rigidTransform(box: Box, m: FloatArray): Box =
    Box.transform(FloatArray(9) { m[(it / 3) * 4 + it % 3] }, m.copyOfRange(12, 15), box)

/** The Box [fill] writes into a native one. */
internal inline fun box(fill: (NativePointer) -> Unit): Box = withHandle({ FilaBox_create() }, { FilaBox_destroy(it) }) { b ->
    fill(b)
    Box(floats(3) { FilaBox_getCenter(b, it) }, floats(3) { FilaBox_getHalfExtent(b, it) })
}

/** [size] floats [fill] writes. */
internal inline fun floats(size: Int, fill: (NativePointer) -> Unit): FloatArray = FloatArray(size).also { it.usePinned(fill) }

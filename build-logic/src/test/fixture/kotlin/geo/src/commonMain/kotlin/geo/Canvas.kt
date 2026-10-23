package geo

import geo.capi.*
import geo.interop.NativePointer

// The hand-written API: the externals it calls are wrapped, the rest unwrapped.
class Canvas {
    private val handle = GeoCanvas_create()

    val count: Int get() = GeoCanvas_GetCount(handle)

    fun add(shape: Shape) = GeoCanvas_Add_Shape(handle, shape.handle)

    fun close() = GeoCanvas_destroy(handle)
}

class Shape internal constructor(internal val handle: NativePointer) {
    val kind: Kind get() = GeoShape_GetKind(handle).let { value -> Kind.entries.first { it.value == value } }
}

enum class Kind(internal val value: Int) { CIRCLE(0), POLYGON(5) }

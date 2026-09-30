package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.interop.withHandle

/**
 * Viewport specifies the rectangular region where a View's Scene is rendered.
 *
 * The viewport defines where the content of the View (the Scene) is rendered in the render
 * target. The render target is automatically clipped to the Viewport. All coordinates are in
 * window/screen space with the origin typically at the bottom-left.
 *
 * @param left Left coordinate of the viewport (default: 0)
 * @param bottom Bottom coordinate of the viewport (default: 0)
 * @param width Width of the viewport in pixels (default: 0)
 * @param height Height of the viewport in pixels (default: 0)
 */
data class Viewport(
    var left: Int = 0,
    var bottom: Int = 0,
    var width: Int = 0,
    var height: Int = 0
) {
    /** Whether the viewport has no area (zero width or height). */
    fun empty(): Boolean = width == 0 || height == 0
    /** The right coordinate: [left] + [width]. */
    fun right(): Int = left + width
    /** The top coordinate: [bottom] + [height]. */
    fun top(): Int = bottom + height
}

/** A native Viewport with this one's rectangle, for the duration of [block]. */
internal inline fun <T> Viewport.useNative(block: (NativePointer) -> T): T =
    withHandle({ FilaViewport_create_int32_t_int32_t_uint32_t_uint32_t(left, bottom, width, height) }, { FilaViewport_destroy(it) }, block)

/** The Viewport [fill] writes into a native one. */
internal inline fun viewportOf(fill: (NativePointer) -> Unit): Viewport = withHandle({ FilaViewport_create() }, { FilaViewport_destroy(it) }) {
    fill(it)
    Viewport(FilaViewport_getLeft(it), FilaViewport_getBottom(it), FilaViewport_getWidth(it), FilaViewport_getHeight(it))
}

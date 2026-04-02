package dev.filament.kmp

/**
 * Specifies a rectangular region within a render target in terms of pixel coordinates.
 *
 * <p>
 * The rectangle spans from <code>(left,bottom)</code> to <code>(left+width-1, top+height-1)</code>,
 * inclusive. Width and height must be non-negative.
 * </p>
 *
 * @see View#setViewport
 */
expect class Viewport {
    constructor(left: Int, bottom: Int, width: Int, height: Int)

    // left coordinate in pixels
    var left: Int

    // bottom coordinate in pixels
    var bottom: Int

    // width in pixels
    var width: Int

    // height in pixels
    var height: Int
}


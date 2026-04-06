package dev.filament.kmp

/**
 * Specifies a rectangular region within a render target in terms of pixel coordinates.
 */
expect class Viewport {
    constructor(left: Int, bottom: Int, width: Int, height: Int)

    var left: Int
    var bottom: Int
    var width: Int
    var height: Int
}

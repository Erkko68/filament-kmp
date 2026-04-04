package dev.filament.kmp

/**
 * NativeSurface wraps a platform native surface suitable for creating SwapChains.
 */
expect class NativeSurface {
    constructor(width: Int, height: Int)

    fun dispose()

    fun getWidth(): Int

    fun getHeight(): Int

    fun getNativeObject(): Long

    internal fun invalidate()
}

package dev.filament.kmp

/**
 * NativeSurface wraps a platform native surface suitable for creating SwapChains.
 */
expect class NativeSurface {
    constructor(width: Int, height: Int)

    fun dispose()

    val width: Int

    val height: Int

    val nativeObject: Long

    internal fun invalidate()
}

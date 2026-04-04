package dev.filament.kmp

actual class NativeSurface actual constructor(
    private val width: Int,
    private val height: Int,
) {
    private var handle: Long = 0L

    actual fun dispose() {
        handle = 0L
    }

    actual fun getWidth(): Int = width

    actual fun getHeight(): Int = height

    actual fun getNativeObject(): Long = handle

    actual internal fun invalidate() {
        handle = 0L
    }
}

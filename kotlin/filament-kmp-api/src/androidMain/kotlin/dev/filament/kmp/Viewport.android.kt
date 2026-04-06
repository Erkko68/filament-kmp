package dev.filament.kmp

actual class Viewport actual constructor(
    actual var left: Int,
    actual var bottom: Int,
    actual var width: Int,
    actual var height: Int
) {
    internal val nativeViewport: com.google.android.filament.Viewport
        get() = com.google.android.filament.Viewport(left, bottom, width, height)
}

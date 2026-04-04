package dev.filament.kmp

import com.google.android.filament.NativeSurface as AndroidNativeSurface

actual class NativeSurface internal constructor(
    internal var androidNativeSurface: AndroidNativeSurface?,
) {
    actual constructor(width: Int, height: Int) : this(AndroidNativeSurface(width, height))

    actual fun dispose() {
        val surface = requireNotNull(androidNativeSurface) { "Calling method on destroyed NativeSurface" }
        surface.dispose()
        androidNativeSurface = null
    }

    actual fun getWidth(): Int =
        requireNotNull(androidNativeSurface) { "Calling method on destroyed NativeSurface" }.width

    actual fun getHeight(): Int =
        requireNotNull(androidNativeSurface) { "Calling method on destroyed NativeSurface" }.height

    actual fun getNativeObject(): Long =
        requireNotNull(androidNativeSurface) { "Calling method on destroyed NativeSurface" }.nativeObject

    actual internal fun invalidate() {
        androidNativeSurface = null
    }
}

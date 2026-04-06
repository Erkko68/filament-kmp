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

    actual val width: Int
        get() =
        requireNotNull(androidNativeSurface) { "Calling method on destroyed NativeSurface" }.width

    actual val height: Int
        get() =
        requireNotNull(androidNativeSurface) { "Calling method on destroyed NativeSurface" }.height

    actual val nativeObject: Long
        get() =
        requireNotNull(androidNativeSurface) { "Calling method on destroyed NativeSurface" }.nativeObject

    actual internal fun invalidate() {
        androidNativeSurface = null
    }
}

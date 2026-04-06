package dev.filament.kmp

import com.google.android.filament.RenderTarget as AndroidRenderTarget

actual class RenderTarget internal constructor(val nativeRenderTarget: AndroidRenderTarget) {
    actual fun getNativeObject(): Long = nativeRenderTarget.nativeObject
}

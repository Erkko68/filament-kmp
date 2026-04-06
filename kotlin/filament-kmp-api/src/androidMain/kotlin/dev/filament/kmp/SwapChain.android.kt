package dev.filament.kmp

import com.google.android.filament.SwapChain as AndroidSwapChain

actual class SwapChain internal constructor(val nativeSwapChain: AndroidSwapChain) {
    actual fun getNativeObject(): Long = nativeSwapChain.nativeObject
}

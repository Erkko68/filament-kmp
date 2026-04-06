package dev.filament.kmp

import com.google.android.filament.Fence as AndroidFence

actual class Fence internal constructor(val nativeFence: AndroidFence) {
    actual fun getNativeObject(): Long = nativeFence.nativeObject
}

package dev.filament.kmp

import com.google.android.filament.Renderer as AndroidFilamentRenderer

actual class Renderer internal constructor(
    internal var androidRenderer: AndroidFilamentRenderer?,
) {
    actual val isValid: Boolean
        get() = androidRenderer != null

    actual internal fun invalidate() {
        androidRenderer = null
    }
}


package dev.filament.kmp

import com.google.android.filament.Scene as AndroidFilamentScene

actual class Scene internal constructor(
    internal var androidScene: AndroidFilamentScene?,
) {
    actual val isValid: Boolean
        get() = androidScene != null

    actual internal fun invalidate() {
        androidScene = null
    }
}


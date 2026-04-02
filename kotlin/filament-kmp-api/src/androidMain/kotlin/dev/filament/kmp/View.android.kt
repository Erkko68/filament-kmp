package dev.filament.kmp

import com.google.android.filament.View as AndroidFilamentView

actual class View internal constructor(
    internal var androidView: AndroidFilamentView?,
) {
    private var sceneRef: Scene? = null

    actual val isValid: Boolean
        get() = androidView != null

    actual var scene: Scene?
        get() = sceneRef
        set(value) {
            val view = requireNotNull(androidView) { "View is invalid." }
            view.scene = value?.androidScene
            sceneRef = value
        }

    actual internal fun invalidate() {
        androidView = null
        sceneRef = null
    }
}


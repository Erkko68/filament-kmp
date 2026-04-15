package io.github.erkko68.filament

import io.github.erkko68.filament.jni.NativeSurface as JniNativeSurface

actual class NativeSurface {
    val nativeSurface: JniNativeSurface

    constructor(width: Int, height: Int) {
        nativeSurface = JniNativeSurface(width, height)
    }

    constructor(nativeObject: Long) {
        nativeSurface = JniNativeSurface(nativeObject)
    }

    fun dispose() {
        nativeSurface.dispose()
    }
}

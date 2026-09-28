package io.github.erkko68.filament.jni

import android.view.Surface

/** Android-only JNI entry points (native side: src/androidMain/cpp/FilaAndroid.cpp); the rest is in [FilaJni]. */
object FilaAndroid {
    /** `ANativeWindow*` for [surface], for FilaEngine_createSwapChain; release with [releaseWindow]. */
    @JvmStatic external fun windowFromSurface(surface: Surface): Long
    @JvmStatic external fun releaseWindow(window: Long)
}

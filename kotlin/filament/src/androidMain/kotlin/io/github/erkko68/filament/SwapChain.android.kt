package io.github.erkko68.filament

import com.google.android.filament.SwapChain as AndroidSwapChain

actual class SwapChain internal constructor(val nativeSwapChain: AndroidSwapChain) {
    actual enum class FrameRateCompatibility { DEFAULT, FIXED_SOURCE }
    actual enum class ChangeFrameRateStrategy { ONLY_IF_SEAMLESS, ALWAYS }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = AndroidSwapChain.isProtectedContentSupported(engine.nativeEngine)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = AndroidSwapChain.isSRGBSwapChainSupported(engine.nativeEngine)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = AndroidSwapChain.isMSAASwapChainSupported(engine.nativeEngine, samples)
    }

    actual val nativeWindow: Any? get() = nativeSwapChain.nativeWindow
    
    actual fun setFrameCompletedCallback(callback: () -> Unit) {
        nativeSwapChain.setFrameCompletedCallback(Runnable::run, Runnable { callback() })
    }

    actual fun setFrameScheduledCallback(callback: () -> Unit) {
        nativeSwapChain.setFrameScheduledCallback(Runnable::run, Runnable { callback() })
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = nativeSwapChain.isFrameScheduledCallbackSet

    actual fun isFrameRateChangeSupported(): Boolean = nativeSwapChain.isFrameRateChangeSupported()

    actual fun setFrameRate(frameRate: Float) = nativeSwapChain.setFrameRate(frameRate)

    actual fun setFrameRate(frameRate: Float, compatibility: FrameRateCompatibility, strategy: ChangeFrameRateStrategy) =
        nativeSwapChain.setFrameRate(
            frameRate,
            AndroidSwapChain.FrameRateCompatibility.values()[compatibility.ordinal],
            AndroidSwapChain.ChangeFrameRateStrategy.values()[strategy.ordinal]
        )

    actual val nativeObject: Long get() = nativeSwapChain.nativeObject
}

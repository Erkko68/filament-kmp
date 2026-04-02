package dev.filament.kmp

actual class SwapChain {
    actual val isValid: Boolean
        get() = TODO("Not yet implemented")

    actual fun getNativeWindow(): Any? {
        TODO("Not yet implemented")
    }

    actual fun setFrameCompletedCallback(handler: Any, callback: () -> Unit) {
        TODO("Not yet implemented")
    }

    actual fun setFrameScheduledCallback(handler: Any, callback: () -> Unit) {
        TODO("Not yet implemented")
    }

    actual fun isFrameScheduledCallbackSet(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long {
        TODO("Not yet implemented")
    }

    actual internal fun invalidate() {
    }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean {
            TODO("Not yet implemented")
        }

        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean {
            TODO("Not yet implemented")
        }

        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean {
            TODO("Not yet implemented")
        }
    }
}


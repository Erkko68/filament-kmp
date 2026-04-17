package io.github.erkko68.filament

actual class SwapChain {
    actual fun getNativeWindow(): Any? {
        TODO("Not yet implemented")
    }

    actual fun setFrameCompletedCallback(handler: Any?, callback: () -> Unit) {
    }

    actual fun setFrameScheduledCallback(handler: Any?, callback: () -> Unit) {
    }

    actual fun isFrameScheduledCallbackSet(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean {
            TODO("Not yet implemented")
        }

        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean {
            TODO("Not yet implemented")
        }

        actual fun isMSAASwapChainSupported(
            engine: Engine,
            samples: Int
        ): Boolean {
            TODO("Not yet implemented")
        }
    }
}
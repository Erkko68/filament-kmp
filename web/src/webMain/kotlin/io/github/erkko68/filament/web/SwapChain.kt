package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class SwapChain : JsAny {
fun setFrameScheduledCallback(callback: (() -> Unit)?): Unit
fun isFrameScheduledCallbackSet(): Boolean

companion object {
fun isSRGBSwapChainSupported(engine: Engine): Boolean
fun isProtectedContentSupported(engine: Engine): Boolean
fun isMSAASwapChainSupported(engine: Engine, sampleCount: Double): Boolean
}
}

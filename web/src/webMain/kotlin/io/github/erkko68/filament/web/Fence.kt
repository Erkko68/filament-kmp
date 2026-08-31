package io.github.erkko68.filament.web

/**
 * A synchronization primitive that can be waited on.
 * WebGL cannot block the main thread, so a timeout of 0 (a non-blocking
 * query of the fence state) is the only sensible value.
 * @see Engine.createFence
 * @see Engine.destroyFence
 */
external class Fence : JsAny {
fun wait(mode: Fence_Mode = definedExternally, timeout: Double = definedExternally): FenceStatus
}

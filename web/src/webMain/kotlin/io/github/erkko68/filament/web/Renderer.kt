// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Renderer : JsAny {
fun render(swapChain: SwapChain, view: View): Unit
fun setClearOptions(options: Renderer_ClearOptions): Unit
fun renderView(view: View): Unit
fun beginFrame(swapChain: SwapChain): Boolean
fun endFrame(): Unit
fun getUserTime(): Double
fun resetUserTime(): Unit
fun getMaterialTime(): Double
fun setMaterialTimeEpoch(timeEpochInNs: Double): Unit
fun pauseRenderThread(timeNs: Double): Unit
fun skipNextFrames(frames: Double): Unit
fun getFrameToSkipCount(): Double
fun shouldRenderFrame(): Boolean
fun setVsyncTime(steadyClockTimeNano: Double): Unit
fun skipFrame(vsyncSteadyClockTimeNano: Double): Unit
}

// ── Renderer ──────────────────────────────────────────────────────────────────

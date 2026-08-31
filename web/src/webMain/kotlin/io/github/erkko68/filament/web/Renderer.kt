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
fun getClearOptions(): Renderer_ClearOptions
fun copyFrame(dstSwapChain: SwapChain, dstViewport: float4, srcViewport: float4, flags: Double): Unit
fun readPixels(x: Double, y: Double, width: Double, height: Double, format: PixelDataFormat, type: PixelDataType, callback: (pixels: org.khronos.webgl.Uint8Array) -> Unit): Unit
fun readPixels(renderTarget: RenderTarget, x: Double, y: Double, width: Double, height: Double, format: PixelDataFormat, type: PixelDataType, callback: (pixels: org.khronos.webgl.Uint8Array) -> Unit): Unit
fun renderStandaloneView(view: View): Unit
fun setPresentationTime(monotonicClockNs: Double): Unit
fun setDesiredPresentationTime(monotonicClockNs: Double): Unit
fun setRenderingDeadline(monotonicClockNs: Double): Unit
}

// ── Renderer ──────────────────────────────────────────────────────────────────

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Skybox : JsAny {
fun setColor(color: float4): Unit
fun getTexture(): Texture?
fun setLayerMask(select: Double, value: Double): Unit
fun getLayerMask(): Double
fun getIntensity(): Double
companion object {
fun Builder(): Skybox_Builder
}
}

// ── Skybox ────────────────────────────────────────────────────────────────────

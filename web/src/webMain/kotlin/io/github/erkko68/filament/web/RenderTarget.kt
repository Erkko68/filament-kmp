// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class RenderTarget : JsAny {
fun getMipLevel(): Double
fun getMipLevel(attachment: RenderTarget_AttachmentPoint): Double
fun getFace(): Texture_CubemapFace
fun getFace(attachment: RenderTarget_AttachmentPoint): Texture_CubemapFace
fun getLayer(): Double
fun getLayer(attachment: RenderTarget_AttachmentPoint): Double
fun getTexture(attachment: RenderTarget_AttachmentPoint): Texture
fun getSupportedColorAttachmentsCount(): Double
companion object {
fun Builder(): RenderTarget_Builder
}
}

// ── Texture ───────────────────────────────────────────────────────────────────
// ── RenderTarget — upstream declares these without the attachment argument ─────

// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("RenderTarget\$Builder")
external class RenderTarget_Builder : JsAny {
fun texture(attachment: RenderTarget_AttachmentPoint, texture: Texture): RenderTarget_Builder
fun mipLevel(attachment: RenderTarget_AttachmentPoint, mipLevel: Double): RenderTarget_Builder
fun face(attachment: RenderTarget_AttachmentPoint, face: Texture_CubemapFace): RenderTarget_Builder
fun layer(attachment: RenderTarget_AttachmentPoint, layer: Double): RenderTarget_Builder
fun build(engine: Engine): RenderTarget
}

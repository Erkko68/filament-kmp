package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

// Clients should use createTextureFromKtx1/ImageFile helper functions if low level control is not needed
@JsName("Texture\$Builder")
external class Texture_Builder : JsAny {
fun width(width: Double): Texture_Builder
fun height(height: Double): Texture_Builder
fun depth(depth: Double): Texture_Builder
fun levels(levels: Double): Texture_Builder
fun samples(samples: Double): Texture_Builder
fun sampler(sampler: Texture_Sampler): Texture_Builder
fun external(): Texture_Builder
fun swizzle(r: Texture_Swizzle, g: Texture_Swizzle, b: Texture_Swizzle, a: Texture_Swizzle): Texture_Builder
fun format(format: Texture_InternalFormat): Texture_Builder
fun usage(usage: Double): Texture_Builder
fun build(engine: Engine): Texture
}

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("IndirectLight\$Builder")
external class IndirectLight_Builder : JsAny {
fun reflections(cubemap: Texture): IndirectLight_Builder
fun irradiance(cubemap: Texture): IndirectLight_Builder
fun irradiance(nbands: Double, f32array: JsAny?): IndirectLight_Builder
fun radiance(nbands: Double, f32array: JsAny?): IndirectLight_Builder
fun intensity(value: Double): IndirectLight_Builder
fun rotation(value: mat3): IndirectLight_Builder
fun build(engine: Engine): IndirectLight
}

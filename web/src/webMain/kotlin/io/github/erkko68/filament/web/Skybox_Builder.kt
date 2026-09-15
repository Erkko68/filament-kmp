package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("Skybox\$Builder")
external class Skybox_Builder : JsAny {
fun build(engine: Engine): Skybox
fun color(rgba: float4): Skybox_Builder
fun environment(envmap: Texture): Skybox_Builder
fun showSun(show: Boolean): Skybox_Builder
fun priority(priority: Double): Skybox_Builder
fun intensity(envIntensity: Double): Skybox_Builder
}

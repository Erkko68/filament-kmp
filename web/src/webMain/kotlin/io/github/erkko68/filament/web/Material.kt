package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Material : JsAny {
fun createInstance(): MaterialInstance
fun createNamedInstance(name: String): MaterialInstance
fun getDefaultInstance(): MaterialInstance
fun getName(): String
fun getParameterTransformName(parameter: String): String
fun hasParameter(name: String): Boolean
fun getParameterCount(): Double
fun getParameters(): js.array.ReadonlyArray<Material_ParameterInfo>
fun getShading(): Shading
fun getInterpolation(): Interpolation
fun getBlendingMode(): BlendingMode
fun getRefractionMode(): RefractionMode
fun getRefractionType(): RefractionType
fun getReflectionMode(): ReflectionMode
fun getTransparencyMode(): TransparencyMode
fun getVertexDomain(): VertexDomain
fun getCullingMode(): CullingMode
fun getFeatureLevel(): FeatureLevel
fun getRequiredAttributes(): Double
fun getMaskThreshold(): Double
fun getSpecularAntiAliasingVariance(): Double
fun getSpecularAntiAliasingThreshold(): Double
fun isColorWriteEnabled(): Boolean
fun isDepthWriteEnabled(): Boolean
fun isDepthCullingEnabled(): Boolean
fun isDoubleSided(): Boolean
fun isAlphaToCoverageEnabled(): Boolean
}

// ── Material ──────────────────────────────────────────────────────────────────

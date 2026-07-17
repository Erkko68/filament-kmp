package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class MaterialInstance : JsAny {
fun getName(): String
fun getMaterial(): Material
fun duplicate(): MaterialInstance
fun duplicateNamed(name: String): MaterialInstance
fun setBoolParameter(name: String, value: Boolean): Unit
fun setFloatParameter(name: String, value: Double): Unit
fun setFloat2Parameter(name: String, value: float2): Unit
fun setFloat3Parameter(name: String, value: float3): Unit
fun setFloat4Parameter(name: String, value: float4): Unit
fun setMat3Parameter(name: String, value: mat4): Unit
fun setMat4Parameter(name: String, value: mat3): Unit
fun setTextureParameter(name: String, value: Texture, sampler: TextureSampler): Unit
fun setColor3Parameter(name: String, ctype: RgbType, value: float3): Unit
fun setColor4Parameter(name: String, ctype: RgbaType, value: float4): Unit
fun setPolygonOffset(scale: Double, constant: Double): Unit
fun setMaskThreshold(threshold: Double): Unit
fun setScissor(left: Double, bottom: Double, width: Double, height: Double): Unit
fun unsetScissor(): Unit
fun setDoubleSided(doubleSided: Boolean): Unit
fun setCullingMode(mode: CullingMode): Unit
fun setColorWrite(enable: Boolean): Unit
fun setDepthWrite(enable: Boolean): Unit
fun setStencilWrite(enable: Boolean): Unit
fun setDepthCulling(enable: Boolean): Unit
fun setDepthFunc(func: CompareFunc): Unit
fun setStencilCompareFunction(func: CompareFunc, face: StencilFace = definedExternally): Unit
fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace = definedExternally): Unit
fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace = definedExternally): Unit
fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace = definedExternally): Unit
fun setStencilReferenceValue(value: Double, face: StencilFace = definedExternally): Unit
fun setStencilReadMask(readMask: Double, face: StencilFace = definedExternally): Unit
fun setStencilWriteMask(writeMask: Double, face: StencilFace = definedExternally): Unit
fun getCullingMode(): CullingMode
fun setCullingModeSeparate(color: CullingMode, shadows: CullingMode): Unit
fun getShadowCullingMode(): CullingMode
fun getDepthFunc(): CompareFunc
fun setTransparencyMode(mode: TransparencyMode): Unit
fun getTransparencyMode(): TransparencyMode
fun isColorWriteEnabled(): Boolean
fun isDepthCullingEnabled(): Boolean
fun isDepthWriteEnabled(): Boolean
fun isDoubleSided(): Boolean
fun getMaskThreshold(): Double
fun getSpecularAntiAliasingThreshold(): Double
fun setSpecularAntiAliasingThreshold(value: Double): Unit
fun getSpecularAntiAliasingVariance(): Double
fun setSpecularAntiAliasingVariance(value: Double): Unit
}

// ── MaterialInstance ──────────────────────────────────────────────────────────

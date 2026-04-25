package io.github.erkko68.filament

import io.github.erkko68.filament.js.MaterialInstance as JSMaterialInstance
import io.github.erkko68.filament.js.RgbType
import io.github.erkko68.filament.js.RgbaType
import io.github.erkko68.filament.js.StencilFace
import io.github.erkko68.filament.js.StencilOperation
import io.github.erkko68.filament.js.CompareFunc
import io.github.erkko68.filament.js.CullingMode

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class MaterialInstance(internal val jsMaterialInstance: JSMaterialInstance) {
    actual fun getMaterial(): Material {
        return js("{}").unsafeCast<Material>()
    }

    actual fun getName(): String {
        return jsMaterialInstance.getName()
    }

    actual fun setParameter(name: String, x: Boolean) {
        jsMaterialInstance.setBoolParameter(name, x)
    }

    actual fun setParameter(name: String, x: Float) {
        jsMaterialInstance.setFloatParameter(name, x)
    }

    actual fun setParameter(name: String, x: Int) {
        jsMaterialInstance.setFloatParameter(name, x)
    }

    actual fun setParameter(name: String, x: Boolean, y: Boolean) {
        jsMaterialInstance.asDynamic().setBoolParameter(name, arrayOf(x, y))
    }

    actual fun setParameter(name: String, x: Float, y: Float) {
        jsMaterialInstance.setFloat2Parameter(name, arrayOf(x, y) as Array<Number>)
    }

    actual fun setParameter(name: String, x: Int, y: Int) {
        jsMaterialInstance.setFloat2Parameter(name, arrayOf(x.toFloat(), y.toFloat()) as Array<Number>)
    }

    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) {
        jsMaterialInstance.asDynamic().setBoolParameter(name, arrayOf(x, y, z))
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float) {
        jsMaterialInstance.setFloat3Parameter(name, arrayOf(x, y, z) as Array<Number>)
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int) {
        jsMaterialInstance.setFloat3Parameter(name, arrayOf(x.toFloat(), y.toFloat(), z.toFloat()) as Array<Number>)
    }

    actual fun setParameter(
        name: String,
        x: Boolean,
        y: Boolean,
        z: Boolean,
        w: Boolean
    ) {
        jsMaterialInstance.asDynamic().setBoolParameter(name, arrayOf(x, y, z, w))
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
        jsMaterialInstance.setFloat4Parameter(name, arrayOf(x, y, z, w) as Array<Number>)
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) {
        jsMaterialInstance.setFloat4Parameter(name, arrayOf(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat()) as Array<Number>)
    }

    actual fun setParameter(
        name: String,
        texture: Texture,
        sampler: TextureSampler
    ) {
        jsMaterialInstance.setTextureParameter(name, texture.jsTexture, sampler.jsTextureSampler)
    }

    actual fun setParameter(
        name: String,
        type: BooleanElement,
        v: BooleanArray,
        offset: Int,
        count: Int
    ) {
        val sub = v.slice(offset until (offset + count)).toTypedArray()
        jsMaterialInstance.asDynamic().setBoolParameter(name, sub)
    }

    actual fun setParameter(
        name: String,
        type: IntElement,
        v: IntArray,
        offset: Int,
        count: Int
    ) {
        val sub = v.slice(offset until (offset + count)).toTypedArray()
        jsMaterialInstance.asDynamic().setFloatParameter(name, sub)
    }

    actual fun setParameter(
        name: String,
        type: FloatElement,
        v: FloatArray,
        offset: Int,
        count: Int
    ) {
        val sub = v.slice(offset until (offset + count)).toTypedArray()
        when (type) {
            FloatElement.FLOAT -> jsMaterialInstance.asDynamic().setFloatParameter(name, sub)
            FloatElement.FLOAT2 -> jsMaterialInstance.setFloat2Parameter(name, sub as Array<Number>)
            FloatElement.FLOAT3 -> jsMaterialInstance.setFloat3Parameter(name, sub as Array<Number>)
            FloatElement.FLOAT4 -> jsMaterialInstance.setFloat4Parameter(name, sub as Array<Number>)
            FloatElement.MAT3 -> jsMaterialInstance.setMat3Parameter(name, sub as Array<Number>)
            FloatElement.MAT4 -> jsMaterialInstance.setMat4Parameter(name, sub as Array<Number>)
        }
    }

    actual fun setParameter(
        name: String,
        type: Colors.RgbType,
        r: Float,
        g: Float,
        b: Float
    ) {
        val jsType = when(type) {
            Colors.RgbType.SRGB -> io.github.erkko68.filament.js.RgbType.sRGB
            Colors.RgbType.LINEAR -> io.github.erkko68.filament.js.RgbType.LINEAR
        }
        jsMaterialInstance.setColor3Parameter(name, jsType, arrayOf(r, g, b) as Array<Number>)
    }

    actual fun setParameter(
        name: String,
        type: Colors.RgbaType,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        val jsType = when(type) {
            Colors.RgbaType.SRGB -> io.github.erkko68.filament.js.RgbaType.sRGB
            Colors.RgbaType.LINEAR -> io.github.erkko68.filament.js.RgbaType.LINEAR
            Colors.RgbaType.PREMULTIPLIED_SRGB -> io.github.erkko68.filament.js.RgbaType.PREMULTIPLIED_sRGB
            Colors.RgbaType.PREMULTIPLIED_LINEAR -> io.github.erkko68.filament.js.RgbaType.PREMULTIPLIED_LINEAR
        }
        jsMaterialInstance.setColor4Parameter(name, jsType, arrayOf(r, g, b, a) as Array<Number>)
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        val jsInst = jsMaterialInstance.asDynamic()
        if (jsInst.setScissor != null) {
            jsInst.setScissor(left, bottom, width, height)
        }
    }

    actual fun unsetScissor() {
        val jsInst = jsMaterialInstance.asDynamic()
        if (jsInst.unsetScissor != null) {
            jsInst.unsetScissor()
        }
    }

    actual fun setPolygonOffset(scale: Float, constant: Float) {
        jsMaterialInstance.setPolygonOffset(scale, constant)
    }

    actual fun setMaskThreshold(threshold: Float) {
        jsMaterialInstance.setMaskThreshold(threshold)
    }

    actual fun getMaskThreshold(): Float {
        return 0.4f
    }

    actual fun setSpecularAntiAliasingVariance(variance: Float) {
        val jsInst = jsMaterialInstance.asDynamic()
        if (jsInst.setSpecularAntiAliasingVariance != null) {
            jsInst.setSpecularAntiAliasingVariance(variance)
        }
    }

    actual fun getSpecularAntiAliasingVariance(): Float {
        return 0.0f
    }

    actual fun setSpecularAntiAliasingThreshold(threshold: Float) {
        val jsInst = jsMaterialInstance.asDynamic()
        if (jsInst.setSpecularAntiAliasingThreshold != null) {
            jsInst.setSpecularAntiAliasingThreshold(threshold)
        }
    }

    actual fun getSpecularAntiAliasingThreshold(): Float {
        return 0.0f
    }

    actual fun setDoubleSided(doubleSided: Boolean) {
        jsMaterialInstance.setDoubleSided(doubleSided)
    }

    actual fun isDoubleSided(): Boolean {
        return false
    }

    actual fun setTransparencyMode(mode: Material.TransparencyMode) {
    }

    actual fun getTransparencyMode(): Material.TransparencyMode {
        return Material.TransparencyMode.DEFAULT
    }

    actual fun setCullingMode(mode: Material.CullingMode) {
        jsMaterialInstance.setCullingMode(when(mode) {
            Material.CullingMode.NONE -> io.github.erkko68.filament.js.CullingMode.NONE
            Material.CullingMode.FRONT -> io.github.erkko68.filament.js.CullingMode.FRONT
            Material.CullingMode.BACK -> io.github.erkko68.filament.js.CullingMode.BACK
            Material.CullingMode.FRONT_AND_BACK -> io.github.erkko68.filament.js.CullingMode.FRONT_AND_BACK
        })
    }

    actual fun setCullingMode(
        colorPassCullingMode: Material.CullingMode,
        shadowPassCullingMode: Material.CullingMode
    ) {
        setCullingMode(colorPassCullingMode)
    }

    actual fun getCullingMode(): Material.CullingMode {
        return Material.CullingMode.BACK
    }

    actual fun getShadowCullingMode(): Material.CullingMode {
        return Material.CullingMode.BACK
    }

    actual fun setColorWrite(enable: Boolean) {
        jsMaterialInstance.setColorWrite(enable)
    }

    actual fun isColorWriteEnabled(): Boolean {
        return true
    }

    actual fun setDepthWrite(enable: Boolean) {
        jsMaterialInstance.setDepthWrite(enable)
    }

    actual fun isDepthWriteEnabled(): Boolean {
        return true
    }

    actual fun setStencilWrite(enable: Boolean) {
        jsMaterialInstance.setStencilWrite(enable)
    }

    actual fun isStencilWriteEnabled(): Boolean {
        return true
    }

    actual fun setDepthCulling(enable: Boolean) {
        jsMaterialInstance.setDepthCulling(enable)
    }

    actual fun setDepthFunc(func: TextureSampler.CompareFunction) {
        jsMaterialInstance.setDepthFunc(when(func) {
            TextureSampler.CompareFunction.LESS_EQUAL -> CompareFunc.LESS_EQUAL
            TextureSampler.CompareFunction.GREATER_EQUAL -> CompareFunc.GREATER_EQUAL
            TextureSampler.CompareFunction.LESS -> CompareFunc.LESS
            TextureSampler.CompareFunction.GREATER -> CompareFunc.GREATER
            TextureSampler.CompareFunction.EQUAL -> CompareFunc.EQUAL
            TextureSampler.CompareFunction.NOT_EQUAL -> CompareFunc.NOT_EQUAL
            TextureSampler.CompareFunction.ALWAYS -> CompareFunc.ALWAYS
            TextureSampler.CompareFunction.NEVER -> CompareFunc.NEVER
        })
    }

    actual fun isDepthCullingEnabled(): Boolean {
        return true
    }

    actual fun getDepthFunc(): TextureSampler.CompareFunction {
        return TextureSampler.CompareFunction.LESS_EQUAL
    }

    actual fun setStencilCompareFunction(
        func: TextureSampler.CompareFunction,
        face: StencilFace
    ) {
        val jsFunc = when(func) {
            TextureSampler.CompareFunction.LESS_EQUAL -> CompareFunc.LESS_EQUAL
            TextureSampler.CompareFunction.GREATER_EQUAL -> CompareFunc.GREATER_EQUAL
            TextureSampler.CompareFunction.LESS -> CompareFunc.LESS
            TextureSampler.CompareFunction.GREATER -> CompareFunc.GREATER
            TextureSampler.CompareFunction.EQUAL -> CompareFunc.EQUAL
            TextureSampler.CompareFunction.NOT_EQUAL -> CompareFunc.NOT_EQUAL
            TextureSampler.CompareFunction.ALWAYS -> CompareFunc.ALWAYS
            TextureSampler.CompareFunction.NEVER -> CompareFunc.NEVER
        }
        val jsFace = when(face) {
            MaterialInstance.StencilFace.FRONT -> io.github.erkko68.filament.js.StencilFace.FRONT
            MaterialInstance.StencilFace.BACK -> io.github.erkko68.filament.js.StencilFace.BACK
            MaterialInstance.StencilFace.FRONT_AND_BACK -> io.github.erkko68.filament.js.StencilFace.FRONT_AND_BACK
        }
        jsMaterialInstance.setStencilCompareFunction(jsFunc, jsFace)
    }

    actual fun setStencilCompareFunction(func: TextureSampler.CompareFunction) {
        setStencilCompareFunction(func, StencilFace.FRONT_AND_BACK)
    }

    actual fun setStencilOpStencilFail(
        op: StencilOperation,
        face: StencilFace
    ) {
        val jsOp = mapStencilOp(op)
        val jsFace = mapStencilFace(face)
        jsMaterialInstance.setStencilOpStencilFail(jsOp, jsFace)
    }

    actual fun setStencilOpStencilFail(op: StencilOperation) {
        setStencilOpStencilFail(op, StencilFace.FRONT_AND_BACK)
    }

    actual fun setStencilOpDepthFail(
        op: StencilOperation,
        face: StencilFace
    ) {
        val jsOp = mapStencilOp(op)
        val jsFace = mapStencilFace(face)
        jsMaterialInstance.setStencilOpDepthFail(jsOp, jsFace)
    }

    actual fun setStencilOpDepthFail(op: StencilOperation) {
        setStencilOpDepthFail(op, StencilFace.FRONT_AND_BACK)
    }

    actual fun setStencilOpDepthStencilPass(
        op: StencilOperation,
        face: StencilFace
    ) {
        val jsOp = mapStencilOp(op)
        val jsFace = mapStencilFace(face)
        jsMaterialInstance.setStencilOpDepthStencilPass(jsOp, jsFace)
    }

    actual fun setStencilOpDepthStencilPass(op: StencilOperation) {
        setStencilOpDepthStencilPass(op, StencilFace.FRONT_AND_BACK)
    }

    actual fun setStencilReferenceValue(
        value: Int,
        face: StencilFace
    ) {
        jsMaterialInstance.setStencilReferenceValue(value, mapStencilFace(face))
    }

    actual fun setStencilReferenceValue(value: Int) {
        setStencilReferenceValue(value, StencilFace.FRONT_AND_BACK)
    }

    actual fun setStencilReadMask(readMask: Int, face: StencilFace) {
        jsMaterialInstance.setStencilReadMask(readMask, mapStencilFace(face))
    }

    actual fun setStencilReadMask(readMask: Int) {
        setStencilReadMask(readMask, StencilFace.FRONT_AND_BACK)
    }

    actual fun setStencilWriteMask(
        writeMask: Int,
        face: StencilFace
    ) {
        jsMaterialInstance.setStencilWriteMask(writeMask, mapStencilFace(face))
    }

    actual fun setStencilWriteMask(writeMask: Int) {
        setStencilWriteMask(writeMask, StencilFace.FRONT_AND_BACK)
    }
    
    private fun mapStencilOp(op: StencilOperation) = when(op) {
        MaterialInstance.StencilOperation.KEEP -> io.github.erkko68.filament.js.StencilOperation.KEEP
        MaterialInstance.StencilOperation.ZERO -> io.github.erkko68.filament.js.StencilOperation.ZERO
        MaterialInstance.StencilOperation.REPLACE -> io.github.erkko68.filament.js.StencilOperation.REPLACE
        MaterialInstance.StencilOperation.INCR_CLAMP -> io.github.erkko68.filament.js.StencilOperation.INCR_CLAMP
        MaterialInstance.StencilOperation.INCR_WRAP -> io.github.erkko68.filament.js.StencilOperation.INCR_WRAP
        MaterialInstance.StencilOperation.DECR_CLAMP -> io.github.erkko68.filament.js.StencilOperation.DECR_CLAMP
        MaterialInstance.StencilOperation.DECR_WRAP -> io.github.erkko68.filament.js.StencilOperation.DECR_WRAP
        MaterialInstance.StencilOperation.INVERT -> io.github.erkko68.filament.js.StencilOperation.INVERT
    }
    
    private fun mapStencilFace(face: StencilFace) = when(face) {
        MaterialInstance.StencilFace.FRONT -> io.github.erkko68.filament.js.StencilFace.FRONT
        MaterialInstance.StencilFace.BACK -> io.github.erkko68.filament.js.StencilFace.BACK
        MaterialInstance.StencilFace.FRONT_AND_BACK -> io.github.erkko68.filament.js.StencilFace.FRONT_AND_BACK
    }

    actual enum class BooleanElement { BOOL, BOOL2, BOOL3, BOOL4 }
    actual enum class IntElement { INT, INT2, INT3, INT4 }
    actual enum class FloatElement { FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4 }
    actual enum class StencilOperation { KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT }
    actual enum class StencilFace { FRONT, BACK, FRONT_AND_BACK }
    actual companion object {
        actual fun duplicate(
            other: MaterialInstance,
            name: String?
        ): MaterialInstance {
            return other
        }
    }
}
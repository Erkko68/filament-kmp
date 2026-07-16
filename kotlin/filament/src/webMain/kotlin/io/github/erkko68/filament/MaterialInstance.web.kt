package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.toJsBooleans

import io.github.erkko68.filament.web.interop.emptyJsObject

import js.array.ReadonlyArray

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.MaterialInstance as JSMaterialInstance
import io.github.erkko68.filament.web.RgbType
import io.github.erkko68.filament.web.RgbaType
import io.github.erkko68.filament.web.StencilFace
import io.github.erkko68.filament.web.StencilOperation
import io.github.erkko68.filament.web.CompareFunc
import io.github.erkko68.filament.web.CullingMode

// Members the generated MaterialInstance external doesn't cover: the vector/array overloads
// of setBoolParameter/setFloatParameter (the scalar forms are generated), and the optional
// setScissor/unsetScissor (present only in newer filament.js, so feature-detected).
private external interface JsMaterialInstanceExt : JsAny  {
    fun setBoolParameter(name: String, value: ReadonlyArray<JsBoolean>)
    fun setFloatParameter(name: String, value: ReadonlyArray<JsNumber>)
    // Declared as methods (not function-typed properties) so they keep their `this` binding when
    // invoked — a detached embind method aborts with a native BindingError. Probed before calling.
    fun setScissor(left: Int, bottom: Int, width: Int, height: Int)
    fun unsetScissor()
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class MaterialInstance(internal val jsMaterialInstance: JSMaterialInstance) {
    private val ext: JsMaterialInstanceExt get() = jsMaterialInstance.unsafeCast<JsMaterialInstanceExt>()
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "getter throws UnsupportedOperationException — filament.js does not expose MaterialInstance.getMaterial.")
    actual val material: Material
        get() = jsUnsupported("MaterialInstance.getMaterial", "filament.js does not expose MaterialInstance.getMaterial.")

    actual val name: String
        get() = jsMaterialInstance.getName()

    actual fun setParameter(name: String, x: Boolean) {
        jsMaterialInstance.setBoolParameter(name, x)
    }

    actual fun setParameter(name: String, x: Float) {
        jsMaterialInstance.setFloatParameter(name, x.toDouble())
    }

    actual fun setParameter(name: String, x: Int) {
        jsMaterialInstance.setFloatParameter(name, x.toDouble())
    }

    actual fun setParameter(name: String, x: Boolean, y: Boolean) {
        ext.setBoolParameter(name, listOf(x, y).toJsBooleans())
    }

    actual fun setParameter(name: String, x: Float, y: Float) {
        jsMaterialInstance.setFloat2Parameter(name, jsNumbers(x, y))
    }

    actual fun setParameter(name: String, x: Int, y: Int) {
        jsMaterialInstance.setFloat2Parameter(name, jsNumbers(x.toFloat(), y.toFloat()))
    }

    actual fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) {
        ext.setBoolParameter(name, listOf(x, y, z).toJsBooleans())
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float) {
        jsMaterialInstance.setFloat3Parameter(name, jsNumbers(x, y, z))
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int) {
        jsMaterialInstance.setFloat3Parameter(name, jsNumbers(x.toFloat(), y.toFloat(), z.toFloat()))
    }

    actual fun setParameter(
        name: String,
        x: Boolean,
        y: Boolean,
        z: Boolean,
        w: Boolean
    ) {
        ext.setBoolParameter(name, listOf(x, y, z, w).toJsBooleans())
    }

    actual fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) {
        jsMaterialInstance.setFloat4Parameter(name, jsNumbers(x, y, z, w))
    }

    actual fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) {
        jsMaterialInstance.setFloat4Parameter(name, jsNumbers(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat()))
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
        val sub = v.slice(offset until (offset + count))
        ext.setBoolParameter(name, sub.toJsBooleans())
    }

    actual fun setParameter(
        name: String,
        type: IntElement,
        v: IntArray,
        offset: Int,
        count: Int
    ) {
        val sub = v.slice(offset until (offset + count))
        ext.setFloatParameter(name, sub.toJsNumbers())
    }

    actual fun setParameter(
        name: String,
        type: FloatElement,
        v: FloatArray,
        offset: Int,
        count: Int
    ) {
        val sub = v.slice(offset until (offset + count))
        when (type) {
            FloatElement.FLOAT -> ext.setFloatParameter(name, sub.toJsNumbers())
            FloatElement.FLOAT2 -> jsMaterialInstance.setFloat2Parameter(name, sub.toJsNumbers())
            FloatElement.FLOAT3 -> jsMaterialInstance.setFloat3Parameter(name, sub.toJsNumbers())
            FloatElement.FLOAT4 -> jsMaterialInstance.setFloat4Parameter(name, sub.toJsNumbers())
            FloatElement.MAT3 -> jsMaterialInstance.setMat3Parameter(name, sub.toJsNumbers())
            FloatElement.MAT4 -> jsMaterialInstance.setMat4Parameter(name, sub.toJsNumbers())
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
            Colors.RgbType.SRGB -> io.github.erkko68.filament.web.RgbType.sRGB
            Colors.RgbType.LINEAR -> io.github.erkko68.filament.web.RgbType.LINEAR
        }
        jsMaterialInstance.setColor3Parameter(name, jsType, jsNumbers(r, g, b))
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
            Colors.RgbaType.SRGB -> io.github.erkko68.filament.web.RgbaType.sRGB
            Colors.RgbaType.LINEAR -> io.github.erkko68.filament.web.RgbaType.LINEAR
            Colors.RgbaType.PREMULTIPLIED_SRGB -> io.github.erkko68.filament.web.RgbaType.PREMULTIPLIED_sRGB
            Colors.RgbaType.PREMULTIPLIED_LINEAR -> io.github.erkko68.filament.web.RgbaType.PREMULTIPLIED_LINEAR
        }
        jsMaterialInstance.setColor4Parameter(name, jsType, jsNumbers(r, g, b, a))
    }

    actual fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        if (jsHasMember(jsMaterialInstance, "setScissor")) ext.setScissor(left, bottom, width, height)
    }

    actual fun unsetScissor() {
        if (jsHasMember(jsMaterialInstance, "unsetScissor")) ext.unsetScissor()
    }

    actual fun setPolygonOffset(scale: Float, constant: Float) {
        jsMaterialInstance.setPolygonOffset(scale.toDouble(), constant.toDouble())
    }

    actual var maskThreshold: Float
        get() = jsMaterialInstance.getMaskThreshold().toFloat()
        set(value) { jsMaterialInstance.setMaskThreshold(value.toDouble()) }

    actual var specularAntiAliasingVariance: Float
        get() = jsMaterialInstance.getSpecularAntiAliasingVariance().toFloat()
        set(value) { jsMaterialInstance.setSpecularAntiAliasingVariance(value.toDouble()) }

    actual var specularAntiAliasingThreshold: Float
        get() = jsMaterialInstance.getSpecularAntiAliasingThreshold().toFloat()
        set(value) { jsMaterialInstance.setSpecularAntiAliasingThreshold(value.toDouble()) }

    actual var isDoubleSided: Boolean
        get() = jsMaterialInstance.isDoubleSided()
        set(value) { jsMaterialInstance.setDoubleSided(value) }

    actual var transparencyMode: Material.TransparencyMode
        get() = when (jsMaterialInstance.getTransparencyMode()) {
            io.github.erkko68.filament.web.TransparencyMode.DEFAULT -> Material.TransparencyMode.DEFAULT
            io.github.erkko68.filament.web.TransparencyMode.TWO_PASSES_ONE_SIDE -> Material.TransparencyMode.TWO_PASSES_ONE_SIDE
            io.github.erkko68.filament.web.TransparencyMode.TWO_PASSES_TWO_SIDES -> Material.TransparencyMode.TWO_PASSES_TWO_SIDES
            else -> error("unreachable")
        }
        set(value) {
            jsMaterialInstance.setTransparencyMode(when (value) {
                Material.TransparencyMode.DEFAULT -> io.github.erkko68.filament.web.TransparencyMode.DEFAULT
                Material.TransparencyMode.TWO_PASSES_ONE_SIDE -> io.github.erkko68.filament.web.TransparencyMode.TWO_PASSES_ONE_SIDE
                Material.TransparencyMode.TWO_PASSES_TWO_SIDES -> io.github.erkko68.filament.web.TransparencyMode.TWO_PASSES_TWO_SIDES
            })
        }

    actual var cullingMode: Material.CullingMode
        get() = fromJsCullingMode(jsMaterialInstance.getCullingMode())
        set(value) { jsMaterialInstance.setCullingMode(toJsCullingMode(value)) }

    actual fun setCullingMode(
        colorPassCullingMode: Material.CullingMode,
        shadowPassCullingMode: Material.CullingMode
    ) {
        // Now properly maps color/shadow passes separately, matching the
        // Android API instead of dropping the shadow argument.
        jsMaterialInstance.setCullingModeSeparate(
            toJsCullingMode(colorPassCullingMode),
            toJsCullingMode(shadowPassCullingMode),
        )
    }

    actual val shadowCullingMode: Material.CullingMode
        get() = fromJsCullingMode(jsMaterialInstance.getShadowCullingMode())

    actual var isColorWriteEnabled: Boolean
        get() = jsMaterialInstance.isColorWriteEnabled()
        set(value) { jsMaterialInstance.setColorWrite(value) }

    actual var isDepthWriteEnabled: Boolean
        get() = jsMaterialInstance.isDepthWriteEnabled()
        set(value) { jsMaterialInstance.setDepthWrite(value) }

    actual var isStencilWriteEnabled: Boolean
        // Upstream oversight: `MaterialInstance::isStencilWriteEnabled()` exists in C++
        // and every sibling (`isColorWriteEnabled`, `isDepthWriteEnabled`,
        // `isDepthCullingEnabled`, `isDoubleSided`) is bound in jsbindings.cpp, but this
        // one was missed. Falling back to the Filament runtime default (StencilState's
        // `stencilWrite = false` in backend/DriverEnums.h). TODO: file a one-line PR
        // upstream to bind it next to `setStencilWrite`.
        get() = false
        set(value) { jsMaterialInstance.setStencilWrite(value) }

    actual var isDepthCullingEnabled: Boolean
        get() = jsMaterialInstance.isDepthCullingEnabled()
        set(value) { jsMaterialInstance.setDepthCulling(value) }

    actual var depthFunc: TextureSampler.CompareFunction
        get() = when (jsMaterialInstance.getDepthFunc()) {
            CompareFunc.LESS_EQUAL -> TextureSampler.CompareFunction.LESS_EQUAL
            CompareFunc.GREATER_EQUAL -> TextureSampler.CompareFunction.GREATER_EQUAL
            CompareFunc.LESS -> TextureSampler.CompareFunction.LESS
            CompareFunc.GREATER -> TextureSampler.CompareFunction.GREATER
            CompareFunc.EQUAL -> TextureSampler.CompareFunction.EQUAL
            CompareFunc.NOT_EQUAL -> TextureSampler.CompareFunction.NOT_EQUAL
            CompareFunc.ALWAYS -> TextureSampler.CompareFunction.ALWAYS
            CompareFunc.NEVER -> TextureSampler.CompareFunction.NEVER
            else -> error("unreachable")
        }
        set(value) {
            jsMaterialInstance.setDepthFunc(when(value) {
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
            MaterialInstance.StencilFace.FRONT -> io.github.erkko68.filament.web.StencilFace.FRONT
            MaterialInstance.StencilFace.BACK -> io.github.erkko68.filament.web.StencilFace.BACK
            MaterialInstance.StencilFace.FRONT_AND_BACK -> io.github.erkko68.filament.web.StencilFace.FRONT_AND_BACK
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
        jsMaterialInstance.setStencilReferenceValue(value.toDouble(), mapStencilFace(face))
    }

    actual fun setStencilReferenceValue(value: Int) {
        setStencilReferenceValue(value, StencilFace.FRONT_AND_BACK)
    }

    actual fun setStencilReadMask(readMask: Int, face: StencilFace) {
        jsMaterialInstance.setStencilReadMask(readMask.toDouble(), mapStencilFace(face))
    }

    actual fun setStencilReadMask(readMask: Int) {
        setStencilReadMask(readMask, StencilFace.FRONT_AND_BACK)
    }

    actual fun setStencilWriteMask(
        writeMask: Int,
        face: StencilFace
    ) {
        jsMaterialInstance.setStencilWriteMask(writeMask.toDouble(), mapStencilFace(face))
    }

    actual fun setStencilWriteMask(writeMask: Int) {
        setStencilWriteMask(writeMask, StencilFace.FRONT_AND_BACK)
    }
    
    private fun mapStencilOp(op: StencilOperation) = when(op) {
        MaterialInstance.StencilOperation.KEEP -> io.github.erkko68.filament.web.StencilOperation.KEEP
        MaterialInstance.StencilOperation.ZERO -> io.github.erkko68.filament.web.StencilOperation.ZERO
        MaterialInstance.StencilOperation.REPLACE -> io.github.erkko68.filament.web.StencilOperation.REPLACE
        MaterialInstance.StencilOperation.INCR_CLAMP -> io.github.erkko68.filament.web.StencilOperation.INCR_CLAMP
        MaterialInstance.StencilOperation.INCR_WRAP -> io.github.erkko68.filament.web.StencilOperation.INCR_WRAP
        MaterialInstance.StencilOperation.DECR_CLAMP -> io.github.erkko68.filament.web.StencilOperation.DECR_CLAMP
        MaterialInstance.StencilOperation.DECR_WRAP -> io.github.erkko68.filament.web.StencilOperation.DECR_WRAP
        MaterialInstance.StencilOperation.INVERT -> io.github.erkko68.filament.web.StencilOperation.INVERT
    }
    
    private fun mapStencilFace(face: StencilFace) = when(face) {
        MaterialInstance.StencilFace.FRONT -> io.github.erkko68.filament.web.StencilFace.FRONT
        MaterialInstance.StencilFace.BACK -> io.github.erkko68.filament.web.StencilFace.BACK
        MaterialInstance.StencilFace.FRONT_AND_BACK -> io.github.erkko68.filament.web.StencilFace.FRONT_AND_BACK
    }

    actual enum class BooleanElement { BOOL, BOOL2, BOOL3, BOOL4 }
    actual enum class IntElement { INT, INT2, INT3, INT4 }
    actual enum class FloatElement { FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4 }
    actual enum class StencilOperation { KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT }
    actual enum class StencilFace { FRONT, BACK, FRONT_AND_BACK }
    actual companion object {
        @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — returns the source instance unchanged; filament.js does not expose MaterialInstance duplication.")
        actual fun duplicate(
            other: MaterialInstance,
            name: String?
        ): MaterialInstance {
            return other
        }
    }
}

private fun toJsCullingMode(mode: Material.CullingMode): CullingMode = when (mode) {
    Material.CullingMode.NONE -> CullingMode.NONE
    Material.CullingMode.FRONT -> CullingMode.FRONT
    Material.CullingMode.BACK -> CullingMode.BACK
    Material.CullingMode.FRONT_AND_BACK -> CullingMode.FRONT_AND_BACK
}

private fun fromJsCullingMode(mode: CullingMode): Material.CullingMode = when (mode) {
    CullingMode.NONE -> Material.CullingMode.NONE
    CullingMode.FRONT -> Material.CullingMode.FRONT
    CullingMode.BACK -> Material.CullingMode.BACK
    CullingMode.FRONT_AND_BACK -> Material.CullingMode.FRONT_AND_BACK
    else -> error("unreachable")
}
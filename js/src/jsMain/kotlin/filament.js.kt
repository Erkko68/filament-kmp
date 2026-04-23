@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package io.github.erkko68.filament.js

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external object glm {
    interface vec2
    interface vec3
    interface vec4
    interface mat3
    interface mat4
}

external fun getSupportedFormatSuffix(desired: String)

external fun init(assets: Array<String>, onready: (() -> Unit)? = definedExternally)

external fun fetch(assets: Array<String>, onDone: (() -> Unit)? = definedExternally, onFetched: ((name: String) -> Unit)? = definedExternally)

external fun clearAssetCache()

external fun <T> vectorToArray(vector: Vector<T>): Array<T>

external fun fitIntoUnitCube(box: Aabb): dynamic /* glm.mat4 | Array<Number> */

external fun multiplyMatrices(a: glm.mat4, b: glm.mat4): dynamic /* glm.mat4 | Array<Number> */

external fun multiplyMatrices(a: glm.mat4, b: Array<Number>): dynamic /* glm.mat4 | Array<Number> */

external fun multiplyMatrices(a: Array<Number>, b: glm.mat4): dynamic /* glm.mat4 | Array<Number> */

external fun multiplyMatrices(a: Array<Number>, b: Array<Number>): dynamic /* glm.mat4 | Array<Number> */

external object assets {
    @nativeGetter
    operator fun get(url: String): Uint8Array?
    @nativeSetter
    operator fun set(url: String, value: Uint8Array)
}

external interface Vector<T> {
    fun size(): Number
    fun get(i: Number): T
}

external open class SwapChain

external interface PickingQueryResult {
    var renderable: Number
    var depth: Number
    var fragCoords: Array<Number>
}

typealias PickCallback = (result: PickingQueryResult) -> Unit

external open class ColorGrading {
    companion object {
        fun Builder(): `ColorGrading_Builder`
    }
}

external interface Box {
    var center: dynamic /* glm.vec3 | Array<Number> */
        get() = definedExternally
        set(value) = definedExternally
    var halfExtent: dynamic /* glm.vec3 | Array<Number> */
        get() = definedExternally
        set(value) = definedExternally
}

external interface Aabb {
    var min: dynamic /* glm.vec3 | Array<Number> */
        get() = definedExternally
        set(value) = definedExternally
    var max: dynamic /* glm.vec3 | Array<Number> */
        get() = definedExternally
        set(value) = definedExternally
}

external interface `Renderer_ClearOptions` {
    var clearColor: dynamic /* glm.vec4? | Array<Number>? */
        get() = definedExternally
        set(value) = definedExternally
    var clear: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var discard: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `LightManager_ShadowOptions` {
    var mapSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shadowCascades: Number?
        get() = definedExternally
        set(value) = definedExternally
    var constantBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var normalBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shadowFar: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shadowNearHint: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shadowFarHint: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffsetConstant: Number?
        get() = definedExternally
        set(value) = definedExternally
    var polygonOffsetSlope: Number?
        get() = definedExternally
        set(value) = definedExternally
    var screenSpaceContactShadows: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var stepCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxShadowDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external open class `driver_PixelBufferDescriptor` {
    constructor(byteLength: Number, format: PixelDataFormat, datatype: PixelDataType)
    constructor(byteLength: Number, cdtype: CompressedPixelDataType, imageSize: Number, compressed: Boolean)
    open fun getBytes(): ArrayBuffer
}

external open class `Texture_Builder` {
    open fun width(width: Number): `Texture_Builder`
    open fun height(height: Number): `Texture_Builder`
    open fun depth(depth: Number): `Texture_Builder`
    open fun levels(levels: Number): `Texture_Builder`
    open fun sampler(sampler: `Texture_Sampler`): `Texture_Builder`
    open fun format(format: `Texture_InternalFormat`): `Texture_Builder`
    open fun usage(usage: Number): `Texture_Builder`
    open fun build(engine: Engine): Texture
}

external open class Texture {
    open fun setImage(engine: Engine, level: Number, pbd: `driver_PixelBufferDescriptor`)
    open fun setImageCube(engine: Engine, level: Number, pbd: `driver_PixelBufferDescriptor`)
    open fun getWidth(engine: Engine, level: Number = definedExternally): Number
    open fun getHeight(engine: Engine, level: Number = definedExternally): Number
    open fun getDepth(engine: Engine, level: Number = definedExternally): Number
    open fun getLevels(engine: Engine): Number
    open fun generateMipmaps(engine: Engine)

    companion object {
        fun Builder(): `Texture_Builder`
    }
}

external open class Entity {
    open fun getId(): Number
    open fun delete()
}

external open class Skybox {
    open fun setColor(color: glm.vec4)
    open fun setColor(color: Array<Number>)
    open fun getTexture(): Texture

    companion object {
        fun Builder(): `Skybox_Builder`
    }
}

external open class `LightManager_Instance` {
    open fun delete()
}

external open class `RenderableManager_Instance` {
    open fun delete()
}

external open class `TransformManager_Instance` {
    open fun delete()
}

external open class TextureSampler(minfilter: MinFilter, magfilter: MagFilter, wrapmode: WrapMode) {
    open fun setAnisotropy(value: Number)
    open fun setCompareMode(mode: CompareMode, func: CompareFunc)
}

external open class MaterialInstance {
    open fun getName(): String
    open fun setBoolParameter(name: String, value: Boolean)
    open fun setFloatParameter(name: String, value: Number)
    open fun setFloat2Parameter(name: String, value: glm.vec2)
    open fun setFloat2Parameter(name: String, value: Array<Number>)
    open fun setFloat3Parameter(name: String, value: glm.vec3)
    open fun setFloat3Parameter(name: String, value: Array<Number>)
    open fun setFloat4Parameter(name: String, value: glm.vec4)
    open fun setFloat4Parameter(name: String, value: Array<Number>)
    open fun setMat3Parameter(name: String, value: glm.mat4)
    open fun setMat3Parameter(name: String, value: Array<Number>)
    open fun setMat4Parameter(name: String, value: glm.mat3)
    open fun setMat4Parameter(name: String, value: Array<Number>)
    open fun setTextureParameter(name: String, value: Texture, sampler: TextureSampler)
    open fun setColor3Parameter(name: String, ctype: RgbType, value: glm.vec3)
    open fun setColor3Parameter(name: String, ctype: RgbType, value: Array<Number>)
    open fun setColor4Parameter(name: String, ctype: RgbaType, value: glm.vec4)
    open fun setColor4Parameter(name: String, ctype: RgbaType, value: Array<Number>)
    open fun setPolygonOffset(scale: Number, constant: Number)
    open fun setMaskThreshold(threshold: Number)
    open fun setDoubleSided(doubleSided: Boolean)
    open fun setCullingMode(mode: CullingMode)
    open fun setColorWrite(enable: Boolean)
    open fun setDepthWrite(enable: Boolean)
    open fun setStencilWrite(enable: Boolean)
    open fun setDepthCulling(enable: Boolean)
    open fun setDepthFunc(func: CompareFunc)
    open fun setStencilCompareFunction(func: CompareFunc, face: StencilFace = definedExternally)
    open fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace = definedExternally)
    open fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace = definedExternally)
    open fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace = definedExternally)
    open fun setStencilReferenceValue(value: Number, face: StencilFace = definedExternally)
    open fun setStencilReadMask(readMask: Number, face: StencilFace = definedExternally)
    open fun setStencilWriteMask(writeMask: Number, face: StencilFace = definedExternally)
}

external open class EntityManager {
    open fun create(): Entity

    companion object {
        fun get(): EntityManager
    }
}

external open class `VertexBuffer_Builder` {
    open fun vertexCount(count: Number): `VertexBuffer_Builder`
    open fun bufferCount(count: Number): `VertexBuffer_Builder`
    open fun attribute(attrib: VertexAttribute, bufindex: Number, atype: `VertexBuffer_AttributeType`, offset: Number, stride: Number): `VertexBuffer_Builder`
    open fun enableBufferObjects(enabled: Boolean): `VertexBuffer_Builder`
    open fun normalized(attrib: VertexAttribute): `VertexBuffer_Builder`
    open fun normalizedIf(attrib: VertexAttribute, normalized: Boolean): `VertexBuffer_Builder`
    open fun build(engine: Engine): VertexBuffer
}

external open class `IndexBuffer_Builder` {
    open fun indexCount(count: Number): `IndexBuffer_Builder`
    open fun bufferType(type: `IndexBuffer_IndexType`): `IndexBuffer_Builder`
    open fun build(engine: Engine): IndexBuffer
}

external open class `BufferObject_Builder` {
    open fun size(byteCount: Number): `BufferObject_Builder`
    open fun bindingType(type: `BufferObject_BindingType`): `BufferObject_Builder`
    open fun build(engine: Engine): BufferObject
}

external open class `RenderableManager_Builder` {
    open fun geometry(slot: Number, ptype: `RenderableManager_PrimitiveType`, vb: VertexBuffer, ib: IndexBuffer): `RenderableManager_Builder`
    open fun geometryOffset(slot: Number, ptype: `RenderableManager_PrimitiveType`, vb: VertexBuffer, ib: IndexBuffer, offset: Number, count: Number): `RenderableManager_Builder`
    open fun geometryMinMax(slot: Number, ptype: `RenderableManager_PrimitiveType`, vb: VertexBuffer, ib: IndexBuffer, offset: Number, minIndex: Number, maxIndex: Number, count: Number): `RenderableManager_Builder`
    open fun material(geo: Number, minstance: MaterialInstance): `RenderableManager_Builder`
    open fun boundingBox(box: Box): `RenderableManager_Builder`
    open fun layerMask(select: Number, values: Number): `RenderableManager_Builder`
    open fun priority(value: Number): `RenderableManager_Builder`
    open fun culling(enable: Boolean): `RenderableManager_Builder`
    open fun castShadows(enable: Boolean): `RenderableManager_Builder`
    open fun receiveShadows(enable: Boolean): `RenderableManager_Builder`
    open fun skinning(boneCount: Number): `RenderableManager_Builder`
    open fun skinningBones(transforms: Array<`RenderableManager_Bone`>): `RenderableManager_Builder`
    open fun skinningMatrices(transforms: Array<Any /* glm.mat4 | Array<Number> */>): `RenderableManager_Builder`
    open fun morphing(enable: Boolean): `RenderableManager_Builder`
    open fun blendOrder(index: Number, order: Number): `RenderableManager_Builder`
    open fun build(engine: Engine, entity: Entity)
}

external open class `RenderTarget_Builder` {
    open fun texture(attachment: `RenderTarget_AttachmentPoint`, texture: Texture): `RenderTarget_Builder`
    open fun mipLevel(attachment: `RenderTarget_AttachmentPoint`, mipLevel: Number): `RenderTarget_Builder`
    open fun face(attachment: `RenderTarget_AttachmentPoint`, face: `Texture_CubemapFace`): `RenderTarget_Builder`
    open fun layer(attachment: `RenderTarget_AttachmentPoint`, layer: Number): `RenderTarget_Builder`
    open fun build(engine: Engine): RenderTarget
}

external open class `LightManager_Builder` {
    open fun build(engine: Engine, entity: Entity)
    open fun castLight(enable: Boolean): `LightManager_Builder`
    open fun castShadows(enable: Boolean): `LightManager_Builder`
    open fun shadowOptions(options: `LightManager_ShadowOptions`): `LightManager_Builder`
    open fun color(rgb: glm.vec3): `LightManager_Builder`
    open fun color(rgb: Array<Number>): `LightManager_Builder`
    open fun direction(value: glm.vec3): `LightManager_Builder`
    open fun direction(value: Array<Number>): `LightManager_Builder`
    open fun intensity(value: Number): `LightManager_Builder`
    open fun falloff(value: Number): `LightManager_Builder`
    open fun position(value: glm.vec3): `LightManager_Builder`
    open fun position(value: Array<Number>): `LightManager_Builder`
    open fun spotLightCone(inner: Number, outer: Number): `LightManager_Builder`
    open fun sunAngularRadius(angularRadius: Number): `LightManager_Builder`
    open fun sunHaloFalloff(haloFalloff: Number): `LightManager_Builder`
    open fun sunHaloSize(haloSize: Number): `LightManager_Builder`
}

external open class `Skybox_Builder` {
    open fun build(engine: Engine): Skybox
    open fun color(rgba: glm.vec4): `Skybox_Builder`
    open fun color(rgba: Array<Number>): `Skybox_Builder`
    open fun environment(envmap: Texture): `Skybox_Builder`
    open fun showSun(show: Boolean): `Skybox_Builder`
}

external open class LightManager {
    open fun hasComponent(entity: Entity): Boolean
    open fun getInstance(entity: Entity): `LightManager_Instance`
    open fun getType(instance: `LightManager_Instance`): `LightManager_Type`
    open fun isDirectional(instance: `LightManager_Instance`): Boolean
    open fun isPointLight(instance: `LightManager_Instance`): Boolean
    open fun isSpotLight(instance: `LightManager_Instance`): Boolean
    open fun setPosition(instance: `LightManager_Instance`, value: glm.vec3)
    open fun setPosition(instance: `LightManager_Instance`, value: Array<Number>)
    open fun getPosition(instance: `LightManager_Instance`): dynamic /* glm.vec3 | Array<Number> */
    open fun setDirection(instance: `LightManager_Instance`, value: glm.vec3)
    open fun setDirection(instance: `LightManager_Instance`, value: Array<Number>)
    open fun getDirection(instance: `LightManager_Instance`): dynamic /* glm.vec3 | Array<Number> */
    open fun setColor(instance: `LightManager_Instance`, value: glm.vec3)
    open fun setColor(instance: `LightManager_Instance`, value: Array<Number>)
    open fun getColor(instance: `LightManager_Instance`): dynamic /* glm.vec3 | Array<Number> */
    open fun setIntensity(instance: `LightManager_Instance`, intensity: Number)
    open fun setIntensityEnergy(instance: `LightManager_Instance`, watts: Number, efficiency: Number)
    open fun getIntensity(instance: `LightManager_Instance`): Number
    open fun setFalloff(instance: `LightManager_Instance`, radius: Number)
    open fun getFalloff(instance: `LightManager_Instance`): Number
    open fun setShadowOptions(instance: `LightManager_Instance`, options: `LightManager_ShadowOptions`)
    open fun setSpotLightCone(instance: `LightManager_Instance`, inner: Number, outer: Number)
    open fun setSunAngularRadius(instance: `LightManager_Instance`, angularRadius: Number)
    open fun getSunAngularRadius(instance: `LightManager_Instance`): Number
    open fun setSunHaloSize(instance: `LightManager_Instance`, haloSize: Number)
    open fun getSunHaloSize(instance: `LightManager_Instance`): Number
    open fun setSunHaloFalloff(instance: `LightManager_Instance`, haloFalloff: Number)
    open fun getSunHaloFalloff(instance: `LightManager_Instance`): Number
    open fun setShadowCaster(instance: `LightManager_Instance`, shadowCaster: Boolean): Number
    open fun isShadowCaster(instance: `LightManager_Instance`): Boolean

    companion object {
        fun Builder(ltype: `LightManager_Type`): `LightManager_Builder`
    }
}

external interface `RenderableManager_Bone` {
    var unitQuaternion: dynamic /* glm.quat | Array<Number> */
        get() = definedExternally
        set(value) = definedExternally
    var translation: dynamic /* glm.vec3 | Array<Number> */
        get() = definedExternally
        set(value) = definedExternally
}

external open class RenderableManager {
    open fun hasComponent(entity: Entity): Boolean
    open fun getInstance(entity: Entity): `RenderableManager_Instance`
    open fun destroy(entity: Entity)
    open fun setAxisAlignedBoundingBox(instance: `RenderableManager_Instance`, aabb: Box)
    open fun setLayerMask(instance: `RenderableManager_Instance`, select: Number, values: Number)
    open fun setPriority(instance: `RenderableManager_Instance`, priority: Number)
    open fun setCastShadows(instance: `RenderableManager_Instance`, enable: Boolean)
    open fun setReceiveShadows(inst: `RenderableManager_Instance`, enable: Boolean)
    open fun isShadowCaster(instance: `RenderableManager_Instance`): Boolean
    open fun isShadowReceiver(instance: `RenderableManager_Instance`): Boolean
    open fun setBones(instance: `RenderableManager_Instance`, transforms: Array<`RenderableManager_Bone`>, offset: Number)
    open fun setBonesFromMatrices(instance: `RenderableManager_Instance`, transforms: Array<Any /* glm.mat4 | Array<Number> */>, offset: Number)
    open fun setMorphWeights(instance: `RenderableManager_Instance`, a: Number, b: Number, c: Number, d: Number)
    open fun getAxisAlignedBoundingBox(instance: `RenderableManager_Instance`): Box
    open fun getPrimitiveCount(instance: `RenderableManager_Instance`): Number
    open fun setMaterialInstanceAt(instance: `RenderableManager_Instance`, primitiveIndex: Number, materialInstance: MaterialInstance)
    open fun getMaterialInstanceAt(instance: `RenderableManager_Instance`, primitiveIndex: Number): MaterialInstance
    open fun setGeometryAt(instance: `RenderableManager_Instance`, primitiveIndex: Number, type: `RenderableManager_PrimitiveType`, vertices: VertexBuffer, indices: IndexBuffer, offset: Number, count: Number)
    open fun setBlendOrderAt(instance: `RenderableManager_Instance`, primitiveIndex: Number, order: Number)
    open fun getEnabledAttributesAt(instance: `RenderableManager_Instance`, primitiveIndex: Number): Number

    companion object {
        fun Builder(ngeos: Number): `RenderableManager_Builder`
    }
}

external open class VertexBuffer {
    open fun setBufferAt(engine: Engine, bufindex: Number, f32array: String, byteOffset: Number = definedExternally)
    open fun setBufferAt(engine: Engine, bufindex: Number, f32array: String)
    open fun setBufferAt(engine: Engine, bufindex: Number, f32array: ArrayBufferView, byteOffset: Number = definedExternally)
    open fun setBufferAt(engine: Engine, bufindex: Number, f32array: ArrayBufferView)
    open fun setBufferObjectAt(engine: Engine, bufindex: Number, bo: BufferObject)

    companion object {
        fun Builder(): `VertexBuffer_Builder`
    }
}

external open class BufferObject {
    open fun setBuffer(engine: Engine, data: String, byteOffset: Number = definedExternally)
    open fun setBuffer(engine: Engine, data: String)
    open fun setBuffer(engine: Engine, data: ArrayBufferView, byteOffset: Number = definedExternally)
    open fun setBuffer(engine: Engine, data: ArrayBufferView)

    companion object {
        fun Builder(): `BufferObject_Builder`
    }
}

external open class IndexBuffer {
    open fun setBuffer(engine: Engine, u16array: String, byteOffset: Number = definedExternally)
    open fun setBuffer(engine: Engine, u16array: String)
    open fun setBuffer(engine: Engine, u16array: ArrayBufferView, byteOffset: Number = definedExternally)
    open fun setBuffer(engine: Engine, u16array: ArrayBufferView)

    companion object {
        fun Builder(): `IndexBuffer_Builder`
    }
}

external open class Renderer {
    open fun render(swapChain: SwapChain, view: View)
    open fun setClearOptions(options: `Renderer_ClearOptions`)
    open fun renderView(view: View)
    open fun beginFrame(swapChain: SwapChain): Boolean
    open fun endFrame()
}

external open class Material {
    open fun createInstance(): MaterialInstance
    open fun createNamedInstance(name: String): MaterialInstance
    open fun getDefaultInstance(): MaterialInstance
    open fun getName(): String
}

external enum class Material_UboBatchingMode {
    DISABLED,
    DEFAULT
}

external open class Frustum {
    constructor(pv: glm.mat4)
    constructor(pv: Array<Number>)
    open fun setProjection(pv: glm.mat4)
    open fun setProjection(pv: Array<Number>)
    open fun getNormalizedPlane(plane: `Frustum_Plane`): dynamic /* glm.vec4 | Array<Number> */
    open fun intersectsBox(box: Box): Boolean
    open fun intersectsSphere(sphere: glm.vec4): Boolean
    open fun intersectsSphere(sphere: Array<Number>): Boolean
}

external open class Camera {
    open fun setProjection(proj: `Camera_Projection`, left: Number, right: Number, bottom: Number, top: Number, near: Number, far: Number)
    open fun setProjectionFov(fovInDegrees: Number, aspect: Number, near: Number, far: Number, fov: `Camera_Fov`)
    open fun setLensProjection(focalLength: Number, aspect: Number, near: Number, far: Number)
    open fun setCustomProjection(projection: glm.mat4, near: Number, far: Number)
    open fun setCustomProjection(projection: Array<Number>, near: Number, far: Number)
    open fun setScaling(scale: glm.vec2)
    open fun setScaling(scale: Array<Number>)
    open fun getProjectionMatrix(): dynamic /* glm.mat4 | Array<Number> */
    open fun getCullingProjectionMatrix(): dynamic /* glm.mat4 | Array<Number> */
    open fun getScaling(): dynamic /* glm.vec4 | Array<Number> */
    open fun getNear(): Number
    open fun getCullingFar(): Number
    open fun setModelMatrix(view: glm.mat4)
    open fun setModelMatrix(view: Array<Number>)
    open fun lookAt(eye: glm.vec3, center: glm.vec3, up: glm.vec3)
    open fun lookAt(eye: glm.vec3, center: glm.vec3, up: Array<Number>)
    open fun lookAt(eye: glm.vec3, center: Array<Number>, up: glm.vec3)
    open fun lookAt(eye: glm.vec3, center: Array<Number>, up: Array<Number>)
    open fun lookAt(eye: Array<Number>, center: glm.vec3, up: glm.vec3)
    open fun lookAt(eye: Array<Number>, center: glm.vec3, up: Array<Number>)
    open fun lookAt(eye: Array<Number>, center: Array<Number>, up: glm.vec3)
    open fun lookAt(eye: Array<Number>, center: Array<Number>, up: Array<Number>)
    open fun getModelMatrix(): dynamic /* glm.mat4 | Array<Number> */
    open fun getViewMatrix(): dynamic /* glm.mat4 | Array<Number> */
    open fun getPosition(): dynamic /* glm.vec3 | Array<Number> */
    open fun getLeftVector(): dynamic /* glm.vec3 | Array<Number> */
    open fun getUpVector(): dynamic /* glm.vec3 | Array<Number> */
    open fun getForwardVector(): dynamic /* glm.vec3 | Array<Number> */
    open fun getFrustum(): Frustum
    open fun setExposure(aperture: Number, shutterSpeed: Number, sensitivity: Number)
    open fun setExposureDirect(exposure: Number)
    open fun getAperture(): Number
    open fun getShutterSpeed(): Number
    open fun getSensitivity(): Number
    open fun getFocalLength(): Number
    open fun getFocusDistance(): Number
    open fun setFocusDistance(distance: Number)

    companion object {
        fun inverseProjection(p: glm.mat4): dynamic /* glm.mat4 | Array<Number> */
        fun inverseProjection(p: Array<Number>): dynamic /* glm.mat4 | Array<Number> */
        fun computeEffectiveFocalLength(focalLength: Number, focusDistance: Number): Number
        fun computeEffectiveFov(fovInDegrees: Number, focusDistance: Number): Number
    }
}

external open class `ColorGrading_Builder` {
    open fun quality(qualityLevel: `ColorGrading_QualityLevel`): `ColorGrading_Builder`
    open fun format(format: `ColorGrading_LutFormat`): `ColorGrading_Builder`
    open fun dimensions(dim: Number): `ColorGrading_Builder`
    open fun toneMapping(toneMapping: `ColorGrading_ToneMapping`): `ColorGrading_Builder`
    open fun luminanceScaling(luminanceScaling: Boolean): `ColorGrading_Builder`
    open fun gamutMapping(gamutMapping: Boolean): `ColorGrading_Builder`
    open fun exposure(exposure: Number): `ColorGrading_Builder`
    open fun nightAdaptation(adaptation: Boolean): `ColorGrading_Builder`
    open fun whiteBalance(temperature: Number, tint: Number): `ColorGrading_Builder`
    open fun channelMixer(outRed: glm.vec3, outGreen: glm.vec3, outBlue: glm.vec3): `ColorGrading_Builder`
    open fun channelMixer(outRed: glm.vec3, outGreen: glm.vec3, outBlue: Array<Number>): `ColorGrading_Builder`
    open fun channelMixer(outRed: glm.vec3, outGreen: Array<Number>, outBlue: glm.vec3): `ColorGrading_Builder`
    open fun channelMixer(outRed: glm.vec3, outGreen: Array<Number>, outBlue: Array<Number>): `ColorGrading_Builder`
    open fun channelMixer(outRed: Array<Number>, outGreen: glm.vec3, outBlue: glm.vec3): `ColorGrading_Builder`
    open fun channelMixer(outRed: Array<Number>, outGreen: glm.vec3, outBlue: Array<Number>): `ColorGrading_Builder`
    open fun channelMixer(outRed: Array<Number>, outGreen: Array<Number>, outBlue: glm.vec3): `ColorGrading_Builder`
    open fun channelMixer(outRed: Array<Number>, outGreen: Array<Number>, outBlue: Array<Number>): `ColorGrading_Builder`
    open fun shadowsMidtonesHighlights(shadows: glm.vec4, midtones: glm.vec4, highlights: glm.vec4, ranges: Any /* glm.vec4 | Array<Number> */): `ColorGrading_Builder`
    open fun shadowsMidtonesHighlights(shadows: glm.vec4, midtones: glm.vec4, highlights: Array<Number>, ranges: Any /* glm.vec4 | Array<Number> */): `ColorGrading_Builder`
    open fun shadowsMidtonesHighlights(shadows: glm.vec4, midtones: Array<Number>, highlights: glm.vec4, ranges: Any /* glm.vec4 | Array<Number> */): `ColorGrading_Builder`
    open fun shadowsMidtonesHighlights(shadows: glm.vec4, midtones: Array<Number>, highlights: Array<Number>, ranges: Any /* glm.vec4 | Array<Number> */): `ColorGrading_Builder`
    open fun shadowsMidtonesHighlights(shadows: Array<Number>, midtones: glm.vec4, highlights: glm.vec4, ranges: Any /* glm.vec4 | Array<Number> */): `ColorGrading_Builder`
    open fun shadowsMidtonesHighlights(shadows: Array<Number>, midtones: glm.vec4, highlights: Array<Number>, ranges: Any /* glm.vec4 | Array<Number> */): `ColorGrading_Builder`
    open fun shadowsMidtonesHighlights(shadows: Array<Number>, midtones: Array<Number>, highlights: glm.vec4, ranges: Any /* glm.vec4 | Array<Number> */): `ColorGrading_Builder`
    open fun shadowsMidtonesHighlights(shadows: Array<Number>, midtones: Array<Number>, highlights: Array<Number>, ranges: Any /* glm.vec4 | Array<Number> */): `ColorGrading_Builder`
    open fun slopeOffsetPower(slope: glm.vec3, offset: glm.vec3, power: glm.vec3): `ColorGrading_Builder`
    open fun slopeOffsetPower(slope: glm.vec3, offset: glm.vec3, power: Array<Number>): `ColorGrading_Builder`
    open fun slopeOffsetPower(slope: glm.vec3, offset: Array<Number>, power: glm.vec3): `ColorGrading_Builder`
    open fun slopeOffsetPower(slope: glm.vec3, offset: Array<Number>, power: Array<Number>): `ColorGrading_Builder`
    open fun slopeOffsetPower(slope: Array<Number>, offset: glm.vec3, power: glm.vec3): `ColorGrading_Builder`
    open fun slopeOffsetPower(slope: Array<Number>, offset: glm.vec3, power: Array<Number>): `ColorGrading_Builder`
    open fun slopeOffsetPower(slope: Array<Number>, offset: Array<Number>, power: glm.vec3): `ColorGrading_Builder`
    open fun slopeOffsetPower(slope: Array<Number>, offset: Array<Number>, power: Array<Number>): `ColorGrading_Builder`
    open fun contrast(contrast: Number): `ColorGrading_Builder`
    open fun vibrance(vibrance: Number): `ColorGrading_Builder`
    open fun saturation(saturation: Number): `ColorGrading_Builder`
    open fun curves(shadowGamma: glm.vec3, midPoint: glm.vec3, highlightScale: glm.vec3): `ColorGrading_Builder`
    open fun curves(shadowGamma: glm.vec3, midPoint: glm.vec3, highlightScale: Array<Number>): `ColorGrading_Builder`
    open fun curves(shadowGamma: glm.vec3, midPoint: Array<Number>, highlightScale: glm.vec3): `ColorGrading_Builder`
    open fun curves(shadowGamma: glm.vec3, midPoint: Array<Number>, highlightScale: Array<Number>): `ColorGrading_Builder`
    open fun curves(shadowGamma: Array<Number>, midPoint: glm.vec3, highlightScale: glm.vec3): `ColorGrading_Builder`
    open fun curves(shadowGamma: Array<Number>, midPoint: glm.vec3, highlightScale: Array<Number>): `ColorGrading_Builder`
    open fun curves(shadowGamma: Array<Number>, midPoint: Array<Number>, highlightScale: glm.vec3): `ColorGrading_Builder`
    open fun curves(shadowGamma: Array<Number>, midPoint: Array<Number>, highlightScale: Array<Number>): `ColorGrading_Builder`
    open fun build(engine: Engine): ColorGrading
}

external open class IndirectLight {
    open fun setIntensity(intensity: Number)
    open fun getIntensity(): Number
    open fun setRotation(value: glm.mat3)
    open fun setRotation(value: Array<Number>)
    open fun getRotation(): dynamic /* glm.mat3 | Array<Number> */
    open fun getReflectionsTexture(): Texture
    open fun getIrradianceTexture(): Texture
    open var shfloats: Array<Number>

    companion object {
        fun Builder(): `IndirectLight_Builder`
        fun getDirectionEstimate(f32array: Any): dynamic /* glm.vec3 | Array<Number> */
        fun getColorEstimate(f32array: Any, direction: glm.vec3): dynamic /* glm.vec4 | Array<Number> */
        fun getColorEstimate(f32array: Any, direction: Array<Number>): dynamic /* glm.vec4 | Array<Number> */
    }
}

external open class `IndirectLight_Builder` {
    open fun reflections(cubemap: Texture): `IndirectLight_Builder`
    open fun irradianceTex(cubemap: Texture): `IndirectLight_Builder`
    open fun irradianceSh(nbands: Number, f32array: Any): `IndirectLight_Builder`
    open fun intensity(value: Number): `IndirectLight_Builder`
    open fun rotation(value: glm.mat3): `IndirectLight_Builder`
    open fun rotation(value: Array<Number>): `IndirectLight_Builder`
    open fun build(engine: Engine): IndirectLight
}

external open class IcoSphere(nsubdivs: Number) {
    open fun subdivide()
    open var vertices: Float32Array
    open var tangents: Int16Array
    open var triangles: Uint16Array
}

external open class Scene {
    open fun addEntity(entity: Entity)
    open fun addEntities(entities: Array<Entity>)
    open fun getLightCount(): Number
    open fun getRenderableCount(): Number
    open fun remove(entity: Entity)
    open fun removeEntities(entities: Array<Entity>)
    open fun setIndirectLight(ibl: IndirectLight?)
    open fun setSkybox(sky: Skybox?)
}

external open class RenderTarget {
    open fun getMipLevel(): Number
    open fun getFace(): `Texture_CubemapFace`
    open fun getLayer(): Number

    companion object {
        fun Builder(): `RenderTarget_Builder`
    }
}

external open class View {
    open fun pick(x: Number, y: Number, cb: PickCallback)
    open fun setCamera(camera: Camera)
    open fun setColorGrading(colorGrading: ColorGrading)
    open fun setScene(scene: Scene)
    open fun setViewport(viewport: glm.vec4)
    open fun setViewport(viewport: Array<Number>)
    open fun setVisibleLayers(select: Number, values: Number)
    open fun setRenderTarget(renderTarget: RenderTarget)
    open fun setAmbientOcclusionOptions(options: `View_AmbientOcclusionOptions`)
    open fun setDepthOfFieldOptions(options: `View_DepthOfFieldOptions`)
    open fun setMultiSampleAntiAliasingOptions(options: `View_MultiSampleAntiAliasingOptions`)
    open fun setTemporalAntiAliasingOptions(options: `View_TemporalAntiAliasingOptions`)
    open fun setScreenSpaceReflectionsOptions(options: `View_ScreenSpaceReflectionsOptions`)
    open fun setBloomOptions(options: `View_BloomOptions`)
    open fun setFogOptions(options: `View_FogOptions`)
    open fun setVignetteOptions(options: `View_VignetteOptions`)
    open fun setGuardBandOptions(options: `View_GuardBandOptions`)
    open fun setStereoscopicOptions(options: `View_StereoscopicOptions`)
    open fun setAmbientOcclusion(ambientOcclusion: `View_AmbientOcclusion`)
    open fun getAmbientOcclusion(): `View_AmbientOcclusion`
    open fun setBlendMode(mode: `View_BlendMode`)
    open fun getBlendMode(): `View_BlendMode`
    open fun setPostProcessingEnabled(enabled: Boolean)
    open fun setAntiAliasing(antialiasing: `View_AntiAliasing`)
    open fun setStencilBufferEnabled(enabled: Boolean)
    open fun isStencilBufferEnabled(): Boolean
    open fun setTransparentPickingEnabled(enabled: Boolean)
    open fun isTransparentPickingEnabled(): Boolean
}

external open class TransformManager {
    open fun hasComponent(entity: Entity): Boolean
    open fun getInstance(entity: Entity): `TransformManager_Instance`
    open fun create(entity: Entity)
    open fun destroy(entity: Entity)
    open fun setParent(instance: `TransformManager_Instance`, parent: `TransformManager_Instance`)
    open fun setTransform(instance: `TransformManager_Instance`, xform: glm.mat4)
    open fun setTransform(instance: `TransformManager_Instance`, xform: Array<Number>)
    open fun getTransform(instance: `TransformManager_Instance`): dynamic /* glm.mat4 | Array<Number> */
    open fun getWorldTransform(instance: `TransformManager_Instance`): dynamic /* glm.mat4 | Array<Number> */
    open fun openLocalTransformTransaction()
    open fun commitLocalTransformTransaction()
}

external interface Filamesh {
    var renderable: Entity
    var vertexBuffer: VertexBuffer
    var indexBuffer: IndexBuffer
}

external interface `T_0` {
    var uboBatching: `Material_UboBatchingMode`?
        get() = definedExternally
        set(value) = definedExternally
}

external open class Engine {
    open fun execute()
    open fun createCamera(entity: Entity): Camera
    open fun createMaterial(urlOrBuffer: String, options: `T_0` = definedExternally): Material
    open fun createMaterial(urlOrBuffer: String): Material
    open fun createMaterial(urlOrBuffer: ArrayBufferView, options: `T_0` = definedExternally): Material
    open fun createMaterial(urlOrBuffer: ArrayBufferView): Material
    open fun createRenderer(): Renderer
    open fun createScene(): Scene
    open fun createSwapChain(): SwapChain
    open fun createTextureFromJpeg(urlOrBuffer: String, options: Any? = definedExternally): Texture
    open fun createTextureFromJpeg(urlOrBuffer: String): Texture
    open fun createTextureFromJpeg(urlOrBuffer: ArrayBufferView, options: Any? = definedExternally): Texture
    open fun createTextureFromJpeg(urlOrBuffer: ArrayBufferView): Texture
    open fun createTextureFromPng(urlOrBuffer: String, options: Any? = definedExternally): Texture
    open fun createTextureFromPng(urlOrBuffer: String): Texture
    open fun createTextureFromPng(urlOrBuffer: ArrayBufferView, options: Any? = definedExternally): Texture
    open fun createTextureFromPng(urlOrBuffer: ArrayBufferView): Texture
    open fun createIblFromKtx1(urlOrBuffer: String): IndirectLight
    open fun createIblFromKtx1(urlOrBuffer: ArrayBufferView): IndirectLight
    open fun createSkyFromKtx1(urlOrBuffer: String): Skybox
    open fun createSkyFromKtx1(urlOrBuffer: ArrayBufferView): Skybox
    open fun createTextureFromKtx1(urlOrBuffer: String, options: Any? = definedExternally): Texture
    open fun createTextureFromKtx1(urlOrBuffer: String): Texture
    open fun createTextureFromKtx1(urlOrBuffer: ArrayBufferView, options: Any? = definedExternally): Texture
    open fun createTextureFromKtx1(urlOrBuffer: ArrayBufferView): Texture
    open fun createTextureFromKtx2(urlOrBuffer: String, options: Any? = definedExternally): Texture
    open fun createTextureFromKtx2(urlOrBuffer: String): Texture
    open fun createTextureFromKtx2(urlOrBuffer: ArrayBufferView, options: Any? = definedExternally): Texture
    open fun createTextureFromKtx2(urlOrBuffer: ArrayBufferView): Texture
    open fun createView(): View
    open fun createAssetLoader(): `gltfio_AssetLoader`
    open fun destroySwapChain(swapChain: SwapChain)
    open fun destroyRenderer(renderer: Renderer)
    open fun destroyView(view: View)
    open fun destroyScene(scene: Scene)
    open fun destroyCameraComponent(camera: Entity)
    open fun destroyMaterial(material: Material)
    open fun destroyEntity(entity: Entity)
    open fun destroyIndexBuffer(indexBuffer: IndexBuffer)
    open fun destroyIndirectLight(indirectLight: IndirectLight)
    open fun destroyMaterialInstance(materialInstance: MaterialInstance)
    open fun destroyRenderTarget(renderTarget: RenderTarget)
    open fun destroySkybox(skybox: Skybox)
    open fun destroyTexture(texture: Texture)
    open fun destroyColorGrading(colorGrading: ColorGrading)
    open fun getCameraComponent(entity: Entity): Camera
    open fun getLightManager(): LightManager
    open fun destroyVertexBuffer(vertexBuffer: VertexBuffer)
    open fun getRenderableManager(): RenderableManager
    open fun getSupportedFormatSuffix(suffix: String)
    open fun getTransformManager(): TransformManager
    open fun init(assets: Array<String>, onready: () -> Unit)
    open fun loadFilamesh(urlOrBuffer: String, definstance: MaterialInstance = definedExternally, matinstances: Any? = definedExternally): Filamesh
    open fun loadFilamesh(urlOrBuffer: String): Filamesh
    open fun loadFilamesh(urlOrBuffer: String, definstance: MaterialInstance = definedExternally): Filamesh
    open fun loadFilamesh(urlOrBuffer: ArrayBufferView, definstance: MaterialInstance = definedExternally, matinstances: Any? = definedExternally): Filamesh
    open fun loadFilamesh(urlOrBuffer: ArrayBufferView): Filamesh
    open fun loadFilamesh(urlOrBuffer: ArrayBufferView, definstance: MaterialInstance = definedExternally): Filamesh

    companion object {
        fun create(canvas: HTMLCanvasElement, contextOptions: Any? = definedExternally): Engine
        fun destroy(engine: Engine)
        fun getMaxStereoscopicEyes(): Number
    }
}

external open class Ktx2Reader(engine: Engine, quiet: Boolean) {
    open fun requestFormat(format: `Texture_InternalFormat`)
    open fun unrequestFormat(format: `Texture_InternalFormat`)
    open fun load(urlOrBuffer: String, transfer: Ktx2Reader_TransferFunction): Texture?
    open fun load(urlOrBuffer: ArrayBufferView, transfer: Ktx2Reader_TransferFunction): Texture?
}

external open class `gltfio_AssetLoader` {
    open fun createAsset(urlOrBuffer: String): `gltfio_FilamentAsset`
    open fun createAsset(urlOrBuffer: ArrayBufferView): `gltfio_FilamentAsset`
    open fun createInstancedAsset(urlOrBuffer: String, instances: Array<`gltfio_FilamentInstance`?>): `gltfio_FilamentAsset`
    open fun createInstancedAsset(urlOrBuffer: ArrayBufferView, instances: Array<`gltfio_FilamentInstance`?>): `gltfio_FilamentAsset`
    open fun destroyAsset(asset: `gltfio_FilamentAsset`)
    open fun createInstance(asset: `gltfio_FilamentAsset`): `gltfio_FilamentInstance`?
    open fun delete()
}

external open class `gltfio_FilamentAsset` {
    open fun loadResources(onDone: () -> Unit?, onFetched: (s: String) -> Unit?, basePath: String?, asyncInterval: Number?, options: Any? = definedExternally)
    open fun getEntities(): Array<Entity>
    open fun getEntitiesByName(name: String): Array<Entity>
    open fun getEntityByName(name: String): Entity
    open fun getEntitiesByPrefix(name: String): Array<Entity>
    open fun getLightEntities(): Array<Entity>
    open fun getRenderableEntities(): Array<Entity>
    open fun getCameraEntities(): Array<Entity>
    open fun getRoot(): Entity
    open fun popRenderable(): Entity
    open fun getInstance(): `gltfio_FilamentInstance`
    open fun geAssetInstances(): Array<`gltfio_FilamentInstance`>
    open fun getResourceUris(): Array<String>
    open fun getBoundingBox(): Aabb
    open fun getName(entity: Entity): String
    open fun getExtras(entity: Entity): String
    open fun getWireframe(): Entity
    open fun getEngine(): Engine
    open fun releaseSourceData()
}

external open class `gltfio_FilamentInstance` {
    open fun getAsset(): `gltfio_FilamentAsset`
    open fun getEntities(): Vector<Entity>
    open fun getRoot(): Entity
    open fun getAnimator(): `gltfio_Animator`
    open fun getSkinNames(): Vector<String>
    open fun attachSkin(skinIndex: Number, entity: Entity)
    open fun detachSkin(skinIndex: Number, entity: Entity)
    open fun getMaterialInstances(): Vector<MaterialInstance>
    open fun detachMaterialInstances()
    open fun getMaterialVariantNames(): Array<String>
    open fun applyMaterialVariant(index: Number)
}

external open class `gltfio_Animator` {
    open fun applyAnimation(index: Number)
    open fun applyCrossFade(previousAnimIndex: Number, previousAnimTime: Number, alpha: Number)
    open fun updateBoneMatrices()
    open fun resetBoneMatrices()
    open fun getAnimationCount(): Number
    open fun getAnimationDuration(index: Number): Number
    open fun getAnimationName(index: Number): String
}

external open class `SurfaceOrientation_Builder` {
    open fun vertexCount(count: Number): `SurfaceOrientation_Builder`
    open fun normals(vec3array: Float32Array, stride: Number): `SurfaceOrientation_Builder`
    open fun uvs(vec2array: Float32Array, stride: Number): `SurfaceOrientation_Builder`
    open fun positions(vec3array: Float32Array, stride: Number): `SurfaceOrientation_Builder`
    open fun triangleCount(count: Number): `SurfaceOrientation_Builder`
    open fun triangles16(indices: Uint16Array): `SurfaceOrientation_Builder`
    open fun triangles32(indices: Uint32Array): `SurfaceOrientation_Builder`
    open fun build(): SurfaceOrientation
}

external open class SurfaceOrientation {
    open fun getQuats(quatCount: Number): Int16Array
    open fun getQuatsHalf4(quatCount: Number): Uint16Array
    open fun getQuatsFloat4(quatCount: Number): Float32Array
    open fun delete()

    companion object {
        fun Builder(): `SurfaceOrientation_Builder`
    }
}

external enum class Frustum_Plane {
    LEFT,
    RIGHT,
    BOTTOM,
    TOP,
    FAR,
    NEAR
}

external enum class Camera_Fov {
    VERTICAL,
    HORIZONTAL
}

external enum class Camera_Projection {
    PERSPECTIVE,
    ORTHO
}

external enum class ColorGrading_QualityLevel {
    LOW,
    MEDIUM,
    HIGH,
    ULTRA
}

external enum class ColorGrading_LutFormat {
    INTEGER,
    FLOAT
}

external enum class ColorGrading_ToneMapping {
    LINEAR,
    ACES_LEGACY,
    ACES,
    FILMIC,
    EVILS,
    REINHARD,
    DISPLAY_RANGE
}

external enum class CompressedPixelDataType {
    EAC_R11,
    EAC_R11_SIGNED,
    EAC_RG11,
    EAC_RG11_SIGNED,
    ETC2_RGB8,
    ETC2_SRGB8,
    ETC2_RGB8_A1,
    ETC2_SRGB8_A1,
    ETC2_EAC_RGBA8,
    ETC2_EAC_SRGBA8,
    DXT1_RGB,
    DXT1_RGBA,
    DXT3_RGBA,
    DXT5_RGBA,
    DXT1_SRGB,
    DXT1_SRGBA,
    DXT3_SRGBA,
    DXT5_SRGBA,
    RGBA_ASTC_4x4,
    RGBA_ASTC_5x4,
    RGBA_ASTC_5x5,
    RGBA_ASTC_6x5,
    RGBA_ASTC_6x6,
    RGBA_ASTC_8x5,
    RGBA_ASTC_8x6,
    RGBA_ASTC_8x8,
    RGBA_ASTC_10x5,
    RGBA_ASTC_10x6,
    RGBA_ASTC_10x8,
    RGBA_ASTC_10x10,
    RGBA_ASTC_12x10,
    RGBA_ASTC_12x12,
    SRGB8_ALPHA8_ASTC_4x4,
    SRGB8_ALPHA8_ASTC_5x4,
    SRGB8_ALPHA8_ASTC_5x5,
    SRGB8_ALPHA8_ASTC_6x5,
    SRGB8_ALPHA8_ASTC_6x6,
    SRGB8_ALPHA8_ASTC_8x5,
    SRGB8_ALPHA8_ASTC_8x6,
    SRGB8_ALPHA8_ASTC_8x8,
    SRGB8_ALPHA8_ASTC_10x5,
    SRGB8_ALPHA8_ASTC_10x6,
    SRGB8_ALPHA8_ASTC_10x8,
    SRGB8_ALPHA8_ASTC_10x10,
    SRGB8_ALPHA8_ASTC_12x10,
    SRGB8_ALPHA8_ASTC_12x12
}

external enum class IndexBuffer_IndexType {
    USHORT,
    UINT
}

external enum class BufferObject_BindingType {
    VERTEX
}

external enum class LightManager_Type {
    SUN,
    DIRECTIONAL,
    POINT,
    FOCUSED_SPOT,
    SPOT
}

external enum class MagFilter {
    NEAREST,
    LINEAR
}

external enum class MinFilter {
    NEAREST,
    LINEAR,
    NEAREST_MIPMAP_NEAREST,
    LINEAR_MIPMAP_NEAREST,
    NEAREST_MIPMAP_LINEAR,
    LINEAR_MIPMAP_LINEAR
}

external enum class CompareMode {
    NONE,
    COMPARE_TO_TEXTURE
}

external enum class CompareFunc {
    LESS_EQUAL,
    GREATER_EQUAL,
    LESS,
    GREATER,
    EQUAL,
    NOT_EQUAL,
    ALWAYS,
    NEVER
}

external enum class CullingMode {
    NONE,
    FRONT,
    BACK,
    FRONT_AND_BACK
}

external enum class StencilOperation {
    KEEP,
    ZERO,
    REPLACE,
    INCR_CLAMP,
    INCR_WRAP,
    DECR_CLAMP,
    DECR_WRAP,
    INVERT
}

external enum class StencilFace {
    FRONT,
    BACK,
    FRONT_AND_BACK
}

external enum class PixelDataFormat {
    R,
    R_INTEGER,
    RG,
    RG_INTEGER,
    RGB,
    RGB_INTEGER,
    RGBA,
    RGBA_INTEGER,
    UNUSED,
    DEPTH_COMPONENT,
    DEPTH_STENCIL,
    ALPHA
}

external enum class PixelDataType {
    UBYTE,
    BYTE,
    USHORT,
    SHORT,
    UINT,
    INT,
    HALF,
    FLOAT,
    UINT_10F_11F_11F_REV,
    USHORT_565
}

external enum class RenderableManager_PrimitiveType {
    POINTS,
    LINES,
    LINE_STRIP,
    TRIANGLES,
    TRIANGLE_STRIP,
    NONE
}

external enum class RgbType {
    sRGB,
    LINEAR
}

external enum class RgbaType {
    sRGB,
    LINEAR,
    PREMULTIPLIED_sRGB,
    PREMULTIPLIED_LINEAR
}

external enum class Texture_InternalFormat {
    R8,
    R8_SNORM,
    R8UI,
    R8I,
    STENCIL8,
    R16F,
    R16UI,
    R16I,
    RG8,
    RG8_SNORM,
    RG8UI,
    RG8I,
    RGB565,
    RGB9_E5,
    RGB5_A1,
    RGBA4,
    DEPTH16,
    RGB8,
    SRGB8,
    RGB8_SNORM,
    RGB8UI,
    RGB8I,
    DEPTH24,
    R32F,
    R32UI,
    R32I,
    RG16F,
    RG16UI,
    RG16I,
    R11F_G11F_B10F,
    RGBA8,
    SRGB8_A8,
    RGBA8_SNORM,
    UNUSED,
    RGB10_A2,
    RGBA8UI,
    RGBA8I,
    DEPTH32F,
    DEPTH24_STENCIL8,
    DEPTH32F_STENCIL8,
    RGB16F,
    RGB16UI,
    RGB16I,
    RG32F,
    RG32UI,
    RG32I,
    RGBA16F,
    RGBA16UI,
    RGBA16I,
    RGB32F,
    RGB32UI,
    RGB32I,
    RGBA32F,
    RGBA32UI,
    RGBA32I,
    EAC_R11,
    EAC_R11_SIGNED,
    EAC_RG11,
    EAC_RG11_SIGNED,
    ETC2_RGB8,
    ETC2_SRGB8,
    ETC2_RGB8_A1,
    ETC2_SRGB8_A1,
    ETC2_EAC_RGBA8,
    ETC2_EAC_SRGBA8,
    DXT1_RGB,
    DXT1_RGBA,
    DXT3_RGBA,
    DXT5_RGBA,
    DXT1_SRGB,
    DXT1_SRGBA,
    DXT3_SRGBA,
    DXT5_SRGBA,
    RGBA_ASTC_4x4,
    RGBA_ASTC_5x4,
    RGBA_ASTC_5x5,
    RGBA_ASTC_6x5,
    RGBA_ASTC_6x6,
    RGBA_ASTC_8x5,
    RGBA_ASTC_8x6,
    RGBA_ASTC_8x8,
    RGBA_ASTC_10x5,
    RGBA_ASTC_10x6,
    RGBA_ASTC_10x8,
    RGBA_ASTC_10x10,
    RGBA_ASTC_12x10,
    RGBA_ASTC_12x12,
    SRGB8_ALPHA8_ASTC_4x4,
    SRGB8_ALPHA8_ASTC_5x4,
    SRGB8_ALPHA8_ASTC_5x5,
    SRGB8_ALPHA8_ASTC_6x5,
    SRGB8_ALPHA8_ASTC_6x6,
    SRGB8_ALPHA8_ASTC_8x5,
    SRGB8_ALPHA8_ASTC_8x6,
    SRGB8_ALPHA8_ASTC_8x8,
    SRGB8_ALPHA8_ASTC_10x5,
    SRGB8_ALPHA8_ASTC_10x6,
    SRGB8_ALPHA8_ASTC_10x8,
    SRGB8_ALPHA8_ASTC_10x10,
    SRGB8_ALPHA8_ASTC_12x10,
    SRGB8_ALPHA8_ASTC_12x12
}

external enum class Texture_Sampler {
    SAMPLER_2D,
    SAMPLER_CUBEMAP,
    SAMPLER_EXTERNAL
}

external enum class TextureUsage {
    COLOR_ATTACHMENT /* = 1 */,
    DEPTH_ATTACHMENT /* = 2 */,
    STENCIL_ATTACHMENT /* = 4 */,
    UPLOADABLE /* = 8 */,
    SAMPLEABLE /* = 16 */,
    SUBPASS_INPUT /* = 32 */,
    DEFAULT /* = UPLOADABLE | SAMPLEABLE */
}

external enum class Texture_CubemapFace {
    POSITIVE_X,
    NEGATIVE_X,
    POSITIVE_Y,
    NEGATIVE_Y,
    POSITIVE_Z,
    NEGATIVE_Z
}

external enum class RenderTarget_AttachmentPoint {
    COLOR,
    DEPTH
}

external enum class View_AmbientOcclusion {
    NONE,
    SSAO
}

external enum class VertexAttribute {
    POSITION /* = 0 */,
    TANGENTS /* = 1 */,
    COLOR /* = 2 */,
    UV0 /* = 3 */,
    UV1 /* = 4 */,
    BONE_INDICES /* = 5 */,
    BONE_WEIGHTS /* = 6 */,
    UNUSED /* = 7 */,
    CUSTOM0 /* = 8 */,
    CUSTOM1 /* = 9 */,
    CUSTOM2 /* = 10 */,
    CUSTOM3 /* = 11 */,
    CUSTOM4 /* = 12 */,
    CUSTOM5 /* = 13 */,
    CUSTOM6 /* = 14 */,
    CUSTOM7 /* = 15 */,
    MORPH_POSITION_0 /* = CUSTOM0 */,
    MORPH_POSITION_1 /* = CUSTOM1 */,
    MORPH_POSITION_2 /* = CUSTOM2 */,
    MORPH_POSITION_3 /* = CUSTOM3 */,
    MORPH_TANGENTS_0 /* = CUSTOM4 */,
    MORPH_TANGENTS_1 /* = CUSTOM5 */,
    MORPH_TANGENTS_2 /* = CUSTOM6 */,
    MORPH_TANGENTS_3 /* = CUSTOM7 */
}

external enum class VertexBuffer_AttributeType {
    BYTE,
    BYTE2,
    BYTE3,
    BYTE4,
    UBYTE,
    UBYTE2,
    UBYTE3,
    UBYTE4,
    SHORT,
    SHORT2,
    SHORT3,
    SHORT4,
    USHORT,
    USHORT2,
    USHORT3,
    USHORT4,
    INT,
    UINT,
    FLOAT,
    FLOAT2,
    FLOAT3,
    FLOAT4,
    HALF,
    HALF2,
    HALF3,
    HALF4
}

external enum class WrapMode {
    CLAMP_TO_EDGE,
    REPEAT,
    MIRRORED_REPEAT
}

external enum class Ktx2Reader_TransferFunction {
    LINEAR,
    sRGB
}

external enum class Ktx2Reader_Result {
    SUCCESS,
    COMPRESSED_TRANSCODE_FAILURE,
    UNCOMPRESSED_TRANSCODE_FAILURE,
    FORMAT_UNSUPPORTED,
    FORMAT_ALREADY_REQUESTED
}

external fun _malloc(size: Number): Number

external fun _free(size: Number)

external interface HeapInterface {
    fun set(buffer: Any, pointer: Number): Any
    fun subarray(buffer: Any, offset: Number): Any
}

external var HEAPU8: HeapInterface

external enum class View_QualityLevel {
    LOW,
    MEDIUM,
    HIGH,
    ULTRA
}

external enum class View_BlendMode {
    OPAQUE,
    TRANSLUCENT
}

external interface `View_DynamicResolutionOptions` {
    var minScale: dynamic /* glm.vec2? | Array<Number>? */
        get() = definedExternally
        set(value) = definedExternally
    var maxScale: dynamic /* glm.vec2? | Array<Number>? */
        get() = definedExternally
        set(value) = definedExternally
    var sharpness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var homogeneousScaling: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var quality: `View_QualityLevel`?
        get() = definedExternally
        set(value) = definedExternally
}

external enum class View_BloomOptions_BlendMode {
    ADD,
    INTERPOLATE
}

external interface `View_BloomOptions` {
    var strength: Number?
        get() = definedExternally
        set(value) = definedExternally
    var resolution: Number?
        get() = definedExternally
        set(value) = definedExternally
    var levels: Number?
        get() = definedExternally
        set(value) = definedExternally
    var blendMode: `View_BloomOptions_BlendMode`?
        get() = definedExternally
        set(value) = definedExternally
    var threshold: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var highlight: Number?
        get() = definedExternally
        set(value) = definedExternally
    var quality: `View_QualityLevel`?
        get() = definedExternally
        set(value) = definedExternally
    var lensFlare: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var starburst: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var chromaticAberration: Number?
        get() = definedExternally
        set(value) = definedExternally
    var ghostCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var ghostSpacing: Number?
        get() = definedExternally
        set(value) = definedExternally
    var ghostThreshold: Number?
        get() = definedExternally
        set(value) = definedExternally
    var haloThickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var haloRadius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var haloThreshold: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_FogOptions` {
    var distance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var cutOffDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maximumOpacity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    var heightFalloff: Number?
        get() = definedExternally
        set(value) = definedExternally
    var color: dynamic /* glm.vec3? | Array<Number>? */
        get() = definedExternally
        set(value) = definedExternally
    var density: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inScatteringStart: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inScatteringSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var fogColorFromIbl: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external enum class View_DepthOfFieldOptions_Filter {
    NONE,
    UNUSED,
    MEDIAN
}

external interface `View_DepthOfFieldOptions` {
    var cocScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var cocAspectRatio: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxApertureDiameter: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var filter: `View_DepthOfFieldOptions_Filter`?
        get() = definedExternally
        set(value) = definedExternally
    var nativeResolution: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var foregroundRingCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var backgroundRingCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var fastGatherRingCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxForegroundCOC: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxBackgroundCOC: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_VignetteOptions` {
    var midPoint: Number?
        get() = definedExternally
        set(value) = definedExternally
    var roundness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var feather: Number?
        get() = definedExternally
        set(value) = definedExternally
    var color: dynamic /* glm.vec4? | Array<Number>? */
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_RenderQuality` {
    var hdrColorBuffer: `View_QualityLevel`?
        get() = definedExternally
        set(value) = definedExternally
}

external enum class View_AmbientOcclusionOptions_AmbientOcclusionType {
    SAO,
    GTAO
}

external interface `View_AmbientOcclusionOptions_Ssct` {
    var lightConeRad: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shadowDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var contactDistanceMax: Number?
        get() = definedExternally
        set(value) = definedExternally
    var intensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lightDirection: dynamic /* glm.vec3? | Array<Number>? */
        get() = definedExternally
        set(value) = definedExternally
    var depthBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depthSlopeBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sampleCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var rayCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_AmbientOcclusionOptions_Gtao` {
    var sampleSliceCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sampleStepsPerSlice: Number?
        get() = definedExternally
        set(value) = definedExternally
    var thicknessHeuristic: Number?
        get() = definedExternally
        set(value) = definedExternally
    var useVisibilityBitmasks: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var constThickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var linearThickness: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_AmbientOcclusionOptions` {
    var aoType: `View_AmbientOcclusionOptions_AmbientOcclusionType`?
        get() = definedExternally
        set(value) = definedExternally
    var radius: Number?
        get() = definedExternally
        set(value) = definedExternally
    var power: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var resolution: Number?
        get() = definedExternally
        set(value) = definedExternally
    var intensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bilateralThreshold: Number?
        get() = definedExternally
        set(value) = definedExternally
    var quality: `View_QualityLevel`?
        get() = definedExternally
        set(value) = definedExternally
    var lowPassFilter: `View_QualityLevel`?
        get() = definedExternally
        set(value) = definedExternally
    var upsampling: `View_QualityLevel`?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var bentNormals: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var minHorizonAngleRad: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_MultiSampleAntiAliasingOptions` {
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var sampleCount: Number?
        get() = definedExternally
        set(value) = definedExternally
    var customResolve: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external enum class View_TemporalAntiAliasingOptions_BoxType {
    AABB,
    AABB_VARIANCE
}

external enum class View_TemporalAntiAliasingOptions_BoxClipping {
    ACCURATE,
    CLAMP,
    NONE
}

external enum class View_TemporalAntiAliasingOptions_JitterPattern {
    RGSS_X4,
    UNIFORM_HELIX_X4,
    HALTON_23_X8,
    HALTON_23_X16,
    HALTON_23_X32
}

external interface `View_TemporalAntiAliasingOptions` {
    var filterWidth: Number?
        get() = definedExternally
        set(value) = definedExternally
    var feedback: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lodBias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var sharpness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var upscaling: Number?
        get() = definedExternally
        set(value) = definedExternally
    var filterHistory: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var filterInput: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var useYCoCg: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var hdr: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var boxType: `View_TemporalAntiAliasingOptions_BoxType`?
        get() = definedExternally
        set(value) = definedExternally
    var boxClipping: `View_TemporalAntiAliasingOptions_BoxClipping`?
        get() = definedExternally
        set(value) = definedExternally
    var jitterPattern: `View_TemporalAntiAliasingOptions_JitterPattern`?
        get() = definedExternally
        set(value) = definedExternally
    var varianceGamma: Number?
        get() = definedExternally
        set(value) = definedExternally
    var preventFlickering: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var historyReprojection: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_ScreenSpaceReflectionsOptions` {
    var thickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bias: Number?
        get() = definedExternally
        set(value) = definedExternally
    var maxDistance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var stride: Number?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_GuardBandOptions` {
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external enum class View_AntiAliasing {
    NONE,
    FXAA
}

external enum class View_Dithering {
    NONE,
    TEMPORAL
}

external enum class View_ShadowType {
    PCF,
    VSM,
    DPCF,
    PCSS,
    PCFd
}

external interface `View_VsmShadowOptions` {
    var anisotropy: Number?
        get() = definedExternally
        set(value) = definedExternally
    var mipmapping: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var msaaSamples: Number?
        get() = definedExternally
        set(value) = definedExternally
    var highPrecision: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var minVarianceScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var lightBleedReduction: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_SoftShadowOptions` {
    var penumbraScale: Number?
        get() = definedExternally
        set(value) = definedExternally
    var penumbraRatioScale: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `View_StereoscopicOptions` {
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}
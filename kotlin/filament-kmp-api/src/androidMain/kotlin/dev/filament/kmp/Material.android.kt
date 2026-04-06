package dev.filament.kmp

import com.google.android.filament.Material as AndroidMaterial
import java.nio.Buffer

actual class Material internal constructor(val nativeMaterial: AndroidMaterial) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidMaterial.Builder()

        actual fun payload(buffer: Any, size: Int): Builder {
            nativeBuilder.payload(buffer as Buffer, size)
            return this
        }
        actual fun build(engine: Engine): Material = Material(nativeBuilder.build(engine.nativeEngine))
    }

    actual enum class Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    actual enum class BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    actual enum class CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }
    actual enum class TransparencyMode { DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES }

    actual fun getName(): String = nativeMaterial.name
    actual fun getShading(): Shading = Shading.values()[nativeMaterial.shading.ordinal]
    actual fun getBlendingMode(): BlendingMode = BlendingMode.values()[nativeMaterial.blendingMode.ordinal]
    actual fun getTransparencyMode(): TransparencyMode = TransparencyMode.values()[nativeMaterial.transparencyMode.ordinal]
    actual fun getCullingMode(): CullingMode = CullingMode.values()[nativeMaterial.cullingMode.ordinal]

    actual fun isColorWriteEnabled(): Boolean = nativeMaterial.isColorWriteEnabled
    actual fun isDepthWriteEnabled(): Boolean = nativeMaterial.isDepthWriteEnabled
    actual fun isDepthCullingEnabled(): Boolean = nativeMaterial.isDepthCullingEnabled
    actual fun isDoubleSided(): Boolean = nativeMaterial.isDoubleSided
    actual fun isAlphaToCoverageEnabled(): Boolean = nativeMaterial.isAlphaToCoverageEnabled

    actual fun getMaskThreshold(): Float = nativeMaterial.maskThreshold
    actual fun getSpecularAntiAliasingVariance(): Float = nativeMaterial.specularAntiAliasingVariance
    actual fun getSpecularAntiAliasingThreshold(): Float = nativeMaterial.specularAntiAliasingThreshold

    actual fun createInstance(): MaterialInstance = MaterialInstance(nativeMaterial.createInstance())
    actual fun createInstance(name: String): MaterialInstance = MaterialInstance(nativeMaterial.createInstance(name))
    actual fun getDefaultInstance(): MaterialInstance = MaterialInstance(nativeMaterial.defaultInstance)
}

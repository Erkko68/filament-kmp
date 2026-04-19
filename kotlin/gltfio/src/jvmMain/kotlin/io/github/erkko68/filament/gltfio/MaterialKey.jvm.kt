package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.jni.MaterialProvider.MaterialKey as JniMaterialKey

actual class MaterialKey actual constructor() {
    internal val jni = JniMaterialKey()

    actual var doubleSided: Boolean get() = jni.doubleSided; set(v) { jni.doubleSided = v }
    actual var unlit: Boolean get() = jni.unlit; set(v) { jni.unlit = v }
    actual var hasVertexColors: Boolean get() = jni.hasVertexColors; set(v) { jni.hasVertexColors = v }
    actual var hasBaseColorTexture: Boolean get() = jni.hasBaseColorTexture; set(v) { jni.hasBaseColorTexture = v }
    actual var hasNormalTexture: Boolean get() = jni.hasNormalTexture; set(v) { jni.hasNormalTexture = v }
    actual var hasOcclusionTexture: Boolean get() = jni.hasOcclusionTexture; set(v) { jni.hasOcclusionTexture = v }
    actual var hasEmissiveTexture: Boolean get() = jni.hasEmissiveTexture; set(v) { jni.hasEmissiveTexture = v }
    actual var useSpecularGlossiness: Boolean get() = jni.useSpecularGlossiness; set(v) { jni.useSpecularGlossiness = v }
    actual var alphaMode: Int get() = jni.alphaMode; set(v) { jni.alphaMode = v }
    actual var enableDiagnostics: Boolean get() = jni.enableDiagnostics; set(v) { jni.enableDiagnostics = v }
    actual var hasMetallicRoughnessTexture: Boolean get() = jni.hasMetallicRoughnessTexture; set(v) { jni.hasMetallicRoughnessTexture = v }
    actual var metallicRoughnessUV: Int get() = jni.metallicRoughnessUV; set(v) { jni.metallicRoughnessUV = v }
    actual var baseColorUV: Int get() = jni.baseColorUV; set(v) { jni.baseColorUV = v }
    actual var hasClearCoatTexture: Boolean get() = jni.hasClearCoatTexture; set(v) { jni.hasClearCoatTexture = v }
    actual var clearCoatUV: Int get() = jni.clearCoatUV; set(v) { jni.clearCoatUV = v }
    actual var hasClearCoatRoughnessTexture: Boolean get() = jni.hasClearCoatRoughnessTexture; set(v) { jni.hasClearCoatRoughnessTexture = v }
    actual var clearCoatRoughnessUV: Int get() = jni.clearCoatRoughnessUV; set(v) { jni.clearCoatRoughnessUV = v }
    actual var hasClearCoatNormalTexture: Boolean get() = jni.hasClearCoatNormalTexture; set(v) { jni.hasClearCoatNormalTexture = v }
    actual var clearCoatNormalUV: Int get() = jni.clearCoatNormalUV; set(v) { jni.clearCoatNormalUV = v }
    actual var hasClearCoat: Boolean get() = jni.hasClearCoat; set(v) { jni.hasClearCoat = v }
    actual var hasTransmission: Boolean get() = jni.hasTransmission; set(v) { jni.hasTransmission = v }
    actual var hasTextureTransforms: Boolean get() = jni.hasTextureTransforms; set(v) { jni.hasTextureTransforms = v }
    actual var emissiveUV: Int get() = jni.emissiveUV; set(v) { jni.emissiveUV = v }
    actual var aoUV: Int get() = jni.aoUV; set(v) { jni.aoUV = v }
    actual var normalUV: Int get() = jni.normalUV; set(v) { jni.normalUV = v }
    actual var hasTransmissionTexture: Boolean get() = jni.hasTransmissionTexture; set(v) { jni.hasTransmissionTexture = v }
    actual var transmissionUV: Int get() = jni.transmissionUV; set(v) { jni.transmissionUV = v }
    actual var hasSheenColorTexture: Boolean get() = jni.hasSheenColorTexture; set(v) { jni.hasSheenColorTexture = v }
    actual var sheenColorUV: Int get() = jni.sheenColorUV; set(v) { jni.sheenColorUV = v }
    actual var hasSheenRoughnessTexture: Boolean get() = jni.hasSheenRoughnessTexture; set(v) { jni.hasSheenRoughnessTexture = v }
    actual var sheenRoughnessUV: Int get() = jni.sheenRoughnessUV; set(v) { jni.sheenRoughnessUV = v }
    actual var hasVolumeThicknessTexture: Boolean get() = jni.hasVolumeThicknessTexture; set(v) { jni.hasVolumeThicknessTexture = v }
    actual var volumeThicknessUV: Int get() = jni.volumeThicknessUV; set(v) { jni.volumeThicknessUV = v }
    actual var hasSheen: Boolean get() = jni.hasSheen; set(v) { jni.hasSheen = v }
    actual var hasIOR: Boolean get() = jni.hasIOR; set(v) { jni.hasIOR = v }

    actual fun constrainMaterial(uvmap: IntArray) {
        jni.constrainMaterial(uvmap)
    }
}

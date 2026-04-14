package io.github.erkko68.filament.gltfio

actual class MaterialKey {
    internal val nativeObject = com.google.android.filament.gltfio.MaterialProvider.MaterialKey()

    actual constructor()

    actual var doubleSided: Boolean
        get() = nativeObject.doubleSided
        set(value) { nativeObject.doubleSided = value }
    actual var unlit: Boolean
        get() = nativeObject.unlit
        set(value) { nativeObject.unlit = value }
    actual var hasVertexColors: Boolean
        get() = nativeObject.hasVertexColors
        set(value) { nativeObject.hasVertexColors = value }
    actual var hasBaseColorTexture: Boolean
        get() = nativeObject.hasBaseColorTexture
        set(value) { nativeObject.hasBaseColorTexture = value }
    actual var hasNormalTexture: Boolean
        get() = nativeObject.hasNormalTexture
        set(value) { nativeObject.hasNormalTexture = value }
    actual var hasOcclusionTexture: Boolean
        get() = nativeObject.hasOcclusionTexture
        set(value) { nativeObject.hasOcclusionTexture = value }
    actual var hasEmissiveTexture: Boolean
        get() = nativeObject.hasEmissiveTexture
        set(value) { nativeObject.hasEmissiveTexture = value }
    actual var useSpecularGlossiness: Boolean
        get() = nativeObject.useSpecularGlossiness
        set(value) { nativeObject.useSpecularGlossiness = value }
    actual var alphaMode: Int
        get() = nativeObject.alphaMode
        set(value) { nativeObject.alphaMode = value }
    actual var enableDiagnostics: Boolean
        get() = nativeObject.enableDiagnostics
        set(value) { nativeObject.enableDiagnostics = value }
    actual var hasMetallicRoughnessTexture: Boolean
        get() = nativeObject.hasMetallicRoughnessTexture
        set(value) { nativeObject.hasMetallicRoughnessTexture = value }
    actual var metallicRoughnessUV: Int
        get() = nativeObject.metallicRoughnessUV
        set(value) { nativeObject.metallicRoughnessUV = value }
    actual var baseColorUV: Int
        get() = nativeObject.baseColorUV
        set(value) { nativeObject.baseColorUV = value }
    actual var hasClearCoatTexture: Boolean
        get() = nativeObject.hasClearCoatTexture
        set(value) { nativeObject.hasClearCoatTexture = value }
    actual var clearCoatUV: Int
        get() = nativeObject.clearCoatUV
        set(value) { nativeObject.clearCoatUV = value }
    actual var hasClearCoatRoughnessTexture: Boolean
        get() = nativeObject.hasClearCoatRoughnessTexture
        set(value) { nativeObject.hasClearCoatRoughnessTexture = value }
    actual var clearCoatRoughnessUV: Int
        get() = nativeObject.clearCoatRoughnessUV
        set(value) { nativeObject.clearCoatRoughnessUV = value }
    actual var hasClearCoatNormalTexture: Boolean
        get() = nativeObject.hasClearCoatNormalTexture
        set(value) { nativeObject.hasClearCoatNormalTexture = value }
    actual var clearCoatNormalUV: Int
        get() = nativeObject.clearCoatNormalUV
        set(value) { nativeObject.clearCoatNormalUV = value }
    actual var hasClearCoat: Boolean
        get() = nativeObject.hasClearCoat
        set(value) { nativeObject.hasClearCoat = value }
    actual var hasTransmission: Boolean
        get() = nativeObject.hasTransmission
        set(value) { nativeObject.hasTransmission = value }
    actual var hasTextureTransforms: Boolean
        get() = nativeObject.hasTextureTransforms
        set(value) { nativeObject.hasTextureTransforms = value }
    actual var emissiveUV: Int
        get() = nativeObject.emissiveUV
        set(value) { nativeObject.emissiveUV = value }
    actual var aoUV: Int
        get() = nativeObject.aoUV
        set(value) { nativeObject.aoUV = value }
    actual var normalUV: Int
        get() = nativeObject.normalUV
        set(value) { nativeObject.normalUV = value }
    actual var hasTransmissionTexture: Boolean
        get() = nativeObject.hasTransmissionTexture
        set(value) { nativeObject.hasTransmissionTexture = value }
    actual var transmissionUV: Int
        get() = nativeObject.transmissionUV
        set(value) { nativeObject.transmissionUV = value }
    actual var hasSheenColorTexture: Boolean
        get() = nativeObject.hasSheenColorTexture
        set(value) { nativeObject.hasSheenColorTexture = value }
    actual var sheenColorUV: Int
        get() = nativeObject.sheenColorUV
        set(value) { nativeObject.sheenColorUV = value }
    actual var hasSheenRoughnessTexture: Boolean
        get() = nativeObject.hasSheenRoughnessTexture
        set(value) { nativeObject.hasSheenRoughnessTexture = value }
    actual var sheenRoughnessUV: Int
        get() = nativeObject.sheenRoughnessUV
        set(value) { nativeObject.sheenRoughnessUV = value }
    actual var hasVolumeThicknessTexture: Boolean
        get() = nativeObject.hasVolumeThicknessTexture
        set(value) { nativeObject.hasVolumeThicknessTexture = value }
    actual var volumeThicknessUV: Int
        get() = nativeObject.volumeThicknessUV
        set(value) { nativeObject.volumeThicknessUV = value }
    actual var hasSheen: Boolean
        get() = nativeObject.hasSheen
        set(value) { nativeObject.hasSheen = value }
    actual var hasIOR: Boolean
        get() = nativeObject.hasIOR
        set(value) { nativeObject.hasIOR = value }

    actual fun constrainMaterial(uvmap: IntArray) {
        nativeObject.constrainMaterial(uvmap)
    }
}

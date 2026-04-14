package io.github.erkko68.filament.gltfio

expect class MaterialKey {
    constructor()

    var doubleSided: Boolean
    var unlit: Boolean
    var hasVertexColors: Boolean
    var hasBaseColorTexture: Boolean
    var hasNormalTexture: Boolean
    var hasOcclusionTexture: Boolean
    var hasEmissiveTexture: Boolean
    var useSpecularGlossiness: Boolean
    var alphaMode: Int
    var enableDiagnostics: Boolean
    var hasMetallicRoughnessTexture: Boolean
    var metallicRoughnessUV: Int
    var baseColorUV: Int
    var hasClearCoatTexture: Boolean
    var clearCoatUV: Int
    var hasClearCoatRoughnessTexture: Boolean
    var clearCoatRoughnessUV: Int
    var hasClearCoatNormalTexture: Boolean
    var clearCoatNormalUV: Int
    var hasClearCoat: Boolean
    var hasTransmission: Boolean
    var hasTextureTransforms: Boolean
    var emissiveUV: Int
    var aoUV: Int
    var normalUV: Int
    var hasTransmissionTexture: Boolean
    var transmissionUV: Int
    var hasSheenColorTexture: Boolean
    var sheenColorUV: Int
    var hasSheenRoughnessTexture: Boolean
    var sheenRoughnessUV: Int
    var hasVolumeThicknessTexture: Boolean
    var volumeThicknessUV: Int
    var hasSheen: Boolean
    var hasIOR: Boolean

    fun constrainMaterial(uvmap: IntArray)
}

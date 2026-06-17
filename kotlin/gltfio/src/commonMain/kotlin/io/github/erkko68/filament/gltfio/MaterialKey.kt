package io.github.erkko68.filament.gltfio

/**
 * MaterialKey encodes glTF material properties for material selection and creation.
 *
 * MaterialKey holds a set of boolean flags and texture coordinates that describe the
 * properties of a glTF material. MaterialProvider uses this to determine which material
 * to create or select. Key properties include texture presence, shading model, alpha mode,
 * and advanced features like clearcoat and transmission.
 *
 * @see MaterialProvider
 */
expect class MaterialKey {
    /**
     * Create a new MaterialKey with default values.
     */
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

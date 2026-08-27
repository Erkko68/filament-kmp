package io.github.erkko68.filament.gltfio

/** How a glTF material's alpha channel is interpreted. */
enum class AlphaMode {
    /** Alpha is ignored; the material is fully opaque. */
    OPAQUE,
    /** Alpha is thresholded against the material's alpha cutoff. */
    MASK,
    /** Alpha blends the material with what is behind it. */
    BLEND,
}

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
data class MaterialKey(
    /** Renders both faces of each triangle (glTF `doubleSided`). */
    var doubleSided: Boolean = false,
    /** Uses the unlit shading model (`KHR_materials_unlit`). */
    var unlit: Boolean = false,
    /** The mesh provides per-vertex COLOR_0 data to be multiplied into base color. */
    var hasVertexColors: Boolean = false,
    /** A base color texture is bound. */
    var hasBaseColorTexture: Boolean = false,
    /** A tangent-space normal map is bound. */
    var hasNormalTexture: Boolean = false,
    /** An ambient-occlusion texture is bound. */
    var hasOcclusionTexture: Boolean = false,
    /** An emissive texture is bound. */
    var hasEmissiveTexture: Boolean = false,
    /** Uses the legacy specular-glossiness workflow (`KHR_materials_pbrSpecularGlossiness`). */
    var useSpecularGlossiness: Boolean = false,
    /** Alpha mode: 0 = OPAQUE, 1 = MASK (alpha cutoff), 2 = BLEND. */
    var alphaMode: AlphaMode = AlphaMode.OPAQUE,
    /** Enables shader diagnostics (visualizes the material as a debug aid). */
    var enableDiagnostics: Boolean = false,
    /** A metallic-roughness texture is bound (specular-glossiness texture when [useSpecularGlossiness]). */
    var hasMetallicRoughnessTexture: Boolean = false,
    /** glTF texcoord set index for the metallic-roughness texture. */
    var metallicRoughnessUV: Int = 0,
    /** glTF texcoord set index for the base color texture. */
    var baseColorUV: Int = 0,
    /** A clearcoat intensity texture is bound. */
    var hasClearCoatTexture: Boolean = false,
    /** glTF texcoord set index for the clearcoat texture. */
    var clearCoatUV: Int = 0,
    /** A clearcoat roughness texture is bound. */
    var hasClearCoatRoughnessTexture: Boolean = false,
    /** glTF texcoord set index for the clearcoat roughness texture. */
    var clearCoatRoughnessUV: Int = 0,
    /** A clearcoat normal map is bound. */
    var hasClearCoatNormalTexture: Boolean = false,
    /** glTF texcoord set index for the clearcoat normal map. */
    var clearCoatNormalUV: Int = 0,
    /** The clearcoat layer is enabled (`KHR_materials_clearcoat`). */
    var hasClearCoat: Boolean = false,
    /** Transmission is enabled (`KHR_materials_transmission`). */
    var hasTransmission: Boolean = false,
    /** One or more textures use `KHR_texture_transform`. */
    var hasTextureTransforms: Boolean = false,
    /** glTF texcoord set index for the emissive texture. */
    var emissiveUV: Int = 0,
    /** glTF texcoord set index for the ambient-occlusion texture. */
    var aoUV: Int = 0,
    /** glTF texcoord set index for the normal map. */
    var normalUV: Int = 0,
    /** A transmission texture is bound. */
    var hasTransmissionTexture: Boolean = false,
    /** glTF texcoord set index for the transmission texture. */
    var transmissionUV: Int = 0,
    /** A sheen color texture is bound. */
    var hasSheenColorTexture: Boolean = false,
    /** glTF texcoord set index for the sheen color texture. */
    var sheenColorUV: Int = 0,
    /** A sheen roughness texture is bound. */
    var hasSheenRoughnessTexture: Boolean = false,
    /** glTF texcoord set index for the sheen roughness texture. */
    var sheenRoughnessUV: Int = 0,
    /** A volume thickness texture is bound (`KHR_materials_volume`). */
    var hasVolumeThicknessTexture: Boolean = false,
    /** glTF texcoord set index for the volume thickness texture. */
    var volumeThicknessUV: Int = 0,
    /** The sheen layer is enabled (`KHR_materials_sheen`). */
    var hasSheen: Boolean = false,
    /** A custom index of refraction is set (`KHR_materials_ior`). */
    var hasIOR: Boolean = false,
)

/**
 * Mutates this key to trim requested features down to what the provider supports, and fills
 * [uvmap] with the resulting glTF-texcoord → Filament-UV-set mapping. Called by providers
 * before material creation.
 *
 * A free function upstream (`filament::gltfio::constrainMaterial`); an extension here so the
 * call site reads the same.
 */
expect fun MaterialKey.constrainMaterial(uvmap: IntArray)

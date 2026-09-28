package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.interop.*

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
fun MaterialKey.constrainMaterial(uvmap: IntArray) {
    val key = toInts()
    val uv = ByteArray(8) { uvmap.getOrElse(it) { 0 }.toByte() }
    key.usePinned { k -> uv.usePinned { u -> FilaMaterialKey_constrainMaterial(k, u) } }
    for (i in 0 until minOf(8, uvmap.size)) uvmap[i] = uv[i].toInt()
    setFrom(key)
}

// The C API takes a MaterialKey flattened to one Int per field, in declaration order (FILA_MATERIAL_KEY_FIELD_COUNT).
internal fun MaterialKey.toInts(): IntArray = intArrayOf(
    if (doubleSided) 1 else 0,
    if (unlit) 1 else 0,
    if (hasVertexColors) 1 else 0,
    if (hasBaseColorTexture) 1 else 0,
    if (hasNormalTexture) 1 else 0,
    if (hasOcclusionTexture) 1 else 0,
    if (hasEmissiveTexture) 1 else 0,
    if (useSpecularGlossiness) 1 else 0,
    alphaMode.ordinal,
    if (enableDiagnostics) 1 else 0,
    if (hasMetallicRoughnessTexture) 1 else 0,
    metallicRoughnessUV,
    baseColorUV,
    if (hasClearCoatTexture) 1 else 0,
    clearCoatUV,
    if (hasClearCoatRoughnessTexture) 1 else 0,
    clearCoatRoughnessUV,
    if (hasClearCoatNormalTexture) 1 else 0,
    clearCoatNormalUV,
    if (hasClearCoat) 1 else 0,
    if (hasTransmission) 1 else 0,
    if (hasTextureTransforms) 1 else 0,
    emissiveUV,
    aoUV,
    normalUV,
    if (hasTransmissionTexture) 1 else 0,
    transmissionUV,
    if (hasSheenColorTexture) 1 else 0,
    sheenColorUV,
    if (hasSheenRoughnessTexture) 1 else 0,
    sheenRoughnessUV,
    if (hasVolumeThicknessTexture) 1 else 0,
    volumeThicknessUV,
    if (hasSheen) 1 else 0,
    if (hasIOR) 1 else 0
)

private fun MaterialKey.setFrom(f: IntArray) {
    doubleSided = f[0] != 0
    unlit = f[1] != 0
    hasVertexColors = f[2] != 0
    hasBaseColorTexture = f[3] != 0
    hasNormalTexture = f[4] != 0
    hasOcclusionTexture = f[5] != 0
    hasEmissiveTexture = f[6] != 0
    useSpecularGlossiness = f[7] != 0
    alphaMode = AlphaMode.entries[f[8]]
    enableDiagnostics = f[9] != 0
    hasMetallicRoughnessTexture = f[10] != 0
    metallicRoughnessUV = f[11]
    baseColorUV = f[12]
    hasClearCoatTexture = f[13] != 0
    clearCoatUV = f[14]
    hasClearCoatRoughnessTexture = f[15] != 0
    clearCoatRoughnessUV = f[16]
    hasClearCoatNormalTexture = f[17] != 0
    clearCoatNormalUV = f[18]
    hasClearCoat = f[19] != 0
    hasTransmission = f[20] != 0
    hasTextureTransforms = f[21] != 0
    emissiveUV = f[22]
    aoUV = f[23]
    normalUV = f[24]
    hasTransmissionTexture = f[25] != 0
    transmissionUV = f[26]
    hasSheenColorTexture = f[27] != 0
    sheenColorUV = f[28]
    hasSheenRoughnessTexture = f[29] != 0
    sheenRoughnessUV = f[30]
    hasVolumeThicknessTexture = f[31] != 0
    volumeThicknessUV = f[32]
    hasSheen = f[33] != 0
    hasIOR = f[34] != 0
}

@ExternalSymbolName("FilaMaterialKey_constrainMaterial")
private external fun FilaMaterialKey_constrainMaterial(key: NativePointer, uvmap: NativePointer)

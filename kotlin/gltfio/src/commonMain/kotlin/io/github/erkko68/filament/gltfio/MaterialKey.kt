package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.capi.*
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
    /** The volume extension is enabled (`KHR_materials_volume`). */
    var hasVolume: Boolean = false,
    /** Dispersion is enabled (`KHR_materials_dispersion`). */
    var hasDispersion: Boolean = false,
    /** The specular extension is enabled (`KHR_materials_specular`). */
    var hasSpecular: Boolean = false,
    /** A specular strength texture is bound. */
    var hasSpecularTexture: Boolean = false,
    /** A specular color texture is bound. */
    var hasSpecularColorTexture: Boolean = false,
    /** glTF texcoord set index for the specular strength texture. */
    var specularTextureUV: Int = 0,
    /** glTF texcoord set index for the specular color texture. */
    var specularColorTextureUV: Int = 0,
) {
    /** [hasMetallicRoughnessTexture] under its specular-glossiness name: the same bit in C++'s union. */
    var hasSpecularGlossinessTexture: Boolean
        get() = hasMetallicRoughnessTexture
        set(value) { hasMetallicRoughnessTexture = value }

    /** [metallicRoughnessUV] under its specular-glossiness name: the same bits in C++'s union. */
    var specularGlossinessUV: Int
        get() = metallicRoughnessUV
        set(value) { metallicRoughnessUV = value }
}

/** One of Filament's UV sets a glTF texcoord set maps to. */
enum class UvSet { UNUSED, UV0, UV1 }

/** Entries in a [UvMap]. */
const val UV_MAP_SIZE: Int = 8

/** Maps each glTF texcoord set (the index) to a Filament [UvSet]; [UV_MAP_SIZE] entries. */
typealias UvMap = Array<UvSet>

/** The number of Filament UV sets [uvmap] uses. */
fun getNumUvSets(uvmap: UvMap): Int = uvmap.useNative { p, n -> FilaGltfio_getNumUvSets(p, n) }

/**
 * Trims [key]'s requested features down to what the providers support, and fills [uvmap] with the
 * resulting glTF-texcoord → Filament-UV-set mapping. Called by providers before material creation.
 */
fun constrainMaterial(key: MaterialKey, uvmap: UvMap) {
    key.useNative { k -> uvmap.useNative { u, n -> FilaGltfio_constrainMaterial(k, u, n) } }
}

/** A native copy of this key for [block]; what C changed in it is copied back. */
internal inline fun <R> MaterialKey.useNative(block: (NativePointer) -> R): R {
    val k = FilaGltfioMaterialKey_create()
    try {
        writeTo(k)
        return block(k).also { readFrom(k) }
    } finally {
        FilaGltfioMaterialKey_destroy(k)
    }
}

/** A native copy of this map (C enums, one int each) for [block]; what C changed in it is copied back. */
internal inline fun <R> UvMap.useNative(block: (NativePointer, Int) -> R): R {
    val sets = IntArray(size) { this[it].ordinal }
    return sets.usePinned { block(it, size) }.also { sets.forEachIndexed { i, v -> this[i] = UvSet.entries[v] } }
}

@PublishedApi
internal fun MaterialKey.writeTo(k: NativePointer) {
    FilaGltfioMaterialKey_setDoubleSided(k, doubleSided)
    FilaGltfioMaterialKey_setUnlit(k, unlit)
    FilaGltfioMaterialKey_setHasVertexColors(k, hasVertexColors)
    FilaGltfioMaterialKey_setHasBaseColorTexture(k, hasBaseColorTexture)
    FilaGltfioMaterialKey_setHasNormalTexture(k, hasNormalTexture)
    FilaGltfioMaterialKey_setHasOcclusionTexture(k, hasOcclusionTexture)
    FilaGltfioMaterialKey_setHasEmissiveTexture(k, hasEmissiveTexture)
    FilaGltfioMaterialKey_setUseSpecularGlossiness(k, useSpecularGlossiness)
    FilaGltfioMaterialKey_setAlphaMode(k, alphaMode.ordinal)
    FilaGltfioMaterialKey_setEnableDiagnostics(k, enableDiagnostics)
    FilaGltfioMaterialKey_setHasMetallicRoughnessTexture(k, hasMetallicRoughnessTexture)
    FilaGltfioMaterialKey_setMetallicRoughnessUV(k, metallicRoughnessUV)
    FilaGltfioMaterialKey_setBaseColorUV(k, baseColorUV)
    FilaGltfioMaterialKey_setHasClearCoatTexture(k, hasClearCoatTexture)
    FilaGltfioMaterialKey_setClearCoatUV(k, clearCoatUV)
    FilaGltfioMaterialKey_setHasClearCoatRoughnessTexture(k, hasClearCoatRoughnessTexture)
    FilaGltfioMaterialKey_setClearCoatRoughnessUV(k, clearCoatRoughnessUV)
    FilaGltfioMaterialKey_setHasClearCoatNormalTexture(k, hasClearCoatNormalTexture)
    FilaGltfioMaterialKey_setClearCoatNormalUV(k, clearCoatNormalUV)
    FilaGltfioMaterialKey_setHasClearCoat(k, hasClearCoat)
    FilaGltfioMaterialKey_setHasTransmission(k, hasTransmission)
    FilaGltfioMaterialKey_setHasTextureTransforms(k, hasTextureTransforms)
    FilaGltfioMaterialKey_setEmissiveUV(k, emissiveUV)
    FilaGltfioMaterialKey_setAoUV(k, aoUV)
    FilaGltfioMaterialKey_setNormalUV(k, normalUV)
    FilaGltfioMaterialKey_setHasTransmissionTexture(k, hasTransmissionTexture)
    FilaGltfioMaterialKey_setTransmissionUV(k, transmissionUV)
    FilaGltfioMaterialKey_setHasSheenColorTexture(k, hasSheenColorTexture)
    FilaGltfioMaterialKey_setSheenColorUV(k, sheenColorUV)
    FilaGltfioMaterialKey_setHasSheenRoughnessTexture(k, hasSheenRoughnessTexture)
    FilaGltfioMaterialKey_setSheenRoughnessUV(k, sheenRoughnessUV)
    FilaGltfioMaterialKey_setHasVolumeThicknessTexture(k, hasVolumeThicknessTexture)
    FilaGltfioMaterialKey_setVolumeThicknessUV(k, volumeThicknessUV)
    FilaGltfioMaterialKey_setHasSheen(k, hasSheen)
    FilaGltfioMaterialKey_setHasIOR(k, hasIOR)
    FilaGltfioMaterialKey_setHasVolume(k, hasVolume)
    FilaGltfioMaterialKey_setHasDispersion(k, hasDispersion)
    FilaGltfioMaterialKey_setHasSpecular(k, hasSpecular)
    FilaGltfioMaterialKey_setHasSpecularTexture(k, hasSpecularTexture)
    FilaGltfioMaterialKey_setHasSpecularColorTexture(k, hasSpecularColorTexture)
    FilaGltfioMaterialKey_setSpecularTextureUV(k, specularTextureUV)
    FilaGltfioMaterialKey_setSpecularColorTextureUV(k, specularColorTextureUV)
}

@PublishedApi
internal fun MaterialKey.readFrom(k: NativePointer) {
    doubleSided = FilaGltfioMaterialKey_getDoubleSided(k)
    unlit = FilaGltfioMaterialKey_getUnlit(k)
    hasVertexColors = FilaGltfioMaterialKey_getHasVertexColors(k)
    hasBaseColorTexture = FilaGltfioMaterialKey_getHasBaseColorTexture(k)
    hasNormalTexture = FilaGltfioMaterialKey_getHasNormalTexture(k)
    hasOcclusionTexture = FilaGltfioMaterialKey_getHasOcclusionTexture(k)
    hasEmissiveTexture = FilaGltfioMaterialKey_getHasEmissiveTexture(k)
    useSpecularGlossiness = FilaGltfioMaterialKey_getUseSpecularGlossiness(k)
    alphaMode = AlphaMode.entries[FilaGltfioMaterialKey_getAlphaMode(k)]
    enableDiagnostics = FilaGltfioMaterialKey_getEnableDiagnostics(k)
    hasMetallicRoughnessTexture = FilaGltfioMaterialKey_getHasMetallicRoughnessTexture(k)
    metallicRoughnessUV = FilaGltfioMaterialKey_getMetallicRoughnessUV(k)
    baseColorUV = FilaGltfioMaterialKey_getBaseColorUV(k)
    hasClearCoatTexture = FilaGltfioMaterialKey_getHasClearCoatTexture(k)
    clearCoatUV = FilaGltfioMaterialKey_getClearCoatUV(k)
    hasClearCoatRoughnessTexture = FilaGltfioMaterialKey_getHasClearCoatRoughnessTexture(k)
    clearCoatRoughnessUV = FilaGltfioMaterialKey_getClearCoatRoughnessUV(k)
    hasClearCoatNormalTexture = FilaGltfioMaterialKey_getHasClearCoatNormalTexture(k)
    clearCoatNormalUV = FilaGltfioMaterialKey_getClearCoatNormalUV(k)
    hasClearCoat = FilaGltfioMaterialKey_getHasClearCoat(k)
    hasTransmission = FilaGltfioMaterialKey_getHasTransmission(k)
    hasTextureTransforms = FilaGltfioMaterialKey_getHasTextureTransforms(k)
    emissiveUV = FilaGltfioMaterialKey_getEmissiveUV(k)
    aoUV = FilaGltfioMaterialKey_getAoUV(k)
    normalUV = FilaGltfioMaterialKey_getNormalUV(k)
    hasTransmissionTexture = FilaGltfioMaterialKey_getHasTransmissionTexture(k)
    transmissionUV = FilaGltfioMaterialKey_getTransmissionUV(k)
    hasSheenColorTexture = FilaGltfioMaterialKey_getHasSheenColorTexture(k)
    sheenColorUV = FilaGltfioMaterialKey_getSheenColorUV(k)
    hasSheenRoughnessTexture = FilaGltfioMaterialKey_getHasSheenRoughnessTexture(k)
    sheenRoughnessUV = FilaGltfioMaterialKey_getSheenRoughnessUV(k)
    hasVolumeThicknessTexture = FilaGltfioMaterialKey_getHasVolumeThicknessTexture(k)
    volumeThicknessUV = FilaGltfioMaterialKey_getVolumeThicknessUV(k)
    hasSheen = FilaGltfioMaterialKey_getHasSheen(k)
    hasIOR = FilaGltfioMaterialKey_getHasIOR(k)
    hasVolume = FilaGltfioMaterialKey_getHasVolume(k)
    hasDispersion = FilaGltfioMaterialKey_getHasDispersion(k)
    hasSpecular = FilaGltfioMaterialKey_getHasSpecular(k)
    hasSpecularTexture = FilaGltfioMaterialKey_getHasSpecularTexture(k)
    hasSpecularColorTexture = FilaGltfioMaterialKey_getHasSpecularColorTexture(k)
    specularTextureUV = FilaGltfioMaterialKey_getSpecularTextureUV(k)
    specularColorTextureUV = FilaGltfioMaterialKey_getSpecularColorTextureUV(k)
}

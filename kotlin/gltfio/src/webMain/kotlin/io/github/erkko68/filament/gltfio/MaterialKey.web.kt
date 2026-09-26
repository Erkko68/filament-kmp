package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.wasm.*

internal fun MaterialKey.toNative(native: FilaMaterialKey, fields: FilaMaterialKeyFields) {
    fields.doubleSided = doubleSided
    fields.unlit = unlit
    fields.hasVertexColors = hasVertexColors
    fields.hasBaseColorTexture = hasBaseColorTexture
    fields.hasNormalTexture = hasNormalTexture
    fields.hasOcclusionTexture = hasOcclusionTexture
    fields.hasEmissiveTexture = hasEmissiveTexture
    fields.useSpecularGlossiness = useSpecularGlossiness
    fields.alphaMode = alphaMode.ordinal
    fields.enableDiagnostics = (if (enableDiagnostics) 1 else 0)
    fields.hasMetallicRoughnessTexture = hasMetallicRoughnessTexture
    fields.metallicRoughnessUV = metallicRoughnessUV
    fields.baseColorUV = baseColorUV
    fields.hasClearCoatTexture = hasClearCoatTexture
    fields.clearCoatUV = clearCoatUV
    fields.hasClearCoatRoughnessTexture = hasClearCoatRoughnessTexture
    fields.clearCoatRoughnessUV = clearCoatRoughnessUV
    fields.hasClearCoatNormalTexture = hasClearCoatNormalTexture
    fields.clearCoatNormalUV = clearCoatNormalUV
    fields.hasClearCoat = hasClearCoat
    fields.hasTransmission = hasTransmission
    fields.hasTextureTransforms = (if (hasTextureTransforms) 1 else 0)
    fields.emissiveUV = emissiveUV
    fields.aoUV = aoUV
    fields.normalUV = normalUV
    fields.hasTransmissionTexture = hasTransmissionTexture
    fields.transmissionUV = transmissionUV
    fields.hasSheenColorTexture = hasSheenColorTexture
    fields.sheenColorUV = sheenColorUV
    fields.hasSheenRoughnessTexture = hasSheenRoughnessTexture
    fields.sheenRoughnessUV = sheenRoughnessUV
    fields.hasVolumeThicknessTexture = hasVolumeThicknessTexture
    fields.volumeThicknessUV = volumeThicknessUV
    fields.hasSheen = hasSheen
    fields.hasIOR = hasIOR
    FilaMaterialKey_pack(fields.ptr, native.ptr)
}

actual fun MaterialKey.constrainMaterial(uvmap: IntArray) {
    fila.heapScoped {
        val nativeKey = FilaMaterialKey(alloc(FilaMaterialKey.SIZE))
        val fields = FilaMaterialKeyFields(alloc(FilaMaterialKeyFields.SIZE))
        toNative(nativeKey, fields)
        val byteUvMap = ByteArray(8) { uvmap.getOrElse(it) { 0 }.toByte() }
        byteUvMap.usePinned { pinned ->
            FilaMaterialKey_constrainMaterial(nativeKey.ptr, pinned)
        }
        // Sync back the modified uvmap if necessary? 
        // In Android, constrainMaterial(int[] uvmap) modifies the array.
        for (i in 0 until 8) {
            if (i < uvmap.size) {
                uvmap[i] = byteUvMap[i].toInt()
            }
        }
        // Sync back just in case constrainMaterial modifies the key
        FilaMaterialKey_unpack(nativeKey.ptr, fields.ptr)
        fromNative(fields)
    }
}

internal fun MaterialKey.fromNative(fields: FilaMaterialKeyFields) {
    doubleSided = fields.doubleSided
    unlit = fields.unlit
    hasVertexColors = fields.hasVertexColors
    hasBaseColorTexture = fields.hasBaseColorTexture
    hasNormalTexture = fields.hasNormalTexture
    hasOcclusionTexture = fields.hasOcclusionTexture
    hasEmissiveTexture = fields.hasEmissiveTexture
    useSpecularGlossiness = fields.useSpecularGlossiness
    alphaMode = AlphaMode.entries[fields.alphaMode.toInt()]
    enableDiagnostics = fields.enableDiagnostics.toInt() != 0
    hasMetallicRoughnessTexture = fields.hasMetallicRoughnessTexture
    metallicRoughnessUV = fields.metallicRoughnessUV.toInt()
    baseColorUV = fields.baseColorUV.toInt()
    hasClearCoatTexture = fields.hasClearCoatTexture
    clearCoatUV = fields.clearCoatUV.toInt()
    hasClearCoatRoughnessTexture = fields.hasClearCoatRoughnessTexture
    clearCoatRoughnessUV = fields.clearCoatRoughnessUV.toInt()
    hasClearCoatNormalTexture = fields.hasClearCoatNormalTexture
    clearCoatNormalUV = fields.clearCoatNormalUV.toInt()
    hasClearCoat = fields.hasClearCoat
    hasTransmission = fields.hasTransmission
    hasTextureTransforms = fields.hasTextureTransforms.toInt() != 0
    emissiveUV = fields.emissiveUV.toInt()
    aoUV = fields.aoUV.toInt()
    normalUV = fields.normalUV.toInt()
    hasTransmissionTexture = fields.hasTransmissionTexture
    transmissionUV = fields.transmissionUV.toInt()
    hasSheenColorTexture = fields.hasSheenColorTexture
    sheenColorUV = fields.sheenColorUV.toInt()
    hasSheenRoughnessTexture = fields.hasSheenRoughnessTexture
    sheenRoughnessUV = fields.sheenRoughnessUV.toInt()
    hasVolumeThicknessTexture = fields.hasVolumeThicknessTexture
    volumeThicknessUV = fields.volumeThicknessUV.toInt()
    hasSheen = fields.hasSheen
    hasIOR = fields.hasIOR
}

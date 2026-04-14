@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import io.github.erkko68.filament.gltfio.cinterop.*

actual class MaterialKey actual constructor() {
    actual var doubleSided: Boolean = false
    actual var unlit: Boolean = false
    actual var hasVertexColors: Boolean = false
    actual var hasBaseColorTexture: Boolean = false
    actual var hasNormalTexture: Boolean = false
    actual var hasOcclusionTexture: Boolean = false
    actual var hasEmissiveTexture: Boolean = false
    actual var useSpecularGlossiness: Boolean = false
    actual var alphaMode: Int = 0
    actual var enableDiagnostics: Boolean = false
    actual var hasMetallicRoughnessTexture: Boolean = false
    actual var metallicRoughnessUV: Int = 0
    actual var baseColorUV: Int = 0
    actual var hasClearCoatTexture: Boolean = false
    actual var clearCoatUV: Int = 0
    actual var hasClearCoatRoughnessTexture: Boolean = false
    actual var clearCoatRoughnessUV: Int = 0
    actual var hasClearCoatNormalTexture: Boolean = false
    actual var clearCoatNormalUV: Int = 0
    actual var hasClearCoat: Boolean = false
    actual var hasTransmission: Boolean = false
    actual var hasTextureTransforms: Boolean = false
    actual var emissiveUV: Int = 0
    actual var aoUV: Int = 0
    actual var normalUV: Int = 0
    actual var hasTransmissionTexture: Boolean = false
    actual var transmissionUV: Int = 0
    actual var hasSheenColorTexture: Boolean = false
    actual var sheenColorUV: Int = 0
    actual var hasSheenRoughnessTexture: Boolean = false
    actual var sheenRoughnessUV: Int = 0
    actual var hasVolumeThicknessTexture: Boolean = false
    actual var volumeThicknessUV: Int = 0
    actual var hasSheen: Boolean = false
    actual var hasIOR: Boolean = false

    internal fun toNative(native: FilaMaterialKey, fields: FilaMaterialKeyFields) {
        fields.doubleSided = doubleSided
        fields.unlit = unlit
        fields.hasVertexColors = hasVertexColors
        fields.hasBaseColorTexture = hasBaseColorTexture
        fields.hasNormalTexture = hasNormalTexture
        fields.hasOcclusionTexture = hasOcclusionTexture
        fields.hasEmissiveTexture = hasEmissiveTexture
        fields.useSpecularGlossiness = useSpecularGlossiness
        fields.alphaMode = alphaMode.toUByte()
        fields.enableDiagnostics = (if (enableDiagnostics) 1 else 0).toUByte()
        fields.hasMetallicRoughnessTexture = hasMetallicRoughnessTexture
        fields.metallicRoughnessUV = metallicRoughnessUV.toUByte()
        fields.baseColorUV = baseColorUV.toUByte()
        fields.hasClearCoatTexture = hasClearCoatTexture
        fields.clearCoatUV = clearCoatUV.toUByte()
        fields.hasClearCoatRoughnessTexture = hasClearCoatRoughnessTexture
        fields.clearCoatRoughnessUV = clearCoatRoughnessUV.toUByte()
        fields.hasClearCoatNormalTexture = hasClearCoatNormalTexture
        fields.clearCoatNormalUV = clearCoatNormalUV.toUByte()
        fields.hasClearCoat = hasClearCoat
        fields.hasTransmission = hasTransmission
        fields.hasTextureTransforms = (if (hasTextureTransforms) 1 else 0).toUByte()
        fields.emissiveUV = emissiveUV.toUByte()
        fields.aoUV = aoUV.toUByte()
        fields.normalUV = normalUV.toUByte()
        fields.hasTransmissionTexture = hasTransmissionTexture
        fields.transmissionUV = transmissionUV.toUByte()
        fields.hasSheenColorTexture = hasSheenColorTexture
        fields.sheenColorUV = sheenColorUV.toUByte()
        fields.hasSheenRoughnessTexture = hasSheenRoughnessTexture
        fields.sheenRoughnessUV = sheenRoughnessUV.toUByte()
        fields.hasVolumeThicknessTexture = hasVolumeThicknessTexture
        fields.volumeThicknessUV = volumeThicknessUV.toUByte()
        fields.hasSheen = hasSheen
        fields.hasIOR = hasIOR
        FilaMaterialKey_pack(fields.ptr, native.ptr)
    }

    actual fun constrainMaterial(uvmap: IntArray) {
        memScoped {
            val nativeKey = alloc<FilaMaterialKey>()
            val fields = alloc<FilaMaterialKeyFields>()
            toNative(nativeKey, fields)
            val byteUvMap = ByteArray(8) { uvmap.getOrElse(it) { 0 }.toByte() }
            byteUvMap.usePinned { pinned ->
                FilaMaterialKey_constrainMaterial(nativeKey.ptr, pinned.addressOf(0).reinterpret<UByteVar>())
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

    internal fun fromNative(fields: FilaMaterialKeyFields) {
        doubleSided = fields.doubleSided
        unlit = fields.unlit
        hasVertexColors = fields.hasVertexColors
        hasBaseColorTexture = fields.hasBaseColorTexture
        hasNormalTexture = fields.hasNormalTexture
        hasOcclusionTexture = fields.hasOcclusionTexture
        hasEmissiveTexture = fields.hasEmissiveTexture
        useSpecularGlossiness = fields.useSpecularGlossiness
        alphaMode = fields.alphaMode.toInt()
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
}

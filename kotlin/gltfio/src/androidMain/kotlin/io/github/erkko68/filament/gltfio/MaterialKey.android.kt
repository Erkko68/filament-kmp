package io.github.erkko68.filament.gltfio

// The Android bindings model MaterialKey as a mutable upstream object, so the common data
// class is marshalled across field by field.

internal fun MaterialKey.toAndroid(): com.google.android.filament.gltfio.MaterialProvider.MaterialKey {
    val n = com.google.android.filament.gltfio.MaterialProvider.MaterialKey()
    n.doubleSided = doubleSided
    n.unlit = unlit
    n.hasVertexColors = hasVertexColors
    n.hasBaseColorTexture = hasBaseColorTexture
    n.hasNormalTexture = hasNormalTexture
    n.hasOcclusionTexture = hasOcclusionTexture
    n.hasEmissiveTexture = hasEmissiveTexture
    n.useSpecularGlossiness = useSpecularGlossiness
    n.alphaMode = alphaMode.ordinal
    n.enableDiagnostics = enableDiagnostics
    n.hasMetallicRoughnessTexture = hasMetallicRoughnessTexture
    n.metallicRoughnessUV = metallicRoughnessUV
    n.baseColorUV = baseColorUV
    n.hasClearCoatTexture = hasClearCoatTexture
    n.clearCoatUV = clearCoatUV
    n.hasClearCoatRoughnessTexture = hasClearCoatRoughnessTexture
    n.clearCoatRoughnessUV = clearCoatRoughnessUV
    n.hasClearCoatNormalTexture = hasClearCoatNormalTexture
    n.clearCoatNormalUV = clearCoatNormalUV
    n.hasClearCoat = hasClearCoat
    n.hasTransmission = hasTransmission
    n.hasTextureTransforms = hasTextureTransforms
    n.emissiveUV = emissiveUV
    n.aoUV = aoUV
    n.normalUV = normalUV
    n.hasTransmissionTexture = hasTransmissionTexture
    n.transmissionUV = transmissionUV
    n.hasSheenColorTexture = hasSheenColorTexture
    n.sheenColorUV = sheenColorUV
    n.hasSheenRoughnessTexture = hasSheenRoughnessTexture
    n.sheenRoughnessUV = sheenRoughnessUV
    n.hasVolumeThicknessTexture = hasVolumeThicknessTexture
    n.volumeThicknessUV = volumeThicknessUV
    n.hasSheen = hasSheen
    n.hasIOR = hasIOR
    return n
}

internal fun MaterialKey.copyFrom(n: com.google.android.filament.gltfio.MaterialProvider.MaterialKey) {
    doubleSided = n.doubleSided
    unlit = n.unlit
    hasVertexColors = n.hasVertexColors
    hasBaseColorTexture = n.hasBaseColorTexture
    hasNormalTexture = n.hasNormalTexture
    hasOcclusionTexture = n.hasOcclusionTexture
    hasEmissiveTexture = n.hasEmissiveTexture
    useSpecularGlossiness = n.useSpecularGlossiness
    alphaMode = AlphaMode.entries[n.alphaMode]
    enableDiagnostics = n.enableDiagnostics
    hasMetallicRoughnessTexture = n.hasMetallicRoughnessTexture
    metallicRoughnessUV = n.metallicRoughnessUV
    baseColorUV = n.baseColorUV
    hasClearCoatTexture = n.hasClearCoatTexture
    clearCoatUV = n.clearCoatUV
    hasClearCoatRoughnessTexture = n.hasClearCoatRoughnessTexture
    clearCoatRoughnessUV = n.clearCoatRoughnessUV
    hasClearCoatNormalTexture = n.hasClearCoatNormalTexture
    clearCoatNormalUV = n.clearCoatNormalUV
    hasClearCoat = n.hasClearCoat
    hasTransmission = n.hasTransmission
    hasTextureTransforms = n.hasTextureTransforms
    emissiveUV = n.emissiveUV
    aoUV = n.aoUV
    normalUV = n.normalUV
    hasTransmissionTexture = n.hasTransmissionTexture
    transmissionUV = n.transmissionUV
    hasSheenColorTexture = n.hasSheenColorTexture
    sheenColorUV = n.sheenColorUV
    hasSheenRoughnessTexture = n.hasSheenRoughnessTexture
    sheenRoughnessUV = n.sheenRoughnessUV
    hasVolumeThicknessTexture = n.hasVolumeThicknessTexture
    volumeThicknessUV = n.volumeThicknessUV
    hasSheen = n.hasSheen
    hasIOR = n.hasIOR
}

actual fun MaterialKey.constrainMaterial(uvmap: IntArray) {
    val n = toAndroid()
    n.constrainMaterial(uvmap)
    copyFrom(n)
}

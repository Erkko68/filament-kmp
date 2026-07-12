package io.github.erkko68.filament.gltfio

actual class MaterialKey {
    private var _doubleSided = false
    private var _unlit = false
    private var _hasVertexColors = false
    private var _hasBaseColorTexture = false
    private var _hasNormalTexture = false
    private var _hasOcclusionTexture = false
    private var _hasEmissiveTexture = false
    private var _useSpecularGlossiness = false
    private var _alphaMode = 0
    private var _enableDiagnostics = false
    private var _hasMetallicRoughnessTexture = false
    private var _metallicRoughnessUV = 0
    private var _baseColorUV = 0
    private var _hasClearCoatTexture = false
    private var _clearCoatUV = 0
    private var _hasClearCoatRoughnessTexture = false
    private var _clearCoatRoughnessUV = 0
    private var _hasClearCoatNormalTexture = false
    private var _clearCoatNormalUV = 0
    private var _hasClearCoat = false
    private var _hasTransmission = false
    private var _hasTextureTransforms = false
    private var _emissiveUV = 0
    private var _aoUV = 0
    private var _normalUV = 0
    private var _hasTransmissionTexture = false
    private var _transmissionUV = 0
    private var _hasSheenColorTexture = false
    private var _sheenColorUV = 0
    private var _hasSheenRoughnessTexture = false
    private var _sheenRoughnessUV = 0
    private var _hasVolumeThicknessTexture = false
    private var _volumeThicknessUV = 0
    private var _hasSheen = false
    private var _hasIOR = false

    actual var doubleSided: Boolean
        get() = _doubleSided
        set(value) { _doubleSided = value }

    actual var unlit: Boolean
        get() = _unlit
        set(value) { _unlit = value }

    actual var hasVertexColors: Boolean
        get() = _hasVertexColors
        set(value) { _hasVertexColors = value }

    actual var hasBaseColorTexture: Boolean
        get() = _hasBaseColorTexture
        set(value) { _hasBaseColorTexture = value }

    actual var hasNormalTexture: Boolean
        get() = _hasNormalTexture
        set(value) { _hasNormalTexture = value }

    actual var hasOcclusionTexture: Boolean
        get() = _hasOcclusionTexture
        set(value) { _hasOcclusionTexture = value }

    actual var hasEmissiveTexture: Boolean
        get() = _hasEmissiveTexture
        set(value) { _hasEmissiveTexture = value }

    actual var useSpecularGlossiness: Boolean
        get() = _useSpecularGlossiness
        set(value) { _useSpecularGlossiness = value }

    actual var alphaMode: Int
        get() = _alphaMode
        set(value) { _alphaMode = value }

    actual var enableDiagnostics: Boolean
        get() = _enableDiagnostics
        set(value) { _enableDiagnostics = value }

    actual var hasMetallicRoughnessTexture: Boolean
        get() = _hasMetallicRoughnessTexture
        set(value) { _hasMetallicRoughnessTexture = value }

    actual var metallicRoughnessUV: Int
        get() = _metallicRoughnessUV
        set(value) { _metallicRoughnessUV = value }

    actual var baseColorUV: Int
        get() = _baseColorUV
        set(value) { _baseColorUV = value }

    actual var hasClearCoatTexture: Boolean
        get() = _hasClearCoatTexture
        set(value) { _hasClearCoatTexture = value }

    actual var clearCoatUV: Int
        get() = _clearCoatUV
        set(value) { _clearCoatUV = value }

    actual var hasClearCoatRoughnessTexture: Boolean
        get() = _hasClearCoatRoughnessTexture
        set(value) { _hasClearCoatRoughnessTexture = value }

    actual var clearCoatRoughnessUV: Int
        get() = _clearCoatRoughnessUV
        set(value) { _clearCoatRoughnessUV = value }

    actual var hasClearCoatNormalTexture: Boolean
        get() = _hasClearCoatNormalTexture
        set(value) { _hasClearCoatNormalTexture = value }

    actual var clearCoatNormalUV: Int
        get() = _clearCoatNormalUV
        set(value) { _clearCoatNormalUV = value }

    actual var hasClearCoat: Boolean
        get() = _hasClearCoat
        set(value) { _hasClearCoat = value }

    actual var hasTransmission: Boolean
        get() = _hasTransmission
        set(value) { _hasTransmission = value }

    actual var hasTextureTransforms: Boolean
        get() = _hasTextureTransforms
        set(value) { _hasTextureTransforms = value }

    actual var emissiveUV: Int
        get() = _emissiveUV
        set(value) { _emissiveUV = value }

    actual var aoUV: Int
        get() = _aoUV
        set(value) { _aoUV = value }

    actual var normalUV: Int
        get() = _normalUV
        set(value) { _normalUV = value }

    actual var hasTransmissionTexture: Boolean
        get() = _hasTransmissionTexture
        set(value) { _hasTransmissionTexture = value }

    actual var transmissionUV: Int
        get() = _transmissionUV
        set(value) { _transmissionUV = value }

    actual var hasSheenColorTexture: Boolean
        get() = _hasSheenColorTexture
        set(value) { _hasSheenColorTexture = value }

    actual var sheenColorUV: Int
        get() = _sheenColorUV
        set(value) { _sheenColorUV = value }

    actual var hasSheenRoughnessTexture: Boolean
        get() = _hasSheenRoughnessTexture
        set(value) { _hasSheenRoughnessTexture = value }

    actual var sheenRoughnessUV: Int
        get() = _sheenRoughnessUV
        set(value) { _sheenRoughnessUV = value }

    actual var hasVolumeThicknessTexture: Boolean
        get() = _hasVolumeThicknessTexture
        set(value) { _hasVolumeThicknessTexture = value }

    actual var volumeThicknessUV: Int
        get() = _volumeThicknessUV
        set(value) { _volumeThicknessUV = value }

    actual var hasSheen: Boolean
        get() = _hasSheen
        set(value) { _hasSheen = value }

    actual var hasIOR: Boolean
        get() = _hasIOR
        set(value) { _hasIOR = value }

    actual fun constrainMaterial(uvmap: IntArray) {
    }

    actual constructor()
}

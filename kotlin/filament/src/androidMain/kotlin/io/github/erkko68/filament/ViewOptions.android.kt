package io.github.erkko68.filament

import com.google.android.filament.View as FilamentView

// The Android bindings model each option struct as a mutable upstream object, so the
// common data classes are marshalled across field by field.

internal fun DynamicResolutionOptions.toAndroid(): FilamentView.DynamicResolutionOptions {
    val n = FilamentView.DynamicResolutionOptions()
    n.minScale = minScale
    n.maxScale = maxScale
    n.sharpness = sharpness
    n.enabled = enabled
    n.homogeneousScaling = homogeneousScaling
    n.quality = FilamentView.QualityLevel.values()[quality.ordinal]
    return n
}

internal fun RenderQuality.toAndroid(): FilamentView.RenderQuality {
    val n = FilamentView.RenderQuality()
    n.hdrColorBuffer = FilamentView.QualityLevel.values()[hdrColorBuffer.ordinal]
    return n
}

internal fun BloomOptions.toAndroid(): FilamentView.BloomOptions {
    val n = FilamentView.BloomOptions()
    n.dirt = dirt?.nativeObject
    n.dirtStrength = dirtStrength
    n.strength = strength
    n.resolution = resolution
    n.levels = levels
    n.blendMode = FilamentView.BloomOptions.BlendMode.values()[blendMode.ordinal]
    n.threshold = threshold
    n.enabled = enabled
    n.highlight = highlight
    n.quality = FilamentView.QualityLevel.values()[quality.ordinal]
    n.lensFlare = lensFlare
    n.starburst = starburst
    n.chromaticAberration = chromaticAberration
    n.ghostCount = ghostCount
    n.ghostSpacing = ghostSpacing
    n.ghostThreshold = ghostThreshold
    n.haloThickness = haloThickness
    n.haloRadius = haloRadius
    n.haloThreshold = haloThreshold
    return n
}

internal fun FogOptions.toAndroid(): FilamentView.FogOptions {
    val n = FilamentView.FogOptions()
    n.distance = distance
    n.cutOffDistance = cutOffDistance
    n.maximumOpacity = maximumOpacity
    n.height = height
    n.heightFalloff = heightFalloff
    n.color = color
    n.density = density
    n.inScatteringStart = inScatteringStart
    n.inScatteringSize = inScatteringSize
    n.fogColorFromIbl = fogColorFromIbl
    n.skyColor = skyColor?.nativeObject
    n.enabled = enabled
    return n
}

internal fun DepthOfFieldOptions.toAndroid(): FilamentView.DepthOfFieldOptions {
    val n = FilamentView.DepthOfFieldOptions()
    n.cocScale = cocScale
    n.cocAspectRatio = cocAspectRatio
    n.maxApertureDiameter = maxApertureDiameter
    n.enabled = enabled
    n.filter = FilamentView.DepthOfFieldOptions.Filter.values()[filter.ordinal]
    n.nativeResolution = nativeResolution
    n.foregroundRingCount = foregroundRingCount
    n.backgroundRingCount = backgroundRingCount
    n.fastGatherRingCount = fastGatherRingCount
    n.maxForegroundCOC = maxForegroundCOC
    n.maxBackgroundCOC = maxBackgroundCOC
    return n
}

internal fun VignetteOptions.toAndroid(): FilamentView.VignetteOptions {
    val n = FilamentView.VignetteOptions()
    n.midPoint = midPoint
    n.roundness = roundness
    n.feather = feather
    n.color = color
    n.enabled = enabled
    return n
}

internal fun AmbientOcclusionOptions.toAndroid(): FilamentView.AmbientOcclusionOptions {
    val n = FilamentView.AmbientOcclusionOptions()
    n.aoType = FilamentView.AmbientOcclusionOptions.AmbientOcclusionType.values()[aoType.ordinal]
    n.radius = radius
    n.power = power
    n.bias = bias
    n.resolution = resolution
    n.intensity = intensity
    n.bilateralThreshold = bilateralThreshold
    n.quality = FilamentView.QualityLevel.values()[quality.ordinal]
    n.lowPassFilter = FilamentView.QualityLevel.values()[lowPassFilter.ordinal]
    n.upsampling = FilamentView.QualityLevel.values()[upsampling.ordinal]
    n.enabled = enabled
    n.bentNormals = bentNormals
    n.minHorizonAngleRad = minHorizonAngleRad
    n.ssctLightConeRad = ssct.lightConeRad
    n.ssctShadowDistance = ssct.shadowDistance
    n.ssctContactDistanceMax = ssct.contactDistanceMax
    n.ssctIntensity = ssct.intensity
    n.ssctLightDirection = ssct.lightDirection
    n.ssctDepthBias = ssct.depthBias
    n.ssctDepthSlopeBias = ssct.depthSlopeBias
    n.ssctSampleCount = ssct.sampleCount
    n.ssctRayCount = ssct.rayCount
    n.ssctEnabled = ssct.enabled
    n.gtaoSampleSliceCount = gtao.sampleSliceCount
    n.gtaoSampleStepsPerSlice = gtao.sampleStepsPerSlice
    n.gtaoThicknessHeuristic = gtao.thicknessHeuristic
    n.gtaoUseVisibilityBitmasks = gtao.useVisibilityBitmasks
    n.gtaoConstThickness = gtao.constThickness
    n.gtaoLinearThickness = gtao.linearThickness
    return n
}

internal fun TemporalAntiAliasingOptions.toAndroid(): FilamentView.TemporalAntiAliasingOptions {
    val n = FilamentView.TemporalAntiAliasingOptions()
    // no common field for filterWidth: float
    n.feedback = feedback
    n.lodBias = lodBias
    n.sharpness = sharpness
    n.enabled = enabled
    n.upscaling = upscaling
    n.filterHistory = filterHistory
    n.filterInput = filterInput
    n.useYCoCg = useYCoCg
    n.hdr = hdr
    n.boxType = FilamentView.TemporalAntiAliasingOptions.BoxType.values()[boxType.ordinal]
    n.boxClipping = FilamentView.TemporalAntiAliasingOptions.BoxClipping.values()[boxClipping.ordinal]
    n.jitterPattern = FilamentView.TemporalAntiAliasingOptions.JitterPattern.values()[jitterPattern.ordinal]
    n.varianceGamma = varianceGamma
    n.preventFlickering = preventFlickering
    n.historyReprojection = historyReprojection
    return n
}

internal fun ScreenSpaceReflectionsOptions.toAndroid(): FilamentView.ScreenSpaceReflectionsOptions {
    val n = FilamentView.ScreenSpaceReflectionsOptions()
    n.thickness = thickness
    n.bias = bias
    n.maxDistance = maxDistance
    n.stride = stride
    n.enabled = enabled
    return n
}

internal fun VsmShadowOptions.toAndroid(): FilamentView.VsmShadowOptions {
    val n = FilamentView.VsmShadowOptions()
    n.anisotropy = anisotropy
    n.mipmapping = mipmapping
    n.msaaSamples = msaaSamples
    n.highPrecision = highPrecision
    // no common field for minVarianceScale: float
    n.lightBleedReduction = lightBleedReduction
    return n
}

internal fun SoftShadowOptions.toAndroid(): FilamentView.SoftShadowOptions {
    val n = FilamentView.SoftShadowOptions()
    n.penumbraScale = penumbraScale
    n.penumbraRatioScale = penumbraRatioScale
    return n
}

internal fun GuardBandOptions.toAndroid(): FilamentView.GuardBandOptions {
    val n = FilamentView.GuardBandOptions()
    n.enabled = enabled
    return n
}

internal fun StereoscopicOptions.toAndroid(): FilamentView.StereoscopicOptions {
    val n = FilamentView.StereoscopicOptions()
    n.enabled = enabled
    return n
}

internal fun MultiSampleAntiAliasingOptions.toAndroid(): FilamentView.MultiSampleAntiAliasingOptions {
    val n = FilamentView.MultiSampleAntiAliasingOptions()
    n.enabled = enabled
    n.sampleCount = sampleCount
    n.customResolve = customResolve
    return n
}


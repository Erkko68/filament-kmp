package io.github.erkko68.filament

import com.google.android.filament.Engine as AndroidEngine

// The Android bindings model Config as a mutable upstream object, so the common data class
// is marshalled across field by field.

internal fun EngineConfig.ShaderLanguage.toAndroid() =
    AndroidEngine.Config.ShaderLanguage.entries[ordinal]

internal fun shaderLanguageFromAndroid(lang: AndroidEngine.Config.ShaderLanguage) =
    EngineConfig.ShaderLanguage.entries[lang.ordinal]

internal fun EngineConfig.toAndroid(): AndroidEngine.Config {
    val n = AndroidEngine.Config()
    n.commandBufferSizeMB = commandBufferSizeMB
    n.perRenderPassArenaSizeMB = perRenderPassArenaSizeMB
    n.driverHandleArenaSizeMB = driverHandleArenaSizeMB
    n.minCommandBufferSizeMB = minCommandBufferSizeMB
    n.perFrameCommandsSizeMB = perFrameCommandsSizeMB
    n.jobSystemThreadCount = jobSystemThreadCount
    n.disableParallelShaderCompile = disableParallelShaderCompile
    n.stereoscopicType = stereoscopicType.toAndroid()
    n.stereoscopicEyeCount = stereoscopicEyeCount
    n.resourceAllocatorCacheSizeMB = resourceAllocatorCacheSizeMB
    n.resourceAllocatorCacheMaxAge = resourceAllocatorCacheMaxAge
    n.disableHandleUseAfterFreeCheck = disableHandleUseAfterFreeCheck
    n.preferredShaderLanguage = preferredShaderLanguage.toAndroid()
    n.forceGLES2Context = forceGLES2Context
    n.assertNativeWindowIsValid = assertNativeWindowIsValid
    n.gpuContextPriority = gpuContextPriority.toAndroid()
    n.sharedUboInitialSizeInBytes = sharedUboInitialSizeInBytes
    return n
}

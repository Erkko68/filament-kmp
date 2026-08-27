@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import io.github.erkko68.filament.cinterop.FilaEngineConfig

internal fun EngineConfig.toNative(native: FilaEngineConfig) {
    native.commandBufferSizeMB = commandBufferSizeMB.toUInt()
    native.perRenderPassArenaSizeMB = perRenderPassArenaSizeMB.toUInt()
    native.driverHandleArenaSizeMB = driverHandleArenaSizeMB.toUInt()
    native.minCommandBufferSizeMB = minCommandBufferSizeMB.toUInt()
    native.perFrameCommandsSizeMB = perFrameCommandsSizeMB.toUInt()
    native.jobSystemThreadCount = jobSystemThreadCount.toUInt()
    native.disableParallelShaderCompile = disableParallelShaderCompile
    native.stereoscopicType = stereoscopicType.toNative()
    native.stereoscopicEyeCount = stereoscopicEyeCount.toUByte()
    native.resourceAllocatorCacheSizeMB = resourceAllocatorCacheSizeMB.toUInt()
    native.resourceAllocatorCacheMaxAge = resourceAllocatorCacheMaxAge.toUByte()
    native.disableHandleUseAfterFreeCheck = disableHandleUseAfterFreeCheck
    native.preferredShaderLanguage = preferredShaderLanguage.ordinal
    native.forceGLES2Context = forceGLES2Context
    native.assertNativeWindowIsValid = assertNativeWindowIsValid
    native.gpuContextPriority = gpuContextPriority.toNative()
    native.sharedUboInitialSizeInBytes = sharedUboInitialSizeInBytes.toUInt()
}

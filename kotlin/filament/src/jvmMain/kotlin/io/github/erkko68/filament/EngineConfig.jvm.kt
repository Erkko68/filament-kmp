package io.github.erkko68.filament

import io.github.erkko68.filament.ffm.FilaEngineConfig
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

internal fun EngineConfig.toNative(arena: Arena): MemorySegment {
    val s = FilaEngineConfig.allocate(arena)
    FilaEngineConfig.commandBufferSizeMB(s, commandBufferSizeMB.toInt())
    FilaEngineConfig.perRenderPassArenaSizeMB(s, perRenderPassArenaSizeMB.toInt())
    FilaEngineConfig.driverHandleArenaSizeMB(s, driverHandleArenaSizeMB.toInt())
    FilaEngineConfig.minCommandBufferSizeMB(s, minCommandBufferSizeMB.toInt())
    FilaEngineConfig.perFrameCommandsSizeMB(s, perFrameCommandsSizeMB.toInt())
    FilaEngineConfig.jobSystemThreadCount(s, jobSystemThreadCount.toInt())
    FilaEngineConfig.disableParallelShaderCompile(s, disableParallelShaderCompile)
    FilaEngineConfig.stereoscopicType(s, stereoscopicType.toNative())
    FilaEngineConfig.stereoscopicEyeCount(s, stereoscopicEyeCount.toByte())
    FilaEngineConfig.resourceAllocatorCacheSizeMB(s, resourceAllocatorCacheSizeMB.toInt())
    FilaEngineConfig.resourceAllocatorCacheMaxAge(s, resourceAllocatorCacheMaxAge.toByte())
    FilaEngineConfig.disableHandleUseAfterFreeCheck(s, disableHandleUseAfterFreeCheck)
    FilaEngineConfig.preferredShaderLanguage(s, preferredShaderLanguage.ordinal)
    FilaEngineConfig.forceGLES2Context(s, forceGLES2Context)
    FilaEngineConfig.assertNativeWindowIsValid(s, assertNativeWindowIsValid)
    FilaEngineConfig.gpuContextPriority(s, gpuContextPriority.toNative())
    FilaEngineConfig.sharedUboInitialSizeInBytes(s, sharedUboInitialSizeInBytes.toInt())
    return s
}

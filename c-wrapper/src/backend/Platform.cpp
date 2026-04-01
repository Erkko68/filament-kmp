#include "../../include/backend/Platform.h"

#include <backend/Platform.h>

#include <type_traits>

static_assert(static_cast<int>(filament::backend::Platform::StereoscopicType::NONE) ==
                  FILA_BACKEND_PLATFORM_STEREOSCOPIC_TYPE_NONE,
        "StereoscopicType::NONE must match C enum");
static_assert(static_cast<int>(filament::backend::Platform::StereoscopicType::INSTANCED) ==
                  FILA_BACKEND_PLATFORM_STEREOSCOPIC_TYPE_INSTANCED,
        "StereoscopicType::INSTANCED must match C enum");
static_assert(static_cast<int>(filament::backend::Platform::StereoscopicType::MULTIVIEW) ==
                  FILA_BACKEND_PLATFORM_STEREOSCOPIC_TYPE_MULTIVIEW,
        "StereoscopicType::MULTIVIEW must match C enum");

static_assert(static_cast<int>(filament::backend::Platform::GpuContextPriority::DEFAULT) ==
                  FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_DEFAULT,
        "GpuContextPriority::DEFAULT must match C enum");
static_assert(static_cast<int>(filament::backend::Platform::GpuContextPriority::LOW) ==
                  FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_LOW,
        "GpuContextPriority::LOW must match C enum");
static_assert(static_cast<int>(filament::backend::Platform::GpuContextPriority::MEDIUM) ==
                  FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_MEDIUM,
        "GpuContextPriority::MEDIUM must match C enum");
static_assert(static_cast<int>(filament::backend::Platform::GpuContextPriority::HIGH) ==
                  FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_HIGH,
        "GpuContextPriority::HIGH must match C enum");
static_assert(static_cast<int>(filament::backend::Platform::GpuContextPriority::REALTIME) ==
                  FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_REALTIME,
        "GpuContextPriority::REALTIME must match C enum");

static_assert(static_cast<int>(filament::backend::Platform::AsynchronousMode::NONE) ==
                  FILA_BACKEND_PLATFORM_ASYNCHRONOUS_MODE_NONE,
        "AsynchronousMode::NONE must match C enum");
static_assert(static_cast<int>(filament::backend::Platform::AsynchronousMode::THREAD_PREFERRED) ==
                  FILA_BACKEND_PLATFORM_ASYNCHRONOUS_MODE_THREAD_PREFERRED,
        "AsynchronousMode::THREAD_PREFERRED must match C enum");
static_assert(static_cast<int>(filament::backend::Platform::AsynchronousMode::AMORTIZATION) ==
                  FILA_BACKEND_PLATFORM_ASYNCHRONOUS_MODE_AMORTIZATION,
        "AsynchronousMode::AMORTIZATION must match C enum");

extern "C" {

void FilaBackendPlatformDriverConfig_setDefaults(FilaBackendPlatformDriverConfigData* outData) {
    if (!outData) {
        return;
    }
    const filament::backend::Platform::DriverConfig defaults;
    outData->featureFlagManager = defaults.featureFlagManager;
    outData->handleArenaSize = defaults.handleArenaSize;
    outData->metalUploadBufferSizeBytes = defaults.metalUploadBufferSizeBytes;
    outData->disableParallelShaderCompile = defaults.disableParallelShaderCompile;
    outData->disableAmortizedShaderCompile = defaults.disableAmortizedShaderCompile;
    outData->disableHandleUseAfterFreeCheck = defaults.disableHandleUseAfterFreeCheck;
    outData->disableHeapHandleTags = defaults.disableHeapHandleTags;
    outData->forceGLES2Context = defaults.forceGLES2Context;
    outData->stereoscopicType =
        static_cast<FilaBackendPlatformStereoscopicType>(defaults.stereoscopicType);
    outData->stereoscopicEyeCount = defaults.stereoscopicEyeCount;
    outData->assertNativeWindowIsValid = defaults.assertNativeWindowIsValid;
    outData->metalDisablePanicOnDrawableFailure = defaults.metalDisablePanicOnDrawableFailure;
    outData->gpuContextPriority =
        static_cast<FilaBackendPlatformGpuContextPriority>(defaults.gpuContextPriority);
    outData->vulkanEnableAsyncPipelineCachePrewarming =
        defaults.vulkanEnableAsyncPipelineCachePrewarming;
    outData->vulkanEnableStagingBufferBypass = defaults.vulkanEnableStagingBufferBypass;
    outData->asynchronousMode =
        static_cast<FilaBackendPlatformAsynchronousMode>(defaults.asynchronousMode);
}

void FilaBackendPlatformCompositorTiming_setInvalid(FilaBackendPlatformCompositorTimingData* outData) {
    if (!outData) {
        return;
    }
    outData->compositeInterval = 0;
    outData->compositeDeadline = filament::backend::Platform::CompositorTiming::INVALID;
    outData->compositeToPresentLatency = 0;
    outData->expectedPresentLatency = 0;
}

void FilaBackendPlatformFrameTimestamps_setInvalid(FilaBackendPlatformFrameTimestampsData* outData) {
    if (!outData) {
        return;
    }
    outData->requestedPresentTime = filament::backend::Platform::FrameTimestamps::INVALID;
    outData->acquireTime = filament::backend::Platform::FrameTimestamps::INVALID;
    outData->latchTime = filament::backend::Platform::FrameTimestamps::INVALID;
    outData->firstCompositionStartTime = filament::backend::Platform::FrameTimestamps::INVALID;
    outData->lastCompositionStartTime = filament::backend::Platform::FrameTimestamps::INVALID;
    outData->gpuCompositionDoneTime = filament::backend::Platform::FrameTimestamps::INVALID;
    outData->displayPresentTime = filament::backend::Platform::FrameTimestamps::INVALID;
    outData->dequeueReadyTime = filament::backend::Platform::FrameTimestamps::INVALID;
    outData->releaseTime = filament::backend::Platform::FrameTimestamps::INVALID;
}

int64_t FilaBackendPlatformCompositorTiming_getInvalidTimePointNs(void) {
    return filament::backend::Platform::CompositorTiming::INVALID;
}

int64_t FilaBackendPlatformFrameTimestamps_getInvalidTimePointNs(void) {
    return filament::backend::Platform::FrameTimestamps::INVALID;
}

int64_t FilaBackendPlatformFrameTimestamps_getPendingTimePointNs(void) {
    return filament::backend::Platform::FrameTimestamps::PENDING;
}

}


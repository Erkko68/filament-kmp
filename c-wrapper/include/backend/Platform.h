#ifndef FILAMENT_C_BACKEND_PLATFORM_H
#define FILAMENT_C_BACKEND_PLATFORM_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaBackendPlatformSwapChain FilaBackendPlatformSwapChain;
typedef struct FilaBackendPlatformFence FilaBackendPlatformFence;
typedef struct FilaBackendPlatformStream FilaBackendPlatformStream;
typedef struct FilaBackendPlatformSync FilaBackendPlatformSync;

typedef void (*FilaBackendPlatformSyncCallback)(
    FilaBackendPlatformSync* sync, void* userData);

typedef enum FilaBackendPlatformStereoscopicType {
    FILA_BACKEND_PLATFORM_STEREOSCOPIC_TYPE_NONE = 0,
    FILA_BACKEND_PLATFORM_STEREOSCOPIC_TYPE_INSTANCED = 1,
    FILA_BACKEND_PLATFORM_STEREOSCOPIC_TYPE_MULTIVIEW = 2,
} FilaBackendPlatformStereoscopicType;

typedef enum FilaBackendPlatformDeviceInfoType {
    FILA_BACKEND_PLATFORM_DEVICE_INFO_TYPE_OPENGL_RENDERER = 0,
    FILA_BACKEND_PLATFORM_DEVICE_INFO_TYPE_OPENGL_VENDOR = 1,
    FILA_BACKEND_PLATFORM_DEVICE_INFO_TYPE_VULKAN_DEVICE_NAME = 2,
    FILA_BACKEND_PLATFORM_DEVICE_INFO_TYPE_VULKAN_DRIVER_NAME = 3,
    FILA_BACKEND_PLATFORM_DEVICE_INFO_TYPE_VULKAN_DRIVER_INFO = 4,
} FilaBackendPlatformDeviceInfoType;

typedef enum FilaBackendPlatformGpuContextPriority {
    FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_DEFAULT = 0,
    FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_LOW = 1,
    FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_MEDIUM = 2,
    FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_HIGH = 3,
    FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_REALTIME = 4,
} FilaBackendPlatformGpuContextPriority;

typedef enum FilaBackendPlatformAsynchronousMode {
    FILA_BACKEND_PLATFORM_ASYNCHRONOUS_MODE_NONE = 0,
    FILA_BACKEND_PLATFORM_ASYNCHRONOUS_MODE_THREAD_PREFERRED = 1,
    FILA_BACKEND_PLATFORM_ASYNCHRONOUS_MODE_AMORTIZATION = 2,
} FilaBackendPlatformAsynchronousMode;

typedef struct FilaBackendPlatformCompositorTimingData {
    int64_t compositeInterval;
    int64_t compositeDeadline;
    int64_t compositeToPresentLatency;
    int64_t expectedPresentLatency;
} FilaBackendPlatformCompositorTimingData;

typedef struct FilaBackendPlatformFrameTimestampsData {
    int64_t requestedPresentTime;
    int64_t acquireTime;
    int64_t latchTime;
    int64_t firstCompositionStartTime;
    int64_t lastCompositionStartTime;
    int64_t gpuCompositionDoneTime;
    int64_t displayPresentTime;
    int64_t dequeueReadyTime;
    int64_t releaseTime;
} FilaBackendPlatformFrameTimestampsData;

typedef struct FilaBackendPlatformDriverConfigData {
    const void* featureFlagManager;
    size_t handleArenaSize;
    size_t metalUploadBufferSizeBytes;
    bool disableParallelShaderCompile;
    bool disableAmortizedShaderCompile;
    bool disableHandleUseAfterFreeCheck;
    bool disableHeapHandleTags;
    bool forceGLES2Context;
    FilaBackendPlatformStereoscopicType stereoscopicType;
    uint8_t stereoscopicEyeCount;
    bool assertNativeWindowIsValid;
    bool metalDisablePanicOnDrawableFailure;
    FilaBackendPlatformGpuContextPriority gpuContextPriority;
    bool vulkanEnableAsyncPipelineCachePrewarming;
    bool vulkanEnableStagingBufferBypass;
    FilaBackendPlatformAsynchronousMode asynchronousMode;
} FilaBackendPlatformDriverConfigData;

void FilaBackendPlatformDriverConfig_setDefaults(FilaBackendPlatformDriverConfigData* outData);
void FilaBackendPlatformCompositorTiming_setInvalid(FilaBackendPlatformCompositorTimingData* outData);
void FilaBackendPlatformFrameTimestamps_setInvalid(FilaBackendPlatformFrameTimestampsData* outData);

int64_t FilaBackendPlatformCompositorTiming_getInvalidTimePointNs(void);
int64_t FilaBackendPlatformFrameTimestamps_getInvalidTimePointNs(void);
int64_t FilaBackendPlatformFrameTimestamps_getPendingTimePointNs(void);

const char* FilaBackendPlatformStereoscopicType_toString(
        FilaBackendPlatformStereoscopicType type);
const char* FilaBackendPlatformDeviceInfoType_toString(
        FilaBackendPlatformDeviceInfoType type);
const char* FilaBackendPlatformGpuContextPriority_toString(
        FilaBackendPlatformGpuContextPriority priority);
const char* FilaBackendPlatformAsynchronousMode_toString(
        FilaBackendPlatformAsynchronousMode mode);

bool FilaBackendPlatformCompositorTiming_isInvalidTimePointNs(int64_t value);
bool FilaBackendPlatformFrameTimestamps_isInvalidTimePointNs(int64_t value);
bool FilaBackendPlatformFrameTimestamps_isPendingTimePointNs(int64_t value);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PLATFORM_H


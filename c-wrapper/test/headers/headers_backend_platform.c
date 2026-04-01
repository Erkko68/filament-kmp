#include "backend/Platform.h"

void test_headers_backend_platform(void) {
    FilaBackendPlatformDriverConfigData config;
    FilaBackendPlatformDriverConfig_setDefaults(&config);

    FilaBackendPlatformCompositorTimingData compositorTiming;
    FilaBackendPlatformCompositorTiming_setInvalid(&compositorTiming);

    FilaBackendPlatformFrameTimestampsData frameTimestamps;
    FilaBackendPlatformFrameTimestamps_setInvalid(&frameTimestamps);

    FilaBackendPlatformCompositorTiming_getInvalidTimePointNs();
    FilaBackendPlatformFrameTimestamps_getInvalidTimePointNs();
    FilaBackendPlatformFrameTimestamps_getPendingTimePointNs();

    FilaBackendPlatformStereoscopicType_toString(FILA_BACKEND_PLATFORM_STEREOSCOPIC_TYPE_MULTIVIEW);
    FilaBackendPlatformStereoscopicType_toString((FilaBackendPlatformStereoscopicType)255);
    FilaBackendPlatformDeviceInfoType_toString(FILA_BACKEND_PLATFORM_DEVICE_INFO_TYPE_VULKAN_DRIVER_INFO);
    FilaBackendPlatformDeviceInfoType_toString((FilaBackendPlatformDeviceInfoType)255);
    FilaBackendPlatformGpuContextPriority_toString(FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_REALTIME);
    FilaBackendPlatformGpuContextPriority_toString((FilaBackendPlatformGpuContextPriority)255);
    FilaBackendPlatformAsynchronousMode_toString(FILA_BACKEND_PLATFORM_ASYNCHRONOUS_MODE_AMORTIZATION);
    FilaBackendPlatformAsynchronousMode_toString((FilaBackendPlatformAsynchronousMode)255);

    FilaBackendPlatformCompositorTiming_isInvalidTimePointNs(
            FilaBackendPlatformCompositorTiming_getInvalidTimePointNs());
    FilaBackendPlatformFrameTimestamps_isInvalidTimePointNs(
            FilaBackendPlatformFrameTimestamps_getInvalidTimePointNs());
    FilaBackendPlatformFrameTimestamps_isPendingTimePointNs(
            FilaBackendPlatformFrameTimestamps_getPendingTimePointNs());
}


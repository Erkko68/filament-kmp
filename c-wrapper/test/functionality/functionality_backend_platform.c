#include <stdio.h>
#include <string.h>

#include "backend/Platform.h"

int main(void) {
    printf("Running backend_platform functionality program...\n");

    FilaBackendPlatformDriverConfigData config;
    FilaBackendPlatformDriverConfig_setDefaults(&config);
    if (config.stereoscopicEyeCount == 0u) {
        printf("Platform driver defaults produced invalid eye count\n");
        return 1;
    }

    FilaBackendPlatformCompositorTimingData compositorTiming;
    FilaBackendPlatformCompositorTiming_setInvalid(&compositorTiming);
    if (!FilaBackendPlatformCompositorTiming_isInvalidTimePointNs(compositorTiming.compositeDeadline)) {
        printf("Compositor timing invalid sentinel mismatch\n");
        return 1;
    }

    FilaBackendPlatformFrameTimestampsData frameTimestamps;
    FilaBackendPlatformFrameTimestamps_setInvalid(&frameTimestamps);
    if (!FilaBackendPlatformFrameTimestamps_isInvalidTimePointNs(frameTimestamps.displayPresentTime)) {
        printf("Frame timestamps invalid sentinel mismatch\n");
        return 1;
    }

    if (!FilaBackendPlatformFrameTimestamps_isPendingTimePointNs(
                FilaBackendPlatformFrameTimestamps_getPendingTimePointNs())) {
        printf("Frame timestamps pending sentinel mismatch\n");
        return 1;
    }

    if (strcmp(FilaBackendPlatformStereoscopicType_toString(
                       FILA_BACKEND_PLATFORM_STEREOSCOPIC_TYPE_INSTANCED), "INSTANCED") != 0 ||
            strcmp(FilaBackendPlatformStereoscopicType_toString(
                       (FilaBackendPlatformStereoscopicType)255), "UNKNOWN") != 0 ||
            strcmp(FilaBackendPlatformDeviceInfoType_toString(
                       FILA_BACKEND_PLATFORM_DEVICE_INFO_TYPE_OPENGL_VENDOR), "OPENGL_VENDOR") != 0 ||
            strcmp(FilaBackendPlatformDeviceInfoType_toString(
                       (FilaBackendPlatformDeviceInfoType)255), "UNKNOWN") != 0 ||
            strcmp(FilaBackendPlatformGpuContextPriority_toString(
                       FILA_BACKEND_PLATFORM_GPU_CONTEXT_PRIORITY_HIGH), "HIGH") != 0 ||
            strcmp(FilaBackendPlatformGpuContextPriority_toString(
                       (FilaBackendPlatformGpuContextPriority)255), "UNKNOWN") != 0 ||
            strcmp(FilaBackendPlatformAsynchronousMode_toString(
                       FILA_BACKEND_PLATFORM_ASYNCHRONOUS_MODE_THREAD_PREFERRED), "THREAD_PREFERRED") != 0 ||
            strcmp(FilaBackendPlatformAsynchronousMode_toString(
                       (FilaBackendPlatformAsynchronousMode)255), "UNKNOWN") != 0) {
        printf("Platform enum string helper mismatch\n");
        return 1;
    }

    printf("backend_platform functionality program completed\n");
    return 0;
}


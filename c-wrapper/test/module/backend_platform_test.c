#include "backend/Platform.h"

void backend_platform_test(void) {
    FilaBackendPlatformDriverConfigData config;
    FilaBackendPlatformDriverConfig_setDefaults(&config);

    FilaBackendPlatformCompositorTimingData compositorTiming;
    FilaBackendPlatformCompositorTiming_setInvalid(&compositorTiming);

    FilaBackendPlatformFrameTimestampsData frameTimestamps;
    FilaBackendPlatformFrameTimestamps_setInvalid(&frameTimestamps);

    FilaBackendPlatformCompositorTiming_getInvalidTimePointNs();
    FilaBackendPlatformFrameTimestamps_getInvalidTimePointNs();
    FilaBackendPlatformFrameTimestamps_getPendingTimePointNs();
}


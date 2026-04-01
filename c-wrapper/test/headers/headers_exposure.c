#include "filament/Exposure.h"

void test_headers_exposure(void) {
    const FilaCamera* camera = (const FilaCamera*)0;
    (void)FilaExposure_ev100Camera(camera);
    (void)FilaExposure_ev100(16.0f, 1.0f / 125.0f, 100.0f);
    (void)FilaExposure_ev100FromLuminance(50.0f);
    (void)FilaExposure_ev100FromIlluminance(1000.0f);

    (void)FilaExposure_exposureCamera(camera);
    (void)FilaExposure_exposureFromParams(16.0f, 1.0f / 125.0f, 100.0f);
    (void)FilaExposure_exposureFromEv100(12.0f);

    (void)FilaExposure_luminanceCamera(camera);
    (void)FilaExposure_luminanceFromParams(16.0f, 1.0f / 125.0f, 100.0f);
    (void)FilaExposure_luminanceFromEv100(12.0f);

    (void)FilaExposure_illuminanceCamera(camera);
    (void)FilaExposure_illuminanceFromParams(16.0f, 1.0f / 125.0f, 100.0f);
    (void)FilaExposure_illuminanceFromEv100(12.0f);
}


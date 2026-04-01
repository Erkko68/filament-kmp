#include <math.h>
#include <stdio.h>

#include "filament/Exposure.h"

int main(void) {
    printf("Running functionality_engine_camera_exposure...\n");

    // Validate deterministic scalar helpers.
    const float ev = FilaExposure_ev100(16.0f, 1.0f / 125.0f, 100.0f);
    const float exposure = FilaExposure_exposureFromEv100(ev);
    const float luminance = FilaExposure_luminanceFromEv100(ev);
    const float illuminance = FilaExposure_illuminanceFromEv100(ev);
    const float evFromLum = FilaExposure_ev100FromLuminance(luminance);
    const float evFromIll = FilaExposure_ev100FromIlluminance(illuminance);

    if (!(isfinite(ev) && isfinite(exposure) && isfinite(luminance) && isfinite(illuminance) &&
            isfinite(evFromLum) && isfinite(evFromIll))) {
        printf("Exposure helper returned non-finite result\n");
        return 1;
    }
    if (exposure <= 0.0f || luminance <= 0.0f || illuminance <= 0.0f) {
        printf("Exposure helper returned non-positive result\n");
        return 1;
    }

    // Null camera should remain safe.
    if (FilaExposure_ev100Camera((const FilaCamera*)0) != 0.0f ||
            FilaExposure_exposureCamera((const FilaCamera*)0) != 0.0f ||
            FilaExposure_luminanceCamera((const FilaCamera*)0) != 0.0f ||
            FilaExposure_illuminanceCamera((const FilaCamera*)0) != 0.0f) {
        printf("Exposure null-camera guard mismatch\n");
        return 1;
    }

    printf("functionality_engine_camera_exposure completed\n");
    return 0;
}


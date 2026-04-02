#include <math.h>
#include <stdbool.h>
#include <stdio.h>

#include "filament/Color.h"

static bool all_finite3(const float v[3]) {
    return isfinite(v[0]) && isfinite(v[1]) && isfinite(v[2]);
}

static bool all_finite4(const float v[4]) {
    return isfinite(v[0]) && isfinite(v[1]) && isfinite(v[2]) && isfinite(v[3]);
}

int main(void) {
    printf("Running functionality_color...\n");

    const float srgb3[3] = {0.2f, 0.5f, 0.8f};
    const float srgb4[4] = {0.2f, 0.5f, 0.8f, 0.75f};
    float linear3[3] = {0.0f, 0.0f, 0.0f};
    float linear4[4] = {0.0f, 0.0f, 0.0f, 0.0f};
    float roundTrip3[3] = {0.0f, 0.0f, 0.0f};
    float roundTrip4[4] = {0.0f, 0.0f, 0.0f, 0.0f};
    float cct3[3] = {0.0f, 0.0f, 0.0f};
    float d3[3] = {0.0f, 0.0f, 0.0f};
    float absorption3[3] = {0.0f, 0.0f, 0.0f};

    FilaColor_toLinearRGB(FILA_RGB_TYPE_SRGB, srgb3, linear3);
    FilaColor_toLinearRGBA(FILA_RGBA_TYPE_SRGB, srgb4, linear4);
    FilaColor_toSrgbRGB(linear3, roundTrip3);
    FilaColor_toSrgbRGBA(linear4, roundTrip4);

    if (!all_finite3(linear3) || !all_finite4(linear4) || !all_finite3(roundTrip3) || !all_finite4(roundTrip4)) {
        printf("Color conversion produced non-finite values\n");
        return 1;
    }

    if (fabsf(roundTrip3[0] - srgb3[0]) > 0.02f ||
            fabsf(roundTrip3[1] - srgb3[1]) > 0.02f ||
            fabsf(roundTrip3[2] - srgb3[2]) > 0.02f ||
            fabsf(roundTrip4[3] - linear4[3]) > 1e-6f) {
        printf("Color round-trip conversion drift too large\n");
        return 1;
    }

    FilaColor_cct(6500.0f, cct3);
    FilaColor_illuminantD(6500.0f, d3);
    FilaColor_absorptionAtDistance((const float[3]){0.8f, 0.7f, 0.6f}, 1.5f, absorption3);

    if (!all_finite3(cct3) || !all_finite3(d3) || !all_finite3(absorption3)) {
        printf("Color helper output produced non-finite values\n");
        return 1;
    }

    // Null-safety checks: functions are expected to no-op rather than crash.
    FilaColor_toLinearRGB(FILA_RGB_TYPE_SRGB, (const float*)0, linear3);
    FilaColor_toLinearRGBA(FILA_RGBA_TYPE_SRGB, (const float*)0, linear4);
    FilaColor_toSrgbRGB((const float*)0, roundTrip3);
    FilaColor_toSrgbRGBA((const float*)0, roundTrip4);
    FilaColor_cct(6500.0f, (float*)0);
    FilaColor_illuminantD(6500.0f, (float*)0);
    FilaColor_absorptionAtDistance((const float*)0, 1.0f, absorption3);

    printf("functionality_color completed\n");
    return 0;
}


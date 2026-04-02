#include "filament/Color.h"

void test_headers_color(void) {
    float rgb[3] = {0.5f, 0.6f, 0.7f};
    float rgba[4] = {0.25f, 0.5f, 0.75f, 1.0f};
    float out3[3] = {0.0f, 0.0f, 0.0f};
    float out4[4] = {0.0f, 0.0f, 0.0f, 0.0f};

    FilaColor_toLinearRGB(FILA_RGB_TYPE_SRGB, rgb, out3);
    FilaColor_toLinearRGBA(FILA_RGBA_TYPE_SRGB, rgba, out4);
    FilaColor_toSrgbRGB(rgb, out3);
    FilaColor_toSrgbRGBA(rgba, out4);
    FilaColor_cct(6500.0f, out3);
    FilaColor_illuminantD(6500.0f, out3);
    FilaColor_absorptionAtDistance(rgb, 1.0f, out3);
}


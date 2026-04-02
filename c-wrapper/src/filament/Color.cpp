#include <filament/Color.h>
#include <math/vec3.h>

// C wrapper header
#include "../../include/filament/Color.h"

extern "C" {

void FilaColor_toLinearRGB(FilaRgbType type, const float color[3], float outLinear[3]) {
    if (!color || !outLinear) {
        return;
    }
    filament::math::float3 result = filament::Color::toLinear(static_cast<filament::RgbType>(type), filament::math::float3(color[0], color[1], color[2]));
    outLinear[0] = result.x;
    outLinear[1] = result.y;
    outLinear[2] = result.z;
}

void FilaColor_toLinearRGBA(FilaRgbaType type, const float color[4], float outLinear[4]) {
    if (!color || !outLinear) {
        return;
    }
    filament::math::float4 result = filament::Color::toLinear(static_cast<filament::RgbaType>(type), filament::math::float4(color[0], color[1], color[2], color[3]));
    outLinear[0] = result.x;
    outLinear[1] = result.y;
    outLinear[2] = result.z;
    outLinear[3] = result.w;
}

void FilaColor_toSrgbRGB(const float linearColor[3], float outSrgb[3]) {
    if (!linearColor || !outSrgb) {
        return;
    }
    const filament::math::float3 result = filament::Color::toSRGB(filament::math::float3{
            linearColor[0], linearColor[1], linearColor[2]});
    outSrgb[0] = result.x;
    outSrgb[1] = result.y;
    outSrgb[2] = result.z;
}

void FilaColor_toSrgbRGBA(const float linearColor[4], float outSrgb[4]) {
    if (!linearColor || !outSrgb) {
        return;
    }
    const filament::math::float4 result = filament::Color::toSRGB(filament::math::float4{
            linearColor[0], linearColor[1], linearColor[2], linearColor[3]});
    outSrgb[0] = result.x;
    outSrgb[1] = result.y;
    outSrgb[2] = result.z;
    outSrgb[3] = result.w;
}

void FilaColor_cct(float temperatureKelvin, float outLinear[3]) {
    if (!outLinear) {
        return;
    }
    const filament::math::float3 result = filament::Color::cct(temperatureKelvin);
    outLinear[0] = result.x;
    outLinear[1] = result.y;
    outLinear[2] = result.z;
}

void FilaColor_illuminantD(float temperatureKelvin, float outLinear[3]) {
    if (!outLinear) {
        return;
    }
    const filament::math::float3 result = filament::Color::illuminantD(temperatureKelvin);
    outLinear[0] = result.x;
    outLinear[1] = result.y;
    outLinear[2] = result.z;
}

void FilaColor_absorptionAtDistance(const float linearColor[3], float distance, float outAbsorption[3]) {
    if (!linearColor || !outAbsorption) {
        return;
    }
    const filament::math::float3 result = filament::Color::absorptionAtDistance(
            filament::math::float3{linearColor[0], linearColor[1], linearColor[2]}, distance);
    outAbsorption[0] = result.x;
    outAbsorption[1] = result.y;
    outAbsorption[2] = result.z;
}

}

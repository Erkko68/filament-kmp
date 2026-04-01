#include "../../../filament-prebuilts/include/filament/Color.h"
#include "../../../filament-prebuilts/include/math/vec3.h"

// C wrapper header
#include "../../include/filament/Color.h"

extern "C" {

void FilaColor_toLinearRGB(FilaRgbType type, const float color[3], float outLinear[3]) {
    filament::math::float3 result = filament::Color::toLinear(static_cast<filament::RgbType>(type), filament::math::float3(color[0], color[1], color[2]));
    outLinear[0] = result.x;
    outLinear[1] = result.y;
    outLinear[2] = result.z;
}

void FilaColor_toLinearRGBA(FilaRgbaType type, const float color[4], float outLinear[4]) {
    filament::math::float4 result = filament::Color::toLinear(static_cast<filament::RgbaType>(type), filament::math::float4(color[0], color[1], color[2], color[3]));
    outLinear[0] = result.x;
    outLinear[1] = result.y;
    outLinear[2] = result.z;
    outLinear[3] = result.w;
}

}

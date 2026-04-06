#include <filament/Color.h>
#include <math/vec3.h>

#include "FilaCommon.h"
#include "../c/Colors.h"

using namespace filament;

extern "C" {

void FilaColors_cct(float temperature, float* outColor) {
    const math::float3 cct = Color::cct(temperature);
    outColor[0] = cct.r;
    outColor[1] = cct.g;
    outColor[2] = cct.b;
}

void FilaColors_illuminantD(float temperature, float* outColor) {
    const math::float3 illuminantD = Color::illuminantD(temperature);
    outColor[0] = illuminantD.r;
    outColor[1] = illuminantD.g;
    outColor[2] = illuminantD.b;
}

} // extern "C"

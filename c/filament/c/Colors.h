#ifndef FILAMENT_C_COLORS_H
#define FILAMENT_C_COLORS_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

// Static color utilities
// outColor is a float array of size 3 (RGB)
void FilaColors_cct(float temperature, float* outColor);
void FilaColors_illuminantD(float temperature, float* outColor);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_COLORS_H

#ifndef FILAMENT_C_COLOR_H
#define FILAMENT_C_COLOR_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Types of RGB colors
typedef enum FilaRgbType {
    FILA_RGB_TYPE_SRGB = 0,
    FILA_RGB_TYPE_LINEAR = 1,
} FilaRgbType;

// Types of RGBA colors
typedef enum FilaRgbaType {
    FILA_RGBA_TYPE_SRGB = 0,
    FILA_RGBA_TYPE_LINEAR = 1,
    FILA_RGBA_TYPE_PREMULTIPLIED_SRGB = 2,
    FILA_RGBA_TYPE_PREMULTIPLIED_LINEAR = 3,
} FilaRgbaType;

// Color conversions
// These are static utility methods on filament::Color
void FilaColor_toLinearRGB(FilaRgbType type, const float color[3], float outLinear[3]);
void FilaColor_toLinearRGBA(FilaRgbaType type, const float color[4], float outLinear[4]);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_COLOR_H
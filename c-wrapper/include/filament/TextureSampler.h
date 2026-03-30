#ifndef FILAMENT_C_TEXTURE_SAMPLER_H
#define FILAMENT_C_TEXTURE_SAMPLER_H

#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Sampler filter types
typedef enum FilaSamplerMinFilter {
    FILA_SAMPLER_MIN_NEAREST = 0,
    FILA_SAMPLER_MIN_LINEAR = 1,
    FILA_SAMPLER_MIN_NEAREST_MIPMAP_NEAREST = 2,
    FILA_SAMPLER_MIN_LINEAR_MIPMAP_NEAREST = 3,
    FILA_SAMPLER_MIN_NEAREST_MIPMAP_LINEAR = 4,
    FILA_SAMPLER_MIN_LINEAR_MIPMAP_LINEAR = 5,
} FilaSamplerMinFilter;

typedef enum FilaSamplerMagFilter {
    FILA_SAMPLER_MAG_NEAREST = 0,
    FILA_SAMPLER_MAG_LINEAR = 1,
} FilaSamplerMagFilter;

// Wrap modes
typedef enum FilaSamplerWrapMode {
    FILA_SAMPLER_WRAP_CLAMP_TO_EDGE = 0,
    FILA_SAMPLER_WRAP_REPEAT = 1,
    FILA_SAMPLER_WRAP_MIRRORED_REPEAT = 2,
} FilaSamplerWrapMode;

// Compare mode and function
typedef enum FilaSamplerCompareMode {
    FILA_SAMPLER_COMPARE_NONE = 0,
    FILA_SAMPLER_COMPARE_TO_TEXTURE = 1,
} FilaSamplerCompareMode;

typedef enum FilaSamplerCompareFunc {
    FILA_SAMPLER_COMPARE_LE = 0,
    FILA_SAMPLER_COMPARE_GE = 1,
    FILA_SAMPLER_COMPARE_L = 2,
    FILA_SAMPLER_COMPARE_G = 3,
    FILA_SAMPLER_COMPARE_E = 4,
    FILA_SAMPLER_COMPARE_NE = 5,
    FILA_SAMPLER_COMPARE_A = 6,
    FILA_SAMPLER_COMPARE_NA = 7,
} FilaSamplerCompareFunc;

// TextureSampler creation and configuration
FilaTextureParams* FilaTextureParams_create(void);
void FilaTextureParams_destroy(FilaTextureParams* sampler);

// Filter configuration
void FilaTextureParams_setMinFilter(FilaTextureParams* sampler, FilaSamplerMinFilter filter);
void FilaTextureParams_setMagFilter(FilaTextureParams* sampler, FilaSamplerMagFilter filter);

// Wrap mode configuration
void FilaTextureParams_setWrapModeS(FilaTextureParams* sampler, FilaSamplerWrapMode mode);
void FilaTextureParams_setWrapModeT(FilaTextureParams* sampler, FilaSamplerWrapMode mode);
void FilaTextureParams_setWrapModeR(FilaTextureParams* sampler, FilaSamplerWrapMode mode);

// Anisotropy
void FilaTextureParams_setAnisotropy(FilaTextureParams* sampler, float anisotropy);

// Compare mode configuration
void FilaTextureParams_setCompareMode(FilaTextureParams* sampler, FilaSamplerCompareMode mode, FilaSamplerCompareFunc func);

// Getters
FilaSamplerMinFilter FilaTextureParams_getMinFilter(const FilaTextureParams* sampler);
FilaSamplerMagFilter FilaTextureParams_getMagFilter(const FilaTextureParams* sampler);
FilaSamplerWrapMode FilaTextureParams_getWrapModeS(const FilaTextureParams* sampler);
FilaSamplerWrapMode FilaTextureParams_getWrapModeT(const FilaTextureParams* sampler);
FilaSamplerWrapMode FilaTextureParams_getWrapModeR(const FilaTextureParams* sampler);
float FilaTextureParams_getAnisotropy(const FilaTextureParams* sampler);
FilaSamplerCompareMode FilaTextureParams_getCompareMode(const FilaTextureParams* sampler);
FilaSamplerCompareFunc FilaTextureParams_getCompareFunc(const FilaTextureParams* sampler);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TEXTURE_SAMPLER_H


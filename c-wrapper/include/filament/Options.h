#ifndef FILAMENT_C_OPTIONS_H
#define FILAMENT_C_OPTIONS_H

#include <stdbool.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaQualityLevel {
    FILA_QUALITY_LEVEL_LOW = 0,
    FILA_QUALITY_LEVEL_MEDIUM = 1,
    FILA_QUALITY_LEVEL_HIGH = 2,
    FILA_QUALITY_LEVEL_ULTRA = 3,
} FilaQualityLevel;

typedef enum FilaBlendMode {
    FILA_BLEND_MODE_OPAQUE = 0,
    FILA_BLEND_MODE_TRANSLUCENT = 1,
} FilaBlendMode;

typedef enum FilaAntiAliasing {
    FILA_ANTI_ALIASING_NONE = 0,
    FILA_ANTI_ALIASING_FXAA = 1,
} FilaAntiAliasing;

typedef enum FilaDithering {
    FILA_DITHERING_NONE = 0,
    FILA_DITHERING_TEMPORAL = 1,
} FilaDithering;

typedef enum FilaShadowType {
    FILA_SHADOW_TYPE_PCF = 0,
    FILA_SHADOW_TYPE_VSM = 1,
    FILA_SHADOW_TYPE_DPCF = 2,
    FILA_SHADOW_TYPE_PCSS = 3,
    FILA_SHADOW_TYPE_PCFD = 4,
} FilaShadowType;

typedef struct FilaRenderQuality {
    FilaQualityLevel hdrColorBuffer;
} FilaRenderQuality;

typedef struct FilaGuardBandOptions {
    bool enabled;
} FilaGuardBandOptions;

typedef struct FilaStereoscopicOptions {
    bool enabled;
} FilaStereoscopicOptions;

void FilaRenderQuality_setDefaults(FilaRenderQuality* outOptions);
void FilaGuardBandOptions_setDefaults(FilaGuardBandOptions* outOptions);
void FilaStereoscopicOptions_setDefaults(FilaStereoscopicOptions* outOptions);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_OPTIONS_H


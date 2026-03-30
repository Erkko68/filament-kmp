#ifndef FILAMENT_C_COLOR_GRADING_H
#define FILAMENT_C_COLOR_GRADING_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaColorGradingQuality {
    FILA_COLOR_GRADING_QUALITY_LOW = 0,
    FILA_COLOR_GRADING_QUALITY_MEDIUM = 1,
    FILA_COLOR_GRADING_QUALITY_HIGH = 2,
    FILA_COLOR_GRADING_QUALITY_ULTRA = 3,
} FilaColorGradingQuality;

typedef enum FilaColorGradingLutFormat {
    FILA_COLOR_GRADING_LUT_FORMAT_INTEGER = 0,
    FILA_COLOR_GRADING_LUT_FORMAT_FLOAT = 1,
} FilaColorGradingLutFormat;

FilaColorGradingBuilder* FilaColorGradingBuilder_create(void);
void FilaColorGradingBuilder_destroy(FilaColorGradingBuilder* builder);
void FilaColorGradingBuilder_quality(FilaColorGradingBuilder* builder, FilaColorGradingQuality quality);
void FilaColorGradingBuilder_format(FilaColorGradingBuilder* builder, FilaColorGradingLutFormat format);
void FilaColorGradingBuilder_dimensions(FilaColorGradingBuilder* builder, uint8_t dim);
void FilaColorGradingBuilder_luminanceScaling(FilaColorGradingBuilder* builder, bool luminanceScaling);
void FilaColorGradingBuilder_gamutMapping(FilaColorGradingBuilder* builder, bool gamutMapping);
void FilaColorGradingBuilder_exposure(FilaColorGradingBuilder* builder, float exposure);
void FilaColorGradingBuilder_nightAdaptation(FilaColorGradingBuilder* builder, float adaptation);
void FilaColorGradingBuilder_whiteBalance(FilaColorGradingBuilder* builder, float temperature, float tint);
void FilaColorGradingBuilder_contrast(FilaColorGradingBuilder* builder, float contrast);
void FilaColorGradingBuilder_vibrance(FilaColorGradingBuilder* builder, float vibrance);
void FilaColorGradingBuilder_saturation(FilaColorGradingBuilder* builder, float saturation);
FilaColorGrading* FilaColorGradingBuilder_build(FilaColorGradingBuilder* builder, FilaEngine* engine);


#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_COLOR_GRADING_H


#ifndef FILAMENT_C_COLOR_GRADING_H
#define FILAMENT_C_COLOR_GRADING_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"
#include "ToneMapper.h"
#include "ColorSpace.h"

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
void FilaColorGradingBuilder_channelMixer(
        FilaColorGradingBuilder* builder,
        const float outRed3[3],
        const float outGreen3[3],
        const float outBlue3[3]);
void FilaColorGradingBuilder_shadowsMidtonesHighlights(
        FilaColorGradingBuilder* builder,
        const float shadows4[4],
        const float midtones4[4],
        const float highlights4[4],
        const float ranges4[4]);
void FilaColorGradingBuilder_slopeOffsetPower(
        FilaColorGradingBuilder* builder,
        const float slope3[3],
        const float offset3[3],
        const float power3[3]);
void FilaColorGradingBuilder_contrast(FilaColorGradingBuilder* builder, float contrast);
void FilaColorGradingBuilder_vibrance(FilaColorGradingBuilder* builder, float vibrance);
void FilaColorGradingBuilder_saturation(FilaColorGradingBuilder* builder, float saturation);
void FilaColorGradingBuilder_curves(
        FilaColorGradingBuilder* builder,
        const float shadowGamma3[3],
        const float midPoint3[3],
        const float highlightScale3[3]);
void FilaColorGradingBuilder_toneMapper(FilaColorGradingBuilder* builder, const FilaToneMapper* toneMapper);
void FilaColorGradingBuilder_outputColorSpace(FilaColorGradingBuilder* builder, const FilaColorSpace* colorSpace);
FilaColorGrading* FilaColorGradingBuilder_build(FilaColorGradingBuilder* builder, FilaEngine* engine);


#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_COLOR_GRADING_H


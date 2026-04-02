// Compile-only test for ColorGrading C API
// Tests basic builder and getter functionality

#include "filament/ColorGrading.h"
#include "filament/ColorSpace.h"
#include "filament/Engine.h"
#include "filament/ToneMapper.h"

void test_headers_color_grading(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaColorGradingBuilder* builder = FilaColorGradingBuilder_create();
    FilaLinearToneMapper* linearToneMapper = FilaLinearToneMapper_create();
    FilaToneMapper* toneMapper = FilaLinearToneMapper_asToneMapper(linearToneMapper);
    FilaColorSpacePrimaries primaries;
    FilaColorSpaceTransferFunction transferFunction;
    FilaColorSpaceWhitePoint whitePoint;
    FilaColorSpace colorSpace;
    const float outRed3[3] = {1.0f, 0.0f, 0.0f};
    const float outGreen3[3] = {0.0f, 1.0f, 0.0f};
    const float outBlue3[3] = {0.0f, 0.0f, 1.0f};
    const float shadows4[4] = {1.0f, 1.0f, 1.0f, 0.0f};
    const float midtones4[4] = {1.0f, 1.0f, 1.0f, 0.0f};
    const float highlights4[4] = {1.0f, 1.0f, 1.0f, 0.0f};
    const float ranges4[4] = {0.0f, 0.333f, 0.55f, 1.0f};
    const float slope3[3] = {1.0f, 1.0f, 1.0f};
    const float offset3[3] = {0.0f, 0.0f, 0.0f};
    const float power3[3] = {1.0f, 1.0f, 1.0f};
    const float gamma3[3] = {1.0f, 1.0f, 1.0f};
    const float midPoint3[3] = {1.0f, 1.0f, 1.0f};
    const float highlightScale3[3] = {1.0f, 1.0f, 1.0f};

    FilaColorSpace_setRec709Primaries(&primaries);
    FilaColorSpace_setSrgbTransferFunction(&transferFunction);
    FilaColorSpace_setD65WhitePoint(&whitePoint);
    FilaColorSpace_set(&colorSpace, &primaries, &transferFunction, &whitePoint);

    FilaColorGradingBuilder_quality(builder, FILA_COLOR_GRADING_QUALITY_HIGH);
    FilaColorGradingBuilder_format(builder, FILA_COLOR_GRADING_LUT_FORMAT_FLOAT);
    FilaColorGradingBuilder_dimensions(builder, 32);
    FilaColorGradingBuilder_luminanceScaling(builder, true);
    FilaColorGradingBuilder_gamutMapping(builder, true);
    FilaColorGradingBuilder_exposure(builder, 1.0f);
    FilaColorGradingBuilder_nightAdaptation(builder, 0.5f);
    FilaColorGradingBuilder_whiteBalance(builder, 0.1f, 0.2f);
    FilaColorGradingBuilder_channelMixer(builder, outRed3, outGreen3, outBlue3);
    FilaColorGradingBuilder_shadowsMidtonesHighlights(builder, shadows4, midtones4, highlights4, ranges4);
    FilaColorGradingBuilder_slopeOffsetPower(builder, slope3, offset3, power3);
    FilaColorGradingBuilder_contrast(builder, 1.2f);
    FilaColorGradingBuilder_vibrance(builder, 0.9f);
    FilaColorGradingBuilder_saturation(builder, 1.0f);
    FilaColorGradingBuilder_curves(builder, gamma3, midPoint3, highlightScale3);
    FilaColorGradingBuilder_toneMapper(builder, toneMapper);
    FilaColorGradingBuilder_outputColorSpace(builder, &colorSpace);

    FilaColorGrading* colorGrading = FilaColorGradingBuilder_build(builder, engine);
    FilaColorGradingBuilder_destroy(builder);
    FilaToneMapper_destroy(toneMapper);
    FilaEngine_destroyColorGrading(engine, colorGrading);
}

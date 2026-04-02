#include <filament/ColorGrading.h>
#include <filament/ColorSpace.h>
#include <filament/Engine.h>

#include <math/vec3.h>
#include <math/vec4.h>

#include "../../include/filament/ColorGrading.h"

namespace {
using ColorGradingBuilder = filament::ColorGrading::Builder;

filament::ColorGrading::QualityLevel toQualityLevel(FilaColorGradingQuality quality) {
    switch (quality) {
        case FILA_COLOR_GRADING_QUALITY_LOW:
            return filament::ColorGrading::QualityLevel::LOW;
        case FILA_COLOR_GRADING_QUALITY_MEDIUM:
            return filament::ColorGrading::QualityLevel::MEDIUM;
        case FILA_COLOR_GRADING_QUALITY_HIGH:
            return filament::ColorGrading::QualityLevel::HIGH;
        case FILA_COLOR_GRADING_QUALITY_ULTRA:
        default:
            return filament::ColorGrading::QualityLevel::ULTRA;
    }
}

filament::ColorGrading::LutFormat toLutFormat(FilaColorGradingLutFormat format) {
    switch (format) {
        case FILA_COLOR_GRADING_LUT_FORMAT_FLOAT:
            return filament::ColorGrading::LutFormat::FLOAT;
        case FILA_COLOR_GRADING_LUT_FORMAT_INTEGER:
        default:
            return filament::ColorGrading::LutFormat::INTEGER;
    }
}

filament::color::ColorSpace toColorSpace(const FilaColorSpace* colorSpace) {
    if (!colorSpace) {
        return filament::color::Rec709 - filament::color::sRGB - filament::color::D65;
    }
    const filament::color::Primaries primaries{
            filament::math::float2{colorSpace->primaries.r[0], colorSpace->primaries.r[1]},
            filament::math::float2{colorSpace->primaries.g[0], colorSpace->primaries.g[1]},
            filament::math::float2{colorSpace->primaries.b[0], colorSpace->primaries.b[1]}};
    const filament::color::TransferFunction transferFunction{
            colorSpace->transferFunction.a,
            colorSpace->transferFunction.b,
            colorSpace->transferFunction.c,
            colorSpace->transferFunction.d,
            colorSpace->transferFunction.e,
            colorSpace->transferFunction.f,
            colorSpace->transferFunction.g};
    const filament::color::WhitePoint whitePoint{
            colorSpace->whitePoint.xy[0], colorSpace->whitePoint.xy[1]};
    return filament::color::ColorSpace{primaries, transferFunction, whitePoint};
}
} // namespace

extern "C" {

FilaColorGradingBuilder* FilaColorGradingBuilder_create(void) {
    auto builder = new ColorGradingBuilder();
    return reinterpret_cast<FilaColorGradingBuilder*>(builder);
}

void FilaColorGradingBuilder_destroy(FilaColorGradingBuilder* builder) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    delete cppBuilder;
}

void FilaColorGradingBuilder_quality(FilaColorGradingBuilder* builder, FilaColorGradingQuality quality) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->quality(toQualityLevel(quality));
}

void FilaColorGradingBuilder_format(FilaColorGradingBuilder* builder, FilaColorGradingLutFormat format) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->format(toLutFormat(format));
}

void FilaColorGradingBuilder_dimensions(FilaColorGradingBuilder* builder, uint8_t dim) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->dimensions(dim);
}

void FilaColorGradingBuilder_luminanceScaling(FilaColorGradingBuilder* builder, bool luminanceScaling) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->luminanceScaling(luminanceScaling);
}

void FilaColorGradingBuilder_gamutMapping(FilaColorGradingBuilder* builder, bool gamutMapping) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->gamutMapping(gamutMapping);
}

void FilaColorGradingBuilder_exposure(FilaColorGradingBuilder* builder, float exposure) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->exposure(exposure);
}

void FilaColorGradingBuilder_nightAdaptation(FilaColorGradingBuilder* builder, float adaptation) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->nightAdaptation(adaptation);
}

void FilaColorGradingBuilder_whiteBalance(FilaColorGradingBuilder* builder, float temperature, float tint) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->whiteBalance(temperature, tint);
}

void FilaColorGradingBuilder_channelMixer(
        FilaColorGradingBuilder* builder,
        const float outRed3[3],
        const float outGreen3[3],
        const float outBlue3[3]) {
    if (!builder || !outRed3 || !outGreen3 || !outBlue3) {
        return;
    }
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->channelMixer(
            filament::math::float3{outRed3[0], outRed3[1], outRed3[2]},
            filament::math::float3{outGreen3[0], outGreen3[1], outGreen3[2]},
            filament::math::float3{outBlue3[0], outBlue3[1], outBlue3[2]});
}

void FilaColorGradingBuilder_shadowsMidtonesHighlights(
        FilaColorGradingBuilder* builder,
        const float shadows4[4],
        const float midtones4[4],
        const float highlights4[4],
        const float ranges4[4]) {
    if (!builder || !shadows4 || !midtones4 || !highlights4 || !ranges4) {
        return;
    }
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->shadowsMidtonesHighlights(
            filament::math::float4{shadows4[0], shadows4[1], shadows4[2], shadows4[3]},
            filament::math::float4{midtones4[0], midtones4[1], midtones4[2], midtones4[3]},
            filament::math::float4{highlights4[0], highlights4[1], highlights4[2], highlights4[3]},
            filament::math::float4{ranges4[0], ranges4[1], ranges4[2], ranges4[3]});
}

void FilaColorGradingBuilder_slopeOffsetPower(
        FilaColorGradingBuilder* builder,
        const float slope3[3],
        const float offset3[3],
        const float power3[3]) {
    if (!builder || !slope3 || !offset3 || !power3) {
        return;
    }
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->slopeOffsetPower(
            filament::math::float3{slope3[0], slope3[1], slope3[2]},
            filament::math::float3{offset3[0], offset3[1], offset3[2]},
            filament::math::float3{power3[0], power3[1], power3[2]});
}

void FilaColorGradingBuilder_contrast(FilaColorGradingBuilder* builder, float contrast) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->contrast(contrast);
}

void FilaColorGradingBuilder_vibrance(FilaColorGradingBuilder* builder, float vibrance) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->vibrance(vibrance);
}

void FilaColorGradingBuilder_saturation(FilaColorGradingBuilder* builder, float saturation) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->saturation(saturation);
}

void FilaColorGradingBuilder_curves(
        FilaColorGradingBuilder* builder,
        const float shadowGamma3[3],
        const float midPoint3[3],
        const float highlightScale3[3]) {
    if (!builder || !shadowGamma3 || !midPoint3 || !highlightScale3) {
        return;
    }
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->curves(
            filament::math::float3{shadowGamma3[0], shadowGamma3[1], shadowGamma3[2]},
            filament::math::float3{midPoint3[0], midPoint3[1], midPoint3[2]},
            filament::math::float3{highlightScale3[0], highlightScale3[1], highlightScale3[2]});
}

void FilaColorGradingBuilder_toneMapper(FilaColorGradingBuilder* builder, const FilaToneMapper* toneMapper) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->toneMapper(reinterpret_cast<const filament::ToneMapper*>(toneMapper));
}

void FilaColorGradingBuilder_outputColorSpace(FilaColorGradingBuilder* builder, const FilaColorSpace* colorSpace) {
    if (!builder || !colorSpace) {
        return;
    }
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    cppBuilder->outputColorSpace(toColorSpace(colorSpace));
}

FilaColorGrading* FilaColorGradingBuilder_build(FilaColorGradingBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) return nullptr;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaColorGrading*>(cppBuilder->build(*cppEngine));
}


} // extern "C"


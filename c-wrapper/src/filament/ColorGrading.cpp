#include <filament/ColorGrading.h>
#include <filament/Engine.h>

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

FilaColorGrading* FilaColorGradingBuilder_build(FilaColorGradingBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) return nullptr;
    auto cppBuilder = reinterpret_cast<ColorGradingBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaColorGrading*>(cppBuilder->build(*cppEngine));
}


} // extern "C"


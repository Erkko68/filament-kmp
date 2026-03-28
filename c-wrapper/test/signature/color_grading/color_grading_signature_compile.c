// ABI signature lock test for ColorGrading C API
// Validates pointer signatures and enums at compile time

#include <stdbool.h>

#include "filament/ColorGrading.h"
#include "filament/Engine.h"
#include "filament/Types.h"

void fila_color_grading_signature_test(void) {
    // Test builder pointer methods return correct signatures
    FilaColorGradingBuilder* (*builder_create_fn)(void) = FilaColorGradingBuilder_create;
    void (*builder_destroy_fn)(FilaColorGradingBuilder*) = FilaColorGradingBuilder_destroy;
    void (*builder_quality_fn)(FilaColorGradingBuilder*, FilaColorGradingQuality) = FilaColorGradingBuilder_quality;
    void (*builder_format_fn)(FilaColorGradingBuilder*, FilaColorGradingLutFormat) = FilaColorGradingBuilder_format;
    void (*builder_dimensions_fn)(FilaColorGradingBuilder*, unsigned char) = FilaColorGradingBuilder_dimensions;
    void (*builder_exposure_fn)(FilaColorGradingBuilder*, float) = FilaColorGradingBuilder_exposure;
    void (*builder_nightAdaptation_fn)(FilaColorGradingBuilder*, float) = FilaColorGradingBuilder_nightAdaptation;
    void (*builder_whiteBalance_fn)(FilaColorGradingBuilder*, float, float) = FilaColorGradingBuilder_whiteBalance;
    void (*builder_contrast_fn)(FilaColorGradingBuilder*, float) = FilaColorGradingBuilder_contrast;
    void (*builder_vibrance_fn)(FilaColorGradingBuilder*, float) = FilaColorGradingBuilder_vibrance;
    void (*builder_saturation_fn)(FilaColorGradingBuilder*, float) = FilaColorGradingBuilder_saturation;
    void (*builder_luminanceScaling_fn)(FilaColorGradingBuilder*, bool) = FilaColorGradingBuilder_luminanceScaling;
    void (*builder_gamutMapping_fn)(FilaColorGradingBuilder*, bool) = FilaColorGradingBuilder_gamutMapping;
    FilaColorGrading* (*builder_build_fn)(FilaColorGradingBuilder*, FilaEngine*) = FilaColorGradingBuilder_build;


    // Test enum values
    FilaColorGradingQuality quality_low = FILA_COLOR_GRADING_QUALITY_LOW;
    FilaColorGradingQuality quality_medium = FILA_COLOR_GRADING_QUALITY_MEDIUM;
    FilaColorGradingQuality quality_high = FILA_COLOR_GRADING_QUALITY_HIGH;
    FilaColorGradingQuality quality_ultra = FILA_COLOR_GRADING_QUALITY_ULTRA;

    FilaColorGradingLutFormat format_integer = FILA_COLOR_GRADING_LUT_FORMAT_INTEGER;
    FilaColorGradingLutFormat format_float = FILA_COLOR_GRADING_LUT_FORMAT_FLOAT;

    (void)builder_create_fn;
    (void)builder_destroy_fn;
    (void)builder_quality_fn;
    (void)builder_format_fn;
    (void)builder_dimensions_fn;
    (void)builder_exposure_fn;
    (void)builder_nightAdaptation_fn;
    (void)builder_whiteBalance_fn;
    (void)builder_contrast_fn;
    (void)builder_vibrance_fn;
    (void)builder_saturation_fn;
    (void)builder_luminanceScaling_fn;
    (void)builder_gamutMapping_fn;
    (void)builder_build_fn;
    (void)quality_low;
    (void)quality_medium;
    (void)quality_high;
    (void)quality_ultra;
    (void)format_integer;
    (void)format_float;
}


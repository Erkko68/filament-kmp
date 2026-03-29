// Compile-only test for ColorGrading C API
// Tests basic builder and getter functionality

#include "filament/ColorGrading.h"
#include "filament/Engine.h"
#include "filament/Types.h"

#include <stdio.h>

void test_color_grading_builder() {
    FilaEngine* engine = FilaEngine_create();

    // Test builder creation and configuration
    FilaColorGradingBuilder* builder = FilaColorGradingBuilder_create();
    FilaColorGradingBuilder_quality(builder, FILA_COLOR_GRADING_QUALITY_HIGH);
    FilaColorGradingBuilder_format(builder, FILA_COLOR_GRADING_LUT_FORMAT_FLOAT);
    FilaColorGradingBuilder_dimensions(builder, 32);
    FilaColorGradingBuilder_exposure(builder, 1.0f);
    FilaColorGradingBuilder_nightAdaptation(builder, 0.5f);
    FilaColorGradingBuilder_whiteBalance(builder, 0.1f, 0.2f);
    FilaColorGradingBuilder_contrast(builder, 1.2f);
    FilaColorGradingBuilder_vibrance(builder, 0.9f);
    FilaColorGradingBuilder_saturation(builder, 1.0f);
    FilaColorGradingBuilder_luminanceScaling(builder, true);
    FilaColorGradingBuilder_gamutMapping(builder, true);

    // Test build
    FilaColorGrading* colorGrading = FilaColorGradingBuilder_build(builder, engine);
    FilaColorGradingBuilder_destroy(builder);

    // Cleanup
    FilaEngine_destroyColorGrading(engine, colorGrading);
    FilaEngine_destroy(&engine);

    printf("ColorGrading test: builder created and destroyed successfully\n");
}

int main() {
    test_color_grading_builder();
    return 0;
}


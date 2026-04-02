#include "filament/Engine.h"
#include "filament/IndirectLight.h"
#include "filament/Texture.h"

// Verifies IndirectLight builder and runtime APIs are consumable from C.
void test_headers_indirect_light(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaTexture* reflections = (FilaTexture*)0;

    FilaIndirectLightBuilder* builder = FilaIndirectLightBuilder_create();
    float rotation[9] = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
    };
    float direction[3] = {0.0f, 1.0f, 0.0f};
    float color[4] = {0.0f, 0.0f, 0.0f, 0.0f};
    float sh[3] = {0.0f, 0.0f, 0.0f};
    FilaIndirectLightBuilder_reflections(builder, reflections);
    FilaIndirectLightBuilder_intensity(builder, 30000.0f);
    FilaIndirectLightBuilder_radiance(builder, 1u, sh);
    FilaIndirectLightBuilder_rotationMat3f(builder, rotation);

    FilaIndirectLight* indirectLight = FilaIndirectLightBuilder_build(builder, engine);
    FilaIndirectLight_setIntensity(indirectLight, 12000.0f);
    FilaIndirectLight_setRotationMat3f(indirectLight, rotation);
    (void)FilaIndirectLight_getIntensity(indirectLight);
    (void)FilaIndirectLight_getRotationMat3f(indirectLight, rotation);
    (void)FilaIndirectLight_getDirectionEstimate(indirectLight, direction);
    (void)FilaIndirectLight_getColorEstimate(indirectLight, direction, color);
    (void)FilaIndirectLight_getReflectionsTexture(indirectLight);
    (void)FilaIndirectLight_getIrradianceTexture(indirectLight);

    FilaIndirectLightBuilder_destroy(builder);
    FilaEngine_destroyIndirectLight(engine, indirectLight);
}


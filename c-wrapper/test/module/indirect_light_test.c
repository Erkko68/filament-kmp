#include "filament/Engine.h"
#include "filament/IndirectLight.h"
#include "filament/Texture.h"

// Verifies IndirectLight builder and runtime APIs are consumable from C.
void fila_indirect_light_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaTexture* reflections = (FilaTexture*)0;

    FilaIndirectLightBuilder* builder = FilaIndirectLightBuilder_create();
    FilaIndirectLightBuilder_reflections(builder, reflections);
    FilaIndirectLightBuilder_intensity(builder, 30000.0f);

    FilaIndirectLight* indirectLight = FilaIndirectLightBuilder_build(builder, engine);
    FilaIndirectLight_setIntensity(indirectLight, 12000.0f);
    (void)FilaIndirectLight_getIntensity(indirectLight);
    (void)FilaIndirectLight_getReflectionsTexture(indirectLight);
    (void)FilaIndirectLight_getIrradianceTexture(indirectLight);

    FilaIndirectLightBuilder_destroy(builder);
    FilaEngine_destroyIndirectLight(engine, indirectLight);
}


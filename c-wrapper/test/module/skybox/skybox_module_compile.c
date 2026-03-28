#include "filament/Engine.h"
#include "filament/Skybox.h"
#include "filament/Texture.h"

// Verifies Skybox builder and getters are consumable from C.
void fila_skybox_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaTexture* texture = (FilaTexture*)0;

    FilaSkyboxBuilder* builder = FilaSkyboxBuilder_create();
    FilaSkyboxBuilder_environment(builder, texture);
    FilaSkyboxBuilder_showSun(builder, false);
    FilaSkyboxBuilder_intensity(builder, 30000.0f);
    FilaSkyboxBuilder_color(builder, 0.1f, 0.2f, 0.3f, 1.0f);
    FilaSkyboxBuilder_priority(builder, 7u);

    FilaSkybox* skybox = FilaSkyboxBuilder_build(builder, engine);
    FilaSkybox_setLayerMask(skybox, 0xFFu, 0x01u);
    (void)FilaSkybox_getLayerMask(skybox);
    (void)FilaSkybox_getIntensity(skybox);
    (void)FilaSkybox_getTexture(skybox);

    FilaSkyboxBuilder_destroy(builder);
    FilaEngine_destroySkybox(engine, skybox);
}


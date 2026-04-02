#include "filament/Engine.h"
#include "filament/Skybox.h"
#include "filament/Texture.h"

// Verifies Skybox builder and getters are consumable from C.
void test_headers_skybox(void) {
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
    FilaSkybox_setColor(skybox, 0.2f, 0.3f, 0.4f, 1.0f);
    (void)FilaSkybox_getLayerMask(skybox);
    (void)FilaSkybox_getIntensity(skybox);
    (void)FilaSkybox_getTexture(skybox);

    FilaSkyboxBuilder_destroy(builder);
    FilaEngine_destroySkybox(engine, skybox);
}


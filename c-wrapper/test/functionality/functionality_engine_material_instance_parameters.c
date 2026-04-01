#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Material.h"
#include "filament/MaterialInstance.h"
#include "filament/Texture.h"
#include "filament/TextureSampler.h"

int main(void) {
    printf("Running engine+material_instance_parameters functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    // Exercise null guards for parameter setters; this is a safe runtime path even
    // without an embedded valid material package.
    FilaMaterialInstance_setParameterFloat((FilaMaterialInstance*)0, "uFloat", 1.0f);
    FilaMaterialInstance_setParameterFloat2((FilaMaterialInstance*)0, "uFloat2", 1.0f, 2.0f);
    FilaMaterialInstance_setParameterFloat3((FilaMaterialInstance*)0, "uFloat3", 1.0f, 2.0f, 3.0f);
    FilaMaterialInstance_setParameterFloat4((FilaMaterialInstance*)0, "uFloat4", 1.0f, 2.0f, 3.0f, 4.0f);
    FilaMaterialInstance_setParameterInt((FilaMaterialInstance*)0, "uInt", -7);
    FilaMaterialInstance_setParameterUint((FilaMaterialInstance*)0, "uUint", 7u);

    FilaTextureParams* sampler = FilaTextureParams_create();
    FilaMaterialInstance_setParameterTexture(
        (FilaMaterialInstance*)0,
        "uSampler",
        (const FilaTexture*)0,
        sampler);
    FilaTextureParams_destroy(sampler);

    (void)FilaMaterial_createInstanceNamed((const FilaMaterial*)0, "named");
    (void)FilaMaterial_getName((const FilaMaterial*)0);
    (void)FilaMaterial_hasParameter((const FilaMaterial*)0, "uSampler");
    (void)FilaMaterial_isSampler((const FilaMaterial*)0, "uSampler");
    (void)FilaMaterial_getParameterCount((const FilaMaterial*)0);

    // Also verify getMaterial null handling remains stable.
    if (FilaMaterialInstance_getMaterial((FilaMaterialInstance*)0) != (const FilaMaterial*)0) {
        printf("MaterialInstance null material query mismatch\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroy(&engine);

    printf("Engine+material_instance_parameters functionality program completed\n");
    return 0;
}

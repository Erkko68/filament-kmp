#include "filament/TextureSampler.h"

// Verifies TextureSampler C API is consumable from C
void test_headers_texture_sampler(void) {
    // Test sampler creation
    FilaTextureParams* sampler = FilaTextureParams_create();

    // Test filter configuration
    FilaTextureParams_setMinFilter(sampler, FILA_SAMPLER_MIN_LINEAR);
    FilaTextureParams_setMagFilter(sampler, FILA_SAMPLER_MAG_LINEAR);

    // Test wrap mode configuration
    FilaTextureParams_setWrapModeS(sampler, FILA_SAMPLER_WRAP_REPEAT);
    FilaTextureParams_setWrapModeT(sampler, FILA_SAMPLER_WRAP_REPEAT);
    FilaTextureParams_setWrapModeR(sampler, FILA_SAMPLER_WRAP_CLAMP_TO_EDGE);

    // Test anisotropy
    FilaTextureParams_setAnisotropy(sampler, 4.0f);

    // Test compare mode configuration
    FilaTextureParams_setCompareMode(sampler, FILA_SAMPLER_COMPARE_TO_TEXTURE, FILA_SAMPLER_COMPARE_LE);

    // Test getters
    (void)FilaTextureParams_getMinFilter(sampler);
    (void)FilaTextureParams_getMagFilter(sampler);
    (void)FilaTextureParams_getWrapModeS(sampler);
    (void)FilaTextureParams_getWrapModeT(sampler);
    (void)FilaTextureParams_getWrapModeR(sampler);
    (void)FilaTextureParams_getAnisotropy(sampler);
    (void)FilaTextureParams_getCompareMode(sampler);
    (void)FilaTextureParams_getCompareFunc(sampler);

    FilaTextureParams_destroy(sampler);
}


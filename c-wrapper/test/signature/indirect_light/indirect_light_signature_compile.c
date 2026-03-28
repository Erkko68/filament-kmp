#include "filament/IndirectLight.h"

// Function pointer assignments lock exported C signatures.
static FilaIndirectLightBuilder* (*g_il_builder_create)(void) = FilaIndirectLightBuilder_create;
static void (*g_il_builder_destroy)(FilaIndirectLightBuilder*) = FilaIndirectLightBuilder_destroy;
static void (*g_il_builder_reflections)(FilaIndirectLightBuilder*, const FilaTexture*) = FilaIndirectLightBuilder_reflections;
static void (*g_il_builder_intensity)(FilaIndirectLightBuilder*, float) = FilaIndirectLightBuilder_intensity;
static FilaIndirectLight* (*g_il_builder_build)(FilaIndirectLightBuilder*, FilaEngine*) = FilaIndirectLightBuilder_build;
static void (*g_il_set_intensity)(FilaIndirectLight*, float) = FilaIndirectLight_setIntensity;
static float (*g_il_get_intensity)(const FilaIndirectLight*) = FilaIndirectLight_getIntensity;
static const FilaTexture* (*g_il_get_reflections_texture)(const FilaIndirectLight*) = FilaIndirectLight_getReflectionsTexture;
static const FilaTexture* (*g_il_get_irradiance_texture)(const FilaIndirectLight*) = FilaIndirectLight_getIrradianceTexture;

void fila_indirect_light_signature_compile_only(void) {
    (void)g_il_builder_create;
    (void)g_il_builder_destroy;
    (void)g_il_builder_reflections;
    (void)g_il_builder_intensity;
    (void)g_il_builder_build;
    (void)g_il_set_intensity;
    (void)g_il_get_intensity;
    (void)g_il_get_reflections_texture;
    (void)g_il_get_irradiance_texture;
}


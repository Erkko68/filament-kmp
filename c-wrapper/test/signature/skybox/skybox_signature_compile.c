#include "filament/Skybox.h"

// Function pointer assignments lock exported C signatures.
static FilaSkyboxBuilder* (*g_skybox_builder_create)(void) = FilaSkyboxBuilder_create;
static void (*g_skybox_builder_destroy)(FilaSkyboxBuilder*) = FilaSkyboxBuilder_destroy;
static void (*g_skybox_builder_environment)(FilaSkyboxBuilder*, FilaTexture*) = FilaSkyboxBuilder_environment;
static void (*g_skybox_builder_show_sun)(FilaSkyboxBuilder*, bool) = FilaSkyboxBuilder_showSun;
static void (*g_skybox_builder_intensity)(FilaSkyboxBuilder*, float) = FilaSkyboxBuilder_intensity;
static void (*g_skybox_builder_color)(FilaSkyboxBuilder*, float, float, float, float) = FilaSkyboxBuilder_color;
static void (*g_skybox_builder_priority)(FilaSkyboxBuilder*, uint8_t) = FilaSkyboxBuilder_priority;
static FilaSkybox* (*g_skybox_builder_build)(FilaSkyboxBuilder*, FilaEngine*) = FilaSkyboxBuilder_build;
static void (*g_skybox_set_layer_mask)(FilaSkybox*, uint8_t, uint8_t) = FilaSkybox_setLayerMask;
static uint8_t (*g_skybox_get_layer_mask)(const FilaSkybox*) = FilaSkybox_getLayerMask;
static float (*g_skybox_get_intensity)(const FilaSkybox*) = FilaSkybox_getIntensity;
static const FilaTexture* (*g_skybox_get_texture)(const FilaSkybox*) = FilaSkybox_getTexture;

void fila_skybox_signature_compile_only(void) {
    (void)g_skybox_builder_create;
    (void)g_skybox_builder_destroy;
    (void)g_skybox_builder_environment;
    (void)g_skybox_builder_show_sun;
    (void)g_skybox_builder_intensity;
    (void)g_skybox_builder_color;
    (void)g_skybox_builder_priority;
    (void)g_skybox_builder_build;
    (void)g_skybox_set_layer_mask;
    (void)g_skybox_get_layer_mask;
    (void)g_skybox_get_intensity;
    (void)g_skybox_get_texture;
}


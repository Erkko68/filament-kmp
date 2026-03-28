#include "filament/TextureSampler.h"

// Function pointer assignments lock exported C signatures.
static FilaTextureParams* (*g_sampler_create)(void) = FilaTextureParams_create;
static void (*g_sampler_destroy)(FilaTextureParams*) = FilaTextureParams_destroy;
static void (*g_sampler_set_min)(FilaTextureParams*, FilaSamplerMinFilter) = FilaTextureParams_setMinFilter;
static void (*g_sampler_set_mag)(FilaTextureParams*, FilaSamplerMagFilter) = FilaTextureParams_setMagFilter;
static void (*g_sampler_set_wrap_s)(FilaTextureParams*, FilaSamplerWrapMode) = FilaTextureParams_setWrapModeS;
static void (*g_sampler_set_wrap_t)(FilaTextureParams*, FilaSamplerWrapMode) = FilaTextureParams_setWrapModeT;
static void (*g_sampler_set_wrap_r)(FilaTextureParams*, FilaSamplerWrapMode) = FilaTextureParams_setWrapModeR;
static void (*g_sampler_set_aniso)(FilaTextureParams*, float) = FilaTextureParams_setAnisotropy;
static void (*g_sampler_set_compare)(FilaTextureParams*, FilaSamplerCompareMode, FilaSamplerCompareFunc) = FilaTextureParams_setCompareMode;

static FilaSamplerMinFilter (*g_sampler_get_min)(const FilaTextureParams*) = FilaTextureParams_getMinFilter;
static FilaSamplerMagFilter (*g_sampler_get_mag)(const FilaTextureParams*) = FilaTextureParams_getMagFilter;
static FilaSamplerWrapMode (*g_sampler_get_wrap_s)(const FilaTextureParams*) = FilaTextureParams_getWrapModeS;
static FilaSamplerWrapMode (*g_sampler_get_wrap_t)(const FilaTextureParams*) = FilaTextureParams_getWrapModeT;
static FilaSamplerWrapMode (*g_sampler_get_wrap_r)(const FilaTextureParams*) = FilaTextureParams_getWrapModeR;
static float (*g_sampler_get_aniso)(const FilaTextureParams*) = FilaTextureParams_getAnisotropy;
static FilaSamplerCompareMode (*g_sampler_get_compare_mode)(const FilaTextureParams*) = FilaTextureParams_getCompareMode;
static FilaSamplerCompareFunc (*g_sampler_get_compare_func)(const FilaTextureParams*) = FilaTextureParams_getCompareFunc;

void fila_texture_sampler_signature_compile_only(void) {
    (void)g_sampler_create;
    (void)g_sampler_destroy;
    (void)g_sampler_set_min;
    (void)g_sampler_set_mag;
    (void)g_sampler_set_wrap_s;
    (void)g_sampler_set_wrap_t;
    (void)g_sampler_set_wrap_r;
    (void)g_sampler_set_aniso;
    (void)g_sampler_set_compare;
    (void)g_sampler_get_min;
    (void)g_sampler_get_mag;
    (void)g_sampler_get_wrap_s;
    (void)g_sampler_get_wrap_t;
    (void)g_sampler_get_wrap_r;
    (void)g_sampler_get_aniso;
    (void)g_sampler_get_compare_mode;
    (void)g_sampler_get_compare_func;
}


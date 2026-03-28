#include "filament/View.h"
#include "filament/ColorGrading.h"
#include "filament/RenderTarget.h"

// Function pointer assignments lock exported C signatures.
static void (*g_view_set_scene)(FilaView*, FilaScene*) = FilaView_setScene;
static FilaScene* (*g_view_get_scene)(FilaView*) = FilaView_getScene;
static void (*g_view_set_viewport)(FilaView*, FilaViewport) = FilaView_setViewport;
static FilaViewport (*g_view_get_viewport)(const FilaView*) = FilaView_getViewport;
static void (*g_view_set_camera)(FilaView*, FilaCamera*) = FilaView_setCamera;
static bool (*g_view_has_camera)(const FilaView*) = FilaView_hasCamera;
static FilaCamera* (*g_view_get_camera)(FilaView*) = FilaView_getCamera;
static void (*g_view_set_color_grading)(FilaView*, FilaColorGrading*) = FilaView_setColorGrading;
static FilaColorGrading* (*g_view_get_color_grading)(FilaView*) = FilaView_getColorGrading;
static void (*g_view_set_render_target)(FilaView*, FilaRenderTarget*) = FilaView_setRenderTarget;
static FilaRenderTarget* (*g_view_get_render_target)(FilaView*) = FilaView_getRenderTarget;

void fila_view_signature_compile_only(void) {
    (void)g_view_set_scene;
    (void)g_view_get_scene;
    (void)g_view_set_viewport;
    (void)g_view_get_viewport;
    (void)g_view_set_camera;
    (void)g_view_has_camera;
    (void)g_view_get_camera;
    (void)g_view_set_color_grading;
    (void)g_view_get_color_grading;
    (void)g_view_set_render_target;
    (void)g_view_get_render_target;
}

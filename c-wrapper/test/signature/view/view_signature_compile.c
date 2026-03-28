#include "filament/View.h"

// Function pointer assignments lock exported C signatures.
static void (*g_view_set_scene)(FilaView*, FilaScene*) = FilaView_setScene;
static FilaScene* (*g_view_get_scene)(FilaView*) = FilaView_getScene;
static void (*g_view_set_camera)(FilaView*, FilaCamera*) = FilaView_setCamera;
static bool (*g_view_has_camera)(const FilaView*) = FilaView_hasCamera;
static FilaCamera* (*g_view_get_camera)(FilaView*) = FilaView_getCamera;

void fila_view_signature_compile_only(void) {
    (void)g_view_set_scene;
    (void)g_view_get_scene;
    (void)g_view_set_camera;
    (void)g_view_has_camera;
    (void)g_view_get_camera;
}


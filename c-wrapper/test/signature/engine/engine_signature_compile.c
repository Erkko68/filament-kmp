#include "filament/Engine.h"

// Function pointer assignments lock exported C signatures.
static FilaEngine* (*g_engine_create)(void) = FilaEngine_create;
static void (*g_engine_destroy)(FilaEngine**) = FilaEngine_destroy;
static FilaRenderer* (*g_engine_create_renderer)(FilaEngine*) = FilaEngine_createRenderer;
static void (*g_engine_destroy_renderer)(FilaEngine*, FilaRenderer*) = FilaEngine_destroyRenderer;
static FilaScene* (*g_engine_create_scene)(FilaEngine*) = FilaEngine_createScene;
static void (*g_engine_destroy_scene)(FilaEngine*, FilaScene*) = FilaEngine_destroyScene;

void fila_engine_signature_compile_only(void) {
    (void)g_engine_create;
    (void)g_engine_destroy;
    (void)g_engine_create_renderer;
    (void)g_engine_destroy_renderer;
    (void)g_engine_create_scene;
    (void)g_engine_destroy_scene;
}


#include "filament/Engine.h"

// Function pointer assignments lock exported C signatures.
static FilaEngine* (*g_engine_create)(void) = FilaEngine_create;
static void (*g_engine_destroy)(FilaEngine**) = FilaEngine_destroy;
static FilaRenderer* (*g_engine_create_renderer)(FilaEngine*) = FilaEngine_createRenderer;
static void (*g_engine_destroy_renderer)(FilaEngine*, FilaRenderer*) = FilaEngine_destroyRenderer;
static FilaSwapChain* (*g_engine_create_swap_chain)(FilaEngine*, void*, uint64_t) = FilaEngine_createSwapChain;
static FilaSwapChain* (*g_engine_create_swap_chain_headless)(FilaEngine*, uint32_t, uint32_t, uint64_t) = FilaEngine_createSwapChainHeadless;
static void (*g_engine_destroy_swap_chain)(FilaEngine*, FilaSwapChain*) = FilaEngine_destroySwapChain;
static FilaFence* (*g_engine_create_fence)(FilaEngine*) = FilaEngine_createFence;
static void (*g_engine_destroy_fence)(FilaEngine*, FilaFence*) = FilaEngine_destroyFence;
static FilaScene* (*g_engine_create_scene)(FilaEngine*) = FilaEngine_createScene;
static void (*g_engine_destroy_scene)(FilaEngine*, FilaScene*) = FilaEngine_destroyScene;
static FilaView* (*g_engine_create_view)(FilaEngine*) = FilaEngine_createView;
static void (*g_engine_destroy_view)(FilaEngine*, FilaView*) = FilaEngine_destroyView;
static FilaCamera* (*g_engine_create_camera)(FilaEngine*, FilaEntity) = FilaEngine_createCamera;
static FilaCamera* (*g_engine_get_camera_component)(FilaEngine*, FilaEntity) = FilaEngine_getCameraComponent;
static void (*g_engine_destroy_camera_component)(FilaEngine*, FilaEntity) = FilaEngine_destroyCameraComponent;
static FilaTransformManager* (*g_engine_get_transform_manager)(FilaEngine*) = FilaEngine_getTransformManager;

void fila_engine_signature_compile_only(void) {
    (void)g_engine_create;
    (void)g_engine_destroy;
    (void)g_engine_create_renderer;
    (void)g_engine_destroy_renderer;
    (void)g_engine_create_swap_chain;
    (void)g_engine_create_swap_chain_headless;
    (void)g_engine_destroy_swap_chain;
    (void)g_engine_create_fence;
    (void)g_engine_destroy_fence;
    (void)g_engine_create_scene;
    (void)g_engine_destroy_scene;
    (void)g_engine_create_view;
    (void)g_engine_destroy_view;
    (void)g_engine_create_camera;
    (void)g_engine_get_camera_component;
    (void)g_engine_destroy_camera_component;
    (void)g_engine_get_transform_manager;
}


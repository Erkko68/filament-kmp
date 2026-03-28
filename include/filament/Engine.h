#ifndef FILAMENT_C_ENGINE_H
#define FILAMENT_C_ENGINE_H

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Creates a filament::Engine instance
FilaEngine* FilaEngine_create();

// Destroys a filament::Engine instance
void FilaEngine_destroy(FilaEngine** engine);

// Creates a filament::Renderer instance
FilaRenderer* FilaEngine_createRenderer(FilaEngine* engine);

// Destroys a filament::Renderer instance
void FilaEngine_destroyRenderer(FilaEngine* engine, FilaRenderer* renderer);

// Creates a swap chain from a native window handle.
FilaSwapChain* FilaEngine_createSwapChain(FilaEngine* engine, void* nativeWindow, uint64_t flags);

// Creates a headless swap chain with the given size.
FilaSwapChain* FilaEngine_createSwapChainHeadless(FilaEngine* engine, uint32_t width, uint32_t height, uint64_t flags);

// Destroys a filament::SwapChain instance.
void FilaEngine_destroySwapChain(FilaEngine* engine, FilaSwapChain* swapChain);

// Creates a filament::Scene instance.
FilaScene* FilaEngine_createScene(FilaEngine* engine);

// Destroys a filament::Scene instance.
void FilaEngine_destroyScene(FilaEngine* engine, FilaScene* scene);

// Creates a filament::View instance.
FilaView* FilaEngine_createView(FilaEngine* engine);

// Destroys a filament::View instance.
void FilaEngine_destroyView(FilaEngine* engine, FilaView* view);

// Creates a camera component for the given entity.
FilaCamera* FilaEngine_createCamera(FilaEngine* engine, FilaEntity entity);

// Returns the camera component attached to the given entity, if any.
FilaCamera* FilaEngine_getCameraComponent(FilaEngine* engine, FilaEntity entity);

// Destroys the camera component attached to the given entity.
void FilaEngine_destroyCameraComponent(FilaEngine* engine, FilaEntity entity);

// Creates a filament::Fence instance.
FilaFence* FilaEngine_createFence(FilaEngine* engine);

// Destroys a filament::Fence instance.
void FilaEngine_destroyFence(FilaEngine* engine, FilaFence* fence);

// Destroys a filament::VertexBuffer instance.
void FilaEngine_destroyVertexBuffer(FilaEngine* engine, FilaVertexBuffer* vertexBuffer);

// Destroys a filament::IndexBuffer instance.
void FilaEngine_destroyIndexBuffer(FilaEngine* engine, FilaIndexBuffer* indexBuffer);

// Destroys a filament::Material instance.
void FilaEngine_destroyMaterial(FilaEngine* engine, FilaMaterial* material);

// Destroys a filament::MaterialInstance instance.
void FilaEngine_destroyMaterialInstance(FilaEngine* engine, FilaMaterialInstance* materialInstance);

// Returns the engine-owned transform manager.
FilaTransformManager* FilaEngine_getTransformManager(FilaEngine* engine);

// Returns the engine-owned light manager.
FilaLightManager* FilaEngine_getLightManager(FilaEngine* engine);

// Returns the engine-owned renderable manager.
FilaRenderableManager* FilaEngine_getRenderableManager(FilaEngine* engine);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_ENGINE_H

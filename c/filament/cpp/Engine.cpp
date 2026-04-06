#include <filament/Engine.h>
#include <filament/SwapChain.h>
#include <filament/View.h>
#include <filament/Renderer.h>
#include <filament/Camera.h>
#include <filament/Scene.h>
#include <filament/Fence.h>
#include <filament/Stream.h>
#include <filament/IndexBuffer.h>
#include <filament/VertexBuffer.h>
#include <filament/SkinningBuffer.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/IndirectLight.h>
#include <filament/Material.h>
#include <filament/MaterialInstance.h>
#include <filament/Skybox.h>
#include <filament/ColorGrading.h>
#include <filament/Texture.h>
#include <filament/RenderTarget.h>

#include <utils/Entity.h>
#include <utils/EntityManager.h>

#include "FilaCommon.h"
#include "../c/Engine.h"

using namespace filament;
using namespace utils;

extern "C" {

FilaEngineBuilder* FilaEngineBuilder_create() {
    return reinterpret_cast<FilaEngineBuilder*>(new Engine::Builder());
}

void FilaEngineBuilder_destroy(FilaEngineBuilder* builder) {
    delete reinterpret_cast<Engine::Builder*>(builder);
}

void FilaEngineBuilder_backend(FilaEngineBuilder* builder, FilaEngineBackend backend) {
    FILA_CAST(Engine::Builder, builder)->backend(static_cast<Engine::Backend>(backend));
}

void FilaEngineBuilder_config(FilaEngineBuilder* builder, const FilaEngineConfig* config) {
    if (!config) return;
    Engine::Config cppConfig = {};
    cppConfig.commandBufferSizeMB = config->commandBufferSizeMB;
    cppConfig.perRenderPassArenaSizeMB = config->perRenderPassArenaSizeMB;
    cppConfig.driverHandleArenaSizeMB = config->driverHandleArenaSizeMB;
    cppConfig.minCommandBufferSizeMB = config->minCommandBufferSizeMB;
    cppConfig.perFrameCommandsSizeMB = config->perFrameCommandsSizeMB;
    cppConfig.jobSystemThreadCount = config->jobSystemThreadCount;
    cppConfig.disableParallelShaderCompile = config->disableParallelShaderCompile;
    cppConfig.stereoscopicType = static_cast<Engine::StereoscopicType>(config->stereoscopicType);
    cppConfig.stereoscopicEyeCount = config->stereoscopicEyeCount;
    cppConfig.resourceAllocatorCacheSizeMB = config->resourceAllocatorCacheSizeMB;
    cppConfig.resourceAllocatorCacheMaxAge = config->resourceAllocatorCacheMaxAge;
    cppConfig.disableHandleUseAfterFreeCheck = config->disableHandleUseAfterFreeCheck;
    cppConfig.preferredShaderLanguage = static_cast<Engine::Config::ShaderLanguage>(config->preferredShaderLanguage);
    cppConfig.forceGLES2Context = config->forceGLES2Context;
    cppConfig.assertNativeWindowIsValid = config->assertNativeWindowIsValid;
    cppConfig.gpuContextPriority = static_cast<Engine::GpuContextPriority>(config->gpuContextPriority);
    cppConfig.sharedUboInitialSizeInBytes = config->sharedUboInitialSizeInBytes;

    FILA_CAST(Engine::Builder, builder)->config(&cppConfig);
}

void FilaEngineBuilder_featureLevel(FilaEngineBuilder* builder, FilaEngineFeatureLevel featureLevel) {
    FILA_CAST(Engine::Builder, builder)->featureLevel(static_cast<Engine::FeatureLevel>(featureLevel));
}

void FilaEngineBuilder_sharedContext(FilaEngineBuilder* builder, void* sharedContext) {
    FILA_CAST(Engine::Builder, builder)->sharedContext(sharedContext);
}

void FilaEngineBuilder_paused(FilaEngineBuilder* builder, bool paused) {
    FILA_CAST(Engine::Builder, builder)->paused(paused);
}

void FilaEngineBuilder_feature(FilaEngineBuilder* builder, const char* name, bool value) {
    FILA_CAST(Engine::Builder, builder)->feature(name, value);
}

FilaEngine* FilaEngineBuilder_build(FilaEngineBuilder* builder) {
    return reinterpret_cast<FilaEngine*>(FILA_CAST(Engine::Builder, builder)->build());
}

// Engine
void FilaEngine_destroy(FilaEngine* engine) {
    Engine* cppEngine = FILA_CAST(Engine, engine);
    Engine::destroy(&cppEngine);
}

int32_t FilaEngine_getBackend(FilaEngine* engine) {
    return static_cast<int32_t>(FILA_CAST(Engine, engine)->getBackend());
}

FilaSwapChain* FilaEngine_createSwapChain(FilaEngine* engine, void* nativeWindow, uint64_t flags) {
    return reinterpret_cast<FilaSwapChain*>(FILA_CAST(Engine, engine)->createSwapChain(nativeWindow, flags));
}

FilaSwapChain* FilaEngine_createSwapChainHeadless(FilaEngine* engine, uint32_t width, uint32_t height, uint64_t flags) {
    return reinterpret_cast<FilaSwapChain*>(FILA_CAST(Engine, engine)->createSwapChain(width, height, flags));
}

bool FilaEngine_destroySwapChain(FilaEngine* engine, FilaSwapChain* swapChain) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(SwapChain, swapChain));
}

FilaView* FilaEngine_createView(FilaEngine* engine) {
    return reinterpret_cast<FilaView*>(FILA_CAST(Engine, engine)->createView());
}

bool FilaEngine_destroyView(FilaEngine* engine, FilaView* view) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(View, view));
}

FilaRenderer* FilaEngine_createRenderer(FilaEngine* engine) {
    return reinterpret_cast<FilaRenderer*>(FILA_CAST(Engine, engine)->createRenderer());
}

bool FilaEngine_destroyRenderer(FilaEngine* engine, FilaRenderer* renderer) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(Renderer, renderer));
}

FilaCamera* FilaEngine_createCamera(FilaEngine* engine, FilaEntity entity) {
    return reinterpret_cast<FilaCamera*>(FILA_CAST(Engine, engine)->createCamera(Entity::import(entity)));
}

FilaCamera* FilaEngine_getCameraComponent(FilaEngine* engine, FilaEntity entity) {
    return reinterpret_cast<FilaCamera*>(FILA_CAST(Engine, engine)->getCameraComponent(Entity::import(entity)));
}

void FilaEngine_destroyCameraComponent(FilaEngine* engine, FilaEntity entity) {
    FILA_CAST(Engine, engine)->destroyCameraComponent(Entity::import(entity));
}

FilaScene* FilaEngine_createScene(FilaEngine* engine) {
    return reinterpret_cast<FilaScene*>(FILA_CAST(Engine, engine)->createScene());
}

bool FilaEngine_destroyScene(FilaEngine* engine, FilaScene* scene) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(Scene, scene));
}

FilaFence* FilaEngine_createFence(FilaEngine* engine) {
    return reinterpret_cast<FilaFence*>(FILA_CAST(Engine, engine)->createFence());
}

bool FilaEngine_destroyFence(FilaEngine* engine, FilaFence* fence) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(Fence, fence));
}

bool FilaEngine_destroyStream(FilaEngine* engine, FilaStream* stream) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(Stream, stream));
}

bool FilaEngine_destroyIndexBuffer(FilaEngine* engine, FilaIndexBuffer* indexBuffer) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(IndexBuffer, indexBuffer));
}

bool FilaEngine_destroyVertexBuffer(FilaEngine* engine, FilaVertexBuffer* vertexBuffer) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(VertexBuffer, vertexBuffer));
}

bool FilaEngine_destroySkinningBuffer(FilaEngine* engine, FilaSkinningBuffer* skinningBuffer) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(SkinningBuffer, skinningBuffer));
}

bool FilaEngine_destroyMorphTargetBuffer(FilaEngine* engine, FilaMorphTargetBuffer* morphTargetBuffer) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(MorphTargetBuffer, morphTargetBuffer));
}

bool FilaEngine_destroyIndirectLight(FilaEngine* engine, FilaIndirectLight* indirectLight) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(IndirectLight, indirectLight));
}

bool FilaEngine_destroyMaterial(FilaEngine* engine, FilaMaterial* material) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(Material, material));
}

bool FilaEngine_destroyMaterialInstance(FilaEngine* engine, FilaMaterialInstance* materialInstance) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(MaterialInstance, materialInstance));
}

bool FilaEngine_destroySkybox(FilaEngine* engine, FilaSkybox* skybox) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(Skybox, skybox));
}

bool FilaEngine_destroyColorGrading(FilaEngine* engine, FilaColorGrading* colorGrading) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(ColorGrading, colorGrading));
}

bool FilaEngine_destroyTexture(FilaEngine* engine, FilaTexture* texture) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(Texture, texture));
}

bool FilaEngine_destroyRenderTarget(FilaEngine* engine, FilaRenderTarget* target) {
    return FILA_CAST(Engine, engine)->destroy(FILA_CAST(RenderTarget, target));
}

void FilaEngine_destroyEntity(FilaEngine* engine, FilaEntity entity) {
    FILA_CAST(Engine, engine)->destroy(Entity::import(entity));
}

bool FilaEngine_isValidRenderer(FilaEngine* engine, FilaRenderer* renderer) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(Renderer, renderer));
}

bool FilaEngine_isValidView(FilaEngine* engine, FilaView* view) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(View, view));
}

bool FilaEngine_isValidScene(FilaEngine* engine, FilaScene* scene) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(Scene, scene));
}

bool FilaEngine_isValidFence(FilaEngine* engine, FilaFence* fence) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(Fence, fence));
}

bool FilaEngine_isValidStream(FilaEngine* engine, FilaStream* stream) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(Stream, stream));
}

bool FilaEngine_isValidIndexBuffer(FilaEngine* engine, FilaIndexBuffer* indexBuffer) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(IndexBuffer, indexBuffer));
}

bool FilaEngine_isValidVertexBuffer(FilaEngine* engine, FilaVertexBuffer* vertexBuffer) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(VertexBuffer, vertexBuffer));
}

bool FilaEngine_isValidSkinningBuffer(FilaEngine* engine, FilaSkinningBuffer* skinningBuffer) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(SkinningBuffer, skinningBuffer));
}

bool FilaEngine_isValidMorphTargetBuffer(FilaEngine* engine, FilaMorphTargetBuffer* morphTargetBuffer) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(MorphTargetBuffer, morphTargetBuffer));
}

bool FilaEngine_isValidIndirectLight(FilaEngine* engine, FilaIndirectLight* indirectLight) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(IndirectLight, indirectLight));
}

bool FilaEngine_isValidMaterial(FilaEngine* engine, FilaMaterial* material) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(Material, material));
}

bool FilaEngine_isValidMaterialInstance(FilaEngine* engine, FilaMaterial* material, FilaMaterialInstance* materialInstance) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(Material, material), FILA_CAST(MaterialInstance, materialInstance));
}

bool FilaEngine_isValidExpensiveMaterialInstance(FilaEngine* engine, FilaMaterialInstance* materialInstance) {
    return FILA_CAST(Engine, engine)->isValidExpensive(FILA_CAST(MaterialInstance, materialInstance));
}

bool FilaEngine_isValidSkybox(FilaEngine* engine, FilaSkybox* skybox) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(Skybox, skybox));
}

bool FilaEngine_isValidColorGrading(FilaEngine* engine, FilaColorGrading* colorGrading) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(ColorGrading, colorGrading));
}

bool FilaEngine_isValidTexture(FilaEngine* engine, FilaTexture* texture) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(Texture, texture));
}

bool FilaEngine_isValidRenderTarget(FilaEngine* engine, FilaRenderTarget* target) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(RenderTarget, target));
}

bool FilaEngine_isValidSwapChain(FilaEngine* engine, FilaSwapChain* swapChain) {
    return FILA_CAST(Engine, engine)->isValid(FILA_CAST(SwapChain, swapChain));
}

bool FilaEngine_flushAndWait(FilaEngine* engine, uint64_t timeout) {
    return FILA_CAST(Engine, engine)->flushAndWait(timeout);
}

void FilaEngine_flush(FilaEngine* engine) {
    FILA_CAST(Engine, engine)->flush();
}

bool FilaEngine_isPaused(FilaEngine* engine) {
    return FILA_CAST(Engine, engine)->isPaused();
}

void FilaEngine_setPaused(FilaEngine* engine, bool paused) {
    FILA_CAST(Engine, engine)->setPaused(paused);
}

void FilaEngine_unprotected(FilaEngine* engine) {
    FILA_CAST(Engine, engine)->unprotected();
}

FilaTransformManager* FilaEngine_getTransformManager(FilaEngine* engine) {
    return reinterpret_cast<FilaTransformManager*>(&FILA_CAST(Engine, engine)->getTransformManager());
}

FilaLightManager* FilaEngine_getLightManager(FilaEngine* engine) {
    return reinterpret_cast<FilaLightManager*>(&FILA_CAST(Engine, engine)->getLightManager());
}

FilaRenderableManager* FilaEngine_getRenderableManager(FilaEngine* engine) {
    return reinterpret_cast<FilaRenderableManager*>(&FILA_CAST(Engine, engine)->getRenderableManager());
}

FilaEntityManager* FilaEngine_getEntityManager(FilaEngine* engine) {
    return reinterpret_cast<FilaEntityManager*>(&FILA_CAST(Engine, engine)->getEntityManager());
}

void FilaEngine_setAutomaticInstancingEnabled(FilaEngine* engine, bool enable) {
    FILA_CAST(Engine, engine)->setAutomaticInstancingEnabled(enable);
}

bool FilaEngine_isAutomaticInstancingEnabled(FilaEngine* engine) {
    return FILA_CAST(Engine, engine)->isAutomaticInstancingEnabled();
}

size_t FilaEngine_getMaxStereoscopicEyes(FilaEngine* engine) {
    return FILA_CAST(Engine, engine)->getMaxStereoscopicEyes();
}

FilaEngineFeatureLevel FilaEngine_getSupportedFeatureLevel(FilaEngine* engine) {
    return static_cast<FilaEngineFeatureLevel>(FILA_CAST(Engine, engine)->getSupportedFeatureLevel());
}

FilaEngineFeatureLevel FilaEngine_getActiveFeatureLevel(FilaEngine* engine) {
    return static_cast<FilaEngineFeatureLevel>(FILA_CAST(Engine, engine)->getActiveFeatureLevel());
}

FilaEngineFeatureLevel FilaEngine_setActiveFeatureLevel(FilaEngine* engine, FilaEngineFeatureLevel featureLevel) {
    return static_cast<FilaEngineFeatureLevel>(FILA_CAST(Engine, engine)->setActiveFeatureLevel(static_cast<Engine::FeatureLevel>(featureLevel)));
}

bool FilaEngine_hasFeatureFlag(FilaEngine* engine, const char* name) {
    return FILA_CAST(Engine, engine)->getFeatureFlag(name).has_value();
}

void FilaEngine_setFeatureFlag(FilaEngine* engine, const char* name, bool value) {
    FILA_CAST(Engine, engine)->setFeatureFlag(name, value);
}

bool FilaEngine_getFeatureFlag(FilaEngine* engine, const char* name) {
    return FILA_CAST(Engine, engine)->getFeatureFlag(name).value_or(false);
}

uint64_t FilaEngine_getSteadyClockTimeNano() {
    return Engine::getSteadyClockTimeNano();
}

} // extern "C"

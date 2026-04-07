#ifndef FILAMENT_C_ENGINE_H
#define FILAMENT_C_ENGINE_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaEngineBackend {
    FILA_ENGINE_BACKEND_DEFAULT = 0,
    FILA_ENGINE_BACKEND_VULKAN = 1,
    FILA_ENGINE_BACKEND_OPENGL = 2,
    FILA_ENGINE_BACKEND_METAL = 3,
    FILA_ENGINE_BACKEND_NOOP = 4,
} FilaEngineBackend;

typedef enum FilaEngineStereoscopicType {
    FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED = 0,
    FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW = 1,
} FilaEngineStereoscopicType;

typedef enum FilaEngineFeatureLevel {
    FILA_ENGINE_FEATURE_LEVEL_0 = 0,
    FILA_ENGINE_FEATURE_LEVEL_1 = 1,
    FILA_ENGINE_FEATURE_LEVEL_2 = 2,
    FILA_ENGINE_FEATURE_LEVEL_3 = 3,
} FilaEngineFeatureLevel;

typedef struct FilaEngineConfig {
    uint32_t commandBufferSizeMB;
    uint32_t perRenderPassArenaSizeMB;
    uint32_t driverHandleArenaSizeMB;
    uint32_t minCommandBufferSizeMB;
    uint32_t perFrameCommandsSizeMB;
    uint32_t jobSystemThreadCount;
    bool disableParallelShaderCompile;
    FilaEngineStereoscopicType stereoscopicType;
    uint8_t stereoscopicEyeCount;
    uint32_t resourceAllocatorCacheSizeMB;
    uint8_t resourceAllocatorCacheMaxAge;
    bool disableHandleUseAfterFreeCheck;
    int32_t preferredShaderLanguage;
    bool forceGLES2Context;
    bool assertNativeWindowIsValid;
    int32_t gpuContextPriority;
    uint32_t sharedUboInitialSizeInBytes;
} FilaEngineConfig;

// Builder
typedef struct FilaEngineBuilder FilaEngineBuilder;

FilaEngineBuilder* FilaEngineBuilder_create();
void FilaEngineBuilder_destroy(FilaEngineBuilder* builder);
void FilaEngineBuilder_backend(FilaEngineBuilder* builder, FilaEngineBackend backend);
void FilaEngineBuilder_config(FilaEngineBuilder* builder, const FilaEngineConfig* config);
void FilaEngineBuilder_featureLevel(FilaEngineBuilder* builder, FilaEngineFeatureLevel featureLevel);
void FilaEngineBuilder_sharedContext(FilaEngineBuilder* builder, void* sharedContext);
void FilaEngineBuilder_paused(FilaEngineBuilder* builder, bool paused);
void FilaEngineBuilder_feature(FilaEngineBuilder* builder, const char* name, bool value);
FilaEngine* FilaEngineBuilder_build(FilaEngineBuilder* builder);

// Engine
void FilaEngine_destroy(FilaEngine* engine);
int32_t FilaEngine_getBackend(FilaEngine* engine);

FilaSwapChain* FilaEngine_createSwapChain(FilaEngine* engine, void* nativeWindow, uint64_t flags);
FilaSwapChain* FilaEngine_createSwapChainHeadless(FilaEngine* engine, uint32_t width, uint32_t height, uint64_t flags);
bool FilaEngine_destroySwapChain(FilaEngine* engine, FilaSwapChain* swapChain);

FilaView* FilaEngine_createView(FilaEngine* engine);
bool FilaEngine_destroyView(FilaEngine* engine, FilaView* view);

FilaRenderer* FilaEngine_createRenderer(FilaEngine* engine);
bool FilaEngine_destroyRenderer(FilaEngine* engine, FilaRenderer* renderer);

FilaCamera* FilaEngine_createCamera(FilaEngine* engine, FilaEntity entity);
FilaCamera* FilaEngine_createCameraAuto(FilaEngine* engine);
FilaCamera* FilaEngine_getCameraComponent(FilaEngine* engine, FilaEntity entity);
bool FilaEngine_destroyCamera(FilaEngine* engine, FilaCamera* camera);
void FilaEngine_destroyCameraComponent(FilaEngine* engine, FilaEntity entity);

FilaScene* FilaEngine_createScene(FilaEngine* engine);
bool FilaEngine_destroyScene(FilaEngine* engine, FilaScene* scene);

FilaFence* FilaEngine_createFence(FilaEngine* engine);
bool FilaEngine_destroyFence(FilaEngine* engine, FilaFence* fence);

bool FilaEngine_destroyStream(FilaEngine* engine, FilaStream* stream);
bool FilaEngine_destroyIndexBuffer(FilaEngine* engine, FilaIndexBuffer* indexBuffer);
bool FilaEngine_destroyVertexBuffer(FilaEngine* engine, FilaVertexBuffer* vertexBuffer);
bool FilaEngine_destroyBufferObject(FilaEngine* engine, FilaBufferObject* bufferObject);
bool FilaEngine_destroySkinningBuffer(FilaEngine* engine, FilaSkinningBuffer* skinningBuffer);
bool FilaEngine_destroyMorphTargetBuffer(FilaEngine* engine, FilaMorphTargetBuffer* morphTargetBuffer);
bool FilaEngine_destroyIndirectLight(FilaEngine* engine, FilaIndirectLight* indirectLight);
bool FilaEngine_destroyMaterial(FilaEngine* engine, FilaMaterial* material);
bool FilaEngine_destroyMaterialInstance(FilaEngine* engine, FilaMaterialInstance* materialInstance);
bool FilaEngine_destroySkybox(FilaEngine* engine, FilaSkybox* skybox);
bool FilaEngine_destroyColorGrading(FilaEngine* engine, FilaColorGrading* colorGrading);
bool FilaEngine_destroyTexture(FilaEngine* engine, FilaTexture* texture);
bool FilaEngine_destroyTextureSampler(FilaEngine* engine, FilaTextureSampler* sampler);
bool FilaEngine_destroyRenderTarget(FilaEngine* engine, FilaRenderTarget* target);

void FilaEngine_destroyEntity(FilaEngine* engine, FilaEntity entity);

bool FilaEngine_isValidRenderer(FilaEngine* engine, FilaRenderer* renderer);
bool FilaEngine_isValidView(FilaEngine* engine, FilaView* view);
bool FilaEngine_isValidScene(FilaEngine* engine, FilaScene* scene);
bool FilaEngine_isValidFence(FilaEngine* engine, FilaFence* fence);
bool FilaEngine_isValidStream(FilaEngine* engine, FilaStream* stream);
bool FilaEngine_isValidIndexBuffer(FilaEngine* engine, FilaIndexBuffer* indexBuffer);
bool FilaEngine_isValidVertexBuffer(FilaEngine* engine, FilaVertexBuffer* vertexBuffer);
bool FilaEngine_isValidBufferObject(FilaEngine* engine, FilaBufferObject* bufferObject);
bool FilaEngine_isValidSkinningBuffer(FilaEngine* engine, FilaSkinningBuffer* skinningBuffer);
bool FilaEngine_isValidMorphTargetBuffer(FilaEngine* engine, FilaMorphTargetBuffer* morphTargetBuffer);
bool FilaEngine_isValidIndirectLight(FilaEngine* engine, FilaIndirectLight* indirectLight);
bool FilaEngine_isValidMaterial(FilaEngine* engine, FilaMaterial* material);
bool FilaEngine_isValidMaterialInstance(FilaEngine* engine, FilaMaterial* material, FilaMaterialInstance* materialInstance);
bool FilaEngine_isValidExpensiveMaterialInstance(FilaEngine* engine, FilaMaterialInstance* materialInstance);
bool FilaEngine_isValidSkybox(FilaEngine* engine, FilaSkybox* skybox);
bool FilaEngine_isValidColorGrading(FilaEngine* engine, FilaColorGrading* colorGrading);
bool FilaEngine_isValidTexture(FilaEngine* engine, FilaTexture* texture);
bool FilaEngine_isValidRenderTarget(FilaEngine* engine, FilaRenderTarget* target);
bool FilaEngine_isValidSwapChain(FilaEngine* engine, FilaSwapChain* swapChain);

bool FilaEngine_flushAndWait(FilaEngine* engine, uint64_t timeout);
void FilaEngine_flush(FilaEngine* engine);

bool FilaEngine_isPaused(FilaEngine* engine);
void FilaEngine_setPaused(FilaEngine* engine, bool paused);
void FilaEngine_unprotected(FilaEngine* engine);

FilaTransformManager* FilaEngine_getTransformManager(FilaEngine* engine);
FilaLightManager* FilaEngine_getLightManager(FilaEngine* engine);
FilaRenderableManager* FilaEngine_getRenderableManager(FilaEngine* engine);
FilaEntityManager* FilaEngine_getEntityManager(FilaEngine* engine);

void FilaEngine_setAutomaticInstancingEnabled(FilaEngine* engine, bool enable);
bool FilaEngine_isAutomaticInstancingEnabled(FilaEngine* engine);

size_t FilaEngine_getMaxStereoscopicEyes(FilaEngine* engine);

FilaEngineFeatureLevel FilaEngine_getSupportedFeatureLevel(FilaEngine* engine);
FilaEngineFeatureLevel FilaEngine_getActiveFeatureLevel(FilaEngine* engine);
FilaEngineFeatureLevel FilaEngine_setActiveFeatureLevel(FilaEngine* engine, FilaEngineFeatureLevel featureLevel);

bool FilaEngine_hasFeatureFlag(FilaEngine* engine, const char* name);
void FilaEngine_setFeatureFlag(FilaEngine* engine, const char* name, bool value);
bool FilaEngine_getFeatureFlag(FilaEngine* engine, const char* name);

uint64_t FilaEngine_getSteadyClockTimeNano();

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_ENGINE_H

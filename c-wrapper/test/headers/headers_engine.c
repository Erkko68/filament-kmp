#include "filament/Engine.h"

// Verifies Engine header can be consumed from plain C.
void test_headers_engine(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderer* renderer = (FilaRenderer*)0;
    FilaSwapChain* swapChain = (FilaSwapChain*)0;
    FilaVertexBuffer* vertexBuffer = (FilaVertexBuffer*)0;
    FilaIndexBuffer* indexBuffer = (FilaIndexBuffer*)0;
    FilaMaterial* material = (FilaMaterial*)0;
    FilaMaterialInstance* materialInstance = (FilaMaterialInstance*)0;
    FilaTexture* texture = (FilaTexture*)0;
    FilaSkybox* skybox = (FilaSkybox*)0;
    FilaIndirectLight* indirectLight = (FilaIndirectLight*)0;
    FilaColorGrading* colorGrading = (FilaColorGrading*)0;
    FilaRenderTarget* renderTarget = (FilaRenderTarget*)0;
    FilaStream* stream = (FilaStream*)0;
    FilaSkinningBuffer* skinningBuffer = (FilaSkinningBuffer*)0;
    FilaMorphTargetBuffer* morphTargetBuffer = (FilaMorphTargetBuffer*)0;
    FilaInstanceBuffer* instanceBuffer = (FilaInstanceBuffer*)0;
    FilaBufferObject* bufferObject = (FilaBufferObject*)0;
    FilaScene* scene = (FilaScene*)0;
    FilaView* view = (FilaView*)0;
    FilaFence* fence = (FilaFence*)0;
    FilaSync* sync = (FilaSync*)0;
    FilaTransformManager* transformManager = (FilaTransformManager*)0;
    FilaLightManager* lightManager = (FilaLightManager*)0;
    FilaRenderableManager* renderableManager = (FilaRenderableManager*)0;
    FilaEntity entity = 7;
    FilaEngineConfig config;
    FilaEngineConfig readback;
    FilaBackendFeatureLevel activeLevel = FILA_BACKEND_FEATURE_LEVEL_0;
    const FilaMaterial* defaultMaterial = (const FilaMaterial*)0;
    const char* flagName = (const char*)0;
    const char* flagDescription = (const char*)0;
    bool flagValue = false;
    bool flagConstant = false;
    bool completed = false;

    FilaEngineConfig_setDefaults(&config);
    config.stereoscopicEyeCount = 2u;
    config.stereoscopicType = FILA_ENGINE_STEREOSCOPIC_TYPE_NONE;

    engine = FilaEngine_createWithConfig(&config);
    (void)FilaEngine_getMaxStereoscopicEyes();
    (void)FilaEngine_getStereoscopicEyeCount(engine);
    (void)FilaEngine_isStereoSupported(engine, FILA_ENGINE_STEREOSCOPIC_TYPE_NONE);
    (void)FilaEngine_getConfig(engine, &readback);

    (void)FilaEngine_getSupportedFeatureLevel(engine);
    (void)FilaEngine_getActiveFeatureLevel(engine);
    (void)FilaEngine_setActiveFeatureLevel(engine, FILA_BACKEND_FEATURE_LEVEL_1, &activeLevel);
    (void)FilaEngine_getMaxAutomaticInstances(engine);
    FilaEngine_setAutomaticInstancingEnabled(engine, true);
    (void)FilaEngine_isAutomaticInstancingEnabled(engine);
    (void)FilaEngine_isAsynchronousModeEnabled(engine);
    (void)FilaEngine_getBackend(engine);

    (void)FilaEngine_getBufferObjectCount(engine);
    (void)FilaEngine_getViewCount(engine);
    (void)FilaEngine_getSceneCount(engine);
    (void)FilaEngine_getSwapChainCount(engine);
    (void)FilaEngine_getStreamCount(engine);
    (void)FilaEngine_getIndexBufferCount(engine);
    (void)FilaEngine_getSkinningBufferCount(engine);
    (void)FilaEngine_getMorphTargetBufferCount(engine);
    (void)FilaEngine_getInstanceBufferCount(engine);
    (void)FilaEngine_getVertexBufferCount(engine);
    (void)FilaEngine_getIndirectLightCount(engine);
    (void)FilaEngine_getMaterialCount(engine);
    (void)FilaEngine_getTextureCount(engine);
    (void)FilaEngine_getSkyboxCount(engine);
    (void)FilaEngine_getColorGradingCount(engine);
    (void)FilaEngine_getRenderTargetCount(engine);
    (void)FilaEngine_getSteadyClockTimeNano();

    (void)FilaEngine_flush(engine);
    (void)FilaEngine_flushAndWait(engine);
    (void)FilaEngine_flushAndWaitWithTimeout(engine, 1u, &completed);

    (void)FilaEngine_isValidBufferObject(engine, bufferObject);
    (void)FilaEngine_isValidVertexBuffer(engine, vertexBuffer);
    (void)FilaEngine_isValidFence(engine, fence);
    (void)FilaEngine_isValidIndexBuffer(engine, indexBuffer);
    (void)FilaEngine_isValidSkinningBuffer(engine, skinningBuffer);
    (void)FilaEngine_isValidMorphTargetBuffer(engine, morphTargetBuffer);
    (void)FilaEngine_isValidIndirectLight(engine, indirectLight);
    (void)FilaEngine_isValidMaterial(engine, material);
    (void)FilaEngine_isValidMaterialInstanceWithMaterial(engine, material, materialInstance);
    (void)FilaEngine_isValidMaterialInstanceExpensive(engine, materialInstance);
    (void)FilaEngine_isValidRenderer(engine, renderer);
    (void)FilaEngine_isValidScene(engine, scene);
    (void)FilaEngine_isValidSkybox(engine, skybox);
    (void)FilaEngine_isValidColorGrading(engine, colorGrading);
    (void)FilaEngine_isValidSwapChain(engine, swapChain);
    (void)FilaEngine_isValidStream(engine, stream);
    (void)FilaEngine_isValidTexture(engine, texture);
    (void)FilaEngine_isValidRenderTarget(engine, renderTarget);
    (void)FilaEngine_isValidView(engine, view);
    (void)FilaEngine_isValidInstanceBuffer(engine, instanceBuffer);

    (void)FilaEngine_isPaused(engine);
    (void)FilaEngine_setPaused(engine, false);
    (void)FilaEngine_pumpMessageQueues(engine);
    (void)FilaEngine_unprotected(engine);
    (void)FilaEngine_enableAccurateTranslations(engine);
    defaultMaterial = FilaEngine_getDefaultMaterial(engine);
    (void)defaultMaterial;

    (void)FilaEngine_hasFeatureFlag(engine, "backend.disable_parallel_shader_compile");
    (void)FilaEngine_getFeatureFlag(engine, "backend.disable_parallel_shader_compile", &flagValue);
    (void)FilaEngine_setFeatureFlag(engine, "backend.disable_parallel_shader_compile", false);
    (void)FilaEngine_getFeatureFlagCount(engine);
    (void)FilaEngine_getFeatureFlagInfo(
            engine,
            0u,
            &flagName,
            &flagDescription,
            &flagValue,
            &flagConstant);
    (void)FilaEngine_execute(engine);

    swapChain = FilaEngine_createSwapChain(engine, (void*)0, 0);
    swapChain = FilaEngine_createSwapChainHeadless(engine, 640u, 480u, 0);
    sync = FilaEngine_createSync(engine);
    (void)FilaEngine_isValidSync(engine, sync);
    FilaEngine_destroyEntityComponents(engine, entity);

    FilaEngine_destroy(&engine);
    FilaEngine_destroyRenderer(engine, renderer);
    FilaEngine_destroySwapChain(engine, swapChain);
    FilaEngine_destroyVertexBuffer(engine, vertexBuffer);
    FilaEngine_destroyIndexBuffer(engine, indexBuffer);
    FilaEngine_destroyMaterial(engine, material);
    FilaEngine_destroyMaterialInstance(engine, materialInstance);
    FilaEngine_destroyTexture(engine, texture);
    FilaEngine_destroySkybox(engine, skybox);
    FilaEngine_destroyIndirectLight(engine, indirectLight);
    FilaEngine_destroyColorGrading(engine, colorGrading);
    FilaEngine_destroyRenderTarget(engine, renderTarget);
    FilaEngine_destroyStream(engine, stream);
    FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
    FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
    FilaEngine_destroyInstanceBuffer(engine, instanceBuffer);
    FilaEngine_destroyBufferObject(engine, bufferObject);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroyView(engine, view);
    FilaEngine_destroyFence(engine, fence);
    FilaEngine_destroySync(engine, sync);
    FilaEngine_destroyCameraComponent(engine, entity);

    transformManager = FilaEngine_getTransformManager(engine);
    lightManager = FilaEngine_getLightManager(engine);
    renderableManager = FilaEngine_getRenderableManager(engine);
    (void)transformManager;
    (void)lightManager;
    (void)renderableManager;
}

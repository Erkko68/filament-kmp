#include <filament/Engine.h>
#include <filament/DebugRegistry.h>
#include <filament/Fence.h>
#include <filament/IndexBuffer.h>
#include <filament/InstanceBuffer.h>
#include <filament/IndirectLight.h>
#include <filament/LightManager.h>
#include <filament/Material.h>
#include <filament/MaterialInstance.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/Renderer.h>
#include <filament/RenderableManager.h>
#include <filament/Scene.h>
#include <filament/SkinningBuffer.h>
#include <filament/Skybox.h>
#include <filament/Stream.h>
#include <filament/SwapChain.h>
#include <filament/Sync.h>
#include <filament/Texture.h>
#include <filament/TransformManager.h>
#include <filament/VertexBuffer.h>
#include <filament/View.h>
#include <filament/BufferObject.h>

#include <utils/Entity.h>

#include "../../include/filament/Engine.h" // Our C Header
#include "../../include/filament/Types.h"  // Our C Types

void FilaMaterialInstance_clearCachedBoolParameters(const filament::MaterialInstance* materialInstance);
void FilaMaterialInstance_clearAllCachedBoolParameters(void);

namespace {
utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}

filament::Engine::StereoscopicType toStereoscopicType(FilaEngineStereoscopicType type) {
    switch (type) {
        case FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED:
            return filament::Engine::StereoscopicType::INSTANCED;
        case FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW:
            return filament::Engine::StereoscopicType::MULTIVIEW;
        case FILA_ENGINE_STEREOSCOPIC_TYPE_NONE:
        default:
            return filament::Engine::StereoscopicType::NONE;
    }
}

FilaEngineStereoscopicType fromStereoscopicType(filament::Engine::StereoscopicType type) {
    switch (type) {
        case filament::Engine::StereoscopicType::INSTANCED:
            return FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED;
        case filament::Engine::StereoscopicType::MULTIVIEW:
            return FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW;
        case filament::Engine::StereoscopicType::NONE:
        default:
            return FILA_ENGINE_STEREOSCOPIC_TYPE_NONE;
    }
}

filament::Engine::FeatureLevel toFeatureLevel(FilaBackendFeatureLevel level) {
    switch (level) {
        case FILA_BACKEND_FEATURE_LEVEL_3:
            return filament::Engine::FeatureLevel::FEATURE_LEVEL_3;
        case FILA_BACKEND_FEATURE_LEVEL_2:
            return filament::Engine::FeatureLevel::FEATURE_LEVEL_2;
        case FILA_BACKEND_FEATURE_LEVEL_1:
            return filament::Engine::FeatureLevel::FEATURE_LEVEL_1;
        case FILA_BACKEND_FEATURE_LEVEL_0:
        default:
            return filament::Engine::FeatureLevel::FEATURE_LEVEL_0;
    }
}

void applyConfig(const FilaEngineConfig* config, filament::Engine::Config* outConfig) {
    if (!outConfig) {
        return;
    }
    if (!config) {
        *outConfig = filament::Engine::Config{};
        return;
    }
    outConfig->stereoscopicType = toStereoscopicType(config->stereoscopicType);
    outConfig->stereoscopicEyeCount = config->stereoscopicEyeCount == 0 ? 1 : config->stereoscopicEyeCount;
}


FilaBackendFeatureLevel fromFeatureLevel(filament::Engine::FeatureLevel level) {
    switch (level) {
        case filament::Engine::FeatureLevel::FEATURE_LEVEL_3:
            return FILA_BACKEND_FEATURE_LEVEL_3;
        case filament::Engine::FeatureLevel::FEATURE_LEVEL_2:
            return FILA_BACKEND_FEATURE_LEVEL_2;
        case filament::Engine::FeatureLevel::FEATURE_LEVEL_1:
            return FILA_BACKEND_FEATURE_LEVEL_1;
        case filament::Engine::FeatureLevel::FEATURE_LEVEL_0:
        default:
            return FILA_BACKEND_FEATURE_LEVEL_0;
    }
}

FilaBackendBackend fromBackend(filament::Engine::Backend backend) {
    switch (backend) {
        case filament::Engine::Backend::OPENGL:
            return FILA_BACKEND_OPENGL;
        case filament::Engine::Backend::VULKAN:
            return FILA_BACKEND_VULKAN;
        case filament::Engine::Backend::METAL:
            return FILA_BACKEND_METAL;
        case filament::Engine::Backend::WEBGPU:
            return FILA_BACKEND_WEBGPU;
        case filament::Engine::Backend::NOOP:
            return FILA_BACKEND_NOOP;
        case filament::Engine::Backend::DEFAULT:
        default:
            return FILA_BACKEND_DEFAULT;
    }
}
} // namespace

extern "C" {

void FilaEngineConfig_setDefaults(FilaEngineConfig* outConfig) {
    if (!outConfig) {
        return;
    }
    const filament::Engine::Config defaults;
    outConfig->stereoscopicType = static_cast<FilaEngineStereoscopicType>(defaults.stereoscopicType);
    outConfig->stereoscopicEyeCount = defaults.stereoscopicEyeCount;
}

FilaEngine* FilaEngine_create() {
    return reinterpret_cast<FilaEngine*>(filament::Engine::create());
}

FilaEngine* FilaEngine_createWithConfig(const FilaEngineConfig* config) {
    filament::Engine::Config cppConfig;
    applyConfig(config, &cppConfig);
    return reinterpret_cast<FilaEngine*>(filament::Engine::create(
            filament::Engine::Backend::DEFAULT,
            nullptr,
            nullptr,
            &cppConfig));
}

size_t FilaEngine_getMaxStereoscopicEyes(void) {
    return filament::Engine::getMaxStereoscopicEyes();
}

uint8_t FilaEngine_getStereoscopicEyeCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return cppEngine->getConfig().stereoscopicEyeCount;
}

bool FilaEngine_isStereoSupported(const FilaEngine* engine, FilaEngineStereoscopicType type) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return cppEngine->isStereoSupported(toStereoscopicType(type));
}

bool FilaEngine_getConfig(const FilaEngine* engine, FilaEngineConfig* outConfig) {
    if (!engine || !outConfig) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    const auto& config = cppEngine->getConfig();
    outConfig->stereoscopicType = fromStereoscopicType(config.stereoscopicType);
    outConfig->stereoscopicEyeCount = config.stereoscopicEyeCount;
    return true;
}

FilaBackendFeatureLevel FilaEngine_getSupportedFeatureLevel(const FilaEngine* engine) {
    if (!engine) {
        return FILA_BACKEND_FEATURE_LEVEL_0;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return fromFeatureLevel(cppEngine->getSupportedFeatureLevel());
}

FilaBackendFeatureLevel FilaEngine_getActiveFeatureLevel(const FilaEngine* engine) {
    if (!engine) {
        return FILA_BACKEND_FEATURE_LEVEL_0;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return fromFeatureLevel(cppEngine->getActiveFeatureLevel());
}

bool FilaEngine_setActiveFeatureLevel(
        FilaEngine* engine,
        FilaBackendFeatureLevel featureLevel,
        FilaBackendFeatureLevel* outActiveFeatureLevel) {
    if (!engine || !outActiveFeatureLevel) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    try {
        const auto active = cppEngine->setActiveFeatureLevel(toFeatureLevel(featureLevel));
        *outActiveFeatureLevel = fromFeatureLevel(active);
        return true;
    } catch (...) {
        return false;
    }
}

size_t FilaEngine_getMaxAutomaticInstances(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return cppEngine->getMaxAutomaticInstances();
}

void FilaEngine_setAutomaticInstancingEnabled(FilaEngine* engine, bool enabled) {
    if (!engine) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->setAutomaticInstancingEnabled(enabled);
}

bool FilaEngine_isAutomaticInstancingEnabled(const FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return cppEngine->isAutomaticInstancingEnabled();
}

bool FilaEngine_isAsynchronousModeEnabled(const FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return cppEngine->isAsynchronousModeEnabled();
}

FilaBackendBackend FilaEngine_getBackend(const FilaEngine* engine) {
    if (!engine) {
        return FILA_BACKEND_DEFAULT;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return fromBackend(cppEngine->getBackend());
}

size_t FilaEngine_getBufferObjectCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getBufferObjectCount();
}

size_t FilaEngine_getViewCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getViewCount();
}

size_t FilaEngine_getSceneCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getSceneCount();
}

size_t FilaEngine_getSwapChainCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getSwapChainCount();
}

size_t FilaEngine_getStreamCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getStreamCount();
}

size_t FilaEngine_getIndexBufferCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getIndexBufferCount();
}

size_t FilaEngine_getSkinningBufferCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getSkinningBufferCount();
}

size_t FilaEngine_getMorphTargetBufferCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getMorphTargetBufferCount();
}

size_t FilaEngine_getInstanceBufferCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getInstanceBufferCount();
}

size_t FilaEngine_getVertexBufferCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getVertexBufferCount();
}

size_t FilaEngine_getIndirectLightCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getIndirectLightCount();
}

size_t FilaEngine_getMaterialCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getMaterialCount();
}

size_t FilaEngine_getTextureCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getTextureCount();
}

size_t FilaEngine_getSkyboxCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getSkyboxeCount();
}

size_t FilaEngine_getColorGradingCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getColorGradingCount();
}

size_t FilaEngine_getRenderTargetCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    return reinterpret_cast<const filament::Engine*>(engine)->getRenderTargetCount();
}

uint64_t FilaEngine_getSteadyClockTimeNano(void) {
    return filament::Engine::getSteadyClockTimeNano();
}

bool FilaEngine_flush(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    try {
        cppEngine->flush();
        return true;
    } catch (...) {
        return false;
    }
}

bool FilaEngine_flushAndWait(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    try {
        cppEngine->flushAndWait();
        return true;
    } catch (...) {
        return false;
    }
}

bool FilaEngine_flushAndWaitWithTimeout(FilaEngine* engine, uint64_t timeoutNs, bool* outCompleted) {
    if (!engine || !outCompleted) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    try {
        *outCompleted = cppEngine->flushAndWait(timeoutNs);
        return true;
    } catch (...) {
        return false;
    }
}

bool FilaEngine_isValidBufferObject(const FilaEngine* engine, const FilaBufferObject* bufferObject) {
    if (!engine || !bufferObject) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppBufferObject = reinterpret_cast<const filament::BufferObject*>(bufferObject);
    return cppEngine->isValid(cppBufferObject);
}

bool FilaEngine_isValidVertexBuffer(const FilaEngine* engine, const FilaVertexBuffer* vertexBuffer) {
    if (!engine || !vertexBuffer) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppVertexBuffer = reinterpret_cast<const filament::VertexBuffer*>(vertexBuffer);
    return cppEngine->isValid(cppVertexBuffer);
}

bool FilaEngine_isValidFence(const FilaEngine* engine, const FilaFence* fence) {
    if (!engine || !fence) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppFence = reinterpret_cast<const filament::Fence*>(fence);
    return cppEngine->isValid(cppFence);
}

bool FilaEngine_isValidSync(const FilaEngine* engine, const FilaSync* sync) {
    if (!engine || !sync) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppSync = reinterpret_cast<const filament::Sync*>(sync);
    return cppEngine->isValid(cppSync);
}

bool FilaEngine_isValidIndexBuffer(const FilaEngine* engine, const FilaIndexBuffer* indexBuffer) {
    if (!engine || !indexBuffer) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppIndexBuffer = reinterpret_cast<const filament::IndexBuffer*>(indexBuffer);
    return cppEngine->isValid(cppIndexBuffer);
}

bool FilaEngine_isValidSkinningBuffer(const FilaEngine* engine, const FilaSkinningBuffer* skinningBuffer) {
    if (!engine || !skinningBuffer) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppSkinningBuffer = reinterpret_cast<const filament::SkinningBuffer*>(skinningBuffer);
    return cppEngine->isValid(cppSkinningBuffer);
}

bool FilaEngine_isValidMorphTargetBuffer(const FilaEngine* engine, const FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!engine || !morphTargetBuffer) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppMorphTargetBuffer = reinterpret_cast<const filament::MorphTargetBuffer*>(morphTargetBuffer);
    return cppEngine->isValid(cppMorphTargetBuffer);
}

bool FilaEngine_isValidIndirectLight(const FilaEngine* engine, const FilaIndirectLight* indirectLight) {
    if (!engine || !indirectLight) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppIndirectLight = reinterpret_cast<const filament::IndirectLight*>(indirectLight);
    return cppEngine->isValid(cppIndirectLight);
}

bool FilaEngine_isValidMaterial(const FilaEngine* engine, const FilaMaterial* material) {
    if (!engine || !material) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppEngine->isValid(cppMaterial);
}

bool FilaEngine_isValidMaterialInstanceWithMaterial(
        const FilaEngine* engine,
        const FilaMaterial* material,
        const FilaMaterialInstance* materialInstance) {
    if (!engine || !material || !materialInstance) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppEngine->isValid(cppMaterial, cppMaterialInstance);
}

bool FilaEngine_isValidMaterialInstanceExpensive(
        const FilaEngine* engine,
        const FilaMaterialInstance* materialInstance) {
    if (!engine || !materialInstance) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppEngine->isValidExpensive(cppMaterialInstance);
}

bool FilaEngine_isValidRenderer(const FilaEngine* engine, const FilaRenderer* renderer) {
    if (!engine || !renderer) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppRenderer = reinterpret_cast<const filament::Renderer*>(renderer);
    return cppEngine->isValid(cppRenderer);
}

bool FilaEngine_isValidScene(const FilaEngine* engine, const FilaScene* scene) {
    if (!engine || !scene) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppScene = reinterpret_cast<const filament::Scene*>(scene);
    return cppEngine->isValid(cppScene);
}

bool FilaEngine_isValidSkybox(const FilaEngine* engine, const FilaSkybox* skybox) {
    if (!engine || !skybox) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppSkybox = reinterpret_cast<const filament::Skybox*>(skybox);
    return cppEngine->isValid(cppSkybox);
}

bool FilaEngine_isValidColorGrading(const FilaEngine* engine, const FilaColorGrading* colorGrading) {
    if (!engine || !colorGrading) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppColorGrading = reinterpret_cast<const filament::ColorGrading*>(colorGrading);
    return cppEngine->isValid(cppColorGrading);
}

bool FilaEngine_isValidSwapChain(const FilaEngine* engine, const FilaSwapChain* swapChain) {
    if (!engine || !swapChain) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppSwapChain = reinterpret_cast<const filament::SwapChain*>(swapChain);
    return cppEngine->isValid(cppSwapChain);
}

bool FilaEngine_isValidStream(const FilaEngine* engine, const FilaStream* stream) {
    if (!engine || !stream) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppStream = reinterpret_cast<const filament::Stream*>(stream);
    return cppEngine->isValid(cppStream);
}

bool FilaEngine_isValidTexture(const FilaEngine* engine, const FilaTexture* texture) {
    if (!engine || !texture) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    return cppEngine->isValid(cppTexture);
}

bool FilaEngine_isValidRenderTarget(const FilaEngine* engine, const FilaRenderTarget* renderTarget) {
    if (!engine || !renderTarget) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppRenderTarget = reinterpret_cast<const filament::RenderTarget*>(renderTarget);
    return cppEngine->isValid(cppRenderTarget);
}

bool FilaEngine_isValidView(const FilaEngine* engine, const FilaView* view) {
    if (!engine || !view) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppEngine->isValid(cppView);
}

bool FilaEngine_isValidInstanceBuffer(const FilaEngine* engine, const FilaInstanceBuffer* instanceBuffer) {
    if (!engine || !instanceBuffer) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    auto cppInstanceBuffer = reinterpret_cast<const filament::InstanceBuffer*>(instanceBuffer);
    return cppEngine->isValid(cppInstanceBuffer);
}

void FilaEngine_destroyEntityComponents(FilaEngine* engine, FilaEntity entity) {
    if (!engine) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->destroy(toEntity(entity));
}

bool FilaEngine_isPaused(const FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return cppEngine->isPaused();
}

bool FilaEngine_setPaused(FilaEngine* engine, bool paused) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->setPaused(paused);
    return true;
}

bool FilaEngine_pumpMessageQueues(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->pumpMessageQueues();
    return true;
}

bool FilaEngine_unprotected(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->unprotected();
    return true;
}

bool FilaEngine_enableAccurateTranslations(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->enableAccurateTranslations();
    return true;
}

const FilaMaterial* FilaEngine_getDefaultMaterial(const FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return reinterpret_cast<const FilaMaterial*>(cppEngine->getDefaultMaterial());
}

bool FilaEngine_hasFeatureFlag(const FilaEngine* engine, const char* name) {
    if (!engine || !name) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(const_cast<FilaEngine*>(engine));
    return cppEngine->hasFeatureFlag(name);
}

bool FilaEngine_getFeatureFlag(const FilaEngine* engine, const char* name, bool* outValue) {
    if (!engine || !name || !outValue) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    const auto value = cppEngine->getFeatureFlag(name);
    if (!value.has_value()) {
        return false;
    }
    *outValue = value.value();
    return true;
}

bool FilaEngine_setFeatureFlag(FilaEngine* engine, const char* name, bool value) {
    if (!engine || !name) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return cppEngine->setFeatureFlag(name, value);
}

bool* FilaEngine_getFeatureFlagPtr(const FilaEngine* engine, const char* name) {
    if (!engine || !name) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return cppEngine->getFeatureFlagPtr(name);
}

bool FilaEngine_cancelAsyncCall(FilaEngine* engine, FilaBackendAsyncCallId id) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return cppEngine->cancelAsyncCall(static_cast<filament::Engine::AsyncCallId>(id));
}

bool FilaEngine_resetBackendState(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
#if defined(__EMSCRIPTEN__)
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->resetBackendState();
    return true;
#else
    return false;
#endif
}

size_t FilaEngine_getFeatureFlagCount(const FilaEngine* engine) {
    if (!engine) {
        return 0u;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    return cppEngine->getFeatureFlags().size();
}

bool FilaEngine_getFeatureFlagInfo(
        const FilaEngine* engine,
        size_t index,
        const char** outName,
        const char** outDescription,
        bool* outValue,
        bool* outConstant) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<const filament::Engine*>(engine);
    const auto flags = cppEngine->getFeatureFlags();
    if (index >= flags.size()) {
        return false;
    }

    const auto& flag = flags[index];
    if (outName) {
        *outName = flag.name;
    }
    if (outDescription) {
        *outDescription = flag.description;
    }
    if (outValue) {
        *outValue = *flag.value;
    }
    if (outConstant) {
        *outConstant = flag.constant;
    }
    return true;
}

bool FilaEngine_execute(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    try {
        cppEngine->execute();
        return true;
    } catch (...) {
        return false;
    }
}

void FilaEngine_destroy(FilaEngine** engine) {
    if (engine && *engine) {
        FilaMaterialInstance_clearAllCachedBoolParameters();
        auto cppEngine = reinterpret_cast<filament::Engine*>(*engine);
        filament::Engine::destroy(&cppEngine);
        *engine = nullptr;
    }
}

FilaRenderer* FilaEngine_createRenderer(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaRenderer*>(cppEngine->createRenderer());
}

void FilaEngine_destroyRenderer(FilaEngine* engine, FilaRenderer* renderer) {
    if (!engine || !renderer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppEngine->destroy(cppRenderer);
}

FilaSwapChain* FilaEngine_createSwapChain(FilaEngine* engine, void* nativeWindow, uint64_t flags) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaSwapChain*>(cppEngine->createSwapChain(nativeWindow, flags));
}

FilaSwapChain* FilaEngine_createSwapChainHeadless(FilaEngine* engine, uint32_t width, uint32_t height, uint64_t flags) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaSwapChain*>(cppEngine->createSwapChain(width, height, flags));
}

void FilaEngine_destroySwapChain(FilaEngine* engine, FilaSwapChain* swapChain) {
    if (!engine || !swapChain) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppSwapChain = reinterpret_cast<filament::SwapChain*>(swapChain);
    cppEngine->destroy(cppSwapChain);
}

FilaScene* FilaEngine_createScene(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaScene*>(cppEngine->createScene());
}

void FilaEngine_destroyScene(FilaEngine* engine, FilaScene* scene) {
    if (!engine || !scene) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppEngine->destroy(cppScene);
}

FilaView* FilaEngine_createView(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaView*>(cppEngine->createView());
}

void FilaEngine_destroyView(FilaEngine* engine, FilaView* view) {
    if (!engine || !view) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppEngine->destroy(cppView);
}

FilaCamera* FilaEngine_createCamera(FilaEngine* engine, FilaEntity entity) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaCamera*>(cppEngine->createCamera(toEntity(entity)));
}

FilaCamera* FilaEngine_getCameraComponent(FilaEngine* engine, FilaEntity entity) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaCamera*>(cppEngine->getCameraComponent(toEntity(entity)));
}

void FilaEngine_destroyCameraComponent(FilaEngine* engine, FilaEntity entity) {
    if (!engine) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->destroyCameraComponent(toEntity(entity));
}

FilaFence* FilaEngine_createFence(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaFence*>(cppEngine->createFence());
}

FilaSync* FilaEngine_createSync(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaSync*>(cppEngine->createSync());
}

void FilaEngine_destroyFence(FilaEngine* engine, FilaFence* fence) {
    if (!engine || !fence) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppFence = reinterpret_cast<filament::Fence*>(fence);
    cppEngine->destroy(cppFence);
}

void FilaEngine_destroySync(FilaEngine* engine, FilaSync* sync) {
    if (!engine || !sync) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppSync = reinterpret_cast<filament::Sync*>(sync);
    cppEngine->destroy(cppSync);
}

void FilaEngine_destroyVertexBuffer(FilaEngine* engine, FilaVertexBuffer* vertexBuffer) {
    if (!engine || !vertexBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppVertexBuffer = reinterpret_cast<filament::VertexBuffer*>(vertexBuffer);
    cppEngine->destroy(cppVertexBuffer);
}

void FilaEngine_destroyIndexBuffer(FilaEngine* engine, FilaIndexBuffer* indexBuffer) {
    if (!engine || !indexBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppIndexBuffer = reinterpret_cast<filament::IndexBuffer*>(indexBuffer);
    cppEngine->destroy(cppIndexBuffer);
}

void FilaEngine_destroyMaterial(FilaEngine* engine, FilaMaterial* material) {
    if (!engine || !material) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppMaterial = reinterpret_cast<filament::Material*>(material);
    cppEngine->destroy(cppMaterial);
}

void FilaEngine_destroyMaterialInstance(FilaEngine* engine, FilaMaterialInstance* materialInstance) {
    if (!engine || !materialInstance) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    FilaMaterialInstance_clearCachedBoolParameters(cppMaterialInstance);
    cppEngine->destroy(cppMaterialInstance);
}

void FilaEngine_destroyTexture(FilaEngine* engine, FilaTexture* texture) {
    if (!engine || !texture) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    cppEngine->destroy(cppTexture);
}

void FilaEngine_destroySkybox(FilaEngine* engine, FilaSkybox* skybox) {
    if (!engine || !skybox) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppSkybox = reinterpret_cast<filament::Skybox*>(skybox);
    cppEngine->destroy(cppSkybox);
}

void FilaEngine_destroyIndirectLight(FilaEngine* engine, FilaIndirectLight* indirectLight) {
    if (!engine || !indirectLight) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppIndirectLight = reinterpret_cast<filament::IndirectLight*>(indirectLight);
    cppEngine->destroy(cppIndirectLight);
}

void FilaEngine_destroyColorGrading(FilaEngine* engine, FilaColorGrading* colorGrading) {
    if (!engine || !colorGrading) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppColorGrading = reinterpret_cast<filament::ColorGrading*>(colorGrading);
    cppEngine->destroy(cppColorGrading);
}

void FilaEngine_destroyRenderTarget(FilaEngine* engine, FilaRenderTarget* renderTarget) {
    if (!engine || !renderTarget) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppRenderTarget = reinterpret_cast<filament::RenderTarget*>(renderTarget);
    cppEngine->destroy(cppRenderTarget);
}

void FilaEngine_destroyStream(FilaEngine* engine, FilaStream* stream) {
    if (!engine || !stream) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppStream = reinterpret_cast<filament::Stream*>(stream);
    cppEngine->destroy(cppStream);
}

void FilaEngine_destroySkinningBuffer(FilaEngine* engine, FilaSkinningBuffer* skinningBuffer) {
    if (!engine || !skinningBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppSkinningBuffer = reinterpret_cast<filament::SkinningBuffer*>(skinningBuffer);
    cppEngine->destroy(cppSkinningBuffer);
}

void FilaEngine_destroyMorphTargetBuffer(FilaEngine* engine, FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!engine || !morphTargetBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppMorphTargetBuffer = reinterpret_cast<filament::MorphTargetBuffer*>(morphTargetBuffer);
    cppEngine->destroy(cppMorphTargetBuffer);
}

void FilaEngine_destroyInstanceBuffer(FilaEngine* engine, FilaInstanceBuffer* instanceBuffer) {
    if (!engine || !instanceBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppInstanceBuffer = reinterpret_cast<filament::InstanceBuffer*>(instanceBuffer);
    cppEngine->destroy(cppInstanceBuffer);
}

void FilaEngine_destroyBufferObject(FilaEngine* engine, FilaBufferObject* bufferObject) {
    if (!engine || !bufferObject) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBufferObject = reinterpret_cast<filament::BufferObject*>(bufferObject);
    cppEngine->destroy(cppBufferObject);
}

FilaTransformManager* FilaEngine_getTransformManager(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaTransformManager*>(&cppEngine->getTransformManager());
}

FilaDebugRegistry* FilaEngine_getDebugRegistry(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaDebugRegistry*>(&cppEngine->getDebugRegistry());
}

FilaLightManager* FilaEngine_getLightManager(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaLightManager*>(&cppEngine->getLightManager());
}

FilaRenderableManager* FilaEngine_getRenderableManager(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaRenderableManager*>(&cppEngine->getRenderableManager());
}

}

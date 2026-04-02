#ifndef FILAMENT_C_ENGINE_H
#define FILAMENT_C_ENGINE_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "../backend/DriverEnums.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaEngineStereoscopicType {
	FILA_ENGINE_STEREOSCOPIC_TYPE_NONE = 0,
	FILA_ENGINE_STEREOSCOPIC_TYPE_INSTANCED = 1,
	FILA_ENGINE_STEREOSCOPIC_TYPE_MULTIVIEW = 2,
} FilaEngineStereoscopicType;

typedef struct FilaEngineConfig {
	FilaEngineStereoscopicType stereoscopicType;
	uint8_t stereoscopicEyeCount;
} FilaEngineConfig;

// Populates config with Filament default values for exposed fields.
void FilaEngineConfig_setDefaults(FilaEngineConfig* outConfig);

// Creates a filament::Engine instance
FilaEngine* FilaEngine_create();

// Creates a filament::Engine instance with selected config fields.
FilaEngine* FilaEngine_createWithConfig(const FilaEngineConfig* config);

// Returns max stereoscopic eyes supported by Filament.
size_t FilaEngine_getMaxStereoscopicEyes(void);

// Returns this engine's configured stereoscopic eye count.
uint8_t FilaEngine_getStereoscopicEyeCount(const FilaEngine* engine);

// Returns true if this engine/backend supports the requested stereoscopic technique.
bool FilaEngine_isStereoSupported(const FilaEngine* engine, FilaEngineStereoscopicType type);

// Copies this engine's exposed config fields.
bool FilaEngine_getConfig(const FilaEngine* engine, FilaEngineConfig* outConfig);

// Returns backend capability levels for this engine.
FilaBackendFeatureLevel FilaEngine_getSupportedFeatureLevel(const FilaEngine* engine);
FilaBackendFeatureLevel FilaEngine_getActiveFeatureLevel(const FilaEngine* engine);
bool FilaEngine_setActiveFeatureLevel(
		FilaEngine* engine,
		FilaBackendFeatureLevel featureLevel,
		FilaBackendFeatureLevel* outActiveFeatureLevel);

// Automatic instancing controls and related query helpers.
size_t FilaEngine_getMaxAutomaticInstances(const FilaEngine* engine);
void FilaEngine_setAutomaticInstancingEnabled(FilaEngine* engine, bool enabled);
bool FilaEngine_isAutomaticInstancingEnabled(const FilaEngine* engine);

// Runtime mode/state queries.
bool FilaEngine_isAsynchronousModeEnabled(const FilaEngine* engine);
FilaBackendBackend FilaEngine_getBackend(const FilaEngine* engine);

// Resource tracking counters.
size_t FilaEngine_getBufferObjectCount(const FilaEngine* engine);
size_t FilaEngine_getViewCount(const FilaEngine* engine);
size_t FilaEngine_getSceneCount(const FilaEngine* engine);
size_t FilaEngine_getSwapChainCount(const FilaEngine* engine);
size_t FilaEngine_getStreamCount(const FilaEngine* engine);
size_t FilaEngine_getIndexBufferCount(const FilaEngine* engine);
size_t FilaEngine_getSkinningBufferCount(const FilaEngine* engine);
size_t FilaEngine_getMorphTargetBufferCount(const FilaEngine* engine);
size_t FilaEngine_getInstanceBufferCount(const FilaEngine* engine);
size_t FilaEngine_getVertexBufferCount(const FilaEngine* engine);
size_t FilaEngine_getIndirectLightCount(const FilaEngine* engine);
size_t FilaEngine_getMaterialCount(const FilaEngine* engine);
size_t FilaEngine_getTextureCount(const FilaEngine* engine);
size_t FilaEngine_getSkyboxCount(const FilaEngine* engine);
size_t FilaEngine_getColorGradingCount(const FilaEngine* engine);
size_t FilaEngine_getRenderTargetCount(const FilaEngine* engine);

// Returns current monotonic clock time in nanoseconds.
uint64_t FilaEngine_getSteadyClockTimeNano(void);

// Command stream control helpers.
bool FilaEngine_flush(FilaEngine* engine);
bool FilaEngine_flushAndWait(FilaEngine* engine);
bool FilaEngine_flushAndWaitWithTimeout(FilaEngine* engine, uint64_t timeoutNs, bool* outCompleted);

// Validity checks for engine-managed objects.
bool FilaEngine_isValidBufferObject(const FilaEngine* engine, const FilaBufferObject* bufferObject);
bool FilaEngine_isValidVertexBuffer(const FilaEngine* engine, const FilaVertexBuffer* vertexBuffer);
bool FilaEngine_isValidFence(const FilaEngine* engine, const FilaFence* fence);
bool FilaEngine_isValidIndexBuffer(const FilaEngine* engine, const FilaIndexBuffer* indexBuffer);
bool FilaEngine_isValidSkinningBuffer(const FilaEngine* engine, const FilaSkinningBuffer* skinningBuffer);
bool FilaEngine_isValidMorphTargetBuffer(const FilaEngine* engine, const FilaMorphTargetBuffer* morphTargetBuffer);
bool FilaEngine_isValidIndirectLight(const FilaEngine* engine, const FilaIndirectLight* indirectLight);
bool FilaEngine_isValidMaterial(const FilaEngine* engine, const FilaMaterial* material);
bool FilaEngine_isValidMaterialInstanceWithMaterial(
		const FilaEngine* engine,
		const FilaMaterial* material,
		const FilaMaterialInstance* materialInstance);
bool FilaEngine_isValidMaterialInstanceExpensive(
		const FilaEngine* engine,
		const FilaMaterialInstance* materialInstance);
bool FilaEngine_isValidRenderer(const FilaEngine* engine, const FilaRenderer* renderer);
bool FilaEngine_isValidScene(const FilaEngine* engine, const FilaScene* scene);
bool FilaEngine_isValidSkybox(const FilaEngine* engine, const FilaSkybox* skybox);
bool FilaEngine_isValidColorGrading(const FilaEngine* engine, const FilaColorGrading* colorGrading);
bool FilaEngine_isValidSwapChain(const FilaEngine* engine, const FilaSwapChain* swapChain);
bool FilaEngine_isValidStream(const FilaEngine* engine, const FilaStream* stream);
bool FilaEngine_isValidTexture(const FilaEngine* engine, const FilaTexture* texture);
bool FilaEngine_isValidRenderTarget(const FilaEngine* engine, const FilaRenderTarget* renderTarget);
bool FilaEngine_isValidView(const FilaEngine* engine, const FilaView* view);
bool FilaEngine_isValidInstanceBuffer(const FilaEngine* engine, const FilaInstanceBuffer* instanceBuffer);

// Destroys all Filament components owned by the given entity.
void FilaEngine_destroyEntityComponents(FilaEngine* engine, FilaEntity entity);

// Runtime control helpers.
bool FilaEngine_isPaused(const FilaEngine* engine);
bool FilaEngine_setPaused(FilaEngine* engine, bool paused);
bool FilaEngine_pumpMessageQueues(FilaEngine* engine);
bool FilaEngine_unprotected(FilaEngine* engine);
bool FilaEngine_enableAccurateTranslations(FilaEngine* engine);

// Engine-owned default material singleton.
const FilaMaterial* FilaEngine_getDefaultMaterial(const FilaEngine* engine);

// Sync lifecycle and validity.
FilaSync* FilaEngine_createSync(FilaEngine* engine);
void FilaEngine_destroySync(FilaEngine* engine, FilaSync* sync);
bool FilaEngine_isValidSync(const FilaEngine* engine, const FilaSync* sync);

// Feature-flag helpers.
bool FilaEngine_hasFeatureFlag(const FilaEngine* engine, const char* name);
bool FilaEngine_getFeatureFlag(const FilaEngine* engine, const char* name, bool* outValue);
bool FilaEngine_setFeatureFlag(FilaEngine* engine, const char* name, bool value);
bool* FilaEngine_getFeatureFlagPtr(const FilaEngine* engine, const char* name);

// Cancels a pending asynchronous operation by id.
bool FilaEngine_cancelAsyncCall(FilaEngine* engine, FilaBackendAsyncCallId id);

// WebGL-only backend-state reset helper. Returns false when unsupported.
bool FilaEngine_resetBackendState(FilaEngine* engine);

// Feature-flag introspection helpers.
size_t FilaEngine_getFeatureFlagCount(const FilaEngine* engine);
bool FilaEngine_getFeatureFlagInfo(
		const FilaEngine* engine,
		size_t index,
		const char** outName,
		const char** outDescription,
		bool* outValue,
		bool* outConstant);

// Executes one engine loop iteration (single-threaded platforms).
bool FilaEngine_execute(FilaEngine* engine);

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

// Destroys a filament::Texture instance.
void FilaEngine_destroyTexture(FilaEngine* engine, FilaTexture* texture);

// Destroys a filament::Skybox instance.
void FilaEngine_destroySkybox(FilaEngine* engine, FilaSkybox* skybox);

// Destroys a filament::IndirectLight instance.
void FilaEngine_destroyIndirectLight(FilaEngine* engine, FilaIndirectLight* indirectLight);

// Destroys a filament::ColorGrading instance.
void FilaEngine_destroyColorGrading(FilaEngine* engine, FilaColorGrading* colorGrading);

// Destroys a filament::RenderTarget instance.
void FilaEngine_destroyRenderTarget(FilaEngine* engine, FilaRenderTarget* renderTarget);

// Destroys a filament::Stream instance.
void FilaEngine_destroyStream(FilaEngine* engine, FilaStream* stream);

// Destroys a filament::SkinningBuffer instance.
void FilaEngine_destroySkinningBuffer(FilaEngine* engine, FilaSkinningBuffer* skinningBuffer);

// Destroys a filament::MorphTargetBuffer instance.
void FilaEngine_destroyMorphTargetBuffer(FilaEngine* engine, FilaMorphTargetBuffer* morphTargetBuffer);

// Destroys a filament::InstanceBuffer instance.
void FilaEngine_destroyInstanceBuffer(FilaEngine* engine, FilaInstanceBuffer* instanceBuffer);

// Destroys a filament::BufferObject instance.
void FilaEngine_destroyBufferObject(FilaEngine* engine, FilaBufferObject* bufferObject);

// Returns the engine-owned transform manager.
FilaTransformManager* FilaEngine_getTransformManager(FilaEngine* engine);

// Returns the engine-owned entity manager.
FilaEntityManager* FilaEngine_getEntityManager(FilaEngine* engine);

// Returns the engine-owned debug registry.
FilaDebugRegistry* FilaEngine_getDebugRegistry(FilaEngine* engine);

// Returns the engine-owned light manager.
FilaLightManager* FilaEngine_getLightManager(FilaEngine* engine);

// Returns the engine-owned renderable manager.
FilaRenderableManager* FilaEngine_getRenderableManager(FilaEngine* engine);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_ENGINE_H
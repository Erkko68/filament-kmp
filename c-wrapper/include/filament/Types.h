#ifndef FILAMENT_C_TYPES_H
#define FILAMENT_C_TYPES_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque types representing Filament C++ classes
typedef struct FilaEngine FilaEngine;
typedef struct FilaRenderer FilaRenderer;
typedef struct FilaSwapChain FilaSwapChain;
typedef struct FilaView FilaView;
typedef struct FilaScene FilaScene;
typedef struct FilaCamera FilaCamera;
typedef struct FilaFence FilaFence;
typedef struct FilaTransformManager FilaTransformManager;
typedef struct FilaLightManager FilaLightManager;
typedef struct FilaRenderableManager FilaRenderableManager;
typedef struct FilaLightManagerBuilder FilaLightManagerBuilder;
typedef struct FilaRenderableManagerBuilder FilaRenderableManagerBuilder;
typedef struct FilaVertexBuffer FilaVertexBuffer;
typedef struct FilaIndexBuffer FilaIndexBuffer;
typedef struct FilaMaterial FilaMaterial;
typedef struct FilaMaterialInstance FilaMaterialInstance;
typedef struct FilaTexture FilaTexture;
typedef struct FilaTextureBuilder FilaTextureBuilder;
typedef struct FilaSkybox FilaSkybox;
typedef struct FilaSkyboxBuilder FilaSkyboxBuilder;
typedef struct FilaIndirectLight FilaIndirectLight;
typedef struct FilaIndirectLightBuilder FilaIndirectLightBuilder;
typedef struct FilaColorGrading FilaColorGrading;
typedef struct FilaColorGradingBuilder FilaColorGradingBuilder;
typedef struct FilaRenderTarget FilaRenderTarget;
typedef struct FilaRenderTargetBuilder FilaRenderTargetBuilder;
typedef struct FilaStream FilaStream;
typedef struct FilaStreamBuilder FilaStreamBuilder;
typedef struct FilaTextureParams FilaTextureParams;
typedef struct FilaSkinningBuffer FilaSkinningBuffer;
typedef struct FilaSkinningBufferBuilder FilaSkinningBufferBuilder;
typedef struct FilaMorphTargetBuffer FilaMorphTargetBuffer;
typedef struct FilaMorphTargetBufferBuilder FilaMorphTargetBufferBuilder;
typedef struct FilaInstanceBuffer FilaInstanceBuffer;
typedef struct FilaInstanceBufferBuilder FilaInstanceBufferBuilder;
typedef struct FilaVertexBufferBuilder FilaVertexBufferBuilder;
typedef struct FilaIndexBufferBuilder FilaIndexBufferBuilder;
typedef struct FilaMaterialBuilder FilaMaterialBuilder;
typedef struct FilaBufferObject FilaBufferObject;
typedef struct FilaBufferObjectBuilder FilaBufferObjectBuilder;
typedef struct FilaSync FilaSync;
typedef struct FilaDebugRegistry FilaDebugRegistry;
typedef struct FilaEntityManager FilaEntityManager;
typedef struct FilaNameComponentManager FilaNameComponentManager;
typedef struct FilaUtilsCallStack FilaUtilsCallStack;
typedef struct FilaUtilsPanic FilaUtilsPanic;
typedef struct FilaGltfioAnimator FilaGltfioAnimator;
typedef struct FilaGltfioFilamentInstance FilaGltfioFilamentInstance;
typedef struct FilaGltfioFilamentAsset FilaGltfioFilamentAsset;
typedef struct FilaGltfioAssetLoader FilaGltfioAssetLoader;
typedef struct FilaGltfioResourceLoader FilaGltfioResourceLoader;
typedef struct FilaGltfioMaterialProvider FilaGltfioMaterialProvider;
typedef struct FilaGltfioTextureProvider FilaGltfioTextureProvider;
typedef struct FilaGeometryTranscoder FilaGeometryTranscoder;
typedef struct FilaGeometrySurfaceOrientation FilaGeometrySurfaceOrientation;
typedef struct FilaGeometrySurfaceOrientationBuilder FilaGeometrySurfaceOrientationBuilder;
typedef struct FilaGeometryTangentSpaceMesh FilaGeometryTangentSpaceMesh;
typedef struct FilaGeometryTangentSpaceMeshBuilder FilaGeometryTangentSpaceMeshBuilder;

// C representation of utils::Entity identity.
typedef int32_t FilaEntity;

// C representation of filament::TransformManager::Instance.
typedef uint32_t FilaTransformManagerInstance;

// C representation of filament::LightManager::Instance.
typedef uint32_t FilaLightManagerInstance;

// C representation of filament::RenderableManager::Instance.
typedef uint32_t FilaRenderableManagerInstance;

typedef enum FilaCameraFov {
	FILA_CAMERA_FOV_VERTICAL = 0,
	FILA_CAMERA_FOV_HORIZONTAL = 1,
} FilaCameraFov;

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TYPES_H
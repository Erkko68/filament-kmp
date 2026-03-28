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
typedef struct FilaVertexBufferBuilder FilaVertexBufferBuilder;
typedef struct FilaIndexBufferBuilder FilaIndexBufferBuilder;
typedef struct FilaMaterialBuilder FilaMaterialBuilder;

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

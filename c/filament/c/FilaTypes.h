#ifndef FILAMENT_C_TYPES_H
#define FILAMENT_C_TYPES_H

#include <stdint.h>
#include <stdbool.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque handles to Filament classes
typedef struct FilaEngine FilaEngine;
typedef struct FilaCamera FilaCamera;
typedef struct FilaRenderer FilaRenderer;
typedef struct FilaScene FilaScene;
typedef struct FilaView FilaView;
typedef struct FilaViewport FilaViewport;
typedef struct FilaSwapChain FilaSwapChain;
typedef struct FilaIndirectLight FilaIndirectLight;
typedef struct FilaSkybox FilaSkybox;
typedef struct FilaColorGrading FilaColorGrading;
typedef struct FilaRenderTarget FilaRenderTarget;

typedef struct FilaVertexBuffer FilaVertexBuffer;
typedef struct FilaIndexBuffer FilaIndexBuffer;
typedef struct FilaBufferObject FilaBufferObject;
typedef struct FilaTexture FilaTexture;
typedef struct FilaStream FilaStream;
typedef struct FilaMaterial FilaMaterial;
typedef struct FilaMaterialInstance FilaMaterialInstance;

typedef struct FilaRenderableManager FilaRenderableManager;
typedef struct FilaTransformManager FilaTransformManager;
typedef struct FilaLightManager FilaLightManager;
typedef struct FilaEntityManager FilaEntityManager;

typedef struct FilaFence FilaFence;
typedef struct FilaSwapChain FilaSwapChain;
typedef struct FilaStream FilaStream;
typedef struct FilaIndexBuffer FilaIndexBuffer;
typedef struct FilaVertexBuffer FilaVertexBuffer;
typedef struct FilaBufferObject FilaBufferObject;
typedef struct FilaSkinningBuffer FilaSkinningBuffer;
typedef struct FilaMorphTargetBuffer FilaMorphTargetBuffer;
typedef struct FilaToneMapper FilaToneMapper;

// Filamat types
typedef struct FilaMaterialBuilder FilaMaterialBuilder;
typedef struct FilaPackage FilaPackage;

// Filament Utils types
typedef struct FilaIBLPrefilterContext FilaIBLPrefilterContext;
typedef struct FilaIBLPrefilterEquirectangularToCubemap FilaIBLPrefilterEquirectangularToCubemap;
typedef struct FilaIBLPrefilterSpecularFilter FilaIBLPrefilterSpecularFilter;

// Manipulator types
typedef struct FilaManipulator FilaManipulator;
typedef struct FilaManipulatorBuilder FilaManipulatorBuilder;
typedef struct FilaBookmark FilaBookmark;

// Packed texture sampler parameters (matches JNI long)
typedef uint64_t FilaTextureSampler;

typedef struct FilaCallbackHandler FilaCallbackHandler;

typedef enum FilaVertexAttribute {
    FILA_VERTEX_ATTRIBUTE_POSITION = 0,
    FILA_VERTEX_ATTRIBUTE_TANGENTS = 1,
    FILA_VERTEX_ATTRIBUTE_COLOR = 2,
    FILA_VERTEX_ATTRIBUTE_UV0 = 3,
    FILA_VERTEX_ATTRIBUTE_UV1 = 4,
    FILA_VERTEX_ATTRIBUTE_BONE_INDICES = 5,
    FILA_VERTEX_ATTRIBUTE_BONE_WEIGHTS = 6,
    FILA_VERTEX_ATTRIBUTE_CUSTOM0 = 7,
    FILA_VERTEX_ATTRIBUTE_CUSTOM1 = 8,
    FILA_VERTEX_ATTRIBUTE_CUSTOM2 = 9,
    FILA_VERTEX_ATTRIBUTE_CUSTOM3 = 10,
    FILA_VERTEX_ATTRIBUTE_CUSTOM4 = 11,
    FILA_VERTEX_ATTRIBUTE_CUSTOM5 = 12,
    FILA_VERTEX_ATTRIBUTE_CUSTOM6 = 13,
    FILA_VERTEX_ATTRIBUTE_CUSTOM7 = 14,
} FilaVertexAttribute;

// Entity type (matches utils::Entity::Type)
typedef uint32_t FilaEntity;

// Callback types
typedef void (*FilaBufferCallback)(void* buffer, size_t size, void* userData);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TYPES_H

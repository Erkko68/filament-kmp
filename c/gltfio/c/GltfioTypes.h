#ifndef GLTFIO_C_TYPES_H
#define GLTFIO_C_TYPES_H

#include "../../filament/c/FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

// gltfio opaque handles
typedef struct FilaAssetLoader FilaAssetLoader;
typedef struct FilaFilamentAsset FilaFilamentAsset;
typedef struct FilaFilamentInstance FilaFilamentInstance;
typedef struct FilaAnimator FilaAnimator;
typedef struct FilaMaterialProvider FilaMaterialProvider;
typedef struct FilaResourceLoader FilaResourceLoader;
typedef struct FilaTextureProvider FilaTextureProvider;

// A gltfio MaterialKey crosses the API flattened to FILA_MATERIAL_KEY_FIELD_COUNT int32s, one per field in
// declaration order (doubleSided, unlit, ..., hasIOR); bools are 0/1, alphaMode is the AlphaMode value.
#define FILA_MATERIAL_KEY_FIELD_COUNT 35

#ifdef __cplusplus
}
#endif

#endif // GLTFIO_C_TYPES_H

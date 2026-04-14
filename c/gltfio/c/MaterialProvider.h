#ifndef GLTFIO_C_MATERIAL_PROVIDER_H
#define GLTFIO_C_MATERIAL_PROVIDER_H

#include "../../filament/c/FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

void FilaMaterialProvider_destroy(FilaMaterialProvider* provider);

FilaMaterialProvider* FilaMaterialProvider_createUbershaderProvider(FilaEngine* engine, const void* archive, size_t archiveByteCount);
FilaMaterialProvider* FilaMaterialProvider_createJitShaderProvider(FilaEngine* engine);

void FilaMaterialProvider_destroyMaterials(FilaMaterialProvider* provider);
size_t FilaMaterialProvider_getMaterialsCount(FilaMaterialProvider* provider);

#ifdef __cplusplus
}
#endif

#endif // GLTFIO_C_MATERIAL_PROVIDER_H

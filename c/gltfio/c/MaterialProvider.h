#ifndef GLTFIO_C_MATERIAL_PROVIDER_H
#define GLTFIO_C_MATERIAL_PROVIDER_H

#include "GltfioTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

void FilaMaterialProvider_destroy(FilaMaterialProvider* provider);

FilaMaterialProvider* FilaMaterialProvider_createUbershaderProvider(FilaEngine* engine, const void* archive, uint32_t archiveByteCount);

void FilaMaterialProvider_destroyMaterials(FilaMaterialProvider* provider);
uint32_t FilaMaterialProvider_getMaterialsCount(FilaMaterialProvider* provider);
FilaMaterial* FilaMaterialProvider_getMaterialAt(FilaMaterialProvider* provider, uint32_t index);

bool FilaMaterialProvider_needsDummyData(FilaMaterialProvider* provider, int attrib);

FilaMaterialInstance* FilaMaterialProvider_createMaterialInstance(FilaMaterialProvider* provider,
    const int32_t* key, const uint8_t* uvmap, const char* label, const char* extras);

FilaMaterial* FilaMaterialProvider_getMaterial(FilaMaterialProvider* provider,
    const int32_t* key, const uint8_t* uvmap, const char* label);

// Rewrites both the key and the 8-entry uvmap in place.
void FilaMaterialKey_constrainMaterial(int32_t* key, uint8_t* uvmap);

#ifdef __cplusplus
}
#endif

#endif // GLTFIO_C_MATERIAL_PROVIDER_H

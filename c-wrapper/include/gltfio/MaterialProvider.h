#ifndef FILAMENT_C_GLTFIO_MATERIAL_PROVIDER_H
#define FILAMENT_C_GLTFIO_MATERIAL_PROVIDER_H

#include <stddef.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Creates a gltfio ubershader material provider.
FilaGltfioMaterialProvider* FilaGltfioMaterialProvider_createUbershaderProvider(
        FilaEngine* engine,
        const void* archive,
        size_t archiveByteCount);

// Destroys all materials cached by this provider.
void FilaGltfioMaterialProvider_destroyMaterials(FilaGltfioMaterialProvider* provider);

// Returns the number of cached materials.
size_t FilaGltfioMaterialProvider_getMaterialsCount(const FilaGltfioMaterialProvider* provider);

// Destroys the provider object.
void FilaGltfioMaterialProvider_destroy(FilaGltfioMaterialProvider* provider);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_MATERIAL_PROVIDER_H


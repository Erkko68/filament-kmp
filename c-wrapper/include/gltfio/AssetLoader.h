#ifndef FILAMENT_C_GLTFIO_ASSET_LOADER_H
#define FILAMENT_C_GLTFIO_ASSET_LOADER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Creates an asset loader.
FilaGltfioAssetLoader* FilaGltfioAssetLoader_create(
        FilaEngine* engine,
        FilaGltfioMaterialProvider* materials,
        FilaEntityManager* entities,
        const char* defaultNodeName);

// Creates an asset loader with an optional NameComponentManager.
FilaGltfioAssetLoader* FilaGltfioAssetLoader_createWithNames(
        FilaEngine* engine,
        FilaGltfioMaterialProvider* materials,
        FilaEntityManager* entities,
        FilaNameComponentManager* names,
        const char* defaultNodeName);

// Destroys the asset loader and sets the caller handle to NULL.
void FilaGltfioAssetLoader_destroy(FilaGltfioAssetLoader** inOutLoader);

// Creates a single-instance asset from GLB / glTF content.
FilaGltfioFilamentAsset* FilaGltfioAssetLoader_createAsset(
        FilaGltfioAssetLoader* loader,
        const uint8_t* bytes,
        size_t byteCount);

// Creates an instanced asset and writes instance handles into outInstances.
FilaGltfioFilamentAsset* FilaGltfioAssetLoader_createInstancedAsset(
        FilaGltfioAssetLoader* loader,
        const uint8_t* bytes,
        size_t byteCount,
        FilaGltfioFilamentInstance** outInstances,
        size_t instanceCount);

// Creates an additional instance for an existing asset.
FilaGltfioFilamentInstance* FilaGltfioAssetLoader_createInstance(
        FilaGltfioAssetLoader* loader,
        FilaGltfioFilamentAsset* asset);

// Destroys the given asset and all of its instances.
void FilaGltfioAssetLoader_destroyAsset(
        FilaGltfioAssetLoader* loader,
        const FilaGltfioFilamentAsset* asset);

// Enables or disables diagnostics for newly loaded assets.
void FilaGltfioAssetLoader_enableDiagnostics(FilaGltfioAssetLoader* loader, bool enable);

// Returns the count of cached glTF materials.
size_t FilaGltfioAssetLoader_getMaterialsCount(const FilaGltfioAssetLoader* loader);

// Writes up to maxCount cached material handles.
size_t FilaGltfioAssetLoader_getMaterials(const FilaGltfioAssetLoader* loader,
        const FilaMaterial** outMaterials,
        size_t maxCount);

// Returns the NameComponentManager associated with this loader, or NULL.
FilaNameComponentManager* FilaGltfioAssetLoader_getNames(const FilaGltfioAssetLoader* loader);

// Returns the MaterialProvider associated with this loader.
FilaGltfioMaterialProvider* FilaGltfioAssetLoader_getMaterialProvider(FilaGltfioAssetLoader* loader);


#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_ASSET_LOADER_H


#ifndef FILAMENT_C_GLTFIO_RESOURCE_LOADER_H
#define FILAMENT_C_GLTFIO_RESOURCE_LOADER_H

#include <stdbool.h>
#include <stddef.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Creates a resource loader with the given configuration.
FilaGltfioResourceLoader* FilaGltfioResourceLoader_create(
        FilaEngine* engine,
        const char* gltfPath,
        bool normalizeSkinningWeights);

// Destroys the resource loader.
void FilaGltfioResourceLoader_destroy(FilaGltfioResourceLoader* loader);

// Updates loader configuration.
void FilaGltfioResourceLoader_setConfiguration(
        FilaGltfioResourceLoader* loader,
        FilaEngine* engine,
        const char* gltfPath,
        bool normalizeSkinningWeights);

bool FilaGltfioResourceLoader_hasResourceData(const FilaGltfioResourceLoader* loader, const char* uri);
void FilaGltfioResourceLoader_evictResourceData(FilaGltfioResourceLoader* loader);

// Copies data into the loader's URI cache.
bool FilaGltfioResourceLoader_addResourceDataCopy(
        FilaGltfioResourceLoader* loader,
        const char* uri,
        const void* data,
        size_t dataSize);

// Registers a texture provider for a MIME type (e.g. image/png).
void FilaGltfioResourceLoader_addTextureProvider(
        FilaGltfioResourceLoader* loader,
        const char* mimeType,
        FilaGltfioTextureProvider* provider);

bool FilaGltfioResourceLoader_loadResources(
        FilaGltfioResourceLoader* loader,
        FilaGltfioFilamentAsset* asset);

bool FilaGltfioResourceLoader_asyncBeginLoad(
        FilaGltfioResourceLoader* loader,
        FilaGltfioFilamentAsset* asset);

float FilaGltfioResourceLoader_asyncGetLoadProgress(const FilaGltfioResourceLoader* loader);
void FilaGltfioResourceLoader_asyncUpdateLoad(FilaGltfioResourceLoader* loader);
void FilaGltfioResourceLoader_asyncCancelLoad(FilaGltfioResourceLoader* loader);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_RESOURCE_LOADER_H


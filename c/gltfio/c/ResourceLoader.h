#ifndef GLTFIO_C_RESOURCE_LOADER_H
#define GLTFIO_C_RESOURCE_LOADER_H

#include "../../filament/c/FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaResourceLoader* FilaResourceLoader_create(FilaEngine* engine, bool normalizeSkinningWeights);
void FilaResourceLoader_destroy(FilaResourceLoader* loader);

void FilaResourceLoader_addResourceData(FilaResourceLoader* loader, const char* uri, const void* buffer, size_t bufferByteCount);
bool FilaResourceLoader_loadResources(FilaResourceLoader* loader, FilaFilamentAsset* asset);

void FilaResourceLoader_asyncBeginLoad(FilaResourceLoader* loader, FilaFilamentAsset* asset);
float FilaResourceLoader_asyncGetLoadProgress(FilaResourceLoader* loader);
void FilaResourceLoader_asyncCancelLoad(FilaResourceLoader* loader);

void FilaResourceLoader_evictResourceData(FilaResourceLoader* loader);

#ifdef __cplusplus
}
#endif

#endif // GLTFIO_C_RESOURCE_LOADER_H

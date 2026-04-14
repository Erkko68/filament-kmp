#include <gltfio/ResourceLoader.h>
#include <gltfio/FilamentAsset.h>
#include <filament/Engine.h>

#include "../c/ResourceLoader.h"

using namespace filament;
using namespace filament::gltfio;

extern "C" {

FilaResourceLoader* FilaResourceLoader_create(FilaEngine* engine, bool normalizeSkinningWeights) {
    ResourceConfiguration config;
    config.engine = (Engine*) engine;
    config.gltfPath = nullptr;
    config.normalizeSkinningWeights = normalizeSkinningWeights;
    return (FilaResourceLoader*) new ResourceLoader(config);
}

void FilaResourceLoader_destroy(FilaResourceLoader* loader) {
    delete (ResourceLoader*) loader;
}

void FilaResourceLoader_addResourceData(FilaResourceLoader* loader, const char* uri, const void* buffer, size_t bufferByteCount) {
    ((ResourceLoader*) loader)->addResourceData(uri, {
        (const uint8_t*) buffer,
        (uint32_t) bufferByteCount
    });
}

bool FilaResourceLoader_loadResources(FilaResourceLoader* loader, FilaFilamentAsset* asset) {
    return ((ResourceLoader*) loader)->loadResources((FilamentAsset*) asset);
}

void FilaResourceLoader_asyncBeginLoad(FilaResourceLoader* loader, FilaFilamentAsset* asset) {
    ((ResourceLoader*) loader)->asyncBeginLoad((FilamentAsset*) asset);
}

float FilaResourceLoader_asyncGetLoadProgress(FilaResourceLoader* loader) {
    return ((ResourceLoader*) loader)->asyncGetLoadProgress();
}

void FilaResourceLoader_asyncCancelLoad(FilaResourceLoader* loader) {
    ((ResourceLoader*) loader)->asyncCancelLoad();
}

void FilaResourceLoader_evictResourceData(FilaResourceLoader* loader) {
    ((ResourceLoader*) loader)->evictResourceData();
}

}

#include <gltfio/ResourceLoader.h>

#include <gltfio/TextureProvider.h>

#include <backend/BufferDescriptor.h>

#include <cstring>
#include <new>

#include "../../include/gltfio/ResourceLoader.h"

namespace {

filament::gltfio::ResourceConfiguration toConfig(FilaEngine* engine, const char* gltfPath, bool normalizeSkinningWeights) {
    filament::gltfio::ResourceConfiguration config = {};
    config.engine = reinterpret_cast<filament::Engine*>(engine);
    config.gltfPath = gltfPath;
    config.normalizeSkinningWeights = normalizeSkinningWeights;
    return config;
}

} // namespace

extern "C" {

FilaGltfioResourceLoader* FilaGltfioResourceLoader_create(
        FilaEngine* engine,
        const char* gltfPath,
        bool normalizeSkinningWeights) {
    if (!engine) {
        return nullptr;
    }
    try {
        auto* loader = new filament::gltfio::ResourceLoader(toConfig(engine, gltfPath, normalizeSkinningWeights));
        return reinterpret_cast<FilaGltfioResourceLoader*>(loader);
    } catch (...) {
        return nullptr;
    }
}

void FilaGltfioResourceLoader_destroy(FilaGltfioResourceLoader* loader) {
    if (!loader) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    delete cppLoader;
}

void FilaGltfioResourceLoader_setConfiguration(
        FilaGltfioResourceLoader* loader,
        FilaEngine* engine,
        const char* gltfPath,
        bool normalizeSkinningWeights) {
    if (!loader || !engine) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    cppLoader->setConfiguration(toConfig(engine, gltfPath, normalizeSkinningWeights));
}

bool FilaGltfioResourceLoader_hasResourceData(const FilaGltfioResourceLoader* loader, const char* uri) {
    if (!loader || !uri || uri[0] == '\0') {
        return false;
    }
    auto* cppLoader = reinterpret_cast<const filament::gltfio::ResourceLoader*>(loader);
    return cppLoader->hasResourceData(uri);
}

void FilaGltfioResourceLoader_evictResourceData(FilaGltfioResourceLoader* loader) {
    if (!loader) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    cppLoader->evictResourceData();
}

bool FilaGltfioResourceLoader_addResourceDataCopy(
        FilaGltfioResourceLoader* loader,
        const char* uri,
        const void* data,
        size_t dataSize) {
    if (!loader || !uri || uri[0] == '\0' || !data || dataSize == 0u) {
        return false;
    }

    uint8_t* ownedData = new (std::nothrow) uint8_t[dataSize];
    if (!ownedData) {
        return false;
    }
    std::memcpy(ownedData, data, dataSize);

    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    filament::backend::BufferDescriptor descriptor(
            ownedData,
            dataSize,
            [](void* buffer, size_t, void*) {
                delete[] static_cast<uint8_t*>(buffer);
            },
            nullptr);
    cppLoader->addResourceData(uri, std::move(descriptor));
    return true;
}

void FilaGltfioResourceLoader_addTextureProvider(
        FilaGltfioResourceLoader* loader,
        const char* mimeType,
        FilaGltfioTextureProvider* provider) {
    if (!loader || !mimeType || mimeType[0] == '\0' || !provider) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    auto* cppProvider = reinterpret_cast<filament::gltfio::TextureProvider*>(provider);
    cppLoader->addTextureProvider(mimeType, cppProvider);
}

bool FilaGltfioResourceLoader_loadResources(
        FilaGltfioResourceLoader* loader,
        FilaGltfioFilamentAsset* asset) {
    if (!loader || !asset) {
        return false;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    return cppLoader->loadResources(cppAsset);
}

bool FilaGltfioResourceLoader_asyncBeginLoad(
        FilaGltfioResourceLoader* loader,
        FilaGltfioFilamentAsset* asset) {
    if (!loader || !asset) {
        return false;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    return cppLoader->asyncBeginLoad(cppAsset);
}

float FilaGltfioResourceLoader_asyncGetLoadProgress(const FilaGltfioResourceLoader* loader) {
    if (!loader) {
        return 0.0f;
    }
    auto* cppLoader = reinterpret_cast<const filament::gltfio::ResourceLoader*>(loader);
    return cppLoader->asyncGetLoadProgress();
}

void FilaGltfioResourceLoader_asyncUpdateLoad(FilaGltfioResourceLoader* loader) {
    if (!loader) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    cppLoader->asyncUpdateLoad();
}

void FilaGltfioResourceLoader_asyncCancelLoad(FilaGltfioResourceLoader* loader) {
    if (!loader) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::ResourceLoader*>(loader);
    cppLoader->asyncCancelLoad();
}

} // extern "C"


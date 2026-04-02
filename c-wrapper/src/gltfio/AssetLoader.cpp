#include <gltfio/AssetLoader.h>

#include <limits>
#include <vector>

#include <utils/EntityManager.h>

#include "../../include/gltfio/AssetLoader.h"

extern "C" {

FilaGltfioAssetLoader* FilaGltfioAssetLoader_create(
        FilaEngine* engine,
        FilaGltfioMaterialProvider* materials,
        FilaEntityManager* entities,
        const char* defaultNodeName) {
    return FilaGltfioAssetLoader_createWithNames(engine, materials, entities, (FilaNameComponentManager*)0, defaultNodeName);
}

FilaGltfioAssetLoader* FilaGltfioAssetLoader_createWithNames(
        FilaEngine* engine,
        FilaGltfioMaterialProvider* materials,
        FilaEntityManager* entities,
        FilaNameComponentManager* names,
        const char* defaultNodeName) {
    if (!engine || !materials) {
        return nullptr;
    }

    filament::gltfio::AssetConfiguration config = {};
    config.engine = reinterpret_cast<filament::Engine*>(engine);
    config.materials = reinterpret_cast<filament::gltfio::MaterialProvider*>(materials);
    config.entities = reinterpret_cast<utils::EntityManager*>(entities);
    config.names = reinterpret_cast<utils::NameComponentManager*>(names);
    config.defaultNodeName = const_cast<char*>(defaultNodeName);
    config.ext = nullptr;

    return reinterpret_cast<FilaGltfioAssetLoader*>(filament::gltfio::AssetLoader::create(config));
}

void FilaGltfioAssetLoader_destroy(FilaGltfioAssetLoader** inOutLoader) {
    if (!inOutLoader || !*inOutLoader) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::AssetLoader*>(*inOutLoader);
    filament::gltfio::AssetLoader::destroy(&cppLoader);
    *inOutLoader = nullptr;
}

FilaGltfioFilamentAsset* FilaGltfioAssetLoader_createAsset(
        FilaGltfioAssetLoader* loader,
        const uint8_t* bytes,
        size_t byteCount) {
    if (!loader || !bytes || byteCount == 0u || byteCount > std::numeric_limits<uint32_t>::max()) {
        return nullptr;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::AssetLoader*>(loader);
    return reinterpret_cast<FilaGltfioFilamentAsset*>(cppLoader->createAsset(bytes, static_cast<uint32_t>(byteCount)));
}

FilaGltfioFilamentAsset* FilaGltfioAssetLoader_createInstancedAsset(
        FilaGltfioAssetLoader* loader,
        const uint8_t* bytes,
        size_t byteCount,
        FilaGltfioFilamentInstance** outInstances,
        size_t instanceCount) {
    if (!loader || !bytes || byteCount == 0u || byteCount > std::numeric_limits<uint32_t>::max()) {
        return nullptr;
    }
    if (instanceCount > 0u && !outInstances) {
        return nullptr;
    }

    auto* cppLoader = reinterpret_cast<filament::gltfio::AssetLoader*>(loader);
    std::vector<filament::gltfio::FilamentInstance*> instances(instanceCount, nullptr);
    auto* asset = cppLoader->createInstancedAsset(
            bytes,
            static_cast<uint32_t>(byteCount),
            instances.empty() ? nullptr : instances.data(),
            instanceCount);
    if (!asset) {
        return nullptr;
    }

    for (size_t i = 0u; i < instanceCount; ++i) {
        outInstances[i] = reinterpret_cast<FilaGltfioFilamentInstance*>(instances[i]);
    }
    return reinterpret_cast<FilaGltfioFilamentAsset*>(asset);
}

FilaGltfioFilamentInstance* FilaGltfioAssetLoader_createInstance(
        FilaGltfioAssetLoader* loader,
        FilaGltfioFilamentAsset* asset) {
    if (!loader || !asset) {
        return nullptr;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::AssetLoader*>(loader);
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    return reinterpret_cast<FilaGltfioFilamentInstance*>(cppLoader->createInstance(cppAsset));
}

void FilaGltfioAssetLoader_destroyAsset(
        FilaGltfioAssetLoader* loader,
        const FilaGltfioFilamentAsset* asset) {
    if (!loader || !asset) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::AssetLoader*>(loader);
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    cppLoader->destroyAsset(cppAsset);
}

void FilaGltfioAssetLoader_enableDiagnostics(FilaGltfioAssetLoader* loader, bool enable) {
    if (!loader) {
        return;
    }
    auto* cppLoader = reinterpret_cast<filament::gltfio::AssetLoader*>(loader);
    cppLoader->enableDiagnostics(enable);
}

size_t FilaGltfioAssetLoader_getMaterialsCount(const FilaGltfioAssetLoader* loader) {
    if (!loader) {
        return 0u;
    }
    auto* cppLoader = reinterpret_cast<const filament::gltfio::AssetLoader*>(loader);
    return cppLoader->getMaterialsCount();
}

size_t FilaGltfioAssetLoader_getMaterials(const FilaGltfioAssetLoader* loader,
        const FilaMaterial** outMaterials,
        size_t maxCount) {
    if (!loader || !outMaterials || maxCount == 0u) {
        return 0u;
    }
    auto* cppLoader = reinterpret_cast<const filament::gltfio::AssetLoader*>(loader);
    const size_t count = cppLoader->getMaterialsCount();
    const size_t written = (count < maxCount) ? count : maxCount;
    const filament::Material* const* materials = cppLoader->getMaterials();
    if (!materials) {
        return 0u;
    }
    for (size_t i = 0u; i < written; ++i) {
        outMaterials[i] = reinterpret_cast<const FilaMaterial*>(materials[i]);
    }
    return written;
}


} // extern "C"


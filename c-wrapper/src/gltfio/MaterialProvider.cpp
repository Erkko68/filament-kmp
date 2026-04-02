#include <gltfio/MaterialProvider.h>

#include "../../include/gltfio/MaterialProvider.h"

extern "C" {

FilaGltfioMaterialProvider* FilaGltfioMaterialProvider_createUbershaderProvider(
        FilaEngine* engine,
        const void* archive,
        size_t archiveByteCount) {
    if (!engine) {
        return nullptr;
    }
    auto* cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaGltfioMaterialProvider*>(
            filament::gltfio::createUbershaderProvider(cppEngine, archive, archiveByteCount));
}

void FilaGltfioMaterialProvider_destroyMaterials(FilaGltfioMaterialProvider* provider) {
    if (!provider) {
        return;
    }
    auto* cppProvider = reinterpret_cast<filament::gltfio::MaterialProvider*>(provider);
    cppProvider->destroyMaterials();
}

size_t FilaGltfioMaterialProvider_getMaterialsCount(const FilaGltfioMaterialProvider* provider) {
    if (!provider) {
        return 0u;
    }
    auto* cppProvider = reinterpret_cast<const filament::gltfio::MaterialProvider*>(provider);
    return cppProvider->getMaterialsCount();
}

void FilaGltfioMaterialProvider_destroy(FilaGltfioMaterialProvider* provider) {
    if (!provider) {
        return;
    }
    auto* cppProvider = reinterpret_cast<filament::gltfio::MaterialProvider*>(provider);
    delete cppProvider;
}

} // extern "C"


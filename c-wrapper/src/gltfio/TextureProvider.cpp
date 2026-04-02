#include <gltfio/TextureProvider.h>

#include "../../include/gltfio/TextureProvider.h"

extern "C" {

FilaGltfioTextureProvider* FilaGltfioTextureProvider_createStb(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto* cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaGltfioTextureProvider*>(filament::gltfio::createStbProvider(cppEngine));
}

FilaGltfioTextureProvider* FilaGltfioTextureProvider_createKtx2(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto* cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaGltfioTextureProvider*>(filament::gltfio::createKtx2Provider(cppEngine));
}

FilaGltfioTextureProvider* FilaGltfioTextureProvider_createWebp(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto* cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaGltfioTextureProvider*>(filament::gltfio::createWebpProvider(cppEngine));
}

bool FilaGltfioTextureProvider_isWebpSupported(void) {
    return filament::gltfio::isWebpSupported();
}

void FilaGltfioTextureProvider_destroy(FilaGltfioTextureProvider* provider) {
    if (!provider) {
        return;
    }
    auto* cppProvider = reinterpret_cast<filament::gltfio::TextureProvider*>(provider);
    delete cppProvider;
}

} // extern "C"


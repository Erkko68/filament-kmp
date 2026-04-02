#include "gltfio/TextureProvider.h"

void test_headers_gltfio_texture_provider(void) {
    (void)FilaGltfioTextureProvider_createStb((FilaEngine*)0);
    (void)FilaGltfioTextureProvider_createKtx2((FilaEngine*)0);
    (void)FilaGltfioTextureProvider_createWebp((FilaEngine*)0);
    (void)FilaGltfioTextureProvider_isWebpSupported();
    FilaGltfioTextureProvider_destroy((FilaGltfioTextureProvider*)0);
}


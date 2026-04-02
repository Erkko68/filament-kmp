#include <stdio.h>

#include "gltfio/TextureProvider.h"

int main(void) {
    printf("Running functionality_gltfio_texture_provider_null_safety...\n");

    if (FilaGltfioTextureProvider_createStb((FilaEngine*)0) != (FilaGltfioTextureProvider*)0 ||
            FilaGltfioTextureProvider_createKtx2((FilaEngine*)0) != (FilaGltfioTextureProvider*)0 ||
            FilaGltfioTextureProvider_createWebp((FilaEngine*)0) != (FilaGltfioTextureProvider*)0) {
        printf("TextureProvider null create mismatch\n");
        return 1;
    }

    (void)FilaGltfioTextureProvider_isWebpSupported();
    FilaGltfioTextureProvider_destroy((FilaGltfioTextureProvider*)0);

    printf("functionality_gltfio_texture_provider_null_safety completed\n");
    return 0;
}


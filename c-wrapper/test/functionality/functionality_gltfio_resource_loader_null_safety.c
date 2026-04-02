#include <stdio.h>

#include "gltfio/ResourceLoader.h"
#include "gltfio/TextureProvider.h"

int main(void) {
    printf("Running functionality_gltfio_resource_loader_null_safety...\n");

    if (FilaGltfioResourceLoader_create((FilaEngine*)0, (const char*)0, false) != (FilaGltfioResourceLoader*)0 ||
            FilaGltfioResourceLoader_hasResourceData((const FilaGltfioResourceLoader*)0, "x") ||
            FilaGltfioResourceLoader_addResourceDataCopy((FilaGltfioResourceLoader*)0, "x", "a", 1u) ||
            FilaGltfioResourceLoader_loadResources((FilaGltfioResourceLoader*)0, (FilaGltfioFilamentAsset*)0) ||
            FilaGltfioResourceLoader_asyncBeginLoad((FilaGltfioResourceLoader*)0, (FilaGltfioFilamentAsset*)0) ||
            FilaGltfioResourceLoader_asyncGetLoadProgress((const FilaGltfioResourceLoader*)0) != 0.0f) {
        printf("ResourceLoader null defaults mismatch\n");
        return 1;
    }

    FilaGltfioResourceLoader_setConfiguration((FilaGltfioResourceLoader*)0, (FilaEngine*)0, (const char*)0, false);
    FilaGltfioResourceLoader_addTextureProvider(
            (FilaGltfioResourceLoader*)0,
            "image/png",
            (FilaGltfioTextureProvider*)0);
    FilaGltfioResourceLoader_evictResourceData((FilaGltfioResourceLoader*)0);
    FilaGltfioResourceLoader_asyncUpdateLoad((FilaGltfioResourceLoader*)0);
    FilaGltfioResourceLoader_asyncCancelLoad((FilaGltfioResourceLoader*)0);
    FilaGltfioResourceLoader_destroy((FilaGltfioResourceLoader*)0);

    printf("functionality_gltfio_resource_loader_null_safety completed\n");
    return 0;
}


#include <stdio.h>

#include "gltfio/AssetLoader.h"

int main(void) {
    printf("Running functionality_gltfio_asset_loader_null_safety...\n");

    FilaGltfioAssetLoader* loader = FilaGltfioAssetLoader_create(
            (FilaEngine*)0,
            (FilaGltfioMaterialProvider*)0,
            (FilaEntityManager*)0,
            (const char*)0);

    if (loader != (FilaGltfioAssetLoader*)0 ||
            FilaGltfioAssetLoader_createAsset((FilaGltfioAssetLoader*)0, (const uint8_t*)0, 0u) != (FilaGltfioFilamentAsset*)0 ||
            FilaGltfioAssetLoader_createInstancedAsset((FilaGltfioAssetLoader*)0, (const uint8_t*)0, 0u, (FilaGltfioFilamentInstance**)0, 0u) != (FilaGltfioFilamentAsset*)0 ||
            FilaGltfioAssetLoader_createInstance((FilaGltfioAssetLoader*)0, (FilaGltfioFilamentAsset*)0) != (FilaGltfioFilamentInstance*)0 ||
            FilaGltfioAssetLoader_getMaterialsCount((const FilaGltfioAssetLoader*)0) != 0u) {
        printf("AssetLoader null defaults mismatch\n");
        return 1;
    }

    FilaGltfioAssetLoader_destroyAsset((FilaGltfioAssetLoader*)0, (const FilaGltfioFilamentAsset*)0);
    FilaGltfioAssetLoader_enableDiagnostics((FilaGltfioAssetLoader*)0, true);
    FilaGltfioAssetLoader_destroy((FilaGltfioAssetLoader**)0);

    printf("functionality_gltfio_asset_loader_null_safety completed\n");
    return 0;
}


#include "gltfio/AssetLoader.h"

void test_headers_gltfio_asset_loader(void) {
    FilaGltfioAssetLoader* loader = FilaGltfioAssetLoader_create(
            (FilaEngine*)0,
            (FilaGltfioMaterialProvider*)0,
            (FilaEntityManager*)0,
            (const char*)0);

    (void)FilaGltfioAssetLoader_createAsset(loader, (const uint8_t*)0, 0u);
    (void)FilaGltfioAssetLoader_createInstancedAsset(
            loader,
            (const uint8_t*)0,
            0u,
            (FilaGltfioFilamentInstance**)0,
            0u);
    (void)FilaGltfioAssetLoader_createInstance(loader, (FilaGltfioFilamentAsset*)0);
    FilaGltfioAssetLoader_destroyAsset(loader, (const FilaGltfioFilamentAsset*)0);
    FilaGltfioAssetLoader_enableDiagnostics(loader, true);
    (void)FilaGltfioAssetLoader_getMaterialsCount((const FilaGltfioAssetLoader*)0);
    FilaGltfioAssetLoader_destroy(&loader);
}


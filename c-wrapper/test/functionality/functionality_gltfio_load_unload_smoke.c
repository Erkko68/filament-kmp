#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include "filament/Engine.h"
#include "filament/Scene.h"
#include "gltfio/AssetLoader.h"
#include "gltfio/FilamentAsset.h"
#include "gltfio/MaterialProvider.h"

static uint8_t* read_file_bytes(const char* path, size_t* outSize) {
    FILE* fp = fopen(path, "rb");
    if (!fp) {
        return NULL;
    }
    if (fseek(fp, 0, SEEK_END) != 0) {
        fclose(fp);
        return NULL;
    }
    long length = ftell(fp);
    if (length <= 0) {
        fclose(fp);
        return NULL;
    }
    if (fseek(fp, 0, SEEK_SET) != 0) {
        fclose(fp);
        return NULL;
    }

    uint8_t* bytes = (uint8_t*)malloc((size_t)length);
    if (!bytes) {
        fclose(fp);
        return NULL;
    }

    const size_t readCount = fread(bytes, 1u, (size_t)length, fp);
    fclose(fp);
    if (readCount != (size_t)length) {
        free(bytes);
        return NULL;
    }

    *outSize = (size_t)length;
    return bytes;
}

int main(void) {
    printf("Running functionality_gltfio_load_unload_smoke...\n");

    const char* glbPath = getenv("FILA_TEST_GLTF_GLB");
    if (!glbPath || glbPath[0] == '\0') {
        printf("FILA_TEST_GLTF_GLB not set; skipping smoke load\n");
        return 0;
    }

    size_t glbSize = 0u;
    uint8_t* glbBytes = read_file_bytes(glbPath, &glbSize);
    if (!glbBytes) {
        printf("Failed to read GLB fixture: %s\n", glbPath);
        return 1;
    }

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        free(glbBytes);
        printf("Engine creation failed\n");
        return 1;
    }

    FilaGltfioMaterialProvider* provider = FilaGltfioMaterialProvider_createUbershaderProvider(engine, NULL, 0u);
    if (!provider) {
        FilaEngine_destroy(&engine);
        free(glbBytes);
        printf("Material provider creation failed\n");
        return 1;
    }

    FilaGltfioAssetLoader* loader = FilaGltfioAssetLoader_create(
            engine,
            provider,
            FilaEngine_getEntityManager(engine),
            "node");
    if (!loader) {
        FilaGltfioMaterialProvider_destroy(provider);
        FilaEngine_destroy(&engine);
        free(glbBytes);
        printf("AssetLoader creation failed\n");
        return 1;
    }

    FilaGltfioFilamentAsset* asset = FilaGltfioAssetLoader_createAsset(loader, glbBytes, glbSize);
    if (!asset) {
        FilaGltfioAssetLoader_destroy(&loader);
        FilaGltfioMaterialProvider_destroy(provider);
        FilaEngine_destroy(&engine);
        free(glbBytes);
        printf("Asset creation failed\n");
        return 1;
    }

    FilaScene* scene = FilaEngine_createScene(engine);
    if (!scene) {
        FilaGltfioAssetLoader_destroyAsset(loader, asset);
        FilaGltfioAssetLoader_destroy(&loader);
        FilaGltfioMaterialProvider_destroy(provider);
        FilaEngine_destroy(&engine);
        free(glbBytes);
        printf("Scene creation failed\n");
        return 1;
    }

    const size_t entityCount = FilaGltfioFilamentAsset_getEntityCount(asset);
    if (entityCount > 0u) {
        FilaEntity* entities = (FilaEntity*)malloc(entityCount * sizeof(FilaEntity));
        if (!entities) {
            FilaEngine_destroyScene(engine, scene);
            FilaGltfioAssetLoader_destroyAsset(loader, asset);
            FilaGltfioAssetLoader_destroy(&loader);
            FilaGltfioMaterialProvider_destroy(provider);
            FilaEngine_destroy(&engine);
            free(glbBytes);
            printf("Entity scratch allocation failed\n");
            return 1;
        }
        const size_t written = FilaGltfioFilamentAsset_getEntities(asset, entities, entityCount);
        if (written > 0u) {
            FilaGltfioFilamentAsset_addEntitiesToScene(asset, scene, entities, written, ~0u);
        }
        free(entities);
    }

    FilaGltfioFilamentAsset_addAllEntitiesToScene(asset, scene, ~0u);
    FilaGltfioFilamentAsset_removeEntitiesFromScene(asset, scene);

    {
        FilaGltfioFilamentInstance* instances[4] = {
            (FilaGltfioFilamentInstance*)0,
            (FilaGltfioFilamentInstance*)0,
            (FilaGltfioFilamentInstance*)0,
            (FilaGltfioFilamentInstance*)0,
        };
        const size_t instanceCount = FilaGltfioFilamentAsset_getAssetInstanceCount(asset);
        const size_t copied = FilaGltfioFilamentAsset_getAssetInstances(asset, instances, 4u);
        if (copied > 4u || copied > instanceCount) {
            FilaEngine_destroyScene(engine, scene);
            FilaGltfioAssetLoader_destroyAsset(loader, asset);
            FilaGltfioAssetLoader_destroy(&loader);
            FilaGltfioMaterialProvider_destroy(provider);
            FilaEngine_destroy(&engine);
            free(glbBytes);
            printf("Asset instance list contract mismatch\n");
            return 1;
        }
    }


    FilaGltfioAssetLoader_destroyAsset(loader, asset);
    FilaEngine_destroyScene(engine, scene);
    FilaGltfioAssetLoader_destroy(&loader);
    FilaGltfioMaterialProvider_destroyMaterials(provider);
    FilaGltfioMaterialProvider_destroy(provider);
    FilaEngine_destroy(&engine);
    free(glbBytes);

    printf("functionality_gltfio_load_unload_smoke completed\n");
    return 0;
}


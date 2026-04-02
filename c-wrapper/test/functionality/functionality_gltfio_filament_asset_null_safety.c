#include <stdio.h>

#include "gltfio/FilamentAsset.h"

int main(void) {
    printf("Running functionality_gltfio_filament_asset_null_safety...\n");

    FilaEntity entities[2] = {1, 1};
    FilaAabb aabb;
    char text[8] = {'x', 0};

    if (FilaGltfioFilamentAsset_getEntities((const FilaGltfioFilamentAsset*)0, entities, 2u) != 0u ||
            FilaGltfioFilamentAsset_getEntityCount((const FilaGltfioFilamentAsset*)0) != 0u ||
            FilaGltfioFilamentAsset_getLightEntities((const FilaGltfioFilamentAsset*)0, entities, 2u) != 0u ||
            FilaGltfioFilamentAsset_getLightEntityCount((const FilaGltfioFilamentAsset*)0) != 0u ||
            FilaGltfioFilamentAsset_getRenderableEntities((const FilaGltfioFilamentAsset*)0, entities, 2u) != 0u ||
            FilaGltfioFilamentAsset_getRenderableEntityCount((const FilaGltfioFilamentAsset*)0) != 0u ||
            FilaGltfioFilamentAsset_getCameraEntities((const FilaGltfioFilamentAsset*)0, entities, 2u) != 0u ||
            FilaGltfioFilamentAsset_getCameraEntityCount((const FilaGltfioFilamentAsset*)0) != 0u ||
            FilaGltfioFilamentAsset_getRoot((const FilaGltfioFilamentAsset*)0) != 0 ||
            FilaGltfioFilamentAsset_popRenderable((FilaGltfioFilamentAsset*)0) != 0 ||
            FilaGltfioFilamentAsset_popRenderables((FilaGltfioFilamentAsset*)0, entities, 2u) != 0u) {
        printf("FilamentAsset null entity/query mismatch\n");
        return 1;
    }

    if (FilaGltfioFilamentAsset_getResourceUriCount((const FilaGltfioFilamentAsset*)0) != 0u ||
            FilaGltfioFilamentAsset_getResourceUriAt((const FilaGltfioFilamentAsset*)0, 0u) != (const char*)0 ||
            FilaGltfioFilamentAsset_copyResourceUriAt((const FilaGltfioFilamentAsset*)0, 0u, text, sizeof(text)) != 0u ||
            text[0] != '\0' ||
            FilaGltfioFilamentAsset_getBoundingBox((const FilaGltfioFilamentAsset*)0, &aabb)) {
        printf("FilamentAsset null URI/AABB mismatch\n");
        return 1;
    }

    text[0] = 'x';
    if (FilaGltfioFilamentAsset_getName((const FilaGltfioFilamentAsset*)0, 0) != (const char*)0 ||
            FilaGltfioFilamentAsset_copyName((const FilaGltfioFilamentAsset*)0, 0, text, sizeof(text)) != 0u ||
            text[0] != '\0' ||
            FilaGltfioFilamentAsset_getFirstEntityByName((FilaGltfioFilamentAsset*)0, "x") != 0 ||
            FilaGltfioFilamentAsset_getEntitiesByName((const FilaGltfioFilamentAsset*)0, "x", entities, 2u) != 0u ||
            FilaGltfioFilamentAsset_getEntitiesByPrefix((const FilaGltfioFilamentAsset*)0, "x", entities, 2u) != 0u ||
            FilaGltfioFilamentAsset_getExtras((const FilaGltfioFilamentAsset*)0, 0) != (const char*)0 ||
            FilaGltfioFilamentAsset_copyExtras((const FilaGltfioFilamentAsset*)0, 0, text, sizeof(text)) != 0u ||
            text[0] != '\0' ||
            FilaGltfioFilamentAsset_getMorphTargetNameAt((const FilaGltfioFilamentAsset*)0, 0, 0u) != (const char*)0 ||
            FilaGltfioFilamentAsset_copyMorphTargetNameAt((const FilaGltfioFilamentAsset*)0, 0, 0u, text, sizeof(text)) != 0u ||
            text[0] != '\0' ||
            FilaGltfioFilamentAsset_getMorphTargetCountAt((const FilaGltfioFilamentAsset*)0, 0) != 0u ||
            FilaGltfioFilamentAsset_getWireframe((FilaGltfioFilamentAsset*)0) != 0 ||
            FilaGltfioFilamentAsset_getEngine((const FilaGltfioFilamentAsset*)0) != (FilaEngine*)0 ||
            FilaGltfioFilamentAsset_areFilamentComponentsDetached((const FilaGltfioFilamentAsset*)0) ||
            FilaGltfioFilamentAsset_getSourceAsset((FilaGltfioFilamentAsset*)0) != (const void*)0 ||
            FilaGltfioFilamentAsset_getSceneCount((const FilaGltfioFilamentAsset*)0) != 0u ||
            FilaGltfioFilamentAsset_getSceneName((const FilaGltfioFilamentAsset*)0, 0u) != (const char*)0 ||
            FilaGltfioFilamentAsset_copySceneName((const FilaGltfioFilamentAsset*)0, 0u, text, sizeof(text)) != 0u ||
            text[0] != '\0' ||
            FilaGltfioFilamentAsset_getInstance((FilaGltfioFilamentAsset*)0) != (FilaGltfioFilamentInstance*)0) {
        printf("FilamentAsset null name/scene mismatch\n");
        return 1;
    }

    FilaGltfioFilamentAsset_detachFilamentComponents((FilaGltfioFilamentAsset*)0);
    FilaGltfioFilamentAsset_releaseSourceData((FilaGltfioFilamentAsset*)0);

    printf("functionality_gltfio_filament_asset_null_safety completed\n");
    return 0;
}


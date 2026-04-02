#include "gltfio/FilamentAsset.h"

void test_headers_gltfio_filament_asset(void) {
    FilaEntity entities[4];
    FilaGltfioFilamentInstance* instances[2];
    FilaAabb aabb;
    char text[32];

    (void)FilaGltfioFilamentAsset_getEntities((const FilaGltfioFilamentAsset*)0, entities, 4u);
    (void)FilaGltfioFilamentAsset_getEntityCount((const FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getLightEntities((const FilaGltfioFilamentAsset*)0, entities, 4u);
    (void)FilaGltfioFilamentAsset_getLightEntityCount((const FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getRenderableEntities((const FilaGltfioFilamentAsset*)0, entities, 4u);
    (void)FilaGltfioFilamentAsset_getRenderableEntityCount((const FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getCameraEntities((const FilaGltfioFilamentAsset*)0, entities, 4u);
    (void)FilaGltfioFilamentAsset_getCameraEntityCount((const FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getRoot((const FilaGltfioFilamentAsset*)0);
    FilaGltfioFilamentAsset_addEntitiesToScene(
            (const FilaGltfioFilamentAsset*)0,
            (FilaScene*)0,
            entities,
            4u,
            (FilaGltfioSceneMask)0u);
    FilaGltfioFilamentAsset_addAllEntitiesToScene(
            (const FilaGltfioFilamentAsset*)0,
            (FilaScene*)0,
            (FilaGltfioSceneMask)0u);
    FilaGltfioFilamentAsset_removeEntitiesFromScene(
            (const FilaGltfioFilamentAsset*)0,
            (FilaScene*)0);
    (void)FilaGltfioFilamentAsset_popRenderable((FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_popRenderables((FilaGltfioFilamentAsset*)0, entities, 4u);
    (void)FilaGltfioFilamentAsset_getResourceUriCount((const FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getResourceUriAt((const FilaGltfioFilamentAsset*)0, 0u);
    (void)FilaGltfioFilamentAsset_copyResourceUriAt((const FilaGltfioFilamentAsset*)0, 0u, text, sizeof(text));
    (void)FilaGltfioFilamentAsset_getBoundingBox((const FilaGltfioFilamentAsset*)0, &aabb);
    (void)FilaGltfioFilamentAsset_getName((const FilaGltfioFilamentAsset*)0, 0);
    (void)FilaGltfioFilamentAsset_copyName((const FilaGltfioFilamentAsset*)0, 0, text, sizeof(text));
    (void)FilaGltfioFilamentAsset_getFirstEntityByName((FilaGltfioFilamentAsset*)0, "x");
    (void)FilaGltfioFilamentAsset_getEntitiesByName((const FilaGltfioFilamentAsset*)0, "x", entities, 4u);
    (void)FilaGltfioFilamentAsset_getEntitiesByPrefix((const FilaGltfioFilamentAsset*)0, "x", entities, 4u);
    (void)FilaGltfioFilamentAsset_getExtras((const FilaGltfioFilamentAsset*)0, 0);
    (void)FilaGltfioFilamentAsset_copyExtras((const FilaGltfioFilamentAsset*)0, 0, text, sizeof(text));
    (void)FilaGltfioFilamentAsset_getMorphTargetNameAt((const FilaGltfioFilamentAsset*)0, 0, 0u);
    (void)FilaGltfioFilamentAsset_copyMorphTargetNameAt((const FilaGltfioFilamentAsset*)0, 0, 0u, text, sizeof(text));
    (void)FilaGltfioFilamentAsset_getMorphTargetCountAt((const FilaGltfioFilamentAsset*)0, 0);
    (void)FilaGltfioFilamentAsset_getWireframe((FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getEngine((const FilaGltfioFilamentAsset*)0);
    FilaGltfioFilamentAsset_detachFilamentComponents((FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_areFilamentComponentsDetached((const FilaGltfioFilamentAsset*)0);
    FilaGltfioFilamentAsset_releaseSourceData((FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getSourceAsset((FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getSceneCount((const FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getSceneName((const FilaGltfioFilamentAsset*)0, 0u);
    (void)FilaGltfioFilamentAsset_copySceneName((const FilaGltfioFilamentAsset*)0, 0u, text, sizeof(text));
    (void)FilaGltfioFilamentAsset_getInstance((FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getAssetInstanceCount((const FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioFilamentAsset_getAssetInstances((FilaGltfioFilamentAsset*)0, instances, 2u);
}


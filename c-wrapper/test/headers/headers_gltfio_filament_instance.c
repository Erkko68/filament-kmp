#include "gltfio/FilamentInstance.h"

void test_headers_gltfio_filament_instance(void) {
    FilaEntity entities[4];
    FilaMaterialInstance* materials[4];
    char text[32];
    FilaAabb aabb;
    (void)FilaGltfioFilamentInstance_getEntities((const FilaGltfioFilamentInstance*)0, entities, 4u);
    (void)FilaGltfioFilamentInstance_getEntityCount((const FilaGltfioFilamentInstance*)0);
    (void)FilaGltfioFilamentInstance_getRoot((const FilaGltfioFilamentInstance*)0);
    FilaGltfioFilamentInstance_applyMaterialVariant((FilaGltfioFilamentInstance*)0, 0u);
    (void)FilaGltfioFilamentInstance_getMaterialVariantCount((const FilaGltfioFilamentInstance*)0);
    (void)FilaGltfioFilamentInstance_getMaterialVariantName((const FilaGltfioFilamentInstance*)0, 0u);
    (void)FilaGltfioFilamentInstance_copyMaterialVariantName((const FilaGltfioFilamentInstance*)0, 0u, text, sizeof(text));
    (void)FilaGltfioFilamentInstance_getAnimator((FilaGltfioFilamentInstance*)0);
    (void)FilaGltfioFilamentInstance_getSkinCount((const FilaGltfioFilamentInstance*)0);
    (void)FilaGltfioFilamentInstance_getSkinNameAt((const FilaGltfioFilamentInstance*)0, 0u);
    (void)FilaGltfioFilamentInstance_copySkinNameAt((const FilaGltfioFilamentInstance*)0, 0u, text, sizeof(text));
    (void)FilaGltfioFilamentInstance_getJointCountAt((const FilaGltfioFilamentInstance*)0, 0u);
    (void)FilaGltfioFilamentInstance_getJointsAt((const FilaGltfioFilamentInstance*)0, 0u, entities, 4u);
    (void)FilaGltfioFilamentInstance_getMaterialInstanceCount((const FilaGltfioFilamentInstance*)0);
    (void)FilaGltfioFilamentInstance_getMaterialInstances((const FilaGltfioFilamentInstance*)0, materials, 4u);
    FilaGltfioFilamentInstance_recomputeBoundingBoxes((FilaGltfioFilamentInstance*)0);
    (void)FilaGltfioFilamentInstance_getBoundingBox((const FilaGltfioFilamentInstance*)0, &aabb);
}


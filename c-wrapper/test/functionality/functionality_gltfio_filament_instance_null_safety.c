#include <stdio.h>

#include "gltfio/FilamentInstance.h"

int main(void) {
    printf("Running functionality_gltfio_filament_instance_null_safety...\n");

    FilaEntity entities[2] = {1, 1};
    FilaMaterialInstance* materials[2] = {(FilaMaterialInstance*)1, (FilaMaterialInstance*)1};
    FilaAabb aabb;
    char name[8] = {'x', 0};

    if (FilaGltfioFilamentInstance_getEntities((const FilaGltfioFilamentInstance*)0, entities, 2u) != 0u ||
            FilaGltfioFilamentInstance_getEntityCount((const FilaGltfioFilamentInstance*)0) != 0u ||
            FilaGltfioFilamentInstance_getRoot((const FilaGltfioFilamentInstance*)0) != 0) {
        printf("FilamentInstance null entity queries mismatch\n");
        return 1;
    }

    FilaGltfioFilamentInstance_applyMaterialVariant((FilaGltfioFilamentInstance*)0, 0u);
    if (FilaGltfioFilamentInstance_getMaterialVariantCount((const FilaGltfioFilamentInstance*)0) != 0u ||
            FilaGltfioFilamentInstance_getMaterialVariantName((const FilaGltfioFilamentInstance*)0, 0u) != (const char*)0 ||
            FilaGltfioFilamentInstance_copyMaterialVariantName((const FilaGltfioFilamentInstance*)0, 0u, name, sizeof(name)) != 0u ||
            name[0] != '\0') {
        printf("FilamentInstance null material variant mismatch\n");
        return 1;
    }

    if (FilaGltfioFilamentInstance_getAnimator((FilaGltfioFilamentInstance*)0) != (FilaGltfioAnimator*)0 ||
            FilaGltfioFilamentInstance_getSkinCount((const FilaGltfioFilamentInstance*)0) != 0u ||
            FilaGltfioFilamentInstance_getSkinNameAt((const FilaGltfioFilamentInstance*)0, 0u) != (const char*)0 ||
            FilaGltfioFilamentInstance_copySkinNameAt((const FilaGltfioFilamentInstance*)0, 0u, name, sizeof(name)) != 0u ||
            name[0] != '\0' ||
            FilaGltfioFilamentInstance_getJointCountAt((const FilaGltfioFilamentInstance*)0, 0u) != 0u ||
            FilaGltfioFilamentInstance_getJointsAt((const FilaGltfioFilamentInstance*)0, 0u, entities, 2u) != 0u ||
            FilaGltfioFilamentInstance_getMaterialInstanceCount((const FilaGltfioFilamentInstance*)0) != 0u ||
            FilaGltfioFilamentInstance_getMaterialInstances((const FilaGltfioFilamentInstance*)0, materials, 2u) != 0u ||
            FilaGltfioFilamentInstance_getBoundingBox((const FilaGltfioFilamentInstance*)0, &aabb)) {
        printf("FilamentInstance null animator/aabb mismatch\n");
        return 1;
    }

    FilaGltfioFilamentInstance_recomputeBoundingBoxes((FilaGltfioFilamentInstance*)0);

    printf("functionality_gltfio_filament_instance_null_safety completed\n");
    return 0;
}


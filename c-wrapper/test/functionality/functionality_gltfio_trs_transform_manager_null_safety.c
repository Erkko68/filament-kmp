#include <stdio.h>

#include "gltfio/TrsTransformManager.h"

int main(void) {
    printf("Running functionality_gltfio_trs_transform_manager_null_safety...\n");

    float t[3] = {9.0f, 9.0f, 9.0f};
    float r[4] = {9.0f, 9.0f, 9.0f, 9.0f};
    float s[3] = {9.0f, 9.0f, 9.0f};
    float m[16] = {9.0f};

    if (FilaGltfioTrsTransformManager_hasComponent((const FilaGltfioTrsTransformManager*)0, 1) ||
            FilaGltfioTrsTransformManager_getInstance((const FilaGltfioTrsTransformManager*)0, 1) != 0u ||
            FilaGltfioTrsTransformManager_isValidInstance((FilaGltfioTrsTransformManagerInstance)0) ||
            FilaGltfioTrsTransformManager_getTranslation((const FilaGltfioTrsTransformManager*)0,
                    (FilaGltfioTrsTransformManagerInstance)1, t) ||
            FilaGltfioTrsTransformManager_getRotation((const FilaGltfioTrsTransformManager*)0,
                    (FilaGltfioTrsTransformManagerInstance)1, r) ||
            FilaGltfioTrsTransformManager_getScale((const FilaGltfioTrsTransformManager*)0,
                    (FilaGltfioTrsTransformManagerInstance)1, s) ||
            FilaGltfioTrsTransformManager_getTransform((const FilaGltfioTrsTransformManager*)0,
                    (FilaGltfioTrsTransformManagerInstance)1, m)) {
        printf("TrsTransformManager null-safety mismatch\n");
        return 1;
    }

    FilaGltfioTrsTransformManager_create((FilaGltfioTrsTransformManager*)0, 1);
    FilaGltfioTrsTransformManager_createWithTrs((FilaGltfioTrsTransformManager*)0, 1, t, r, s);
    FilaGltfioTrsTransformManager_destroy((FilaGltfioTrsTransformManager*)0, 1);
    FilaGltfioTrsTransformManager_setTranslation((FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, t);
    FilaGltfioTrsTransformManager_setRotation((FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, r);
    FilaGltfioTrsTransformManager_setScale((FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, s);
    FilaGltfioTrsTransformManager_setTrs((FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, t, r, s);

    printf("functionality_gltfio_trs_transform_manager_null_safety completed\n");
    return 0;
}


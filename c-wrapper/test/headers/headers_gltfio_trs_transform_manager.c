#include "gltfio/TrsTransformManager.h"

void test_headers_gltfio_trs_transform_manager(void) {
    float t[3] = {0.0f, 0.0f, 0.0f};
    float r[4] = {0.0f, 0.0f, 0.0f, 1.0f};
    float s[3] = {1.0f, 1.0f, 1.0f};
    float m[16] = {0.0f};

    (void)FilaGltfioTrsTransformManager_hasComponent((const FilaGltfioTrsTransformManager*)0, 1);
    (void)FilaGltfioTrsTransformManager_getInstance((const FilaGltfioTrsTransformManager*)0, 1);
    (void)FilaGltfioTrsTransformManager_isValidInstance((FilaGltfioTrsTransformManagerInstance)0);
    FilaGltfioTrsTransformManager_create((FilaGltfioTrsTransformManager*)0, 1);
    FilaGltfioTrsTransformManager_createWithTrs((FilaGltfioTrsTransformManager*)0, 1, t, r, s);
    FilaGltfioTrsTransformManager_destroy((FilaGltfioTrsTransformManager*)0, 1);

    FilaGltfioTrsTransformManager_setTranslation((FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, t);
    (void)FilaGltfioTrsTransformManager_getTranslation((const FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, t);

    FilaGltfioTrsTransformManager_setRotation((FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, r);
    (void)FilaGltfioTrsTransformManager_getRotation((const FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, r);

    FilaGltfioTrsTransformManager_setScale((FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, s);
    (void)FilaGltfioTrsTransformManager_getScale((const FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, s);

    FilaGltfioTrsTransformManager_setTrs((FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, t, r, s);
    (void)FilaGltfioTrsTransformManager_getTransform((const FilaGltfioTrsTransformManager*)0,
            (FilaGltfioTrsTransformManagerInstance)1, m);
}


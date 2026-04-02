#include "gltfio/NodeManager.h"

void test_headers_gltfio_node_manager(void) {
    char extras[32];

    (void)FilaGltfioNodeManager_getMaxSceneCount();
    (void)FilaGltfioNodeManager_hasComponent((const FilaGltfioNodeManager*)0, 1);
    (void)FilaGltfioNodeManager_getInstance((const FilaGltfioNodeManager*)0, 1);
    (void)FilaGltfioNodeManager_isValidInstance((FilaGltfioNodeManagerInstance)0);
    FilaGltfioNodeManager_create((FilaGltfioNodeManager*)0, 1);
    FilaGltfioNodeManager_destroy((FilaGltfioNodeManager*)0, 1);
    FilaGltfioNodeManager_setExtras((FilaGltfioNodeManager*)0, (FilaGltfioNodeManagerInstance)1, "{}");
    (void)FilaGltfioNodeManager_getExtras((const FilaGltfioNodeManager*)0, (FilaGltfioNodeManagerInstance)1);
    (void)FilaGltfioNodeManager_copyExtras(
            (const FilaGltfioNodeManager*)0,
            (FilaGltfioNodeManagerInstance)1,
            extras,
            sizeof(extras));
    FilaGltfioNodeManager_setSceneMembership(
            (FilaGltfioNodeManager*)0,
            (FilaGltfioNodeManagerInstance)1,
            0x3u);
    (void)FilaGltfioNodeManager_getSceneMembership(
            (const FilaGltfioNodeManager*)0,
            (FilaGltfioNodeManagerInstance)1);
}


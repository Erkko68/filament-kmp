#include <stdio.h>

#include "gltfio/NodeManager.h"

int main(void) {
    printf("Running functionality_gltfio_node_manager_null_safety...\n");

    char extras[8] = {'x', 0};

    if (FilaGltfioNodeManager_getMaxSceneCount() == 0u ||
            FilaGltfioNodeManager_hasComponent((const FilaGltfioNodeManager*)0, 1) ||
            FilaGltfioNodeManager_getInstance((const FilaGltfioNodeManager*)0, 1) != 0u ||
            FilaGltfioNodeManager_isValidInstance((FilaGltfioNodeManagerInstance)0) ||
            FilaGltfioNodeManager_getExtras((const FilaGltfioNodeManager*)0, (FilaGltfioNodeManagerInstance)1) != (const char*)0 ||
            FilaGltfioNodeManager_copyExtras((const FilaGltfioNodeManager*)0,
                    (FilaGltfioNodeManagerInstance)1,
                    extras,
                    sizeof(extras)) != 0u ||
            extras[0] != '\0' ||
            FilaGltfioNodeManager_getSceneMembership((const FilaGltfioNodeManager*)0,
                    (FilaGltfioNodeManagerInstance)1) != 0u) {
        printf("NodeManager null-safety mismatch\n");
        return 1;
    }

    FilaGltfioNodeManager_create((FilaGltfioNodeManager*)0, 1);
    FilaGltfioNodeManager_destroy((FilaGltfioNodeManager*)0, 1);
    FilaGltfioNodeManager_setExtras((FilaGltfioNodeManager*)0, (FilaGltfioNodeManagerInstance)1, "{}");
    FilaGltfioNodeManager_setSceneMembership((FilaGltfioNodeManager*)0, (FilaGltfioNodeManagerInstance)1, 1u);

    printf("functionality_gltfio_node_manager_null_safety completed\n");
    return 0;
}


#ifndef FILAMENT_C_GLTFIO_NODE_MANAGER_H
#define FILAMENT_C_GLTFIO_NODE_MANAGER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Maximum number of glTF scenes representable in scene-membership bitmasks.
size_t FilaGltfioNodeManager_getMaxSceneCount(void);

bool FilaGltfioNodeManager_hasComponent(const FilaGltfioNodeManager* manager, FilaEntity entity);
FilaGltfioNodeManagerInstance FilaGltfioNodeManager_getInstance(
        const FilaGltfioNodeManager* manager,
        FilaEntity entity);
bool FilaGltfioNodeManager_isValidInstance(FilaGltfioNodeManagerInstance instance);

void FilaGltfioNodeManager_create(FilaGltfioNodeManager* manager, FilaEntity entity);
void FilaGltfioNodeManager_destroy(FilaGltfioNodeManager* manager, FilaEntity entity);

void FilaGltfioNodeManager_setExtras(
        FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance,
        const char* extrasJson);
const char* FilaGltfioNodeManager_getExtras(
        const FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance);
size_t FilaGltfioNodeManager_copyExtras(
        const FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance,
        char* outExtras,
        size_t outExtrasSize);

void FilaGltfioNodeManager_setSceneMembership(
        FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance,
        uint32_t sceneMask);
uint32_t FilaGltfioNodeManager_getSceneMembership(
        const FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_NODE_MANAGER_H


#ifndef FILAMENT_C_SCENE_H
#define FILAMENT_C_SCENE_H

#include <stdbool.h>
#include <stddef.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Adds an entity to the scene.
void FilaScene_addEntity(FilaScene* scene, FilaEntity entity);

// Removes an entity from the scene.
void FilaScene_removeEntity(FilaScene* scene, FilaEntity entity);

// Removes all entities from the scene.
void FilaScene_removeAllEntities(FilaScene* scene);

// Returns the number of entities currently tracked by the scene.
size_t FilaScene_getEntityCount(const FilaScene* scene);

// Returns true if the entity exists in the scene.
bool FilaScene_hasEntity(const FilaScene* scene, FilaEntity entity);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SCENE_H


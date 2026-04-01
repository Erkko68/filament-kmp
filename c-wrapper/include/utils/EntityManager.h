#ifndef FILAMENT_C_UTILS_ENTITY_MANAGER_H
#define FILAMENT_C_UTILS_ENTITY_MANAGER_H

#include <stdbool.h>
#include <stddef.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Creates a new entity handle. Returns 0 when allocation fails.
FilaEntity FilaEntityManager_create(void);

// Creates count entities and writes them to outEntities.
void FilaEntityManager_createMany(size_t count, FilaEntity* outEntities);

// Destroys an entity handle.
void FilaEntityManager_destroy(FilaEntity entity);

// Destroys count entities provided in entities and zeroes their entries.
void FilaEntityManager_destroyMany(size_t count, FilaEntity* entities);

// Returns true if the entity is currently alive.
bool FilaEntityManager_isAlive(FilaEntity entity);

// Returns the number of active entities.
size_t FilaEntityManager_getEntityCount(void);

// Returns the maximum number of simultaneous entities.
size_t FilaEntityManager_getMaxEntityCount(void);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_ENTITY_MANAGER_H



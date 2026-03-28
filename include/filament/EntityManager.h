#ifndef FILAMENT_C_ENTITY_MANAGER_H
#define FILAMENT_C_ENTITY_MANAGER_H

#include <stdbool.h>
#include <stddef.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Creates a new entity handle. Returns 0 when allocation fails.
FilaEntity FilaEntityManager_create(void);

// Destroys an entity handle.
void FilaEntityManager_destroy(FilaEntity entity);

// Returns true if the entity is currently alive.
bool FilaEntityManager_isAlive(FilaEntity entity);

// Returns the number of active entities.
size_t FilaEntityManager_getEntityCount(void);

// Returns the maximum number of simultaneous entities.
size_t FilaEntityManager_getMaxEntityCount(void);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_ENTITY_MANAGER_H


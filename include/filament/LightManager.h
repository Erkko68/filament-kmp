#ifndef FILAMENT_C_LIGHT_MANAGER_H
#define FILAMENT_C_LIGHT_MANAGER_H

#include <stdbool.h>
#include <stddef.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns true if the entity has a light component.
bool FilaLightManager_hasComponent(const FilaLightManager* manager, FilaEntity entity);

// Gets the light instance associated with the entity, or 0 if missing.
FilaLightManagerInstance FilaLightManager_getInstance(const FilaLightManager* manager, FilaEntity entity);

// Returns the number of light components.
size_t FilaLightManager_getComponentCount(const FilaLightManager* manager);

// Returns whether the manager has no components.
bool FilaLightManager_empty(const FilaLightManager* manager);

// Returns the entity associated with an instance, or 0 for invalid inputs.
FilaEntity FilaLightManager_getEntity(const FilaLightManager* manager, FilaLightManagerInstance instance);

// Writes up to maxCount entities managed by this component manager and returns number written.
size_t FilaLightManager_getEntities(const FilaLightManager* manager, FilaEntity* outEntities, size_t maxCount);

// Destroys a light component from an entity.
void FilaLightManager_destroy(FilaLightManager* manager, FilaEntity entity);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_LIGHT_MANAGER_H


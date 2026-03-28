#ifndef FILAMENT_C_TRANSFORM_MANAGER_H
#define FILAMENT_C_TRANSFORM_MANAGER_H

#include <stdbool.h>
#include <stddef.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns true if the entity has a transform component.
bool FilaTransformManager_hasComponent(const FilaTransformManager* manager, FilaEntity entity);

// Gets the transform instance associated with the entity, or 0 if missing.
FilaTransformManagerInstance FilaTransformManager_getInstance(const FilaTransformManager* manager, FilaEntity entity);

// Creates a transform component for the entity.
void FilaTransformManager_create(FilaTransformManager* manager, FilaEntity entity, FilaTransformManagerInstance parent);

// Destroys the transform component for the entity.
void FilaTransformManager_destroy(FilaTransformManager* manager, FilaEntity entity);

// Returns the number of transform components.
size_t FilaTransformManager_getComponentCount(const FilaTransformManager* manager);

// Returns whether the manager has no components.
bool FilaTransformManager_empty(const FilaTransformManager* manager);

// Returns the entity associated with an instance, or 0 for invalid inputs.
FilaEntity FilaTransformManager_getEntity(const FilaTransformManager* manager, FilaTransformManagerInstance instance);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TRANSFORM_MANAGER_H


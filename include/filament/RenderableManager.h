#ifndef FILAMENT_C_RENDERABLE_MANAGER_H
#define FILAMENT_C_RENDERABLE_MANAGER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns true if the entity has a renderable component.
bool FilaRenderableManager_hasComponent(const FilaRenderableManager* manager, FilaEntity entity);

// Gets the renderable instance associated with the entity, or 0 if missing.
FilaRenderableManagerInstance FilaRenderableManager_getInstance(const FilaRenderableManager* manager, FilaEntity entity);

// Returns the number of renderable components.
size_t FilaRenderableManager_getComponentCount(const FilaRenderableManager* manager);

// Returns whether the manager has no components.
bool FilaRenderableManager_empty(const FilaRenderableManager* manager);

// Returns the entity associated with an instance, or 0 for invalid inputs.
FilaEntity FilaRenderableManager_getEntity(const FilaRenderableManager* manager, FilaRenderableManagerInstance instance);

// Writes up to maxCount entities managed by this component manager and returns number written.
size_t FilaRenderableManager_getEntities(const FilaRenderableManager* manager, FilaEntity* outEntities, size_t maxCount);

// Destroys a renderable component from an entity.
void FilaRenderableManager_destroy(FilaRenderableManager* manager, FilaEntity entity);

// Returns the primitive count for an instance.
size_t FilaRenderableManager_getPrimitiveCount(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Sets bits in a renderable instance layer mask.
void FilaRenderableManager_setLayerMask(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		uint8_t select,
		uint8_t values);

// Gets layer mask bits for a renderable instance.
uint8_t FilaRenderableManager_getLayerMask(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_RENDERABLE_MANAGER_H


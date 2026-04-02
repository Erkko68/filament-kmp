#ifndef FILAMENT_C_UTILS_NAME_COMPONENT_MANAGER_H
#define FILAMENT_C_UTILS_NAME_COMPONENT_MANAGER_H

#include <stdbool.h>
#include <stddef.h>

#include "EntityInstance.h"
#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Creates a name manager backed by the global EntityManager.
FilaNameComponentManager* FilaNameComponentManager_create(void);

// Destroys a name manager.
void FilaNameComponentManager_destroy(FilaNameComponentManager* manager);

// Returns true if the entity has an attached name component.
bool FilaNameComponentManager_hasComponent(const FilaNameComponentManager* manager, FilaEntity entity);

// Returns the component instance for an entity, or 0 if missing.
FilaEntityInstance FilaNameComponentManager_getInstance(const FilaNameComponentManager* manager, FilaEntity entity);

// Adds/removes a name component on an entity.
void FilaNameComponentManager_addComponent(FilaNameComponentManager* manager, FilaEntity entity);
void FilaNameComponentManager_removeComponent(FilaNameComponentManager* manager, FilaEntity entity);

// Associates name with a component instance.
void FilaNameComponentManager_setName(FilaNameComponentManager* manager, FilaEntityInstance instance, const char* name);

// Returns a weak pointer to the stored name, or NULL.
const char* FilaNameComponentManager_getName(const FilaNameComponentManager* manager, FilaEntityInstance instance);

// Copies stored name into outName and returns source length (excluding null terminator).
size_t FilaNameComponentManager_copyName(const FilaNameComponentManager* manager,
        FilaEntityInstance instance,
        char* outName,
        size_t outNameSize);

// Removes stale components whose entities no longer exist.
void FilaNameComponentManager_gc(FilaNameComponentManager* manager);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_NAME_COMPONENT_MANAGER_H


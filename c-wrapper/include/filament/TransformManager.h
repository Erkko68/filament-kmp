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

// Creates a transform component initialized with a local 4x4 column-major float matrix.
void FilaTransformManager_createWithTransformMat4f(FilaTransformManager* manager,
		FilaEntity entity,
		FilaTransformManagerInstance parent,
		const float localTransform[16]);

// Creates a transform component initialized with a local 4x4 column-major double matrix.
void FilaTransformManager_createWithTransformMat4(FilaTransformManager* manager,
		FilaEntity entity,
		FilaTransformManagerInstance parent,
		const double localTransform[16]);

// Destroys the transform component for the entity.
void FilaTransformManager_destroy(FilaTransformManager* manager, FilaEntity entity);

// Returns the number of transform components.
size_t FilaTransformManager_getComponentCount(const FilaTransformManager* manager);

// Returns whether the manager has no components.
bool FilaTransformManager_empty(const FilaTransformManager* manager);

// Enables or disables accurate translation mode.
void FilaTransformManager_setAccurateTranslationsEnabled(FilaTransformManager* manager, bool enable);

// Returns whether accurate translation mode is enabled.
bool FilaTransformManager_isAccurateTranslationsEnabled(const FilaTransformManager* manager);

// Writes up to maxCount entities managed by this component manager and returns number written.
size_t FilaTransformManager_getEntities(const FilaTransformManager* manager,
		FilaEntity* outEntities,
		size_t maxCount);

// Returns the entity associated with an instance, or 0 for invalid inputs.
FilaEntity FilaTransformManager_getEntity(const FilaTransformManager* manager, FilaTransformManagerInstance instance);

// Reparents an instance to a new parent instance (0 means no parent).
void FilaTransformManager_setParent(FilaTransformManager* manager,
		FilaTransformManagerInstance instance,
		FilaTransformManagerInstance newParent);

// Returns the parent entity for an instance, or 0 when there is no parent/invalid input.
FilaEntity FilaTransformManager_getParent(const FilaTransformManager* manager,
		FilaTransformManagerInstance instance);

// Returns the number of children for an instance.
size_t FilaTransformManager_getChildCount(const FilaTransformManager* manager,
		FilaTransformManagerInstance instance);

// Writes up to maxCount child entities into outChildren and returns number written.
size_t FilaTransformManager_getChildren(const FilaTransformManager* manager,
		FilaTransformManagerInstance instance,
		FilaEntity* outChildren,
		size_t maxCount);

// Sets a local transform from a 4x4 column-major float matrix.
void FilaTransformManager_setTransformMat4f(FilaTransformManager* manager,
		FilaTransformManagerInstance instance,
		const float localTransform[16]);

// Gets the local transform as a 4x4 column-major float matrix. Returns false on invalid input.
bool FilaTransformManager_getTransformMat4f(const FilaTransformManager* manager,
		FilaTransformManagerInstance instance,
		float outLocalTransform[16]);

// Gets the world transform as a 4x4 column-major float matrix. Returns false on invalid input.
bool FilaTransformManager_getWorldTransformMat4f(const FilaTransformManager* manager,
		FilaTransformManagerInstance instance,
		float outWorldTransform[16]);

// Sets a local transform from a 4x4 column-major double matrix.
void FilaTransformManager_setTransformMat4(FilaTransformManager* manager,
		FilaTransformManagerInstance instance,
		const double localTransform[16]);

// Gets the local transform as a 4x4 column-major double matrix. Returns false on invalid input.
bool FilaTransformManager_getTransformMat4(const FilaTransformManager* manager,
		FilaTransformManagerInstance instance,
		double outLocalTransform[16]);

// Gets the world transform as a 4x4 column-major double matrix. Returns false on invalid input.
bool FilaTransformManager_getWorldTransformMat4(const FilaTransformManager* manager,
		FilaTransformManagerInstance instance,
		double outWorldTransform[16]);

// Opens a local transform transaction.
void FilaTransformManager_openLocalTransformTransaction(FilaTransformManager* manager);

// Commits the current local transform transaction.
void FilaTransformManager_commitLocalTransformTransaction(FilaTransformManager* manager);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TRANSFORM_MANAGER_H


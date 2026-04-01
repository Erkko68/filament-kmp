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

// Sets or unsets the scene skybox.
void FilaScene_setSkybox(FilaScene* scene, FilaSkybox* skybox);

// Returns the skybox currently associated with the scene.
FilaSkybox* FilaScene_getSkybox(FilaScene* scene);

// Returns the skybox currently associated with the scene through a const scene handle.
const FilaSkybox* FilaScene_getSkyboxConst(const FilaScene* scene);

// Sets or unsets the scene indirect light.
void FilaScene_setIndirectLight(FilaScene* scene, FilaIndirectLight* indirectLight);

// Returns the indirect light currently associated with the scene.
FilaIndirectLight* FilaScene_getIndirectLight(FilaScene* scene);

// Returns the indirect light currently associated with the scene through a const scene handle.
const FilaIndirectLight* FilaScene_getIndirectLightConst(const FilaScene* scene);

// Adds a list of entities to the scene.
void FilaScene_addEntities(FilaScene* scene, const FilaEntity* entities, size_t count);

// Removes a list of entities from the scene.
void FilaScene_removeEntities(FilaScene* scene, const FilaEntity* entities, size_t count);

// Returns the number of active renderables currently in the scene.
size_t FilaScene_getRenderableCount(const FilaScene* scene);

// Returns the number of active lights currently in the scene.
size_t FilaScene_getLightCount(const FilaScene* scene);

typedef void (*FilaSceneEntityCallback)(FilaEntity entity, void* userData);

// Invokes callback for each entity currently present in the scene.
void FilaScene_forEach(
    const FilaScene* scene,
    FilaSceneEntityCallback callback,
    void* userData);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SCENE_H


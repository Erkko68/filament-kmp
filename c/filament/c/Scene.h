#ifndef FILAMENT_C_SCENE_H
#define FILAMENT_C_SCENE_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

// Scene. Counts are uint32_t, not size_t: the width must match on every target (wasm32 included).
void FilaScene_setSkybox(FilaScene* scene, FilaSkybox* skybox);
void FilaScene_setIndirectLight(FilaScene* scene, FilaIndirectLight* indirectLight);

void FilaScene_addEntity(FilaScene* scene, FilaEntity entity);
void FilaScene_addEntities(FilaScene* scene, const FilaEntity* entities, uint32_t count);

void FilaScene_remove(FilaScene* scene, FilaEntity entity);
void FilaScene_removeEntities(FilaScene* scene, const FilaEntity* entities, uint32_t count);

uint32_t FilaScene_getEntityCount(const FilaScene* scene);
uint32_t FilaScene_getRenderableCount(const FilaScene* scene);
uint32_t FilaScene_getLightCount(const FilaScene* scene);

bool FilaScene_hasEntity(const FilaScene* scene, FilaEntity entity);

void FilaScene_getEntities(const FilaScene* scene, FilaEntity* out, uint32_t length);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SCENE_H

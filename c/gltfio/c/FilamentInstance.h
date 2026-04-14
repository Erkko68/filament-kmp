#ifndef GLTFIO_C_FILAMENT_INSTANCE_H
#define GLTFIO_C_FILAMENT_INSTANCE_H

#include "../../filament/c/FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaFilamentAsset* FilaFilamentInstance_getAsset(FilaFilamentInstance* instance);

size_t FilaFilamentInstance_getEntityCount(FilaFilamentInstance* instance);
void FilaFilamentInstance_getEntities(FilaFilamentInstance* instance, FilaEntity* entities);

FilaEntity FilaFilamentInstance_getRoot(FilaFilamentInstance* instance);
FilaAnimator* FilaFilamentInstance_getAnimator(FilaFilamentInstance* instance);
FilaBox FilaFilamentInstance_getBoundingBox(FilaFilamentInstance* instance);

const char* FilaFilamentInstance_getName(FilaFilamentInstance* instance, FilaEntity entity);

#ifdef __cplusplus
}
#endif

#endif // GLTFIO_C_FILAMENT_INSTANCE_H

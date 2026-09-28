#ifndef GLTFIO_C_FILAMENT_INSTANCE_H
#define GLTFIO_C_FILAMENT_INSTANCE_H

#include "GltfioTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaFilamentAsset* FilaFilamentInstance_getAsset(FilaFilamentInstance* instance);

uint32_t FilaFilamentInstance_getEntityCount(FilaFilamentInstance* instance);
void FilaFilamentInstance_getEntities(FilaFilamentInstance* instance, FilaEntity* entities);

FilaEntity FilaFilamentInstance_getRoot(FilaFilamentInstance* instance);
FilaAnimator* FilaFilamentInstance_getAnimator(FilaFilamentInstance* instance);
void FilaFilamentInstance_getBoundingBox(FilaFilamentInstance* instance, float center[3], float halfExtent[3]);

const char* FilaFilamentInstance_getName(FilaFilamentInstance* instance, FilaEntity entity);

uint32_t FilaFilamentInstance_getSkinCount(FilaFilamentInstance* instance);
const char* FilaFilamentInstance_getSkinNameAt(FilaFilamentInstance* instance, uint32_t skinIndex);
void FilaFilamentInstance_attachSkin(FilaFilamentInstance* instance, uint32_t skinIndex, FilaEntity entity);
void FilaFilamentInstance_detachSkin(FilaFilamentInstance* instance, uint32_t skinIndex, FilaEntity entity);
uint32_t FilaFilamentInstance_getJointCountAt(FilaFilamentInstance* instance, uint32_t skinIndex);
void FilaFilamentInstance_getJointsAt(FilaFilamentInstance* instance, uint32_t skinIndex, FilaEntity* joints);

void FilaFilamentInstance_applyMaterialVariant(FilaFilamentInstance* instance, uint32_t variantIndex);
uint32_t FilaFilamentInstance_getMaterialInstanceCount(FilaFilamentInstance* instance);
FilaMaterialInstance* FilaFilamentInstance_getMaterialInstanceAt(FilaFilamentInstance* instance, uint32_t index);
uint32_t FilaFilamentInstance_getMaterialVariantCount(FilaFilamentInstance* instance);
const char* FilaFilamentInstance_getMaterialVariantNameAt(FilaFilamentInstance* instance, uint32_t variantIndex);

#ifdef __cplusplus
}
#endif

#endif // GLTFIO_C_FILAMENT_INSTANCE_H

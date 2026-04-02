#ifndef FILAMENT_C_GLTFIO_TRS_TRANSFORM_MANAGER_H
#define FILAMENT_C_GLTFIO_TRS_TRANSFORM_MANAGER_H

#include <stdbool.h>
#include <stdint.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

bool FilaGltfioTrsTransformManager_hasComponent(
        const FilaGltfioTrsTransformManager* manager,
        FilaEntity entity);

FilaGltfioTrsTransformManagerInstance FilaGltfioTrsTransformManager_getInstance(
        const FilaGltfioTrsTransformManager* manager,
        FilaEntity entity);

bool FilaGltfioTrsTransformManager_isValidInstance(FilaGltfioTrsTransformManagerInstance instance);

void FilaGltfioTrsTransformManager_create(
        FilaGltfioTrsTransformManager* manager,
        FilaEntity entity);

void FilaGltfioTrsTransformManager_createWithTrs(
        FilaGltfioTrsTransformManager* manager,
        FilaEntity entity,
        const float translation3[3],
        const float rotation4[4],
        const float scale3[3]);

void FilaGltfioTrsTransformManager_destroy(
        FilaGltfioTrsTransformManager* manager,
        FilaEntity entity);

void FilaGltfioTrsTransformManager_setTranslation(
        FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        const float translation3[3]);

bool FilaGltfioTrsTransformManager_getTranslation(
        const FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        float outTranslation3[3]);

void FilaGltfioTrsTransformManager_setRotation(
        FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        const float rotation4[4]);

bool FilaGltfioTrsTransformManager_getRotation(
        const FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        float outRotation4[4]);

void FilaGltfioTrsTransformManager_setScale(
        FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        const float scale3[3]);

bool FilaGltfioTrsTransformManager_getScale(
        const FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        float outScale3[3]);

void FilaGltfioTrsTransformManager_setTrs(
        FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        const float translation3[3],
        const float rotation4[4],
        const float scale3[3]);

bool FilaGltfioTrsTransformManager_getTransform(
        const FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        float outMat4[16]);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_TRS_TRANSFORM_MANAGER_H


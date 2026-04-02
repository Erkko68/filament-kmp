#ifndef FILAMENT_C_GLTFIO_FILAMENT_ASSET_H
#define FILAMENT_C_GLTFIO_FILAMENT_ASSET_H

#include <stdbool.h>
#include <stddef.h>

#include "../filament/Box.h"
#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

size_t FilaGltfioFilamentAsset_getEntities(const FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount);
size_t FilaGltfioFilamentAsset_getEntityCount(const FilaGltfioFilamentAsset* asset);

size_t FilaGltfioFilamentAsset_getLightEntities(const FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount);
size_t FilaGltfioFilamentAsset_getLightEntityCount(const FilaGltfioFilamentAsset* asset);

size_t FilaGltfioFilamentAsset_getRenderableEntities(const FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount);
size_t FilaGltfioFilamentAsset_getRenderableEntityCount(const FilaGltfioFilamentAsset* asset);

size_t FilaGltfioFilamentAsset_getCameraEntities(const FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount);
size_t FilaGltfioFilamentAsset_getCameraEntityCount(const FilaGltfioFilamentAsset* asset);

FilaEntity FilaGltfioFilamentAsset_getRoot(const FilaGltfioFilamentAsset* asset);
FilaEntity FilaGltfioFilamentAsset_popRenderable(FilaGltfioFilamentAsset* asset);
size_t FilaGltfioFilamentAsset_popRenderables(
        FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount);

size_t FilaGltfioFilamentAsset_getResourceUriCount(const FilaGltfioFilamentAsset* asset);
const char* FilaGltfioFilamentAsset_getResourceUriAt(const FilaGltfioFilamentAsset* asset, size_t uriIndex);
size_t FilaGltfioFilamentAsset_copyResourceUriAt(
        const FilaGltfioFilamentAsset* asset,
        size_t uriIndex,
        char* outUri,
        size_t outUriSize);

bool FilaGltfioFilamentAsset_getBoundingBox(const FilaGltfioFilamentAsset* asset, FilaAabb* outAabb);

const char* FilaGltfioFilamentAsset_getName(const FilaGltfioFilamentAsset* asset, FilaEntity entity);
size_t FilaGltfioFilamentAsset_copyName(const FilaGltfioFilamentAsset* asset,
        FilaEntity entity,
        char* outName,
        size_t outNameSize);

FilaEntity FilaGltfioFilamentAsset_getFirstEntityByName(FilaGltfioFilamentAsset* asset, const char* name);
size_t FilaGltfioFilamentAsset_getEntitiesByName(const FilaGltfioFilamentAsset* asset,
        const char* name,
        FilaEntity* outEntities,
        size_t maxCount);
size_t FilaGltfioFilamentAsset_getEntitiesByPrefix(const FilaGltfioFilamentAsset* asset,
        const char* prefix,
        FilaEntity* outEntities,
        size_t maxCount);

const char* FilaGltfioFilamentAsset_getExtras(const FilaGltfioFilamentAsset* asset, FilaEntity entity);
size_t FilaGltfioFilamentAsset_copyExtras(const FilaGltfioFilamentAsset* asset,
        FilaEntity entity,
        char* outExtras,
        size_t outExtrasSize);

const char* FilaGltfioFilamentAsset_getMorphTargetNameAt(const FilaGltfioFilamentAsset* asset,
        FilaEntity entity,
        size_t targetIndex);
size_t FilaGltfioFilamentAsset_copyMorphTargetNameAt(const FilaGltfioFilamentAsset* asset,
        FilaEntity entity,
        size_t targetIndex,
        char* outName,
        size_t outNameSize);
size_t FilaGltfioFilamentAsset_getMorphTargetCountAt(const FilaGltfioFilamentAsset* asset, FilaEntity entity);
FilaEntity FilaGltfioFilamentAsset_getWireframe(FilaGltfioFilamentAsset* asset);

void FilaGltfioFilamentAsset_releaseSourceData(FilaGltfioFilamentAsset* asset);
const void* FilaGltfioFilamentAsset_getSourceAsset(FilaGltfioFilamentAsset* asset);

size_t FilaGltfioFilamentAsset_getSceneCount(const FilaGltfioFilamentAsset* asset);
const char* FilaGltfioFilamentAsset_getSceneName(const FilaGltfioFilamentAsset* asset, size_t sceneIndex);
size_t FilaGltfioFilamentAsset_copySceneName(const FilaGltfioFilamentAsset* asset,
        size_t sceneIndex,
        char* outSceneName,
        size_t outSceneNameSize);

FilaGltfioFilamentInstance* FilaGltfioFilamentAsset_getInstance(FilaGltfioFilamentAsset* asset);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_FILAMENT_ASSET_H


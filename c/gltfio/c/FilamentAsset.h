#ifndef GLTFIO_C_FILAMENT_ASSET_H
#define GLTFIO_C_FILAMENT_ASSET_H

#include "GltfioTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaEntity FilaFilamentAsset_getRoot(FilaFilamentAsset* asset);
uint32_t FilaFilamentAsset_getEntityCount(FilaFilamentAsset* asset);
void FilaFilamentAsset_getEntities(FilaFilamentAsset* asset, FilaEntity* entities);

uint32_t FilaFilamentAsset_getLightEntityCount(FilaFilamentAsset* asset);
void FilaFilamentAsset_getLightEntities(FilaFilamentAsset* asset, FilaEntity* entities);

uint32_t FilaFilamentAsset_getRenderableEntityCount(FilaFilamentAsset* asset);
void FilaFilamentAsset_getRenderableEntities(FilaFilamentAsset* asset, FilaEntity* entities);

uint32_t FilaFilamentAsset_getCameraEntityCount(FilaFilamentAsset* asset);
void FilaFilamentAsset_getCameraEntities(FilaFilamentAsset* asset, FilaEntity* entities);

FilaEntity FilaFilamentAsset_popRenderable(FilaFilamentAsset* asset);
uint32_t FilaFilamentAsset_popRenderables(FilaFilamentAsset* asset, FilaEntity* entities, uint32_t count);

void FilaFilamentAsset_getBoundingBox(FilaFilamentAsset* asset, float center[3], float halfExtent[3]);
const char* FilaFilamentAsset_getName(FilaFilamentAsset* asset, FilaEntity entity);
FilaEntity FilaFilamentAsset_getFirstEntityByName(FilaFilamentAsset* asset, const char* name);

uint32_t FilaFilamentAsset_getEntitiesByName(FilaFilamentAsset* asset, const char* name, FilaEntity* entities, uint32_t maxCount);
uint32_t FilaFilamentAsset_getEntitiesByPrefix(FilaFilamentAsset* asset, const char* prefix, FilaEntity* entities, uint32_t maxCount);

const char* FilaFilamentAsset_getExtras(FilaFilamentAsset* asset, FilaEntity entity);

uint32_t FilaFilamentAsset_getMorphTargetCountAt(FilaFilamentAsset* asset, FilaEntity entity);
const char* FilaFilamentAsset_getMorphTargetNameAt(FilaFilamentAsset* asset, FilaEntity entity, uint32_t targetIndex);

uint32_t FilaFilamentAsset_getResourceUriCount(FilaFilamentAsset* asset);
const char* FilaFilamentAsset_getResourceUriAt(FilaFilamentAsset* asset, uint32_t index);

uint32_t FilaFilamentAsset_getAssetInstanceCount(FilaFilamentAsset* asset);
FilaFilamentInstance* FilaFilamentAsset_getAssetInstanceAt(FilaFilamentAsset* asset, uint32_t index);

void FilaFilamentAsset_releaseSourceData(FilaFilamentAsset* asset);

FilaEngine* FilaFilamentAsset_getEngine(const FilaFilamentAsset* asset);
FilaFilamentInstance* FilaFilamentAsset_getInstance(FilaFilamentAsset* asset);

#ifdef __cplusplus
}
#endif

#endif // GLTFIO_C_FILAMENT_ASSET_H

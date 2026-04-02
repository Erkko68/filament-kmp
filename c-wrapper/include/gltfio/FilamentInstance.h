#ifndef FILAMENT_C_GLTFIO_FILAMENT_INSTANCE_H
#define FILAMENT_C_GLTFIO_FILAMENT_INSTANCE_H

#include <stdbool.h>
#include <stddef.h>

#include "../filament/Box.h"
#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Gets the entity list for this instance; returns number written to outEntities.
size_t FilaGltfioFilamentInstance_getEntities(const FilaGltfioFilamentInstance* instance,
        FilaEntity* outEntities,
        size_t maxCount);

// Returns the total number of entities in the instance.
size_t FilaGltfioFilamentInstance_getEntityCount(const FilaGltfioFilamentInstance* instance);

// Returns the transform root entity for this instance.
FilaEntity FilaGltfioFilamentInstance_getRoot(const FilaGltfioFilamentInstance* instance);

// Applies a material variant to all primitives in this instance.
void FilaGltfioFilamentInstance_applyMaterialVariant(FilaGltfioFilamentInstance* instance, size_t variantIndex);

// Returns the number of material variants available.
size_t FilaGltfioFilamentInstance_getMaterialVariantCount(const FilaGltfioFilamentInstance* instance);

// Returns a weak pointer to a material variant name, or NULL if unavailable.
const char* FilaGltfioFilamentInstance_getMaterialVariantName(const FilaGltfioFilamentInstance* instance, size_t variantIndex);

// Copies a material variant name into outName and returns source length.
size_t FilaGltfioFilamentInstance_copyMaterialVariantName(const FilaGltfioFilamentInstance* instance,
        size_t variantIndex,
        char* outName,
        size_t outNameSize);

// Returns the animator associated with this instance.
FilaGltfioAnimator* FilaGltfioFilamentInstance_getAnimator(FilaGltfioFilamentInstance* instance);

// Returns the number of skins on this instance.
size_t FilaGltfioFilamentInstance_getSkinCount(const FilaGltfioFilamentInstance* instance);

// Returns a weak pointer to the skin name, or NULL if unavailable.
const char* FilaGltfioFilamentInstance_getSkinNameAt(const FilaGltfioFilamentInstance* instance, size_t skinIndex);

// Copies a skin name into outName and returns source length.
size_t FilaGltfioFilamentInstance_copySkinNameAt(const FilaGltfioFilamentInstance* instance,
        size_t skinIndex,
        char* outName,
        size_t outNameSize);

// Returns the joint count for a skin index.
size_t FilaGltfioFilamentInstance_getJointCountAt(const FilaGltfioFilamentInstance* instance, size_t skinIndex);

// Writes up to maxCount joint entities for the given skin index.
size_t FilaGltfioFilamentInstance_getJointsAt(const FilaGltfioFilamentInstance* instance,
        size_t skinIndex,
        FilaEntity* outJoints,
        size_t maxCount);

// Returns number of material instances bound to this instance.
size_t FilaGltfioFilamentInstance_getMaterialInstanceCount(const FilaGltfioFilamentInstance* instance);

// Writes up to maxCount material-instance handles.
size_t FilaGltfioFilamentInstance_getMaterialInstances(const FilaGltfioFilamentInstance* instance,
        FilaMaterialInstance** outMaterialInstances,
        size_t maxCount);

// Recomputes renderable bounding boxes for malformed assets.
void FilaGltfioFilamentInstance_recomputeBoundingBoxes(FilaGltfioFilamentInstance* instance);

// Writes this instance's AABB into outAabb.
bool FilaGltfioFilamentInstance_getBoundingBox(const FilaGltfioFilamentInstance* instance, FilaAabb* outAabb);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_FILAMENT_INSTANCE_H


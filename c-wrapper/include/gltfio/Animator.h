#ifndef FILAMENT_C_GLTFIO_ANIMATOR_H
#define FILAMENT_C_GLTFIO_ANIMATOR_H

#include <stddef.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Applies an animation to transforms in the associated glTF instance.
void FilaGltfioAnimator_applyAnimation(const FilaGltfioAnimator* animator, size_t animationIndex, float timeSeconds);

// Updates skinning matrices after applying transforms.
void FilaGltfioAnimator_updateBoneMatrices(FilaGltfioAnimator* animator);

// Blends from a previous animation frame toward the current frame.
void FilaGltfioAnimator_applyCrossFade(FilaGltfioAnimator* animator,
        size_t previousAnimIndex,
        float previousAnimTimeSeconds,
        float alpha);

// Resets all skinning matrices to identity.
void FilaGltfioAnimator_resetBoneMatrices(FilaGltfioAnimator* animator);

// Returns the number of animations in the asset.
size_t FilaGltfioAnimator_getAnimationCount(const FilaGltfioAnimator* animator);

// Returns the duration (seconds) for the given animation index.
float FilaGltfioAnimator_getAnimationDuration(const FilaGltfioAnimator* animator, size_t animationIndex);

// Returns a weak pointer to the animation name, or NULL on invalid input.
const char* FilaGltfioAnimator_getAnimationName(const FilaGltfioAnimator* animator, size_t animationIndex);

// Copies the animation name into outName and returns source length (excluding null terminator).
size_t FilaGltfioAnimator_copyAnimationName(const FilaGltfioAnimator* animator,
        size_t animationIndex,
        char* outName,
        size_t outNameSize);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_ANIMATOR_H


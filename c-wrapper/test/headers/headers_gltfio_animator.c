#include "gltfio/Animator.h"

void test_headers_gltfio_animator(void) {
    char text[32];
    FilaGltfioAnimator_applyAnimation((const FilaGltfioAnimator*)0, 0u, 0.0f);
    FilaGltfioAnimator_updateBoneMatrices((FilaGltfioAnimator*)0);
    FilaGltfioAnimator_applyCrossFade((FilaGltfioAnimator*)0, 0u, 0.0f, 0.5f);
    FilaGltfioAnimator_resetBoneMatrices((FilaGltfioAnimator*)0);
    (void)FilaGltfioAnimator_getAnimationCount((const FilaGltfioAnimator*)0);
    (void)FilaGltfioAnimator_getAnimationDuration((const FilaGltfioAnimator*)0, 0u);
    (void)FilaGltfioAnimator_getAnimationName((const FilaGltfioAnimator*)0, 0u);
    (void)FilaGltfioAnimator_copyAnimationName((const FilaGltfioAnimator*)0, 0u, text, sizeof(text));
}


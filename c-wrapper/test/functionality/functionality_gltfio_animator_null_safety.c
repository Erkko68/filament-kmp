#include <stdio.h>

#include "gltfio/Animator.h"

int main(void) {
    printf("Running functionality_gltfio_animator_null_safety...\n");

    char name[8] = {'x', 0};
    FilaGltfioAnimator_applyAnimation((const FilaGltfioAnimator*)0, 0u, 0.0f);
    FilaGltfioAnimator_updateBoneMatrices((FilaGltfioAnimator*)0);
    FilaGltfioAnimator_applyCrossFade((FilaGltfioAnimator*)0, 0u, 0.0f, 0.5f);
    FilaGltfioAnimator_resetBoneMatrices((FilaGltfioAnimator*)0);

    if (FilaGltfioAnimator_getAnimationCount((const FilaGltfioAnimator*)0) != 0u ||
            FilaGltfioAnimator_getAnimationDuration((const FilaGltfioAnimator*)0, 0u) != 0.0f ||
            FilaGltfioAnimator_getAnimationName((const FilaGltfioAnimator*)0, 0u) != (const char*)0) {
        printf("Animator null defaults mismatch\n");
        return 1;
    }

    if (FilaGltfioAnimator_copyAnimationName((const FilaGltfioAnimator*)0, 0u, name, sizeof(name)) != 0u ||
            name[0] != '\0') {
        printf("Animator copy null-safety mismatch\n");
        return 1;
    }

    printf("functionality_gltfio_animator_null_safety completed\n");
    return 0;
}


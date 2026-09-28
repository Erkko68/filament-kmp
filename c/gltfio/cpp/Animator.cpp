#include <gltfio/Animator.h>
#include "../c/Animator.h"

using namespace filament;
using namespace filament::gltfio;

extern "C" {

void FilaAnimator_applyAnimation(FilaAnimator* animator, uint32_t animationIndex, float time) {
    ((Animator*) animator)->applyAnimation(animationIndex, time);
}

void FilaAnimator_updateBoneMatrices(FilaAnimator* animator) {
    ((Animator*) animator)->updateBoneMatrices();
}

uint32_t FilaAnimator_getAnimationCount(FilaAnimator* animator) {
    return ((Animator*) animator)->getAnimationCount();
}

float FilaAnimator_getAnimationDuration(FilaAnimator* animator, uint32_t animationIndex) {
    return ((Animator*) animator)->getAnimationDuration(animationIndex);
}

const char* FilaAnimator_getAnimationName(FilaAnimator* animator, uint32_t animationIndex) {
    return ((Animator*) animator)->getAnimationName(animationIndex);
}

void FilaAnimator_applyCrossFade(FilaAnimator* animator, uint32_t previousAnimationIndex, float previousAnimationTime, float alpha) {
    ((Animator*) animator)->applyCrossFade(previousAnimationIndex, previousAnimationTime, alpha);
}

void FilaAnimator_resetBoneMatrices(FilaAnimator* animator) {
    ((Animator*) animator)->resetBoneMatrices();
}

}

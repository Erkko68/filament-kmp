#include <gltfio/Animator.h>

#include <cstring>

#include "../../include/gltfio/Animator.h"

namespace {

size_t copyCString(const char* text, char* outText, size_t outTextSize) {
    if (!text) {
        if (outText && outTextSize > 0u) {
            outText[0] = '\0';
        }
        return 0u;
    }

    const size_t length = std::strlen(text);
    if (!outText || outTextSize == 0u) {
        return length;
    }

    const size_t written = (length < (outTextSize - 1u)) ? length : (outTextSize - 1u);
    std::memcpy(outText, text, written);
    outText[written] = '\0';
    return length;
}

} // namespace

extern "C" {

void FilaGltfioAnimator_applyAnimation(const FilaGltfioAnimator* animator, size_t animationIndex, float timeSeconds) {
    if (!animator) {
        return;
    }
    auto* cppAnimator = reinterpret_cast<const filament::gltfio::Animator*>(animator);
    cppAnimator->applyAnimation(animationIndex, timeSeconds);
}

void FilaGltfioAnimator_updateBoneMatrices(FilaGltfioAnimator* animator) {
    if (!animator) {
        return;
    }
    auto* cppAnimator = reinterpret_cast<filament::gltfio::Animator*>(animator);
    cppAnimator->updateBoneMatrices();
}

void FilaGltfioAnimator_applyCrossFade(FilaGltfioAnimator* animator,
        size_t previousAnimIndex,
        float previousAnimTimeSeconds,
        float alpha) {
    if (!animator) {
        return;
    }
    auto* cppAnimator = reinterpret_cast<filament::gltfio::Animator*>(animator);
    cppAnimator->applyCrossFade(previousAnimIndex, previousAnimTimeSeconds, alpha);
}

void FilaGltfioAnimator_resetBoneMatrices(FilaGltfioAnimator* animator) {
    if (!animator) {
        return;
    }
    auto* cppAnimator = reinterpret_cast<filament::gltfio::Animator*>(animator);
    cppAnimator->resetBoneMatrices();
}

size_t FilaGltfioAnimator_getAnimationCount(const FilaGltfioAnimator* animator) {
    if (!animator) {
        return 0u;
    }
    auto* cppAnimator = reinterpret_cast<const filament::gltfio::Animator*>(animator);
    return cppAnimator->getAnimationCount();
}

float FilaGltfioAnimator_getAnimationDuration(const FilaGltfioAnimator* animator, size_t animationIndex) {
    if (!animator) {
        return 0.0f;
    }
    auto* cppAnimator = reinterpret_cast<const filament::gltfio::Animator*>(animator);
    return cppAnimator->getAnimationDuration(animationIndex);
}

const char* FilaGltfioAnimator_getAnimationName(const FilaGltfioAnimator* animator, size_t animationIndex) {
    if (!animator) {
        return nullptr;
    }
    auto* cppAnimator = reinterpret_cast<const filament::gltfio::Animator*>(animator);
    return cppAnimator->getAnimationName(animationIndex);
}

size_t FilaGltfioAnimator_copyAnimationName(const FilaGltfioAnimator* animator,
        size_t animationIndex,
        char* outName,
        size_t outNameSize) {
    const char* name = FilaGltfioAnimator_getAnimationName(animator, animationIndex);
    return copyCString(name, outName, outNameSize);
}

} // extern "C"


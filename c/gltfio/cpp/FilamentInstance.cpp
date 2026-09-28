#include <gltfio/FilamentInstance.h>
#include <gltfio/FilamentAsset.h>
#include <gltfio/Animator.h>
#include <filament/Box.h>
#include <utils/Entity.h>

#include "../c/FilamentInstance.h"

using namespace filament;
using namespace filament::gltfio;

extern "C" {

FilaFilamentAsset* FilaFilamentInstance_getAsset(FilaFilamentInstance* instance) {
    return (FilaFilamentAsset*) ((FilamentInstance*) instance)->getAsset();
}

uint32_t FilaFilamentInstance_getEntityCount(FilaFilamentInstance* instance) {
    return ((FilamentInstance*) instance)->getEntityCount();
}

void FilaFilamentInstance_getEntities(FilaFilamentInstance* instance, FilaEntity* entities) {
    const utils::Entity* src = ((FilamentInstance*) instance)->getEntities();
    size_t count = ((FilamentInstance*) instance)->getEntityCount();
    for (size_t i = 0; i < count; ++i) {
        entities[i] = src[i].getId();
    }
}

FilaEntity FilaFilamentInstance_getRoot(FilaFilamentInstance* instance) {
    return ((FilamentInstance*) instance)->getRoot().getId();
}

FilaAnimator* FilaFilamentInstance_getAnimator(FilaFilamentInstance* instance) {
    return (FilaAnimator*) ((FilamentInstance*) instance)->getAnimator();
}

void FilaFilamentInstance_getBoundingBox(FilaFilamentInstance* instance, float center[3], float halfExtent[3]) {
    auto aabb = ((FilamentInstance*) instance)->getBoundingBox();
    auto c = aabb.center();
    auto e = aabb.extent();
    center[0] = c.x; center[1] = c.y; center[2] = c.z;
    halfExtent[0] = e.x; halfExtent[1] = e.y; halfExtent[2] = e.z;
}

const char* FilaFilamentInstance_getName(FilaFilamentInstance* instance, FilaEntity entity) {
    return ((FilamentInstance*) instance)->getAsset()->getName(utils::Entity::import(entity));
}

uint32_t FilaFilamentInstance_getSkinCount(FilaFilamentInstance* instance) {
    return ((FilamentInstance*) instance)->getSkinCount();
}

const char* FilaFilamentInstance_getSkinNameAt(FilaFilamentInstance* instance, uint32_t skinIndex) {
    return ((FilamentInstance*) instance)->getSkinNameAt(skinIndex);
}

void FilaFilamentInstance_attachSkin(FilaFilamentInstance* instance, uint32_t skinIndex, FilaEntity entity) {
    ((FilamentInstance*) instance)->attachSkin(skinIndex, utils::Entity::import(entity));
}

void FilaFilamentInstance_detachSkin(FilaFilamentInstance* instance, uint32_t skinIndex, FilaEntity entity) {
    ((FilamentInstance*) instance)->detachSkin(skinIndex, utils::Entity::import(entity));
}

uint32_t FilaFilamentInstance_getJointCountAt(FilaFilamentInstance* instance, uint32_t skinIndex) {
    return ((FilamentInstance*) instance)->getJointCountAt(skinIndex);
}

void FilaFilamentInstance_getJointsAt(FilaFilamentInstance* instance, uint32_t skinIndex, FilaEntity* joints) {
    const utils::Entity* src = ((FilamentInstance*) instance)->getJointsAt(skinIndex);
    size_t count = ((FilamentInstance*) instance)->getJointCountAt(skinIndex);
    for (size_t i = 0; i < count; ++i) {
        joints[i] = src[i].getId();
    }
}

void FilaFilamentInstance_applyMaterialVariant(FilaFilamentInstance* instance, uint32_t variantIndex) {
    ((FilamentInstance*) instance)->applyMaterialVariant(variantIndex);
}

uint32_t FilaFilamentInstance_getMaterialInstanceCount(FilaFilamentInstance* instance) {
    return ((FilamentInstance*) instance)->getMaterialInstanceCount();
}

FilaMaterialInstance* FilaFilamentInstance_getMaterialInstanceAt(FilaFilamentInstance* instance, uint32_t index) {
    return (FilaMaterialInstance*) ((FilamentInstance*) instance)->getMaterialInstances()[index];
}

uint32_t FilaFilamentInstance_getMaterialVariantCount(FilaFilamentInstance* instance) {
    return ((FilamentInstance*) instance)->getMaterialVariantCount();
}

const char* FilaFilamentInstance_getMaterialVariantNameAt(FilaFilamentInstance* instance, uint32_t variantIndex) {
    return ((FilamentInstance*) instance)->getMaterialVariantName(variantIndex);
}

}

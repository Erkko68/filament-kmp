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

size_t FilaFilamentInstance_getEntityCount(FilaFilamentInstance* instance) {
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

FilaBox FilaFilamentInstance_getBoundingBox(FilaFilamentInstance* instance) {
    auto aabb = ((FilamentInstance*) instance)->getBoundingBox();
    FilaBox box;
    auto center = aabb.center();
    auto extent = aabb.extent();
    box.center[0] = center.x;
    box.center[1] = center.y;
    box.center[2] = center.z;
    box.halfExtent[0] = extent.x;
    box.halfExtent[1] = extent.y;
    box.halfExtent[2] = extent.z;
    return box;
}

const char* FilaFilamentInstance_getName(FilaFilamentInstance* instance, FilaEntity entity) {
    return ((FilamentInstance*) instance)->getAsset()->getName(utils::Entity::import(entity));
}

}

#include <filament/RenderableManager.h>

#include <filament/Engine.h>
#include <filament/IndexBuffer.h>
#include <filament/InstanceBuffer.h>
#include <filament/MaterialInstance.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/SkinningBuffer.h>
#include <filament/VertexBuffer.h>

#include <utils/Entity.h>

#include "../../include/filament/RenderableManager.h"

namespace {
utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}

filament::RenderableManager::Instance toInstance(FilaRenderableManagerInstance instance) {
    return filament::RenderableManager::Instance(instance);
}

FilaRenderableManagerInstance fromInstance(filament::RenderableManager::Instance instance) {
    return instance.asValue();
}

using RenderableBuilder = filament::RenderableManager::Builder;

filament::RenderableManager::PrimitiveType toPrimitiveType(FilaRenderablePrimitiveType type) {
    switch (type) {
        case FILA_RENDERABLE_PRIMITIVE_POINTS:
            return filament::RenderableManager::PrimitiveType::POINTS;
        case FILA_RENDERABLE_PRIMITIVE_LINES:
            return filament::RenderableManager::PrimitiveType::LINES;
        case FILA_RENDERABLE_PRIMITIVE_TRIANGLES:
        default:
            return filament::RenderableManager::PrimitiveType::TRIANGLES;
    }
}
} // namespace

extern "C" {

bool FilaRenderableManager_hasComponent(const FilaRenderableManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->hasComponent(toEntity(entity));
}

FilaRenderableManagerInstance FilaRenderableManager_getInstance(const FilaRenderableManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return fromInstance(cppManager->getInstance(toEntity(entity)));
}

size_t FilaRenderableManager_getComponentCount(const FilaRenderableManager* manager) {
    if (!manager) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getComponentCount();
}

bool FilaRenderableManager_empty(const FilaRenderableManager* manager) {
    if (!manager) {
        return true;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->empty();
}

FilaEntity FilaRenderableManager_getEntity(const FilaRenderableManager* manager, FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return utils::Entity::smuggle(cppManager->getEntity(toInstance(instance)));
}

size_t FilaRenderableManager_getEntities(const FilaRenderableManager* manager, FilaEntity* outEntities, size_t maxCount) {
    if (!manager || !outEntities || maxCount == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    const size_t count = cppManager->getComponentCount();
    const size_t written = (count < maxCount) ? count : maxCount;
    const utils::Entity* entities = cppManager->getEntities();
    for (size_t i = 0; i < written; ++i) {
        outEntities[i] = utils::Entity::smuggle(entities[i]);
    }
    return written;
}

void FilaRenderableManager_destroy(FilaRenderableManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->destroy(toEntity(entity));
}

size_t FilaRenderableManager_getPrimitiveCount(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getPrimitiveCount(toInstance(instance));
}

void FilaRenderableManager_setLayerMask(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        uint8_t select,
        uint8_t values) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setLayerMask(toInstance(instance), select, values);
}

uint8_t FilaRenderableManager_getLayerMask(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getLayerMask(toInstance(instance));
}

void FilaRenderableManager_setPriority(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        uint8_t priority) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setPriority(toInstance(instance), priority);
}

uint8_t FilaRenderableManager_getPriority(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getPriority(toInstance(instance));
}

void FilaRenderableManager_setChannel(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        uint8_t channel) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setChannel(toInstance(instance), channel);
}

uint8_t FilaRenderableManager_getChannel(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getChannel(toInstance(instance));
}

void FilaRenderableManager_setCulling(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        bool enable) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setCulling(toInstance(instance), enable);
}

bool FilaRenderableManager_isCullingEnabled(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->isCullingEnabled(toInstance(instance));
}

void FilaRenderableManager_setFogEnabled(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        bool enable) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setFogEnabled(toInstance(instance), enable);
}

bool FilaRenderableManager_getFogEnabled(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getFogEnabled(toInstance(instance));
}

void FilaRenderableManager_setLightChannel(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        unsigned int channel,
        bool enable) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setLightChannel(toInstance(instance), channel, enable);
}

bool FilaRenderableManager_getLightChannel(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        unsigned int channel) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getLightChannel(toInstance(instance), channel);
}

void FilaRenderableManager_setCastShadows(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        bool enable) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setCastShadows(toInstance(instance), enable);
}

void FilaRenderableManager_setReceiveShadows(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        bool enable) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setReceiveShadows(toInstance(instance), enable);
}

void FilaRenderableManager_setScreenSpaceContactShadows(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        bool enable) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setScreenSpaceContactShadows(toInstance(instance), enable);
}

bool FilaRenderableManager_isShadowCaster(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->isShadowCaster(toInstance(instance));
}

bool FilaRenderableManager_isShadowReceiver(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->isShadowReceiver(toInstance(instance));
}

bool FilaRenderableManager_isScreenSpaceContactShadowsEnabled(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->isScreenSpaceContactShadowsEnabled(toInstance(instance));
}

FilaRenderableManagerBuilder* FilaRenderableManagerBuilder_create(size_t primitiveCount) {
    auto builder = new RenderableBuilder(primitiveCount);
    return reinterpret_cast<FilaRenderableManagerBuilder*>(builder);
}

void FilaRenderableManagerBuilder_destroy(FilaRenderableManagerBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    delete cppBuilder;
}

void FilaRenderableManagerBuilder_layerMask(FilaRenderableManagerBuilder* builder, uint8_t select, uint8_t values) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->layerMask(select, values);
}

void FilaRenderableManagerBuilder_priority(FilaRenderableManagerBuilder* builder, uint8_t priority) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->priority(priority);
}

void FilaRenderableManagerBuilder_culling(FilaRenderableManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->culling(enable);
}

void FilaRenderableManagerBuilder_castShadows(FilaRenderableManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->castShadows(enable);
}

void FilaRenderableManagerBuilder_receiveShadows(FilaRenderableManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->receiveShadows(enable);
}

void FilaRenderableManagerBuilder_channel(FilaRenderableManagerBuilder* builder, uint8_t channel) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->channel(channel);
}

void FilaRenderableManagerBuilder_lightChannel(FilaRenderableManagerBuilder* builder, unsigned int channel, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->lightChannel(channel, enable);
}

void FilaRenderableManagerBuilder_fog(FilaRenderableManagerBuilder* builder, bool enabled) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->fog(enabled);
}

void FilaRenderableManagerBuilder_screenSpaceContactShadows(FilaRenderableManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->screenSpaceContactShadows(enable);
}

void FilaRenderableManagerBuilder_blendOrder(FilaRenderableManagerBuilder* builder, size_t primitiveIndex, uint16_t order) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->blendOrder(primitiveIndex, order);
}

void FilaRenderableManagerBuilder_globalBlendOrderEnabled(FilaRenderableManagerBuilder* builder, size_t primitiveIndex, bool enabled) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->globalBlendOrderEnabled(primitiveIndex, enabled);
}

void FilaRenderableManagerBuilder_enableSkinningBuffers(FilaRenderableManagerBuilder* builder, bool enabled) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->enableSkinningBuffers(enabled);
}

void FilaRenderableManagerBuilder_skinning(FilaRenderableManagerBuilder* builder,
        FilaSkinningBuffer* skinningBuffer,
        size_t count,
        size_t offset) {
    if (!builder || !skinningBuffer) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto cppSkinningBuffer = reinterpret_cast<filament::SkinningBuffer*>(skinningBuffer);
    cppBuilder->skinning(cppSkinningBuffer, count, offset);
}

void FilaRenderableManagerBuilder_morphing(FilaRenderableManagerBuilder* builder,
        FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!builder || !morphTargetBuffer) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto cppMorphTargetBuffer = reinterpret_cast<filament::MorphTargetBuffer*>(morphTargetBuffer);
    cppBuilder->morphing(cppMorphTargetBuffer);
}

void FilaRenderableManagerBuilder_instances(FilaRenderableManagerBuilder* builder,
        size_t instanceCount,
        FilaInstanceBuffer* instanceBuffer) {
    if (!builder || instanceCount == 0) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    if (instanceBuffer) {
        auto cppInstanceBuffer = reinterpret_cast<filament::InstanceBuffer*>(instanceBuffer);
        cppBuilder->instances(instanceCount, cppInstanceBuffer);
        return;
    }
    cppBuilder->instances(instanceCount);
}

void FilaRenderableManagerBuilder_geometry(FilaRenderableManagerBuilder* builder,
        size_t index,
        FilaRenderablePrimitiveType type,
        FilaVertexBuffer* vertexBuffer,
        FilaIndexBuffer* indexBuffer,
        size_t offset,
        size_t count) {
    if (!builder || !vertexBuffer || !indexBuffer) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto cppVertexBuffer = reinterpret_cast<filament::VertexBuffer*>(vertexBuffer);
    auto cppIndexBuffer = reinterpret_cast<filament::IndexBuffer*>(indexBuffer);
    cppBuilder->geometry(index, toPrimitiveType(type), cppVertexBuffer, cppIndexBuffer, offset, count);
}

void FilaRenderableManagerBuilder_material(FilaRenderableManagerBuilder* builder,
        size_t index,
        const FilaMaterialInstance* materialInstance) {
    if (!builder || !materialInstance) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    cppBuilder->material(index, cppMaterialInstance);
}

bool FilaRenderableManagerBuilder_build(FilaRenderableManagerBuilder* builder, FilaEngine* engine, FilaEntity entity) {
    if (!builder || !engine || entity == 0) {
        return false;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return cppBuilder->build(*cppEngine, toEntity(entity)) == RenderableBuilder::Result::Success;
}

void FilaRenderableManager_setMaterialInstanceAt(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex,
        const FilaMaterialInstance* materialInstance) {
    if (!manager || instance == 0 || !materialInstance) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    cppManager->setMaterialInstanceAt(toInstance(instance), primitiveIndex, cppMaterialInstance);
}

FilaMaterialInstance* FilaRenderableManager_getMaterialInstanceAt(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex) {
    if (!manager || instance == 0) {
        return nullptr;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return reinterpret_cast<FilaMaterialInstance*>(cppManager->getMaterialInstanceAt(toInstance(instance), primitiveIndex));
}

void FilaRenderableManager_clearMaterialInstanceAt(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->clearMaterialInstanceAt(toInstance(instance), primitiveIndex);
}

void FilaRenderableManager_setBlendOrderAt(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex,
        uint16_t order) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setBlendOrderAt(toInstance(instance), primitiveIndex, order);
}

uint16_t FilaRenderableManager_getBlendOrderAt(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex) {
    if (!manager || instance == 0) {
        return 0u;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getBlendOrderAt(toInstance(instance), primitiveIndex);
}

void FilaRenderableManager_setGlobalBlendOrderEnabledAt(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex,
        bool enabled) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setGlobalBlendOrderEnabledAt(toInstance(instance), primitiveIndex, enabled);
}

bool FilaRenderableManager_isGlobalBlendOrderEnabledAt(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->isGlobalBlendOrderEnabledAt(toInstance(instance), primitiveIndex);
}

void FilaRenderableManager_setSkinningBuffer(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        FilaSkinningBuffer* skinningBuffer,
        size_t count,
        size_t offset) {
    if (!manager || instance == 0 || !skinningBuffer) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    auto cppSkinningBuffer = reinterpret_cast<filament::SkinningBuffer*>(skinningBuffer);
    cppManager->setSkinningBuffer(toInstance(instance), cppSkinningBuffer, count, offset);
}

void FilaRenderableManager_setMorphWeights(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        const float* weights,
        size_t count,
        size_t offset) {
    if (!manager || instance == 0 || !weights || count == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setMorphWeights(toInstance(instance), weights, count, offset);
}

void FilaRenderableManager_setMorphTargetBufferOffsetAt(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        uint8_t level,
        size_t primitiveIndex,
        size_t offset) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setMorphTargetBufferOffsetAt(toInstance(instance), level, primitiveIndex, offset);
}

FilaMorphTargetBuffer* FilaRenderableManager_getMorphTargetBuffer(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return nullptr;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return reinterpret_cast<FilaMorphTargetBuffer*>(cppManager->getMorphTargetBuffer(toInstance(instance)));
}

size_t FilaRenderableManager_getMorphTargetCount(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getMorphTargetCount(toInstance(instance));
}

size_t FilaRenderableManager_getInstanceCount(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getInstanceCount(toInstance(instance));
}

} // extern "C"


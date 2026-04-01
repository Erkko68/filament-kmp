#include <filament/RenderableManager.h>

#include <filament/Engine.h>
#include <filament/Box.h>
#include <filament/IndexBuffer.h>
#include <filament/InstanceBuffer.h>
#include <filament/MaterialInstance.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/SkinningBuffer.h>
#include <filament/VertexBuffer.h>

#include <utils/Entity.h>
#include <utils/FixedCapacityVector.h>

#include <vector>

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

filament::Box toBox(const FilaBox& box) {
    filament::Box converted;
    converted.center = {box.center[0], box.center[1], box.center[2]};
    converted.halfExtent = {box.halfExtent[0], box.halfExtent[1], box.halfExtent[2]};
    return converted;
}

void fromBox(const filament::Box& box, FilaBox* outBox) {
    if (!outBox) {
        return;
    }
    outBox->center[0] = box.center.x;
    outBox->center[1] = box.center.y;
    outBox->center[2] = box.center.z;
    outBox->halfExtent[0] = box.halfExtent.x;
    outBox->halfExtent[1] = box.halfExtent.y;
    outBox->halfExtent[2] = box.halfExtent.z;
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

filament::RenderableManager::Builder::GeometryType toGeometryType(FilaRenderableGeometryType type) {
    switch (type) {
        case FILA_RENDERABLE_GEOMETRY_TYPE_STATIC_BOUNDS:
            return filament::RenderableManager::Builder::GeometryType::STATIC_BOUNDS;
        case FILA_RENDERABLE_GEOMETRY_TYPE_STATIC:
            return filament::RenderableManager::Builder::GeometryType::STATIC;
        case FILA_RENDERABLE_GEOMETRY_TYPE_DYNAMIC:
        default:
            return filament::RenderableManager::Builder::GeometryType::DYNAMIC;
    }
}

std::vector<filament::math::mat4f> toMat4fArray(const float* transforms4x4, size_t count) {
    std::vector<filament::math::mat4f> transforms(count);
    for (size_t i = 0; i < count; ++i) {
        const float* src = transforms4x4 + i * 16;
        filament::math::float4 c0(src[0], src[1], src[2], src[3]);
        filament::math::float4 c1(src[4], src[5], src[6], src[7]);
        filament::math::float4 c2(src[8], src[9], src[10], src[11]);
        filament::math::float4 c3(src[12], src[13], src[14], src[15]);
        transforms[i] = filament::math::mat4f(c0, c1, c2, c3);
    }
    return transforms;
}

std::vector<filament::RenderableManager::Bone> toBoneArray(const FilaRenderableBone* bones, size_t count) {
    std::vector<filament::RenderableManager::Bone> converted(count);
    for (size_t i = 0; i < count; ++i) {
        converted[i].unitQuaternion = filament::math::quatf{
                bones[i].unitQuaternion[0],
                bones[i].unitQuaternion[1],
                bones[i].unitQuaternion[2],
                bones[i].unitQuaternion[3]};
        converted[i].translation = filament::math::float3{
                bones[i].translation[0],
                bones[i].translation[1],
                bones[i].translation[2]};
        converted[i].reserved = bones[i].reserved;
    }
    return converted;
}

std::vector<filament::math::float2> toFloat2Array(
        const FilaRenderableBoneIndexAndWeight* indicesAndWeights,
        size_t count) {
    std::vector<filament::math::float2> converted(count);
    for (size_t i = 0; i < count; ++i) {
        converted[i] = filament::math::float2{
                indicesAndWeights[i].boneIndex,
                indicesAndWeights[i].weight};
    }
    return converted;
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

void FilaRenderableManagerBuilder_geometryType(FilaRenderableManagerBuilder* builder,
        FilaRenderableGeometryType geometryType) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->geometryType(toGeometryType(geometryType));
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

void FilaRenderableManagerBuilder_skinningBoneCount(FilaRenderableManagerBuilder* builder,
        size_t boneCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->skinning(boneCount);
}

void FilaRenderableManagerBuilder_skinningMat4f(FilaRenderableManagerBuilder* builder,
        size_t boneCount,
        const float* transforms4x4) {
    if (!builder || !transforms4x4 || boneCount == 0) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto transforms = toMat4fArray(transforms4x4, boneCount);
    cppBuilder->skinning(boneCount, transforms.data());
}

void FilaRenderableManagerBuilder_skinningBones(FilaRenderableManagerBuilder* builder,
        size_t boneCount,
        const FilaRenderableBone* bones) {
    if (!builder || !bones || boneCount == 0) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto convertedBones = toBoneArray(bones, boneCount);
    cppBuilder->skinning(boneCount, convertedBones.data());
}

void FilaRenderableManagerBuilder_boneIndicesAndWeights(FilaRenderableManagerBuilder* builder,
        size_t primitiveIndex,
        const FilaRenderableBoneIndexAndWeight* indicesAndWeights,
        size_t count,
        size_t bonesPerVertex) {
    if (!builder || !indicesAndWeights || count == 0 || bonesPerVertex == 0) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto converted = toFloat2Array(indicesAndWeights, count);
    cppBuilder->boneIndicesAndWeights(primitiveIndex, converted.data(), count, bonesPerVertex);
}

void FilaRenderableManagerBuilder_boneIndicesAndWeightsVector(FilaRenderableManagerBuilder* builder,
        size_t primitiveIndex,
        const FilaRenderableBoneIndexAndWeight* indicesAndWeights,
        size_t pairCount,
        const size_t* pairsPerVertex,
        size_t vertexCount) {
    if (!builder || !indicesAndWeights || !pairsPerVertex || pairCount == 0 || vertexCount == 0) {
        return;
    }

    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto perVertex =
            utils::FixedCapacityVector<utils::FixedCapacityVector<filament::math::float2>>::with_capacity(vertexCount);

    size_t cursor = 0;
    for (size_t i = 0; i < vertexCount; ++i) {
        const size_t pairSpan = pairsPerVertex[i];
        if (pairSpan == 0 || cursor + pairSpan > pairCount) {
            return;
        }

        auto vertexPairs = utils::FixedCapacityVector<filament::math::float2>::with_capacity(pairSpan);
        for (size_t j = 0; j < pairSpan; ++j) {
            const auto& src = indicesAndWeights[cursor + j];
            vertexPairs.push_back(filament::math::float2{src.boneIndex, src.weight});
        }
        perVertex.push_back(std::move(vertexPairs));
        cursor += pairSpan;
    }

    if (cursor != pairCount) {
        return;
    }
    cppBuilder->boneIndicesAndWeights(primitiveIndex, std::move(perVertex));
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

void FilaRenderableManagerBuilder_morphingLegacy(FilaRenderableManagerBuilder* builder,
        size_t targetCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->morphing(targetCount);
}

void FilaRenderableManagerBuilder_morphingOffset(FilaRenderableManagerBuilder* builder,
        uint8_t level,
        size_t primitiveIndex,
        size_t offset) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->morphing(level, primitiveIndex, offset);
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

void FilaRenderableManagerBuilder_geometryIndexedRange(FilaRenderableManagerBuilder* builder,
        size_t index,
        FilaRenderablePrimitiveType type,
        FilaVertexBuffer* vertexBuffer,
        FilaIndexBuffer* indexBuffer,
        size_t offset,
        size_t minIndex,
        size_t maxIndex,
        size_t count) {
    if (!builder || !vertexBuffer || !indexBuffer) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto cppVertexBuffer = reinterpret_cast<filament::VertexBuffer*>(vertexBuffer);
    auto cppIndexBuffer = reinterpret_cast<filament::IndexBuffer*>(indexBuffer);
    cppBuilder->geometry(index, toPrimitiveType(type), cppVertexBuffer, cppIndexBuffer,
            offset, minIndex, maxIndex, count);
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

void FilaRenderableManagerBuilder_boundingBox(FilaRenderableManagerBuilder* builder,
        const FilaBox* boundingBox) {
    if (!builder || !boundingBox) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->boundingBox(toBox(*boundingBox));
}

bool FilaRenderableManagerBuilder_build(FilaRenderableManagerBuilder* builder, FilaEngine* engine, FilaEntity entity) {
    if (!builder || !engine || entity == 0) {
        return false;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return cppBuilder->build(*cppEngine, toEntity(entity)) == RenderableBuilder::Result::Success;
}

void FilaRenderableManager_setAxisAlignedBoundingBox(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        const FilaBox* boundingBox) {
    if (!manager || instance == 0 || !boundingBox) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setAxisAlignedBoundingBox(toInstance(instance), toBox(*boundingBox));
}

bool FilaRenderableManager_getAxisAlignedBoundingBox(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        FilaBox* outBoundingBox) {
    if (!manager || instance == 0 || !outBoundingBox) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    fromBox(cppManager->getAxisAlignedBoundingBox(toInstance(instance)), outBoundingBox);
    return true;
}

void FilaRenderableManager_setGeometryAt(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex,
        FilaRenderablePrimitiveType type,
        FilaVertexBuffer* vertexBuffer,
        FilaIndexBuffer* indexBuffer,
        size_t offset,
        size_t count) {
    if (!manager || instance == 0 || !vertexBuffer || !indexBuffer) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    auto cppVertexBuffer = reinterpret_cast<filament::VertexBuffer*>(vertexBuffer);
    auto cppIndexBuffer = reinterpret_cast<filament::IndexBuffer*>(indexBuffer);
    cppManager->setGeometryAt(toInstance(instance), primitiveIndex,
            toPrimitiveType(type), cppVertexBuffer, cppIndexBuffer, offset, count);
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

uint32_t FilaRenderableManager_getEnabledAttributesAt(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        size_t primitiveIndex) {
    if (!manager || instance == 0) {
        return 0u;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getEnabledAttributesAt(toInstance(instance), primitiveIndex).getValue();
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

void FilaRenderableManager_setBonesMat4f(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        const float* transforms4x4,
        size_t count,
        size_t offset) {
    if (!manager || instance == 0 || !transforms4x4 || count == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    auto transforms = toMat4fArray(transforms4x4, count);
    cppManager->setBones(toInstance(instance), transforms.data(), count, offset);
}

void FilaRenderableManager_setBones(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        const FilaRenderableBone* bones,
        size_t count,
        size_t offset) {
    if (!manager || instance == 0 || !bones || count == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    auto convertedBones = toBoneArray(bones, count);
    cppManager->setBones(toInstance(instance), convertedBones.data(), count, offset);
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


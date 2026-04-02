#include "filament/Engine.h"
#include "filament/Box.h"
#include "utils/EntityManager.h"
#include "filament/InstanceBuffer.h"
#include "filament/MorphTargetBuffer.h"
#include "filament/RenderableManager.h"
#include "filament/SkinningBuffer.h"

// Verifies RenderableManager API is consumable from C and composes with Engine + EntityManager.
void test_headers_renderable_manager(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderableManager* manager = FilaEngine_getRenderableManager(engine);
    FilaEntity entity = FilaEntityManager_create();
    FilaEntity entities[4] = {0};
    FilaRenderableManagerBuilder* builder = FilaRenderableManagerBuilder_create(0u);
    FilaVertexBuffer* vertexBuffer = (FilaVertexBuffer*)0;
    FilaIndexBuffer* indexBuffer = (FilaIndexBuffer*)0;
    FilaMaterialInstance* materialInstance = (FilaMaterialInstance*)0;
    FilaSkinningBuffer* skinningBuffer = (FilaSkinningBuffer*)0;
    FilaMorphTargetBuffer* morphTargetBuffer = (FilaMorphTargetBuffer*)0;
    FilaInstanceBuffer* instanceBuffer = (FilaInstanceBuffer*)0;
    FilaBox box = {
            {0.0f, 0.0f, 0.0f},
            {1.0f, 1.0f, 1.0f},
    };
    FilaBox outBox = {
            {0.0f, 0.0f, 0.0f},
            {0.0f, 0.0f, 0.0f},
    };
    float identity4x4[16] = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
    };
    FilaRenderableBone identityBone = {
            {1.0f, 0.0f, 0.0f, 0.0f},
            {0.0f, 0.0f, 0.0f},
            0.0f,
    };
    FilaRenderableBoneIndexAndWeight indicesAndWeights[2] = {
            {0.0f, 0.75f},
            {1.0f, 0.25f},
    };
    float positions3[9] = {
            -1.0f, -1.0f, 0.0f,
             1.0f, -1.0f, 0.0f,
             0.0f,  1.0f, 0.0f,
    };
    float positions4[12] = {
            -1.0f, -1.0f, 0.0f, 1.0f,
             1.0f, -1.0f, 0.0f, 1.0f,
             0.0f,  1.0f, 0.0f, 1.0f,
    };
    uint16_t idx16[3] = {0u, 1u, 2u};
    uint32_t idx32[3] = {0u, 1u, 2u};
    size_t pairsPerVertex[2] = {1u, 1u};

    (void)FilaRenderableManager_hasComponent(manager, entity);
    FilaRenderableManagerInstance instance = FilaRenderableManager_getInstance(manager, entity);
    (void)FilaRenderableManager_getEntity(manager, instance);
    (void)FilaRenderableManager_getComponentCount(manager);
    (void)FilaRenderableManager_empty(manager);
    (void)FilaRenderableManager_getEntities(manager, entities, 4u);
    (void)FilaRenderableManager_getPrimitiveCount(manager, instance);
    FilaRenderableManager_setLayerMask(manager, instance, 0xFFu, 0x02u);
    (void)FilaRenderableManager_getLayerMask(manager, instance);
    FilaRenderableManager_setPriority(manager, instance, 4u);
    (void)FilaRenderableManager_getPriority(manager, instance);
    FilaRenderableManager_setChannel(manager, instance, 2u);
    (void)FilaRenderableManager_getChannel(manager, instance);
    FilaRenderableManager_setCulling(manager, instance, true);
    (void)FilaRenderableManager_isCullingEnabled(manager, instance);
    FilaRenderableManager_setFogEnabled(manager, instance, true);
    (void)FilaRenderableManager_getFogEnabled(manager, instance);
    FilaRenderableManager_setLightChannel(manager, instance, 1u, true);
    (void)FilaRenderableManager_getLightChannel(manager, instance, 1u);
    FilaRenderableManager_setCastShadows(manager, instance, true);
    FilaRenderableManager_setReceiveShadows(manager, instance, true);
    FilaRenderableManager_setScreenSpaceContactShadows(manager, instance, true);
    (void)FilaRenderableManager_isShadowCaster(manager, instance);
    (void)FilaRenderableManager_isShadowReceiver(manager, instance);
    (void)FilaRenderableManager_isScreenSpaceContactShadowsEnabled(manager, instance);
    FilaRenderableManagerBuilder_layerMask(builder, 0xFFu, 0x02u);
    FilaRenderableManagerBuilder_priority(builder, 4u);
    FilaRenderableManagerBuilder_channel(builder, 2u);
    FilaRenderableManagerBuilder_lightChannel(builder, 1u, true);
    FilaRenderableManagerBuilder_culling(builder, true);
    FilaRenderableManagerBuilder_fog(builder, true);
    FilaRenderableManagerBuilder_screenSpaceContactShadows(builder, true);
    FilaRenderableManagerBuilder_castShadows(builder, false);
    FilaRenderableManagerBuilder_receiveShadows(builder, true);
    FilaRenderableManagerBuilder_blendOrder(builder, 0u, 1u);
    FilaRenderableManagerBuilder_globalBlendOrderEnabled(builder, 0u, true);
    FilaRenderableManagerBuilder_geometryType(builder, FILA_RENDERABLE_GEOMETRY_TYPE_DYNAMIC);
    FilaRenderableManagerBuilder_enableSkinningBuffers(builder, true);
    FilaRenderableManagerBuilder_skinning(builder, skinningBuffer, 1u, 0u);
    FilaRenderableManagerBuilder_skinningBoneCount(builder, 1u);
    FilaRenderableManagerBuilder_skinningMat4f(builder, 1u, identity4x4);
    FilaRenderableManagerBuilder_skinningBones(builder, 1u, &identityBone);
    FilaRenderableManagerBuilder_boneIndicesAndWeights(builder, 0u, indicesAndWeights, 2u, 2u);
    FilaRenderableManagerBuilder_boneIndicesAndWeightsVector(builder,
            0u,
            indicesAndWeights,
            2u,
            pairsPerVertex,
            2u);
    FilaRenderableManagerBuilder_morphingLegacy(builder, 2u);
    FilaRenderableManagerBuilder_morphing(builder, morphTargetBuffer);
    FilaRenderableManagerBuilder_morphingOffset(builder, 0u, 0u, 0u);
    FilaRenderableManagerBuilder_instances(builder, 1u, instanceBuffer);
    FilaRenderableManagerBuilder_geometry(builder,
            0u,
            FILA_RENDERABLE_PRIMITIVE_TRIANGLES,
            vertexBuffer,
            indexBuffer,
            0u,
            3u);
    FilaRenderableManagerBuilder_geometryIndexedRange(builder,
            0u,
            FILA_RENDERABLE_PRIMITIVE_TRIANGLES,
            vertexBuffer,
            indexBuffer,
            0u,
            0u,
            2u,
            3u);
    FilaRenderableManagerBuilder_material(builder, 0u, materialInstance);
    FilaRenderableManagerBuilder_boundingBox(builder, &box);
    (void)FilaRenderableManagerBuilder_build(builder, engine, entity);
    FilaRenderableManagerBuilder_destroy(builder);
    FilaRenderableManager_setAxisAlignedBoundingBox(manager, instance, &box);
    (void)FilaRenderableManager_getAxisAlignedBoundingBox(manager, instance, &outBox);
    FilaRenderableManager_setGeometryAt(manager,
            instance,
            0u,
            FILA_RENDERABLE_PRIMITIVE_TRIANGLES,
            vertexBuffer,
            indexBuffer,
            0u,
            3u);
    FilaRenderableManager_setMaterialInstanceAt(manager, instance, 0u, materialInstance);
    (void)FilaRenderableManager_getMaterialInstanceAt(manager, instance, 0u);
    FilaRenderableManager_clearMaterialInstanceAt(manager, instance, 0u);
    FilaRenderableManager_setBlendOrderAt(manager, instance, 0u, 1u);
    (void)FilaRenderableManager_getBlendOrderAt(manager, instance, 0u);
    FilaRenderableManager_setGlobalBlendOrderEnabledAt(manager, instance, 0u, true);
    (void)FilaRenderableManager_isGlobalBlendOrderEnabledAt(manager, instance, 0u);
    (void)FilaRenderableManager_getEnabledAttributesAt(manager, instance, 0u);
    FilaRenderableManager_setSkinningBuffer(manager, instance, skinningBuffer, 1u, 0u);
    FilaRenderableManager_setBones(manager, instance, &identityBone, 1u, 0u);
    FilaRenderableManager_setBonesMat4f(manager, instance, identity4x4, 1u, 0u);
    float weights[2] = {0.5f, 0.5f};
    FilaRenderableManager_setMorphWeights(manager, instance, weights, 2u, 0u);
    FilaRenderableManager_setMorphTargetBufferOffsetAt(manager, instance, 0u, 0u, 0u);
    (void)FilaRenderableManager_getMorphTargetBuffer(manager, instance);
    (void)FilaRenderableManager_getMorphTargetCount(manager, instance);
    (void)FilaRenderableManager_getInstanceCount(manager, instance);
    (void)FilaRenderableManager_computeAabbFloat3U16(positions3, idx16, 3u, 0u, &outBox);
    (void)FilaRenderableManager_computeAabbFloat3U32(positions3, idx32, 3u, 0u, &outBox);
    (void)FilaRenderableManager_computeAabbFloat4U16(positions4, idx16, 3u, 0u, &outBox);
    (void)FilaRenderableManager_computeAabbFloat4U32(positions4, idx32, 3u, 0u, &outBox);
    FilaRenderableManager_destroy(manager, entity);
    FilaEntityManager_destroy(entity);
}

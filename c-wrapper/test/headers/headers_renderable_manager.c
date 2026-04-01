#include "filament/Engine.h"
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
    FilaRenderableManager_setCulling(manager, instance, true);
    (void)FilaRenderableManager_isCullingEnabled(manager, instance);
    FilaRenderableManagerBuilder_layerMask(builder, 0xFFu, 0x02u);
    FilaRenderableManagerBuilder_priority(builder, 4u);
    FilaRenderableManagerBuilder_culling(builder, true);
    FilaRenderableManagerBuilder_castShadows(builder, false);
    FilaRenderableManagerBuilder_receiveShadows(builder, true);
    FilaRenderableManagerBuilder_enableSkinningBuffers(builder, true);
    FilaRenderableManagerBuilder_skinning(builder, skinningBuffer, 1u, 0u);
    FilaRenderableManagerBuilder_morphing(builder, morphTargetBuffer);
    FilaRenderableManagerBuilder_instances(builder, 1u, instanceBuffer);
    FilaRenderableManagerBuilder_geometry(builder,
            0u,
            FILA_RENDERABLE_PRIMITIVE_TRIANGLES,
            vertexBuffer,
            indexBuffer,
            0u,
            3u);
    FilaRenderableManagerBuilder_material(builder, 0u, materialInstance);
    (void)FilaRenderableManagerBuilder_build(builder, engine, entity);
    FilaRenderableManagerBuilder_destroy(builder);
    FilaRenderableManager_setMaterialInstanceAt(manager, instance, 0u, materialInstance);
    (void)FilaRenderableManager_getMaterialInstanceAt(manager, instance, 0u);
    FilaRenderableManager_setSkinningBuffer(manager, instance, skinningBuffer, 1u, 0u);
    float weights[2] = {0.5f, 0.5f};
    FilaRenderableManager_setMorphWeights(manager, instance, weights, 2u, 0u);
    FilaRenderableManager_setMorphTargetBufferOffsetAt(manager, instance, 0u, 0u, 0u);
    (void)FilaRenderableManager_getMorphTargetBuffer(manager, instance);
    (void)FilaRenderableManager_getMorphTargetCount(manager, instance);
    (void)FilaRenderableManager_getInstanceCount(manager, instance);
    FilaRenderableManager_destroy(manager, entity);
    FilaEntityManager_destroy(entity);
}

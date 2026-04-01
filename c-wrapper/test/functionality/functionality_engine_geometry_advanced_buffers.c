#include <stdio.h>

#include "filament/Engine.h"
#include "utils/EntityManager.h"
#include "filament/IndexBuffer.h"
#include "filament/InstanceBuffer.h"
#include "filament/MaterialInstance.h"
#include "filament/MorphTargetBuffer.h"
#include "filament/RenderableManager.h"
#include "filament/SkinningBuffer.h"
#include "filament/VertexBuffer.h"

int main(void) {
    printf("Running engine+geometry_advanced_buffers functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaSkinningBufferBuilder* sbBuilder = FilaSkinningBufferBuilder_create();
    FilaSkinningBufferBuilder_boneCount(sbBuilder, 256u);
    FilaSkinningBufferBuilder_initialize(sbBuilder, true);
    FilaSkinningBuffer* skinningBuffer = FilaSkinningBufferBuilder_build(sbBuilder, engine);
    FilaSkinningBufferBuilder_destroy(sbBuilder);
    if (!skinningBuffer) {
        printf("SkinningBuffer build failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaMorphTargetBufferBuilder* mtbBuilder = FilaMorphTargetBufferBuilder_create();
    FilaMorphTargetBufferBuilder_vertexCount(mtbBuilder, 3u);
    FilaMorphTargetBufferBuilder_count(mtbBuilder, 2u);
    FilaMorphTargetBufferBuilder_withPositions(mtbBuilder, true);
    FilaMorphTargetBufferBuilder_withTangents(mtbBuilder, false);
    FilaMorphTargetBuffer* morphTargetBuffer = FilaMorphTargetBufferBuilder_build(mtbBuilder, engine);
    FilaMorphTargetBufferBuilder_destroy(mtbBuilder);
    if (!morphTargetBuffer) {
        printf("MorphTargetBuffer build failed\n");
        FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaInstanceBufferBuilder* ibufBuilder = FilaInstanceBufferBuilder_create(1u);
    FilaInstanceBuffer* instanceBuffer = FilaInstanceBufferBuilder_build(ibufBuilder, engine);
    FilaInstanceBufferBuilder_destroy(ibufBuilder);
    if (!instanceBuffer) {
        printf("InstanceBuffer build failed\n");
        FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
        FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    // Upload one identity transform to instance buffer.
    float identity[16] = {
        1.f, 0.f, 0.f, 0.f,
        0.f, 1.f, 0.f, 0.f,
        0.f, 0.f, 1.f, 0.f,
        0.f, 0.f, 0.f, 1.f
    };
    FilaInstanceBuffer_setLocalTransforms(instanceBuffer, identity, 1u, 0u);

    FilaVertexBufferBuilder* vbBuilder = FilaVertexBufferBuilder_create();
    FilaVertexBufferBuilder_bufferCount(vbBuilder, 1u);
    FilaVertexBufferBuilder_vertexCount(vbBuilder, 3u);
    FilaVertexBufferBuilder_attribute(vbBuilder,
            FILA_VERTEX_ATTRIBUTE_POSITION,
            0u,
            FILA_VERTEX_ATTRIBUTE_TYPE_FLOAT3,
            0u,
            0u);
    FilaVertexBuffer* vb = FilaVertexBufferBuilder_build(vbBuilder, engine);
    FilaVertexBufferBuilder_destroy(vbBuilder);

    FilaIndexBufferBuilder* idxBuilder = FilaIndexBufferBuilder_create();
    FilaIndexBufferBuilder_indexCount(idxBuilder, 3u);
    FilaIndexBufferBuilder_bufferType(idxBuilder, FILA_INDEX_TYPE_USHORT);
    FilaIndexBuffer* ib = FilaIndexBufferBuilder_build(idxBuilder, engine);
    FilaIndexBufferBuilder_destroy(idxBuilder);

    if (!vb || !ib) {
        printf("Geometry buffer build failed\n");
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroyInstanceBuffer(engine, instanceBuffer);
        FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
        FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity entity = FilaEntityManager_create();
    FilaRenderableManagerBuilder* rb = FilaRenderableManagerBuilder_create(1u);
    FilaRenderableManagerBuilder_culling(rb, false);
    FilaRenderableManagerBuilder_castShadows(rb, false);
    FilaRenderableManagerBuilder_receiveShadows(rb, false);
    FilaRenderableManagerBuilder_geometry(rb, 0u, FILA_RENDERABLE_PRIMITIVE_TRIANGLES, vb, ib, 0u, 3u);
    FilaRenderableManagerBuilder_enableSkinningBuffers(rb, true);
    FilaRenderableManagerBuilder_skinning(rb, skinningBuffer, 256u, 0u);
    FilaRenderableManagerBuilder_morphing(rb, morphTargetBuffer);
    FilaRenderableManagerBuilder_instances(rb, 1u, instanceBuffer);
    const bool built = FilaRenderableManagerBuilder_build(rb, engine, entity);
    FilaRenderableManagerBuilder_destroy(rb);

    if (!built) {
        printf("Renderable with advanced geometry buffers build failed\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroyInstanceBuffer(engine, instanceBuffer);
        FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
        FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager* rm = FilaEngine_getRenderableManager(engine);
    FilaRenderableManagerInstance instance = FilaRenderableManager_getInstance(rm, entity);
    if (!instance) {
        printf("Renderable instance retrieval failed\n");
        FilaRenderableManager_destroy(rm, entity);
        FilaEntityManager_destroy(entity);
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroyInstanceBuffer(engine, instanceBuffer);
        FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
        FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_setSkinningBuffer(rm, instance, skinningBuffer, 256u, 0u);
    float weights[2] = {0.7f, 0.3f};
    FilaRenderableManager_setMorphWeights(rm, instance, weights, 2u, 0u);
    FilaRenderableManager_setMorphTargetBufferOffsetAt(rm, instance, 0u, 0u, 0u);

    if (FilaRenderableManager_getMorphTargetBuffer(rm, instance) != morphTargetBuffer ||
            FilaRenderableManager_getMorphTargetCount(rm, instance) == 0u ||
            FilaRenderableManager_getInstanceCount(rm, instance) == 0u) {
        printf("Renderable advanced buffer state query mismatch\n");
        FilaRenderableManager_destroy(rm, entity);
        FilaEntityManager_destroy(entity);
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroyInstanceBuffer(engine, instanceBuffer);
        FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
        FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_destroy(rm, entity);
    FilaEntityManager_destroy(entity);
    FilaEngine_destroyIndexBuffer(engine, ib);
    FilaEngine_destroyVertexBuffer(engine, vb);
    FilaEngine_destroyInstanceBuffer(engine, instanceBuffer);
    FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
    FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
    FilaEngine_destroy(&engine);

    printf("Engine+geometry_advanced_buffers functionality program completed\n");
    return 0;
}


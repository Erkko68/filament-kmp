#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

#include "backend/BufferDescriptor.h"
#include "filament/BufferObject.h"
#include "filament/Engine.h"
#include "filament/IndexBuffer.h"
#include "filament/MorphTargetBuffer.h"
#include "filament/RenderableManager.h"
#include "utils/EntityManager.h"
#include "filament/VertexBuffer.h"

static void release_heap_buffer(void* buffer, size_t size, void* user) {
    (void)size;
    (void)user;
    free(buffer);
}

int main(void) {
    printf("Running engine+resource_builders functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaVertexBufferBuilder* vbBuilder = FilaVertexBufferBuilder_create();
    if (!vbBuilder) {
        printf("VertexBuffer builder creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }
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

    if (!vb) {
        printf("VertexBuffer build failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaIndexBufferBuilder* ibBuilder = FilaIndexBufferBuilder_create();
    if (!ibBuilder) {
        printf("IndexBuffer builder creation failed\n");
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaIndexBufferBuilder_indexCount(ibBuilder, 3u);
    FilaIndexBufferBuilder_bufferType(ibBuilder, FILA_INDEX_TYPE_USHORT);
    FilaIndexBuffer* ib = FilaIndexBufferBuilder_build(ibBuilder, engine);
    FilaIndexBufferBuilder_destroy(ibBuilder);

    if (!ib) {
        printf("IndexBuffer build failed\n");
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaVertexBuffer_getVertexCount(vb) != 3u || FilaIndexBuffer_getIndexCount(ib) != 3u) {
        printf("Resource count query mismatch\n");
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (!FilaVertexBuffer_isCreationComplete(vb) || !FilaIndexBuffer_isCreationComplete(ib)) {
        printf("Resource creation-complete query mismatch\n");
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }

    {
        FilaBufferObjectBuilder* boBuilder = FilaBufferObjectBuilder_create();
        if (!boBuilder) {
            printf("BufferObject builder creation failed\n");
            FilaEngine_destroyIndexBuffer(engine, ib);
            FilaEngine_destroyVertexBuffer(engine, vb);
            FilaEngine_destroy(&engine);
            return 1;
        }
        FilaBufferObjectBuilder_size(boBuilder, sizeof(float) * 9u);
        FilaBufferObjectBuilder_bindingType(boBuilder, FILA_BUFFER_OBJECT_BINDING_VERTEX);
        FilaBufferObject* bo = FilaBufferObjectBuilder_build(boBuilder, engine);
        FilaBufferObjectBuilder_destroy(boBuilder);
        if (!bo) {
            printf("BufferObject build failed\n");
            FilaEngine_destroyIndexBuffer(engine, ib);
            FilaEngine_destroyVertexBuffer(engine, vb);
            FilaEngine_destroy(&engine);
            return 1;
        }

        {
            static const float kPositions[9] = {
                    -0.5f, -0.5f, 0.0f,
                     0.5f, -0.5f, 0.0f,
                     0.0f,  0.5f, 0.0f,
            };
            float* upload = (float*)malloc(sizeof(kPositions));
            if (!upload) {
                printf("BufferObject upload allocation failed\n");
                FilaEngine_destroyBufferObject(engine, bo);
                FilaEngine_destroyIndexBuffer(engine, ib);
                FilaEngine_destroyVertexBuffer(engine, vb);
                FilaEngine_destroy(&engine);
                return 1;
            }
            memcpy(upload, kPositions, sizeof(kPositions));
            FilaBufferDescriptor* boDesc = FilaBufferDescriptor_create(
                    upload,
                    sizeof(kPositions),
                    release_heap_buffer,
                    (void*)0);
            if (!boDesc) {
                free(upload);
                printf("BufferObject descriptor creation failed\n");
                FilaEngine_destroyBufferObject(engine, bo);
                FilaEngine_destroyIndexBuffer(engine, ib);
                FilaEngine_destroyVertexBuffer(engine, vb);
                FilaEngine_destroy(&engine);
                return 1;
            }
            FilaBufferObject_setBuffer(bo, engine, boDesc, 0u);
        }

        FilaVertexBufferBuilder* vbBoBuilder = FilaVertexBufferBuilder_create();
        if (!vbBoBuilder) {
            printf("VertexBuffer BO builder creation failed\n");
            FilaEngine_destroyBufferObject(engine, bo);
            FilaEngine_destroyIndexBuffer(engine, ib);
            FilaEngine_destroyVertexBuffer(engine, vb);
            FilaEngine_destroy(&engine);
            return 1;
        }
        FilaVertexBufferBuilder_bufferCount(vbBoBuilder, 1u);
        FilaVertexBufferBuilder_vertexCount(vbBoBuilder, 3u);
        FilaVertexBufferBuilder_enableBufferObjects(vbBoBuilder, true);
        FilaVertexBufferBuilder_attribute(vbBoBuilder,
                FILA_VERTEX_ATTRIBUTE_POSITION,
                0u,
                FILA_VERTEX_ATTRIBUTE_TYPE_FLOAT3,
                0u,
                0u);
        FilaVertexBufferBuilder_normalized(vbBoBuilder, FILA_VERTEX_ATTRIBUTE_POSITION, false);
        FilaVertexBuffer* vbBo = FilaVertexBufferBuilder_build(vbBoBuilder, engine);
        FilaVertexBufferBuilder_destroy(vbBoBuilder);
        if (!vbBo) {
            printf("VertexBuffer BO build failed\n");
            FilaEngine_destroyBufferObject(engine, bo);
            FilaEngine_destroyIndexBuffer(engine, ib);
            FilaEngine_destroyVertexBuffer(engine, vb);
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaVertexBuffer_setBufferObjectAt(vbBo, engine, 0u, bo);
        if (!FilaVertexBuffer_isCreationComplete(vbBo) || FilaVertexBuffer_getVertexCount(vbBo) != 3u) {
            printf("VertexBuffer BO query mismatch\n");
            FilaEngine_destroyVertexBuffer(engine, vbBo);
            FilaEngine_destroyBufferObject(engine, bo);
            FilaEngine_destroyIndexBuffer(engine, ib);
            FilaEngine_destroyVertexBuffer(engine, vb);
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaEngine_destroyVertexBuffer(engine, vbBo);
        FilaEngine_destroyBufferObject(engine, bo);
    }

    FilaMorphTargetBufferBuilder* morphBuilder = FilaMorphTargetBufferBuilder_create();
    if (!morphBuilder) {
        printf("MorphTargetBuffer builder creation failed\n");
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaMorphTargetBufferBuilder_vertexCount(morphBuilder, 3u);
    FilaMorphTargetBufferBuilder_count(morphBuilder, 1u);
    FilaMorphTargetBufferBuilder_withPositions(morphBuilder, true);
    FilaMorphTargetBufferBuilder_withTangents(morphBuilder, true);
    FilaMorphTargetBuffer* morph = FilaMorphTargetBufferBuilder_build(morphBuilder, engine);
    FilaMorphTargetBufferBuilder_destroy(morphBuilder);
    if (!morph) {
        printf("MorphTargetBuffer build failed\n");
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }

    {
        const float positions[9] = {
                -1.0f, -1.0f, 0.0f,
                 1.0f, -1.0f, 0.0f,
                 0.0f,  1.0f, 0.0f,
        };
        const int16_t tangents[12] = {
                0, 0, 32767, 0,
                0, 0, 32767, 0,
                0, 0, 32767, 0,
        };
        FilaMorphTargetBuffer_setPositionsAtFloat3(morph, engine, 0u, positions, 3u, 0u);
        FilaMorphTargetBuffer_setTangentsAt(morph, engine, 0u, tangents, 3u, 0u);
    }

    FilaEntity renderableEntity = FilaEntityManager_create();
    if (renderableEntity == 0) {
        printf("Renderable entity creation failed\n");
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManagerBuilder* rb = FilaRenderableManagerBuilder_create(1u);
    if (!rb) {
        printf("Renderable builder creation failed\n");
        FilaEntityManager_destroy(renderableEntity);
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaRenderableManagerBuilder_culling(rb, false);
    FilaRenderableManagerBuilder_castShadows(rb, false);
    FilaRenderableManagerBuilder_receiveShadows(rb, false);
    FilaRenderableManagerBuilder_geometry(rb,
            0u,
            FILA_RENDERABLE_PRIMITIVE_TRIANGLES,
            vb,
            ib,
            0u,
            3u);
    const bool renderableBuilt = FilaRenderableManagerBuilder_build(rb, engine, renderableEntity);
    FilaRenderableManagerBuilder_destroy(rb);

    if (!renderableBuilt) {
        printf("Renderable build failed\n");
        FilaEntityManager_destroy(renderableEntity);
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager* rm = FilaEngine_getRenderableManager(engine);
    if (!rm || !FilaRenderableManager_hasComponent(rm, renderableEntity)) {
        printf("Renderable component missing after build\n");
        FilaEntityManager_destroy(renderableEntity);
        FilaEngine_destroyIndexBuffer(engine, ib);
        FilaEngine_destroyVertexBuffer(engine, vb);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_destroy(rm, renderableEntity);
    FilaEntityManager_destroy(renderableEntity);

    FilaEngine_destroyMorphTargetBuffer(engine, morph);
    FilaEngine_destroyIndexBuffer(engine, ib);
    FilaEngine_destroyVertexBuffer(engine, vb);
    FilaEngine_destroy(&engine);

    printf("Engine+resource_builders functionality program completed\n");
    return 0;
}


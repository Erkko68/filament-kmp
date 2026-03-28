#include <stdio.h>

#include "filament/Engine.h"
#include "filament/IndexBuffer.h"
#include "filament/VertexBuffer.h"

int main(void) {
    printf("Running engine+resource_builders smoke program...\n");

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

    FilaEngine_destroyIndexBuffer(engine, ib);
    FilaEngine_destroyVertexBuffer(engine, vb);
    FilaEngine_destroy(&engine);

    printf("Engine+resource_builders smoke program completed\n");
    return 0;
}


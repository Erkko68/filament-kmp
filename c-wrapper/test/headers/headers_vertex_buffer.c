#include "filament/Engine.h"
#include "filament/BufferObject.h"
#include "filament/VertexBuffer.h"

// Verifies VertexBuffer builder and basic query APIs are consumable from C.
void test_headers_vertex_buffer(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaVertexBufferBuilder* builder = FilaVertexBufferBuilder_create();
    FilaVertexBufferBuilder_bufferCount(builder, 1u);
    FilaVertexBufferBuilder_vertexCount(builder, 3u);
    FilaVertexBufferBuilder_attribute(builder,
            FILA_VERTEX_ATTRIBUTE_POSITION,
            0u,
            FILA_VERTEX_ATTRIBUTE_TYPE_FLOAT3,
            0u,
            0u);
    FilaVertexBufferBuilder_enableBufferObjects(builder, true);
    FilaVertexBufferBuilder_normalized(builder, FILA_VERTEX_ATTRIBUTE_POSITION, false);
    FilaVertexBufferBuilder_advancedSkinning(builder, true);
    FilaVertexBuffer* vb = FilaVertexBufferBuilder_build(builder, engine);
    FilaBufferObject* bo = (FilaBufferObject*)0;
    (void)FilaVertexBuffer_getVertexCount(vb);
    (void)FilaVertexBuffer_isCreationComplete(vb);
    FilaVertexBuffer_setBufferObjectAt(vb, engine, 0u, bo);
    FilaVertexBufferBuilder_destroy(builder);
    FilaEngine_destroyVertexBuffer(engine, vb);
}


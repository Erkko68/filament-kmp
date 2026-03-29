#include "filament/Engine.h"
#include "filament/IndexBuffer.h"

// Verifies IndexBuffer builder and basic query APIs are consumable from C.
void fila_index_buffer_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaIndexBufferBuilder* builder = FilaIndexBufferBuilder_create();
    FilaIndexBufferBuilder_indexCount(builder, 3u);
    FilaIndexBufferBuilder_bufferType(builder, FILA_INDEX_TYPE_USHORT);
    FilaIndexBuffer* ib = FilaIndexBufferBuilder_build(builder, engine);
    (void)FilaIndexBuffer_getIndexCount(ib);
    FilaIndexBufferBuilder_destroy(builder);
    FilaEngine_destroyIndexBuffer(engine, ib);
}


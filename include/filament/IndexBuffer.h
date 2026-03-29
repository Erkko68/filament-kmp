#ifndef FILAMENT_C_INDEX_BUFFER_H
#define FILAMENT_C_INDEX_BUFFER_H

#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "BufferDescriptor.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaIndexType {
    FILA_INDEX_TYPE_USHORT = 0,
    FILA_INDEX_TYPE_UINT = 1,
} FilaIndexType;

FilaIndexBufferBuilder* FilaIndexBufferBuilder_create(void);
void FilaIndexBufferBuilder_destroy(FilaIndexBufferBuilder* builder);
void FilaIndexBufferBuilder_indexCount(FilaIndexBufferBuilder* builder, uint32_t indexCount);
void FilaIndexBufferBuilder_bufferType(FilaIndexBufferBuilder* builder, FilaIndexType indexType);
FilaIndexBuffer* FilaIndexBufferBuilder_build(FilaIndexBufferBuilder* builder, FilaEngine* engine);
size_t FilaIndexBuffer_getIndexCount(const FilaIndexBuffer* indexBuffer);

// Set buffer data for an index buffer
void FilaIndexBuffer_setBuffer(FilaIndexBuffer* indexBuffer, FilaEngine* engine, FilaBufferDescriptor* buffer, uint32_t byteOffset);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_INDEX_BUFFER_H


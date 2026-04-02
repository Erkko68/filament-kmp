#ifndef FILAMENT_C_VERTEX_BUFFER_H
#define FILAMENT_C_VERTEX_BUFFER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "BufferDescriptor.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaVertexAttribute {
    FILA_VERTEX_ATTRIBUTE_POSITION = 0,
} FilaVertexAttribute;

typedef enum FilaVertexAttributeType {
    FILA_VERTEX_ATTRIBUTE_TYPE_FLOAT3 = 10,
} FilaVertexAttributeType;

FilaVertexBufferBuilder* FilaVertexBufferBuilder_create(void);
void FilaVertexBufferBuilder_destroy(FilaVertexBufferBuilder* builder);
void FilaVertexBufferBuilder_bufferCount(FilaVertexBufferBuilder* builder, uint8_t bufferCount);
void FilaVertexBufferBuilder_vertexCount(FilaVertexBufferBuilder* builder, uint32_t vertexCount);
void FilaVertexBufferBuilder_attribute(FilaVertexBufferBuilder* builder,
        FilaVertexAttribute attribute,
        uint8_t bufferIndex,
        FilaVertexAttributeType attributeType,
        uint32_t byteOffset,
        uint8_t byteStride);
void FilaVertexBufferBuilder_enableBufferObjects(FilaVertexBufferBuilder* builder, bool enabled);
void FilaVertexBufferBuilder_normalized(FilaVertexBufferBuilder* builder, FilaVertexAttribute attribute, bool normalized);
void FilaVertexBufferBuilder_advancedSkinning(FilaVertexBufferBuilder* builder, bool enabled);
FilaVertexBuffer* FilaVertexBufferBuilder_build(FilaVertexBufferBuilder* builder, FilaEngine* engine);
size_t FilaVertexBuffer_getVertexCount(const FilaVertexBuffer* vertexBuffer);
bool FilaVertexBuffer_isCreationComplete(const FilaVertexBuffer* vertexBuffer);
// Set buffer data for a vertex buffer
void FilaVertexBuffer_setBufferAt(FilaVertexBuffer* vertexBuffer, FilaEngine* engine, uint8_t bufferIndex, FilaBufferDescriptor* buffer, uint32_t byteOffset);
void FilaVertexBuffer_setBufferObjectAt(FilaVertexBuffer* vertexBuffer, FilaEngine* engine, uint8_t bufferIndex, const FilaBufferObject* bufferObject);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_VERTEX_BUFFER_H


#ifndef FILAMENT_C_BUFFEROBJECT_H
#define FILAMENT_C_BUFFEROBJECT_H

#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "../backend/BufferDescriptor.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaBufferObjectBinding {
    FILA_BUFFER_OBJECT_BINDING_VERTEX = 0,
    FILA_BUFFER_OBJECT_BINDING_UNIFORM = 1,
    FILA_BUFFER_OBJECT_BINDING_SHADER_STORAGE = 2
} FilaBufferObjectBinding;

// BufferObject
size_t FilaBufferObject_getByteCount(const FilaBufferObject* bufferObject);
void FilaBufferObject_setBuffer(FilaBufferObject* bufferObject, FilaEngine* engine, FilaBufferDescriptor* buffer, uint32_t byteOffset);

// BufferObject Builder
FilaBufferObjectBuilder* FilaBufferObjectBuilder_create(void);
void FilaBufferObjectBuilder_destroy(FilaBufferObjectBuilder* builder);
void FilaBufferObjectBuilder_size(FilaBufferObjectBuilder* builder, uint32_t byteCount);
void FilaBufferObjectBuilder_bindingType(FilaBufferObjectBuilder* builder, FilaBufferObjectBinding bindingType);
FilaBufferObject* FilaBufferObjectBuilder_build(FilaBufferObjectBuilder* builder, FilaEngine* engine);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BUFFEROBJECT_H

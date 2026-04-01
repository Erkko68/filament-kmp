#include "filament/BufferObject.h"
#include "filament/Engine.h"

static void test_headers_buffer_object_release(void* buffer, size_t size, void* user) {
    (void)buffer;
    (void)size;
    (void)user;
}

void test_headers_buffer_object(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaBufferObjectBuilder* builder = FilaBufferObjectBuilder_create();

    FilaBufferObjectBuilder_size(builder, 1024u);
    FilaBufferObjectBuilder_bindingType(builder, FILA_BUFFER_OBJECT_BINDING_VERTEX);

    FilaBufferObject* bufferObject = FilaBufferObjectBuilder_build(builder, engine);
    (void)FilaBufferObject_getByteCount(bufferObject);

    FilaBufferDescriptor* descriptor =
        FilaBufferDescriptor_create((const void*)0x1, 16u, test_headers_buffer_object_release, (void*)0x2);
    FilaBufferObject_setBuffer(bufferObject, engine, descriptor, 0u);

    FilaBufferObjectBuilder_destroy(builder);
    FilaBufferDescriptor_destroy(descriptor);
}
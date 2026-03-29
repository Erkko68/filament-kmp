#include "filament/BufferObject.h"

void buffer_object_test(void) {
    FilaBufferObjectBuilder* builder = FilaBufferObjectBuilder_create();
    FilaBufferObjectBuilder_size(builder, 1024);
    FilaBufferObjectBuilder_bindingType(builder, FILA_BUFFER_OBJECT_BINDING_VERTEX);
    FilaBufferObjectBuilder_destroy(builder);
}
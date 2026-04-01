#include <stdio.h>
#include "filament/Engine.h"
#include "filament/BufferObject.h"

int main(void) {
    printf("Running engine+bufferobject functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaBufferObjectBuilder* builder = FilaBufferObjectBuilder_create();
    FilaBufferObjectBuilder_size(builder, 1024);
    FilaBufferObjectBuilder_bindingType(builder, FILA_BUFFER_OBJECT_BINDING_VERTEX);
    FilaBufferObject* bufferObject = FilaBufferObjectBuilder_build(builder, engine);
    FilaBufferObjectBuilder_destroy(builder);

    if (!bufferObject) {
        printf("BufferObject creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaBufferObject_getByteCount(bufferObject) != 1024) {
        printf("BufferObject byte count is incorrect\n");
        FilaEngine_destroyBufferObject(engine, bufferObject);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroyBufferObject(engine, bufferObject);
    FilaEngine_destroy(&engine);

    printf("Engine+bufferobject functionality program completed\n");
    return 0;
}
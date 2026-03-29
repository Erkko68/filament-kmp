#include "filament/Engine.h"
#include "filament/InstanceBuffer.h"

// Verifies InstanceBuffer C API is consumable from C.
void fila_instance_buffer_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaInstanceBufferBuilder* builder = FilaInstanceBufferBuilder_create(4u);
    FilaInstanceBuffer* instanceBuffer = FilaInstanceBufferBuilder_build(builder, engine);

    float transforms[16] = {
        1.f, 0.f, 0.f, 0.f,
        0.f, 1.f, 0.f, 0.f,
        0.f, 0.f, 1.f, 0.f,
        0.f, 0.f, 0.f, 1.f
    };
    float out[16] = {0};

    FilaInstanceBuffer_setLocalTransforms(instanceBuffer, transforms, 1u, 0u);
    (void)FilaInstanceBuffer_getLocalTransform(instanceBuffer, 0u, out);
    (void)FilaInstanceBuffer_getInstanceCount(instanceBuffer);

    FilaInstanceBufferBuilder_destroy(builder);
    FilaEngine_destroyInstanceBuffer(engine, instanceBuffer);
}


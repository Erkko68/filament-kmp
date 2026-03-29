#include "filament/Engine.h"
#include "filament/SkinningBuffer.h"

// Verifies SkinningBuffer C API is consumable from C.
void fila_skinning_buffer_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaSkinningBufferBuilder* builder = FilaSkinningBufferBuilder_create();
    FilaSkinningBufferBuilder_boneCount(builder, 32u);
    FilaSkinningBufferBuilder_initialize(builder, true);

    FilaSkinningBuffer* skinningBuffer = FilaSkinningBufferBuilder_build(builder, engine);
    float identity[16] = {
        1.f, 0.f, 0.f, 0.f,
        0.f, 1.f, 0.f, 0.f,
        0.f, 0.f, 1.f, 0.f,
        0.f, 0.f, 0.f, 1.f
    };

    FilaSkinningBuffer_setBonesMat4f(skinningBuffer, engine, identity, 1u, 0u);
    (void)FilaSkinningBuffer_getBoneCount(skinningBuffer);

    FilaSkinningBufferBuilder_destroy(builder);
    FilaEngine_destroySkinningBuffer(engine, skinningBuffer);
}


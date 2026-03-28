#include "filament/Engine.h"
#include "filament/InstanceBuffer.h"

// Function pointer assignments lock exported C signatures.
static FilaInstanceBufferBuilder* (*g_builder_create)(size_t) = FilaInstanceBufferBuilder_create;
static void (*g_builder_destroy)(FilaInstanceBufferBuilder*) = FilaInstanceBufferBuilder_destroy;
static FilaInstanceBuffer* (*g_builder_build)(FilaInstanceBufferBuilder*, FilaEngine*) = FilaInstanceBufferBuilder_build;

static size_t (*g_get_instance_count)(const FilaInstanceBuffer*) = FilaInstanceBuffer_getInstanceCount;
static void (*g_set_local_transforms)(FilaInstanceBuffer*, const float*, size_t, size_t) = FilaInstanceBuffer_setLocalTransforms;
static bool (*g_get_local_transform)(FilaInstanceBuffer*, size_t, float*) = FilaInstanceBuffer_getLocalTransform;
static void (*g_engine_destroy)(FilaEngine*, FilaInstanceBuffer*) = FilaEngine_destroyInstanceBuffer;

void fila_instance_buffer_signature_compile_only(void) {
    (void)g_builder_create;
    (void)g_builder_destroy;
    (void)g_builder_build;
    (void)g_get_instance_count;
    (void)g_set_local_transforms;
    (void)g_get_local_transform;
    (void)g_engine_destroy;
}


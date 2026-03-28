#include "filament/Engine.h"
#include "filament/SkinningBuffer.h"

// Function pointer assignments lock exported C signatures.
static FilaSkinningBufferBuilder* (*g_builder_create)(void) = FilaSkinningBufferBuilder_create;
static void (*g_builder_destroy)(FilaSkinningBufferBuilder*) = FilaSkinningBufferBuilder_destroy;
static void (*g_builder_bone_count)(FilaSkinningBufferBuilder*, uint32_t) = FilaSkinningBufferBuilder_boneCount;
static void (*g_builder_initialize)(FilaSkinningBufferBuilder*, bool) = FilaSkinningBufferBuilder_initialize;
static FilaSkinningBuffer* (*g_builder_build)(FilaSkinningBufferBuilder*, FilaEngine*) = FilaSkinningBufferBuilder_build;
static void (*g_set_bones_mat4f)(FilaSkinningBuffer*, FilaEngine*, const float*, size_t, size_t) = FilaSkinningBuffer_setBonesMat4f;
static size_t (*g_get_bone_count)(const FilaSkinningBuffer*) = FilaSkinningBuffer_getBoneCount;
static void (*g_engine_destroy)(FilaEngine*, FilaSkinningBuffer*) = FilaEngine_destroySkinningBuffer;

void fila_skinning_buffer_signature_compile_only(void) {
    (void)g_builder_create;
    (void)g_builder_destroy;
    (void)g_builder_bone_count;
    (void)g_builder_initialize;
    (void)g_builder_build;
    (void)g_set_bones_mat4f;
    (void)g_get_bone_count;
    (void)g_engine_destroy;
}


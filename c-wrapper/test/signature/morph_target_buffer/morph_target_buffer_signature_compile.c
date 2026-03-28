#include "filament/Engine.h"
#include "filament/MorphTargetBuffer.h"

// Function pointer assignments lock exported C signatures.
static FilaMorphTargetBufferBuilder* (*g_builder_create)(void) = FilaMorphTargetBufferBuilder_create;
static void (*g_builder_destroy)(FilaMorphTargetBufferBuilder*) = FilaMorphTargetBufferBuilder_destroy;
static void (*g_builder_vertex_count)(FilaMorphTargetBufferBuilder*, size_t) = FilaMorphTargetBufferBuilder_vertexCount;
static void (*g_builder_count)(FilaMorphTargetBufferBuilder*, size_t) = FilaMorphTargetBufferBuilder_count;
static void (*g_builder_with_positions)(FilaMorphTargetBufferBuilder*, bool) = FilaMorphTargetBufferBuilder_withPositions;
static void (*g_builder_with_tangents)(FilaMorphTargetBufferBuilder*, bool) = FilaMorphTargetBufferBuilder_withTangents;
static void (*g_builder_custom_morphing)(FilaMorphTargetBufferBuilder*, bool) = FilaMorphTargetBufferBuilder_enableCustomMorphing;
static FilaMorphTargetBuffer* (*g_builder_build)(FilaMorphTargetBufferBuilder*, FilaEngine*) = FilaMorphTargetBufferBuilder_build;

static size_t (*g_get_vertex_count)(const FilaMorphTargetBuffer*) = FilaMorphTargetBuffer_getVertexCount;
static size_t (*g_get_count)(const FilaMorphTargetBuffer*) = FilaMorphTargetBuffer_getCount;
static bool (*g_has_positions)(const FilaMorphTargetBuffer*) = FilaMorphTargetBuffer_hasPositions;
static bool (*g_has_tangents)(const FilaMorphTargetBuffer*) = FilaMorphTargetBuffer_hasTangents;
static bool (*g_is_custom_morphing_enabled)(const FilaMorphTargetBuffer*) = FilaMorphTargetBuffer_isCustomMorphingEnabled;
static void (*g_engine_destroy)(FilaEngine*, FilaMorphTargetBuffer*) = FilaEngine_destroyMorphTargetBuffer;

void fila_morph_target_buffer_signature_compile_only(void) {
    (void)g_builder_create;
    (void)g_builder_destroy;
    (void)g_builder_vertex_count;
    (void)g_builder_count;
    (void)g_builder_with_positions;
    (void)g_builder_with_tangents;
    (void)g_builder_custom_morphing;
    (void)g_builder_build;
    (void)g_get_vertex_count;
    (void)g_get_count;
    (void)g_has_positions;
    (void)g_has_tangents;
    (void)g_is_custom_morphing_enabled;
    (void)g_engine_destroy;
}


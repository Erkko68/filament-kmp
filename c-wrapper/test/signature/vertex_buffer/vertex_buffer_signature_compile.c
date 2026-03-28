#include <stddef.h>
#include <stdint.h>

#include "filament/VertexBuffer.h"

// Function pointer assignments lock exported C signatures.
static FilaVertexBufferBuilder* (*g_vb_builder_create)(void) = FilaVertexBufferBuilder_create;
static void (*g_vb_builder_destroy)(FilaVertexBufferBuilder*) = FilaVertexBufferBuilder_destroy;
static void (*g_vb_builder_buffer_count)(FilaVertexBufferBuilder*, uint8_t) = FilaVertexBufferBuilder_bufferCount;
static void (*g_vb_builder_vertex_count)(FilaVertexBufferBuilder*, uint32_t) = FilaVertexBufferBuilder_vertexCount;
static void (*g_vb_builder_attribute)(FilaVertexBufferBuilder*, FilaVertexAttribute, uint8_t, FilaVertexAttributeType, uint32_t, uint8_t) = FilaVertexBufferBuilder_attribute;
static FilaVertexBuffer* (*g_vb_builder_build)(FilaVertexBufferBuilder*, FilaEngine*) = FilaVertexBufferBuilder_build;
static size_t (*g_vb_get_vertex_count)(const FilaVertexBuffer*) = FilaVertexBuffer_getVertexCount;

void fila_vertex_buffer_signature_compile_only(void) {
    (void)g_vb_builder_create;
    (void)g_vb_builder_destroy;
    (void)g_vb_builder_buffer_count;
    (void)g_vb_builder_vertex_count;
    (void)g_vb_builder_attribute;
    (void)g_vb_builder_build;
    (void)g_vb_get_vertex_count;
}


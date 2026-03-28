#include <stddef.h>
#include <stdint.h>

#include "filament/IndexBuffer.h"

// Function pointer assignments lock exported C signatures.
static FilaIndexBufferBuilder* (*g_ib_builder_create)(void) = FilaIndexBufferBuilder_create;
static void (*g_ib_builder_destroy)(FilaIndexBufferBuilder*) = FilaIndexBufferBuilder_destroy;
static void (*g_ib_builder_index_count)(FilaIndexBufferBuilder*, uint32_t) = FilaIndexBufferBuilder_indexCount;
static void (*g_ib_builder_buffer_type)(FilaIndexBufferBuilder*, FilaIndexType) = FilaIndexBufferBuilder_bufferType;
static FilaIndexBuffer* (*g_ib_builder_build)(FilaIndexBufferBuilder*, FilaEngine*) = FilaIndexBufferBuilder_build;
static size_t (*g_ib_get_index_count)(const FilaIndexBuffer*) = FilaIndexBuffer_getIndexCount;

void fila_index_buffer_signature_compile_only(void) {
    (void)g_ib_builder_create;
    (void)g_ib_builder_destroy;
    (void)g_ib_builder_index_count;
    (void)g_ib_builder_buffer_type;
    (void)g_ib_builder_build;
    (void)g_ib_get_index_count;
}


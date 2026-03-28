#include <stdbool.h>

#include "filament/Engine.h"
#include "filament/Stream.h"
#include "filament/Types.h"

// Function pointer assignments lock exported C signatures.
static FilaStreamBuilder* (*g_stream_builder_create)(void) = FilaStreamBuilder_create;
static void (*g_stream_builder_destroy)(FilaStreamBuilder*) = FilaStreamBuilder_destroy;
static void (*g_stream_builder_width)(FilaStreamBuilder*, uint32_t) = FilaStreamBuilder_width;
static void (*g_stream_builder_height)(FilaStreamBuilder*, uint32_t) = FilaStreamBuilder_height;
static FilaStream* (*g_stream_builder_build)(FilaStreamBuilder*, FilaEngine*) = FilaStreamBuilder_build;

static FilaStreamType (*g_stream_get_type)(const FilaStream*) = FilaStream_getStreamType;
static void (*g_stream_set_dimensions)(FilaStream*, uint32_t, uint32_t) = FilaStream_setDimensions;
static int64_t (*g_stream_get_timestamp)(const FilaStream*) = FilaStream_getTimestamp;

void fila_stream_signature_compile_only(void) {
    (void)g_stream_builder_create;
    (void)g_stream_builder_destroy;
    (void)g_stream_builder_width;
    (void)g_stream_builder_height;
    (void)g_stream_builder_build;
    (void)g_stream_get_type;
    (void)g_stream_set_dimensions;
    (void)g_stream_get_timestamp;

    FilaStreamType type = FILA_STREAM_ACQUIRED;
    (void)type;
}


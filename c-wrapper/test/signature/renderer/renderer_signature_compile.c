#include <stdbool.h>
#include <stdint.h>

#include "filament/Renderer.h"

// Function pointer assignments lock exported C signatures.
static bool (*g_renderer_begin_frame)(FilaRenderer*, FilaSwapChain*, uint64_t) = FilaRenderer_beginFrame;
static void (*g_renderer_render)(FilaRenderer*, const FilaView*) = FilaRenderer_render;
static void (*g_renderer_end_frame)(FilaRenderer*) = FilaRenderer_endFrame;

void fila_renderer_signature_compile_only(void) {
    (void)g_renderer_begin_frame;
    (void)g_renderer_render;
    (void)g_renderer_end_frame;
}


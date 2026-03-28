#include <stdint.h>

#include "filament/Fence.h"

// Function pointer assignments lock exported C signatures.
static FilaFenceStatus (*g_fence_wait)(FilaFence*, FilaFenceMode, uint64_t) = FilaFence_wait;
static FilaFenceStatus (*g_fence_wait_and_destroy)(FilaFence*, FilaFenceMode) = FilaFence_waitAndDestroy;

void fila_fence_signature_compile_only(void) {
    (void)g_fence_wait;
    (void)g_fence_wait_and_destroy;
}


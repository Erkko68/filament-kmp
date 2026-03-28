#include "filament/Engine.h"
#include "filament/Fence.h"

// Verifies Fence API is consumable from C and composes with Engine lifecycle.
void fila_fence_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaFence* fence = FilaEngine_createFence(engine);

    (void)FilaFence_wait(fence, FILA_FENCE_MODE_FLUSH, 0u);
    FilaEngine_destroyFence(engine, fence);
}


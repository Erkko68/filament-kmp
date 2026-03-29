#include "filament/Engine.h"
#include "filament/Fence.h"

// Verifies Fence API is consumable from C and composes with Engine lifecycle.
void fila_fence_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaFence* fenceA = FilaEngine_createFence(engine);
    FilaFence* fenceB = FilaEngine_createFence(engine);

    (void)FilaFence_wait(fenceA, FILA_FENCE_MODE_FLUSH, 0u);
    (void)FilaFence_waitAndDestroy(fenceB, FILA_FENCE_MODE_FLUSH);
    FilaEngine_destroyFence(engine, fenceA);
}


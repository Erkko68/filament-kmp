#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Fence.h"

int main(void) {
    printf("Running engine+fence functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaFence* fence = FilaEngine_createFence(engine);
    if (!fence) {
        printf("Fence creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    const FilaFenceStatus status = FilaFence_wait(fence, FILA_FENCE_MODE_FLUSH, 0u);
    if (status != FILA_FENCE_STATUS_CONDITION_SATISFIED &&
            status != FILA_FENCE_STATUS_TIMEOUT_EXPIRED) {
        printf("Unexpected fence wait status: %d\n", (int)status);
        FilaEngine_destroyFence(engine, fence);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroyFence(engine, fence);

    FilaFence* fence2 = FilaEngine_createFence(engine);
    if (!fence2) {
        printf("Second fence creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    const FilaFenceStatus status2 = FilaFence_waitAndDestroy(fence2, FILA_FENCE_MODE_FLUSH);
    if (status2 != FILA_FENCE_STATUS_CONDITION_SATISFIED &&
            status2 != FILA_FENCE_STATUS_TIMEOUT_EXPIRED) {
        printf("Unexpected waitAndDestroy fence status: %d\n", (int)status2);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroy(&engine);

    printf("Engine+fence functionality program completed\n");
    return 0;
}


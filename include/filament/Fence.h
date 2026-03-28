#ifndef FILAMENT_C_FENCE_H
#define FILAMENT_C_FENCE_H

#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaFenceMode {
    FILA_FENCE_MODE_FLUSH = 0,
    FILA_FENCE_MODE_DONT_FLUSH = 1,
} FilaFenceMode;

typedef enum FilaFenceStatus {
    FILA_FENCE_STATUS_ERROR = 0,
    FILA_FENCE_STATUS_CONDITION_SATISFIED = 1,
    FILA_FENCE_STATUS_TIMEOUT_EXPIRED = 2,
} FilaFenceStatus;

// Special timeout value to wait indefinitely.
static const uint64_t FILA_FENCE_WAIT_FOR_EVER = UINT64_MAX;

// Waits for the fence to signal.
FilaFenceStatus FilaFence_wait(FilaFence* fence, FilaFenceMode mode, uint64_t timeoutNs);

// Waits for a fence and destroys it.
FilaFenceStatus FilaFence_waitAndDestroy(FilaFence* fence, FilaFenceMode mode);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_FENCE_H


#include <filament/Fence.h>

#include "../../include/filament/Fence.h"

namespace {
filament::Fence::Mode toFenceMode(FilaFenceMode mode) {
    switch (mode) {
        case FILA_FENCE_MODE_DONT_FLUSH:
            return filament::Fence::Mode::DONT_FLUSH;
        case FILA_FENCE_MODE_FLUSH:
        default:
            return filament::Fence::Mode::FLUSH;
    }
}

FilaFenceStatus fromFenceStatus(filament::Fence::FenceStatus status) {
    switch (status) {
        case filament::Fence::FenceStatus::CONDITION_SATISFIED:
            return FILA_FENCE_STATUS_CONDITION_SATISFIED;
        case filament::Fence::FenceStatus::TIMEOUT_EXPIRED:
            return FILA_FENCE_STATUS_TIMEOUT_EXPIRED;
        case filament::Fence::FenceStatus::ERROR:
        default:
            return FILA_FENCE_STATUS_ERROR;
    }
}
} // namespace

extern "C" {

FilaFenceStatus FilaFence_wait(FilaFence* fence, FilaFenceMode mode, uint64_t timeoutNs) {
    if (!fence) {
        return FILA_FENCE_STATUS_ERROR;
    }
    auto cppFence = reinterpret_cast<filament::Fence*>(fence);
    return fromFenceStatus(cppFence->wait(toFenceMode(mode), timeoutNs));
}

FilaFenceStatus FilaFence_waitAndDestroy(FilaFence* fence, FilaFenceMode mode) {
    if (!fence) {
        return FILA_FENCE_STATUS_ERROR;
    }
    auto cppFence = reinterpret_cast<filament::Fence*>(fence);
    return fromFenceStatus(filament::Fence::waitAndDestroy(cppFence, toFenceMode(mode)));
}

} // extern "C"


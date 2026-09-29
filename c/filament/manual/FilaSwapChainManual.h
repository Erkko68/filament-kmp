// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILASWAPCHAINMANUAL_H
#define FILA_MANUAL_FILASWAPCHAINMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// SwapChain::setFrameScheduledCallback, presenting before callback runs: C can't hold the PresentCallable. NULL unsets.
void FilaSwapChain_setFrameScheduledCallback(FilaSwapChain* self, FilaCallbackHandler* handler, FilaCallback callback, void* callbackUser, uint64_t flags);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILASWAPCHAINMANUAL_H

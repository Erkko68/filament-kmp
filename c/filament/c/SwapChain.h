#ifndef FILAMENT_C_SWAP_CHAIN_H
#define FILAMENT_C_SWAP_CHAIN_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef void (*FilaSwapChainFrameCompletedCallback)(FilaSwapChain* swapChain, void* userData);
typedef void (*FilaSwapChainFrameScheduledCallback)(void* userData);

// SwapChain
void FilaSwapChain_setFrameCompletedCallback(FilaSwapChain* swapChain, FilaCallbackHandler* handler, FilaSwapChainFrameCompletedCallback callback, void* userData);

bool FilaSwapChain_isSRGBSwapChainSupported(FilaEngine* engine);
bool FilaSwapChain_isMSAASwapChainSupported(FilaEngine* engine, int samples);
bool FilaSwapChain_isProtectedContentSupported(FilaEngine* engine);

void FilaSwapChain_setFrameScheduledCallback(FilaSwapChain* swapChain, FilaCallbackHandler* handler, FilaSwapChainFrameScheduledCallback callback, void* userData);
bool FilaSwapChain_isFrameScheduledCallbackSet(const FilaSwapChain* swapChain);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SWAP_CHAIN_H

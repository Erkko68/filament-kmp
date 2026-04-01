#include <filament/Engine.h>
#include <filament/SwapChain.h>

#include "../../include/filament/SwapChain.h"

extern "C" {

bool FilaSwapChain_isProtectedContentSupported(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::SwapChain::isProtectedContentSupported(*cppEngine);
}

bool FilaSwapChain_isSRGBSwapChainSupported(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::SwapChain::isSRGBSwapChainSupported(*cppEngine);
}

bool FilaSwapChain_isMSAASwapChainSupported(FilaEngine* engine, uint32_t samples) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::SwapChain::isMSAASwapChainSupported(*cppEngine, samples);
}

void* FilaSwapChain_getNativeWindow(const FilaSwapChain* swapChain) {
    if (!swapChain) {
        return nullptr;
    }
    auto cppSwapChain = reinterpret_cast<const filament::SwapChain*>(swapChain);
    return cppSwapChain->getNativeWindow();
}

} // extern "C"


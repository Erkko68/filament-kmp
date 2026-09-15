#include <filament/SwapChain.h>
#include <filament/Engine.h>
#include <backend/CallbackHandler.h>

#include "FilaCommon.h"
#include "../c/SwapChain.h"

using namespace filament;

extern "C" {

void FilaSwapChain_setFrameCompletedCallback(FilaSwapChain* swapChain, FilaCallbackHandler* handler, FilaSwapChainFrameCompletedCallback callback, void* userData) {
    auto* sc = FILA_CAST(SwapChain, swapChain);
    if (!callback) {
        // Filament unsets on a default-constructed callback. Wrapping a null pointer in a
        // lambda would install a live std::function that does nothing instead.
        sc->setFrameCompletedCallback();
        return;
    }
    sc->setFrameCompletedCallback(
        reinterpret_cast<backend::CallbackHandler*>(handler),
        [callback, userData](SwapChain* s) {
            callback(reinterpret_cast<FilaSwapChain*>(s), userData);
        }
    );
}

bool FilaSwapChain_isSRGBSwapChainSupported(FilaEngine* engine) {
    return SwapChain::isSRGBSwapChainSupported(*FILA_CAST(Engine, engine));
}

bool FilaSwapChain_isMSAASwapChainSupported(FilaEngine* engine, int samples) {
    return SwapChain::isMSAASwapChainSupported(*FILA_CAST(Engine, engine), samples);
}

bool FilaSwapChain_isProtectedContentSupported(FilaEngine* engine) {
    return SwapChain::isProtectedContentSupported(*FILA_CAST(Engine, engine));
}

void FilaSwapChain_setFrameScheduledCallback(FilaSwapChain* swapChain, FilaCallbackHandler* handler, FilaSwapChainFrameScheduledCallback callback, void* userData) {
    auto* sc = FILA_CAST(SwapChain, swapChain);
    if (!callback) {
        sc->setFrameScheduledCallback();
        return;
    }
    sc->setFrameScheduledCallback(
        reinterpret_cast<backend::CallbackHandler*>(handler),
        [callback, userData](backend::PresentCallable presentCallable) {
            // Metal hands presentation to whoever sets this callback and presents nothing itself;
            // every other backend has already presented by now and passes noopPresent. Call it
            // first so the ordering matches, and because an uncalled PresentCallable leaks the
            // frame. Our callback carries no PresentCallable, so the caller cannot do it.
            presentCallable();
            callback(userData);
        }
    );
}

bool FilaSwapChain_isFrameScheduledCallbackSet(const FilaSwapChain* swapChain) {
    return FILA_CONST_CAST(SwapChain, swapChain)->isFrameScheduledCallbackSet();
}

bool FilaSwapChain_isFrameRateChangeSupported(const FilaSwapChain* swapChain) {
    return FILA_CONST_CAST(SwapChain, swapChain)->isFrameRateChangeSupported().is_true();
}

void FilaSwapChain_setFrameRate(FilaSwapChain* swapChain, float frameRate, uint8_t compatibility, uint8_t strategy) {
    FILA_CAST(SwapChain, swapChain)->setFrameRate(frameRate,
            static_cast<SwapChain::FrameRateCompatibility>(compatibility),
            static_cast<SwapChain::ChangeFrameRateStrategy>(strategy));
}

} // extern "C"

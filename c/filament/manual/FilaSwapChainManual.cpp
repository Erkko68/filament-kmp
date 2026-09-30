#include "../generated/Includes.hpp"
#include "FilaSwapChainManual.h"

extern "C" {

void FilaSwapChain_setFrameScheduledCallback(FilaSwapChain* self, FilaCallbackHandler* handler, FilaCallback callback, void* callbackUser, uint64_t flags) {
    // Metal leaves presenting to this callback; other backends pass a no-op. An uncalled PresentCallable leaks the frame.
    fila::cpp(self)->setFrameScheduledCallback(fila::cpp(handler), fila::callable(callback, [=](filament::backend::PresentCallable present) {
        present();
        callback(callbackUser);
    }), flags);
}

} // extern "C"

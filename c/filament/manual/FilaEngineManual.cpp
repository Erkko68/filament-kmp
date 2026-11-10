#include "../generated/Includes.hpp"
#include "FilaEngineManual.h"

extern "C" {

void FilaEngine_resetBackendState(FilaEngine* self) {
#if defined(__EMSCRIPTEN__)
    fila::cpp(self)->resetBackendState();
#else
    (void) self;
#endif
}

} // extern "C"

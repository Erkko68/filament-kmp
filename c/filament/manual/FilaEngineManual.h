// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAENGINEMANUAL_H
#define FILA_MANUAL_FILAENGINEMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Engine::resetBackendState, declared only under __EMSCRIPTEN__: a no-op elsewhere.
void FilaEngine_resetBackendState(FilaEngine* self);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAENGINEMANUAL_H

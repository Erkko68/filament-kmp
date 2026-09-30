// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAVIEWMANUAL_H
#define FILA_MANUAL_FILAVIEWMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// View::pick with the callback and its user data in the PickingQuery's storage; callback gets the FilaViewPickingQueryResult.
void FilaView_pick(FilaView* self, uint32_t x, uint32_t y, FilaCallbackHandler* handler, FilaArgCallback callback, void* callbackUser);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAVIEWMANUAL_H

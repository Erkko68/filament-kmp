#include "../generated/Includes.hpp"
#include "FilaViewManual.h"

using filament::View;

extern "C" {

void FilaView_pick(FilaView* self, uint32_t x, uint32_t y, FilaCallbackHandler* handler, FilaArgCallback callback, void* callbackUser) {
    View::PickingQuery& query = fila::cpp(self)->pick(x, y, fila::cpp(handler), [](View::PickingQueryResult const& result, View::PickingQuery* pq) {
        reinterpret_cast<FilaArgCallback>(pq->storage[0])(const_cast<FilaViewPickingQueryResult*>(fila::c(&result)), pq->storage[1]);
    });
    query.storage[0] = reinterpret_cast<void*>(callback);
    query.storage[1] = callbackUser;
}

} // extern "C"

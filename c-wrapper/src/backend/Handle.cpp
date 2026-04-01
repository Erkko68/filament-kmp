#include "../../include/backend/Handle.h"

#include <backend/Handle.h>

static_assert(filament::backend::HandleBase::nullid == FILA_BACKEND_HANDLE_NULL,
    "FILA_BACKEND_HANDLE_NULL must stay aligned with filament::backend::HandleBase::nullid");

extern "C" {

bool FilaBackendHandle_isValid(FilaBackendHandleId handleId) {
    return handleId != FILA_BACKEND_HANDLE_NULL;
}

void FilaBackendHandle_clear(FilaBackendHandleId* handleId) {
    if (!handleId) {
        return;
    }
    *handleId = FILA_BACKEND_HANDLE_NULL;
}

}


#include "backend/Handle.h"

void backend_handle_test(void) {
    FilaBackendHandleId handle = FILA_BACKEND_HANDLE_NULL;
    FilaBackendHandle_isValid(handle);
    handle = 42u;
    FilaBackendHandle_isValid(handle);
    FilaBackendHandle_clear(&handle);

    FilaBackendTextureHandleId textureHandle = FILA_BACKEND_HANDLE_NULL;
    FilaBackendHandle_clear(&textureHandle);
}


#include "backend/TargetBufferInfo.h"

void backend_target_buffer_info_test(void) {
    FilaBackendTargetBufferInfo* info = FilaBackendTargetBufferInfo_create();
    FilaBackendTargetBufferInfoData data = {
        .textureHandleId = FILA_BACKEND_HANDLE_NULL,
        .level = 0,
        .layer = 0,
    };

    FilaBackendTargetBufferInfo_set(info, &data);
    FilaBackendTargetBufferInfo_get(info, &data);
    FilaBackendTargetBufferInfo_destroy(info);

    info = FilaBackendTargetBufferInfo_createWithHandle(42u);
    FilaBackendTargetBufferInfo_destroy(info);

    info = FilaBackendTargetBufferInfo_createWithHandleLevel(42u, 1);
    FilaBackendTargetBufferInfo_destroy(info);

    info = FilaBackendTargetBufferInfo_createWithHandleLevelLayer(42u, 1, 2);
    FilaBackendTargetBufferInfo_destroy(info);

    FilaBackendMRT* mrt = FilaBackendMRT_create();
    FilaBackendMRT_getMinSupportedRenderTargetCount();
    FilaBackendMRT_getMaxSupportedRenderTargetCount();
    FilaBackendMRT_setTargetBufferAt(mrt, 0, &data);
    FilaBackendMRT_getTargetBufferAt(mrt, 0, &data);
    FilaBackendMRT_destroy(mrt);
}


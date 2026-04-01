#include "backend/DriverEnums.h"

void backend_driver_enums_test(void) {
    FilaBackendTargetBufferFlags flags = FILA_BACKEND_TARGET_BUFFER_ALL;
    (void)flags;

    flags = FilaBackendTargetBufferFlags_at(0);
    flags = FilaBackendTargetBufferFlags_at(8);
    flags = FilaBackendTargetBufferFlags_at(9);
    flags = FilaBackendTargetBufferFlags_at(100);
    (void)flags;

    FilaBackendTextureCubemapFace face = FILA_BACKEND_TEXTURE_CUBEMAP_FACE_NEGATIVE_Z;
    (void)face;
}


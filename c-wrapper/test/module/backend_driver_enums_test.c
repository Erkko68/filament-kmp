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

    FilaBackendRenderTargetAttachmentPoint attachment = FILA_BACKEND_RENDER_TARGET_ATTACHMENT_DEPTH;
    attachment = FilaBackendRenderTargetAttachmentPoint_at(2);
    attachment = FilaBackendRenderTargetAttachmentPoint_at(42);
    (void)attachment;

    FilaBackendTextureUsage usage = FILA_BACKEND_TEXTURE_USAGE_DEFAULT;
    usage = FILA_BACKEND_TEXTURE_USAGE_ALL_ATTACHMENTS;
    (void)usage;
}


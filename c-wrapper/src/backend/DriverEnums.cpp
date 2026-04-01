#include "../../include/backend/DriverEnums.h"

#include <backend/DriverEnums.h>

namespace {
static_assert((uint32_t)filament::backend::TargetBufferFlags::COLOR0 == FILA_BACKEND_TARGET_BUFFER_COLOR0,
    "FilaBackendTargetBufferFlags must stay aligned with filament::backend::TargetBufferFlags");
static_assert((uint32_t)filament::backend::TargetBufferFlags::ALL == FILA_BACKEND_TARGET_BUFFER_ALL,
    "FilaBackendTargetBufferFlags must stay aligned with filament::backend::TargetBufferFlags");
static_assert((int)filament::backend::TextureCubemapFace::NEGATIVE_Z ==
        FILA_BACKEND_TEXTURE_CUBEMAP_FACE_NEGATIVE_Z,
    "FilaBackendTextureCubemapFace must stay aligned with filament::backend::TextureCubemapFace");
static_assert((uint16_t)filament::backend::TextureUsage::DEFAULT == FILA_BACKEND_TEXTURE_USAGE_DEFAULT,
    "FilaBackendTextureUsage must stay aligned with filament::backend::TextureUsage");
} // namespace

extern "C" {

FilaBackendTargetBufferFlags FilaBackendTargetBufferFlags_at(size_t index) {
    return (FilaBackendTargetBufferFlags)filament::backend::getTargetBufferFlagsAt(index);
}

FilaBackendRenderTargetAttachmentPoint FilaBackendRenderTargetAttachmentPoint_at(size_t index) {
    if (index <= 8u) {
        return (FilaBackendRenderTargetAttachmentPoint)index;
    }
    return FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR0;
}

}


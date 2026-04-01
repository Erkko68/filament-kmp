#ifndef FILAMENT_C_RENDER_TARGET_H
#define FILAMENT_C_RENDER_TARGET_H

#include <stdint.h>

#include "Types.h"
#include "../backend/DriverEnums.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef FilaBackendRenderTargetAttachmentPoint FilaRenderTargetAttachmentPoint;
typedef FilaBackendTextureCubemapFace FilaCubemapFace;

#define FILA_RENDER_TARGET_ATTACHMENT_COLOR0 FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR0
#define FILA_RENDER_TARGET_ATTACHMENT_COLOR1 FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR1
#define FILA_RENDER_TARGET_ATTACHMENT_COLOR2 FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR2
#define FILA_RENDER_TARGET_ATTACHMENT_COLOR3 FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR3
#define FILA_RENDER_TARGET_ATTACHMENT_COLOR4 FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR4
#define FILA_RENDER_TARGET_ATTACHMENT_COLOR5 FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR5
#define FILA_RENDER_TARGET_ATTACHMENT_COLOR6 FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR6
#define FILA_RENDER_TARGET_ATTACHMENT_COLOR7 FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR7
#define FILA_RENDER_TARGET_ATTACHMENT_DEPTH FILA_BACKEND_RENDER_TARGET_ATTACHMENT_DEPTH
#define FILA_RENDER_TARGET_ATTACHMENT_COLOR FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR

#define FILA_CUBEMAP_FACE_POSITIVE_X FILA_BACKEND_TEXTURE_CUBEMAP_FACE_POSITIVE_X
#define FILA_CUBEMAP_FACE_NEGATIVE_X FILA_BACKEND_TEXTURE_CUBEMAP_FACE_NEGATIVE_X
#define FILA_CUBEMAP_FACE_POSITIVE_Y FILA_BACKEND_TEXTURE_CUBEMAP_FACE_POSITIVE_Y
#define FILA_CUBEMAP_FACE_NEGATIVE_Y FILA_BACKEND_TEXTURE_CUBEMAP_FACE_NEGATIVE_Y
#define FILA_CUBEMAP_FACE_POSITIVE_Z FILA_BACKEND_TEXTURE_CUBEMAP_FACE_POSITIVE_Z
#define FILA_CUBEMAP_FACE_NEGATIVE_Z FILA_BACKEND_TEXTURE_CUBEMAP_FACE_NEGATIVE_Z

FilaRenderTargetBuilder* FilaRenderTargetBuilder_create(void);
void FilaRenderTargetBuilder_destroy(FilaRenderTargetBuilder* builder);
void FilaRenderTargetBuilder_texture(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        FilaTexture* texture);
void FilaRenderTargetBuilder_mipLevel(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        uint8_t level);
void FilaRenderTargetBuilder_face(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        FilaCubemapFace face);
void FilaRenderTargetBuilder_layer(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        uint32_t layer);
void FilaRenderTargetBuilder_samples(FilaRenderTargetBuilder* builder, uint8_t samples);
void FilaRenderTargetBuilder_multiview(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        uint8_t layerCount,
        uint8_t baseLayer);
FilaRenderTarget* FilaRenderTargetBuilder_build(FilaRenderTargetBuilder* builder, FilaEngine* engine);

FilaTexture* FilaRenderTarget_getTexture(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment);
uint8_t FilaRenderTarget_getMipLevel(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment);
FilaCubemapFace FilaRenderTarget_getFace(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment);
uint32_t FilaRenderTarget_getLayer(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment);
uint8_t FilaRenderTarget_getSupportedColorAttachmentsCount(const FilaRenderTarget* renderTarget);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_RENDER_TARGET_H


#ifndef FILAMENT_C_RENDER_TARGET_H
#define FILAMENT_C_RENDER_TARGET_H

#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaRenderTargetAttachmentPoint {
    FILA_RENDER_TARGET_ATTACHMENT_COLOR0 = 0,
    FILA_RENDER_TARGET_ATTACHMENT_COLOR1 = 1,
    FILA_RENDER_TARGET_ATTACHMENT_COLOR2 = 2,
    FILA_RENDER_TARGET_ATTACHMENT_COLOR3 = 3,
    FILA_RENDER_TARGET_ATTACHMENT_COLOR4 = 4,
    FILA_RENDER_TARGET_ATTACHMENT_COLOR5 = 5,
    FILA_RENDER_TARGET_ATTACHMENT_COLOR6 = 6,
    FILA_RENDER_TARGET_ATTACHMENT_COLOR7 = 7,
    FILA_RENDER_TARGET_ATTACHMENT_DEPTH = 8,
    FILA_RENDER_TARGET_ATTACHMENT_COLOR = 0,
} FilaRenderTargetAttachmentPoint;

typedef enum FilaCubemapFace {
    FILA_CUBEMAP_FACE_POSITIVE_X = 0,
    FILA_CUBEMAP_FACE_NEGATIVE_X = 1,
    FILA_CUBEMAP_FACE_POSITIVE_Y = 2,
    FILA_CUBEMAP_FACE_NEGATIVE_Y = 3,
    FILA_CUBEMAP_FACE_POSITIVE_Z = 4,
    FILA_CUBEMAP_FACE_NEGATIVE_Z = 5,
} FilaCubemapFace;

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
FilaRenderTarget* FilaRenderTargetBuilder_build(FilaRenderTargetBuilder* builder, FilaEngine* engine);

FilaTexture* FilaRenderTarget_getTexture(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment);
uint8_t FilaRenderTarget_getMipLevel(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment);
FilaCubemapFace FilaRenderTarget_getFace(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment);
uint32_t FilaRenderTarget_getLayer(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_RENDER_TARGET_H


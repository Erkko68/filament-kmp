// ABI signature lock test for RenderTarget C API
// Validates pointer signatures and enums at compile time

#include "filament/Engine.h"
#include "filament/RenderTarget.h"
#include "filament/Texture.h"
#include "filament/Types.h"

void fila_render_target_signature_test(void) {
    // Test builder pointer methods return correct signatures
    FilaRenderTargetBuilder* (*builder_create_fn)(void) = FilaRenderTargetBuilder_create;
    void (*builder_destroy_fn)(FilaRenderTargetBuilder*) = FilaRenderTargetBuilder_destroy;
    void (*builder_texture_fn)(FilaRenderTargetBuilder*, FilaRenderTargetAttachmentPoint, FilaTexture*) = FilaRenderTargetBuilder_texture;
    void (*builder_mipLevel_fn)(FilaRenderTargetBuilder*, FilaRenderTargetAttachmentPoint, unsigned char) = FilaRenderTargetBuilder_mipLevel;
    void (*builder_face_fn)(FilaRenderTargetBuilder*, FilaRenderTargetAttachmentPoint, FilaCubemapFace) = FilaRenderTargetBuilder_face;
    void (*builder_layer_fn)(FilaRenderTargetBuilder*, FilaRenderTargetAttachmentPoint, unsigned int) = FilaRenderTargetBuilder_layer;
    void (*builder_samples_fn)(FilaRenderTargetBuilder*, unsigned char) = FilaRenderTargetBuilder_samples;
    FilaRenderTarget* (*builder_build_fn)(FilaRenderTargetBuilder*, FilaEngine*) = FilaRenderTargetBuilder_build;

    // Test getter function signatures
    FilaTexture* (*get_texture_fn)(const FilaRenderTarget*, FilaRenderTargetAttachmentPoint) = FilaRenderTarget_getTexture;
    unsigned char (*get_mipLevel_fn)(const FilaRenderTarget*, FilaRenderTargetAttachmentPoint) = FilaRenderTarget_getMipLevel;
    FilaCubemapFace (*get_face_fn)(const FilaRenderTarget*, FilaRenderTargetAttachmentPoint) = FilaRenderTarget_getFace;
    unsigned int (*get_layer_fn)(const FilaRenderTarget*, FilaRenderTargetAttachmentPoint) = FilaRenderTarget_getLayer;

    // Test attachment point enum values
    FilaRenderTargetAttachmentPoint color0 = FILA_RENDER_TARGET_ATTACHMENT_COLOR0;
    FilaRenderTargetAttachmentPoint color1 = FILA_RENDER_TARGET_ATTACHMENT_COLOR1;
    FilaRenderTargetAttachmentPoint depth = FILA_RENDER_TARGET_ATTACHMENT_DEPTH;

    // Test cubemap face enum values
    FilaCubemapFace face_pos_x = FILA_CUBEMAP_FACE_POSITIVE_X;
    FilaCubemapFace face_neg_x = FILA_CUBEMAP_FACE_NEGATIVE_X;
    FilaCubemapFace face_pos_y = FILA_CUBEMAP_FACE_POSITIVE_Y;
    FilaCubemapFace face_neg_y = FILA_CUBEMAP_FACE_NEGATIVE_Y;
    FilaCubemapFace face_pos_z = FILA_CUBEMAP_FACE_POSITIVE_Z;
    FilaCubemapFace face_neg_z = FILA_CUBEMAP_FACE_NEGATIVE_Z;

    (void)builder_create_fn;
    (void)builder_destroy_fn;
    (void)builder_texture_fn;
    (void)builder_mipLevel_fn;
    (void)builder_face_fn;
    (void)builder_layer_fn;
    (void)builder_samples_fn;
    (void)builder_build_fn;
    (void)get_texture_fn;
    (void)get_mipLevel_fn;
    (void)get_face_fn;
    (void)get_layer_fn;
    (void)color0;
    (void)color1;
    (void)depth;
    (void)face_pos_x;
    (void)face_neg_x;
    (void)face_pos_y;
    (void)face_neg_y;
    (void)face_pos_z;
    (void)face_neg_z;
}


// Compile-only test for RenderTarget C API
// Tests builder and query functionality

#include "filament/Engine.h"
#include "filament/RenderTarget.h"
#include "filament/Texture.h"

void test_headers_render_target(void) {
    FilaEngine* engine = (FilaEngine*)0;

    FilaTextureBuilder* textureBuilder = FilaTextureBuilder_create();
    FilaTextureBuilder_width(textureBuilder, 512);
    FilaTextureBuilder_height(textureBuilder, 512);
    FilaTextureBuilder_format(textureBuilder, FILA_TEXTURE_FORMAT_RGBA8);
    FilaTexture* colorTexture = FilaTextureBuilder_build(textureBuilder, engine);
    FilaTextureBuilder_destroy(textureBuilder);

    FilaRenderTargetBuilder* builder = FilaRenderTargetBuilder_create();
    FilaRenderTargetBuilder_texture(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, colorTexture);
    FilaRenderTargetBuilder_mipLevel(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, 0);
    FilaRenderTargetBuilder_face(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, FILA_CUBEMAP_FACE_POSITIVE_X);
    FilaRenderTargetBuilder_layer(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, 0);
    FilaRenderTargetBuilder_samples(builder, 1);
    FilaRenderTargetBuilder_multiview(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, 2u, 0u);

    FilaRenderTarget* renderTarget = FilaRenderTargetBuilder_build(builder, engine);
    FilaRenderTargetBuilder_destroy(builder);

    (void)FilaRenderTarget_getTexture(renderTarget, FILA_RENDER_TARGET_ATTACHMENT_COLOR0);
    (void)FilaRenderTarget_getMipLevel(renderTarget, FILA_RENDER_TARGET_ATTACHMENT_COLOR0);
    (void)FilaRenderTarget_getFace(renderTarget, FILA_RENDER_TARGET_ATTACHMENT_COLOR0);
    (void)FilaRenderTarget_getLayer(renderTarget, FILA_RENDER_TARGET_ATTACHMENT_COLOR0);
    (void)FilaRenderTarget_getSupportedColorAttachmentsCount(renderTarget);

    FilaEngine_destroyRenderTarget(engine, renderTarget);
    FilaEngine_destroyTexture(engine, colorTexture);
}

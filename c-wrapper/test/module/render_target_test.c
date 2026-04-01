// Compile-only test for RenderTarget C API
// Tests builder and query functionality

#include "filament/Engine.h"
#include "filament/RenderTarget.h"
#include "filament/Texture.h"
#include "filament/Types.h"

#include <stdio.h>

void test_render_target_builder() {
    FilaEngine* engine = FilaEngine_create();

    // Create a test texture for COLOR0 attachment
    FilaTextureBuilder* textureBuilder = FilaTextureBuilder_create();
    FilaTextureBuilder_width(textureBuilder, 512);
    FilaTextureBuilder_height(textureBuilder, 512);
    FilaTextureBuilder_format(textureBuilder, FILA_TEXTURE_FORMAT_RGBA8);
    FilaTexture* colorTexture = FilaTextureBuilder_build(textureBuilder, engine);
    FilaTextureBuilder_destroy(textureBuilder);

    // Create render target builder
    FilaRenderTargetBuilder* builder = FilaRenderTargetBuilder_create();
    FilaRenderTargetBuilder_texture(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, colorTexture);
    FilaRenderTargetBuilder_mipLevel(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, 0);
    FilaRenderTargetBuilder_face(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, FILA_CUBEMAP_FACE_POSITIVE_X);
    FilaRenderTargetBuilder_layer(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, 0);
    FilaRenderTargetBuilder_samples(builder, 1);
    FilaRenderTargetBuilder_multiview(builder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, 2u, 0u);

    // Build render target
    FilaRenderTarget* renderTarget = FilaRenderTargetBuilder_build(builder, engine);
    FilaRenderTargetBuilder_destroy(builder);

    // Test getters
    FilaTexture* queriedTexture = FilaRenderTarget_getTexture(renderTarget, FILA_RENDER_TARGET_ATTACHMENT_COLOR0);
    uint8_t mipLevel = FilaRenderTarget_getMipLevel(renderTarget, FILA_RENDER_TARGET_ATTACHMENT_COLOR0);
    FilaCubemapFace face = FilaRenderTarget_getFace(renderTarget, FILA_RENDER_TARGET_ATTACHMENT_COLOR0);
    uint32_t layer = FilaRenderTarget_getLayer(renderTarget, FILA_RENDER_TARGET_ATTACHMENT_COLOR0);
    uint8_t supportedColorCount = FilaRenderTarget_getSupportedColorAttachmentsCount(renderTarget);

    // Cleanup
    FilaEngine_destroyRenderTarget(engine, renderTarget);
    FilaEngine_destroyTexture(engine, colorTexture);
    FilaEngine_destroy(&engine);

    printf("RenderTarget test: mipLevel=%u, face=%u, layer=%u, supportedColors=%u\n", mipLevel, face, layer, supportedColorCount);
}

int main() {
    test_render_target_builder();
    return 0;
}

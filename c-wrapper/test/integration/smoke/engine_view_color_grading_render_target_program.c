#include <stdio.h>

#include "filament/ColorGrading.h"
#include "filament/Engine.h"
#include "filament/RenderTarget.h"
#include "filament/Texture.h"
#include "filament/View.h"

int main(void) {
    printf("Running engine+view+color_grading+render_target smoke program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaView* view = FilaEngine_createView(engine);
    if (!view) {
        printf("View creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTextureBuilder* textureBuilder = FilaTextureBuilder_create();
    if (!textureBuilder) {
        printf("Texture builder creation failed\n");
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTextureBuilder_width(textureBuilder, 4u);
    FilaTextureBuilder_height(textureBuilder, 4u);
    FilaTextureBuilder_levels(textureBuilder, 1u);
    FilaTextureBuilder_samples(textureBuilder, 1u);
    FilaTextureBuilder_usage(textureBuilder, (uint16_t)(FILA_TEXTURE_USAGE_DEFAULT | FILA_TEXTURE_USAGE_COLOR_ATTACHMENT));
    FilaTextureBuilder_sampler(textureBuilder, FILA_TEXTURE_SAMPLER_2D);
    FilaTextureBuilder_format(textureBuilder, FILA_TEXTURE_FORMAT_RGBA8);

    FilaTexture* texture = FilaTextureBuilder_build(textureBuilder, engine);
    FilaTextureBuilder_destroy(textureBuilder);

    if (!texture) {
        printf("Texture build failed\n");
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderTargetBuilder* rtBuilder = FilaRenderTargetBuilder_create();
    if (!rtBuilder) {
        printf("RenderTarget builder creation failed\n");
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderTargetBuilder_texture(rtBuilder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, texture);
    FilaRenderTargetBuilder_mipLevel(rtBuilder, FILA_RENDER_TARGET_ATTACHMENT_COLOR0, 0u);
    FilaRenderTargetBuilder_samples(rtBuilder, 1u);

    FilaRenderTarget* renderTarget = FilaRenderTargetBuilder_build(rtBuilder, engine);
    FilaRenderTargetBuilder_destroy(rtBuilder);

    if (!renderTarget) {
        printf("RenderTarget build failed\n");
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaColorGradingBuilder* cgBuilder = FilaColorGradingBuilder_create();
    if (!cgBuilder) {
        printf("ColorGrading builder creation failed\n");
        FilaEngine_destroyRenderTarget(engine, renderTarget);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaColorGradingBuilder_quality(cgBuilder, FILA_COLOR_GRADING_QUALITY_MEDIUM);
    FilaColorGradingBuilder_format(cgBuilder, FILA_COLOR_GRADING_LUT_FORMAT_INTEGER);
    FilaColorGradingBuilder_dimensions(cgBuilder, 16u);
    FilaColorGradingBuilder_contrast(cgBuilder, 1.05f);

    FilaColorGrading* colorGrading = FilaColorGradingBuilder_build(cgBuilder, engine);
    FilaColorGradingBuilder_destroy(cgBuilder);

    if (!colorGrading) {
        printf("ColorGrading build failed\n");
        FilaEngine_destroyRenderTarget(engine, renderTarget);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setRenderTarget(view, renderTarget);
    FilaView_setColorGrading(view, colorGrading);

    if (FilaView_getRenderTarget(view) != renderTarget) {
        printf("View render target binding mismatch\n");
        FilaView_setRenderTarget(view, (FilaRenderTarget*)0);
        FilaView_setColorGrading(view, (FilaColorGrading*)0);
        FilaEngine_destroyColorGrading(engine, colorGrading);
        FilaEngine_destroyRenderTarget(engine, renderTarget);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaView_getColorGrading(view) != colorGrading) {
        printf("View color grading binding mismatch\n");
        FilaView_setRenderTarget(view, (FilaRenderTarget*)0);
        FilaView_setColorGrading(view, (FilaColorGrading*)0);
        FilaEngine_destroyColorGrading(engine, colorGrading);
        FilaEngine_destroyRenderTarget(engine, renderTarget);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setRenderTarget(view, (FilaRenderTarget*)0);
    FilaView_setColorGrading(view, (FilaColorGrading*)0);

    FilaEngine_destroyColorGrading(engine, colorGrading);
    FilaEngine_destroyRenderTarget(engine, renderTarget);
    FilaEngine_destroyTexture(engine, texture);
    FilaEngine_destroyView(engine, view);
    FilaEngine_destroy(&engine);

    printf("Engine+view+color_grading+render_target smoke program completed\n");
    return 0;
}


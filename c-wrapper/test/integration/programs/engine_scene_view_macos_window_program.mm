#import <Cocoa/Cocoa.h>
#import <QuartzCore/CAMetalLayer.h>

#include <stdint.h>
#include <stdio.h>
#include <unistd.h>

#include "filament/Camera.h"
#include "filament/Engine.h"
#include "filament/IndirectLight.h"
#include "filament/Material.h"
#include "filament/MaterialInstance.h"
#include "filament/Renderer.h"
#include "filament/Scene.h"
#include "filament/Skybox.h"
#include "filament/Texture.h"
#include "filament/View.h"

static void pumpEvents(void) {
    NSEvent* event = nil;
    do {
        event = [NSApp nextEventMatchingMask:NSEventMaskAny
                                   untilDate:[NSDate dateWithTimeIntervalSinceNow:0.0]
                                      inMode:NSDefaultRunLoopMode
                                     dequeue:YES];
        if (event) {
            [NSApp sendEvent:event];
        }
    } while (event != nil);
    [NSApp updateWindows];
}

int main(void) {
    @autoreleasepool {
        [NSApplication sharedApplication];
        [NSApp setActivationPolicy:NSApplicationActivationPolicyRegular];

        const NSUInteger style = NSWindowStyleMaskTitled |
                NSWindowStyleMaskClosable |
                NSWindowStyleMaskMiniaturizable |
                NSWindowStyleMaskResizable;

        NSRect frame = NSMakeRect(120.0, 120.0, 960.0, 540.0);
        NSWindow* window = [[NSWindow alloc] initWithContentRect:frame
                                                       styleMask:style
                                                         backing:NSBackingStoreBuffered
                                                           defer:NO];
        [window setTitle:@"Filament C Wrapper - macOS Window Smoke"];
        [window makeKeyAndOrderFront:nil];
        [NSApp activateIgnoringOtherApps:YES];

        NSView* contentView = [window contentView];
        [contentView setWantsLayer:YES];
        CAMetalLayer* metalLayer = [CAMetalLayer layer];
        [contentView setLayer:metalLayer];

        FilaEngine* engine = FilaEngine_create();
        if (!engine) {
            printf("Engine creation failed\n");
            [window close];
            return 1;
        }

        FilaRenderer* renderer = FilaEngine_createRenderer(engine);
        if (!renderer) {
            printf("Renderer creation failed\n");
            FilaEngine_destroy(&engine);
            [window close];
            return 1;
        }

        // Prefer CAMetalLayer; fall back to NSView if needed by backend/platform integration.
        FilaSwapChain* swapChain = FilaEngine_createSwapChain(engine, (__bridge void*)metalLayer, 0u);
        if (!swapChain) {
            swapChain = FilaEngine_createSwapChain(engine, (__bridge void*)contentView, 0u);
        }
        if (!swapChain) {
            printf("SwapChain creation failed\n");
            FilaEngine_destroyRenderer(engine, renderer);
            FilaEngine_destroy(&engine);
            [window close];
            return 1;
        }

        FilaScene* scene = FilaEngine_createScene(engine);
        FilaView* view = FilaEngine_createView(engine);
        FilaEntity cameraEntity = 201;
        FilaCamera* camera = FilaEngine_createCamera(engine, cameraEntity);
        FilaTexture* envTexture = NULL;
        FilaSkybox* skybox = NULL;
        FilaIndirectLight* indirectLight = NULL;

        if (!scene || !view || !camera) {
            printf("Scene/View/Camera creation failed\n");
            FilaEngine_destroyCameraComponent(engine, cameraEntity);
            FilaEngine_destroyView(engine, view);
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroySwapChain(engine, swapChain);
            FilaEngine_destroyRenderer(engine, renderer);
            FilaEngine_destroy(&engine);
            [window close];
            return 1;
        }

        FilaView_setScene(view, scene);
        FilaView_setCamera(view, camera);
        FilaCamera_setProjectionFov(camera, 45.0, 960.0 / 540.0, 0.1, 1000.0, FILA_CAMERA_FOV_VERTICAL);
        FilaCamera_lookAt(camera, 0.0, 1.5, 4.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

        // Create a simple cubemap texture for environment lighting (4x4x6 minimal cubemap).
        FilaTextureBuilder* texBuilder = FilaTextureBuilder_create();
        FilaTextureBuilder_width(texBuilder, 4u);
        FilaTextureBuilder_height(texBuilder, 4u);
        FilaTextureBuilder_levels(texBuilder, 1u);
        FilaTextureBuilder_sampler(texBuilder, FILA_TEXTURE_SAMPLER_CUBEMAP);
        FilaTextureBuilder_format(texBuilder, FILA_TEXTURE_FORMAT_RGBA8);
        envTexture = FilaTextureBuilder_build(texBuilder, engine);
        FilaTextureBuilder_destroy(texBuilder);

        // Attach skybox with the environment texture.
        if (envTexture) {
            FilaSkyboxBuilder* skyBuilder = FilaSkyboxBuilder_create();
            FilaSkyboxBuilder_environment(skyBuilder, envTexture);
            FilaSkyboxBuilder_intensity(skyBuilder, 25000.0f);
            skybox = FilaSkyboxBuilder_build(skyBuilder, engine);
            FilaSkyboxBuilder_destroy(skyBuilder);

            if (skybox) {
                FilaScene_setSkybox(scene, skybox);
            }
        }

        // Create indirect light for environment illumination.
        FilaIndirectLightBuilder* ilBuilder = FilaIndirectLightBuilder_create();
        if (envTexture) {
            FilaIndirectLightBuilder_reflections(ilBuilder, envTexture);
        }
        FilaIndirectLightBuilder_intensity(ilBuilder, 20000.0f);
        indirectLight = FilaIndirectLightBuilder_build(ilBuilder, engine);
        FilaIndirectLightBuilder_destroy(ilBuilder);

        if (indirectLight) {
            FilaScene_setIndirectLight(scene, indirectLight);
        }

        bool renderedAtLeastOnce = false;
        for (int frameIndex = 0; frameIndex < 300; ++frameIndex) {
            pumpEvents();
            if (![window isVisible]) {
                break;
            }

            const NSRect bounds = [contentView bounds];
            const uint32_t width = (uint32_t)((bounds.size.width > 1.0) ? bounds.size.width : 1.0);
            const uint32_t height = (uint32_t)((bounds.size.height > 1.0) ? bounds.size.height : 1.0);
            FilaViewport viewport = {0, 0, width, height};
            FilaView_setViewport(view, viewport);

            if (FilaRenderer_beginFrame(renderer, swapChain, 0u)) {
                FilaRenderer_render(renderer, view);
                FilaRenderer_endFrame(renderer);
                renderedAtLeastOnce = true;
            }

            usleep(16000);
        }

        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);

        // Clean up environment resources.
        if (skybox) {
            FilaEngine_destroySkybox(engine, skybox);
        }
        if (indirectLight) {
            FilaEngine_destroyIndirectLight(engine, indirectLight);
        }
        if (envTexture) {
            FilaEngine_destroyTexture(engine, envTexture);
        }

        FilaEngine_destroySwapChain(engine, swapChain);
        FilaEngine_destroyRenderer(engine, renderer);
        FilaEngine_destroy(&engine);

        [window close];

        if (!renderedAtLeastOnce) {
            printf("No frame rendered\n");
            return 1;
        }

        printf("macOS window smoke program completed\n");
        return 0;
    }
}


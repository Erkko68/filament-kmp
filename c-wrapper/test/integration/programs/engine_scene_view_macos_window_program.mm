#import <Cocoa/Cocoa.h>
#import <QuartzCore/CAMetalLayer.h>

#include <stdint.h>
#include <stdio.h>
#include <unistd.h>

#include "filament/Camera.h"
#include "filament/Engine.h"
#include "filament/Renderer.h"
#include "filament/Scene.h"
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


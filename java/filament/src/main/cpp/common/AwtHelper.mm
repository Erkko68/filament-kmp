#include "AwtHelper.h"
#include <jawt.h>
#include <jawt_md.h>

#include <iostream>

#ifdef __APPLE__
#import <QuartzCore/CAMetalLayer.h>
#import <AppKit/NSView.h>
#import <Foundation/Foundation.h>

// Protocol defined in jawt_md.h
@protocol JAWT_SurfaceLayers
@property (readwrite, retain) CALayer *layer;
@property (readonly) CALayer *windowLayer;
@end
#endif

void* AwtHelper::getNativeWindow(JNIEnv* env, jobject component) {
    if (component == nullptr) {
        return nullptr;
    }

    JAWT awt = {0};
    
    // Attempt 1: Modern JAWT_VERSION_9 (Standard for JDK 9+)
    awt.version = 0x00090000;
    if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
        // Attempt 2: Legacy JAWT_VERSION_1_4 with explicit CALAYER flag
        awt.version = 0x00010004 | 0x80000000;
        if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
            std::cerr << "AwtHelper: Failed to get AWT (all version paths failed)" << std::endl;
            return nullptr;
        }
    }

    JAWT_DrawingSurface* ds = awt.GetDrawingSurface(env, component);
    if (ds == nullptr) {
        std::cerr << "AwtHelper: Failed to get drawing surface" << std::endl;
        return nullptr;
    }

    jint lock = ds->Lock(ds);
    if ((lock & JAWT_LOCK_ERROR) != 0) {
        std::cerr << "AwtHelper: Failed to lock surface" << std::endl;
        awt.FreeDrawingSurface(ds);
        return nullptr;
    }

    JAWT_DrawingSurfaceInfo* dsi = ds->GetDrawingSurfaceInfo(ds);
    if (dsi == nullptr) {
        std::cerr << "AwtHelper: Failed to get SurfaceInfo" << std::endl;
        ds->Unlock(ds);
        awt.FreeDrawingSurface(ds);
        return nullptr;
    }

    void* result = nullptr;

#if defined(__APPLE__)
    id<JAWT_SurfaceLayers> surfaceLayers = (id<JAWT_SurfaceLayers>) dsi->platformInfo;
    if (surfaceLayers != nil) {
        if (surfaceLayers.layer != nil) {
            result = (__bridge void*) surfaceLayers.layer;
        } else if (surfaceLayers.windowLayer != nil) {
            result = (__bridge void*) surfaceLayers.windowLayer;
        }
    }
#endif

    ds->FreeDrawingSurfaceInfo(dsi);
    ds->Unlock(ds);
    awt.FreeDrawingSurface(ds);

    return result;
}

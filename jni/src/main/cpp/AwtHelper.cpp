#include <jni.h>

#ifndef __ANDROID__
#include "FilaJawt.h"

#ifdef __APPLE__
#import <AppKit/AppKit.h>
#import <QuartzCore/QuartzCore.h>

// A CAMetalLayer keeps the drawable size it first drew at: follow the layer's size as AWT resizes it.
@interface FilaAwtMetalLayer : CAMetalLayer
@end

@implementation FilaAwtMetalLayer
- (void)setBounds:(CGRect)bounds {
    [super setBounds:bounds];
    // ponytail: scale refreshed on resize only; observe the window's screen if dragging across DPIs matters.
    if (self.superlayer) self.contentsScale = self.superlayer.contentsScale;
    self.drawableSize = CGSizeMake(bounds.size.width * self.contentsScale, bounds.size.height * self.contentsScale);
}
@end

namespace {

// AWT only hands out a slot for a CALayer: put a CAMetalLayer in it, which the slot then owns. AWT keeps
// the layer on the canvas from its next resize on; until then it sits at ([x], [y]) from the top left
// of the window's content.
jlong attachMetalLayer(id<JAWT_SurfaceLayers> slot, jint x, jint y, JAWT_Rectangle bounds) {
    if ([slot.layer isKindOfClass:CAMetalLayer.class]) return (jlong) (void*) slot.layer;
    CAMetalLayer* layer = [[FilaAwtMetalLayer alloc] init];
    // Creating it opened an implicit transaction on this thread, which AWT never commits (no run loop):
    // left open, the layer's frames never reach the screen.
    [CATransaction flush];
    void (^attach)() = ^{
        [CATransaction begin];
        [CATransaction setDisableActions:YES];
        slot.layer = layer;
        layer.frame = CGRectMake(x, slot.windowLayer.bounds.size.height - y - bounds.height,
                                 bounds.width, bounds.height);
        [CATransaction commit];
        [layer release];
    };
    // AppKit's thread owns the layer tree. Not waited for: it may itself be waiting on the calling thread.
    if (NSThread.isMainThread) attach(); else dispatch_async(dispatch_get_main_queue(), attach);
    return (jlong) (void*) layer;
}

} // namespace
#endif

// The native window behind a displayable AWT Canvas, as Filament's createSwapChain takes it: an HWND, an
// X11 Window, or on macOS a CAMetalLayer attached to the canvas at ([x], [y]) in its window's content.
// 0 on failure.
extern "C" JNIEXPORT jlong JNICALL Java_io_github_erkko68_filament_jni_AwtHelper_nNativeWindow(
        JNIEnv* env, jclass, jobject canvas, jint x, jint y) {
    JAWT* jawt = filaJawt(env);
    if (!jawt) return 0;
    JAWT_DrawingSurface* ds = jawt->GetDrawingSurface(env, canvas);
    if (!ds) return 0;
    jlong window = 0;
    if (!(ds->Lock(ds) & JAWT_LOCK_ERROR)) {
        if (JAWT_DrawingSurfaceInfo* dsi = ds->GetDrawingSurfaceInfo(ds)) {
#if defined(_WIN32)
            window = (jlong) ((JAWT_Win32DrawingSurfaceInfo*) dsi->platformInfo)->hwnd;
#elif defined(__APPLE__)
            window = attachMetalLayer((id<JAWT_SurfaceLayers>) dsi->platformInfo, x, y, dsi->bounds);
#else
            window = (jlong) ((JAWT_X11DrawingSurfaceInfo*) dsi->platformInfo)->drawable;
#endif
            ds->FreeDrawingSurfaceInfo(dsi);
        }
        ds->Unlock(ds);
    }
    jawt->FreeDrawingSurface(ds);
    return window;
}
#endif

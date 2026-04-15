#include "AwtHelper.h"
#include <jawt.h>

#if defined(__APPLE__)
#include <jawt_md.h>
// Use a neutral definition for the Mac-specific drawing surface info to avoid needing Objective-C++
typedef struct JAWT_MacOSXDrawingSurfaceInfo_ {
    void* cocoaViewRef;
} JAWT_MacOSXDrawingSurfaceInfo_o;
#elif defined(_WIN32)
#include <jawt_md.h>
#else
#include <jawt_md.h>
#endif

void* AwtHelper::getNativeWindow(JNIEnv* env, jobject component) {
    if (component == nullptr) return nullptr;

    jclass componentClass = env->FindClass("java/awt/Component");
    if (!env->IsInstanceOf(component, componentClass)) {
        return nullptr;
    }

    JAWT awt;
    awt.version = JAWT_VERSION_1_4;
    if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
        return nullptr;
    }

    JAWT_DrawingSurface* ds = awt.GetDrawingSurface(env, component);
    if (ds == nullptr) {
        return nullptr;
    }

    jint lock = ds->Lock(ds);
    if ((lock & JAWT_LOCK_ERROR) != 0) {
        awt.FreeDrawingSurface(ds);
        return nullptr;
    }

    JAWT_DrawingSurfaceInfo* dsi = ds->GetDrawingSurfaceInfo(ds);
    if (dsi == nullptr) {
        ds->Unlock(ds);
        awt.FreeDrawingSurface(ds);
        return nullptr;
    }

    void* result = nullptr;

#if defined(__APPLE__)
    // On macOS, the platform-specific info is a pointer to a struct containing the NSView*
    auto* macInfo = (JAWT_MacOSXDrawingSurfaceInfo_o*) dsi->platformInfo;
    if (macInfo != nullptr) {
        result = macInfo->cocoaViewRef;
    }
#elif defined(_WIN32)
    auto* winInfo = (JAWT_Win32DrawingSurfaceInfo*) dsi->platformInfo;
    if (winInfo != nullptr) {
        result = winInfo->hwnd;
    }
#else
    auto* x11Info = (JAWT_X11DrawingSurfaceInfo*) dsi->platformInfo;
    if (x11Info != nullptr) {
        result = (void*) x11Info->drawable;
    }
#endif

    ds->FreeDrawingSurfaceInfo(dsi);
    ds->Unlock(ds);
    awt.FreeDrawingSurface(ds);

    return result;
}

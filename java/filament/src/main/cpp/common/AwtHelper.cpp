#include "AwtHelper.h"
#include <jawt.h>

#if defined(_WIN32)
#include <jawt_md.h>
#else
#include <jawt_md.h>
#endif

#include <iostream>

void *AwtHelper::getNativeWindow(JNIEnv *env, jobject component) {
  if (component == nullptr)
    return nullptr;

  JAWT awt;
  awt.version = 0x00010004; // JAWT_VERSION_1_4
  if (JAWT_GetAWT(env, &awt) == JNI_FALSE) {
    std::cerr << "AwtHelper: Failed to get AWT" << std::endl;
    return nullptr;
  }

  JAWT_DrawingSurface *ds = awt.GetDrawingSurface(env, component);
  if (ds == nullptr) {
    std::cerr << "AwtHelper: Failed to get drawing surface from component "
              << component << std::endl;
    return nullptr;
  }

  jint lock = ds->Lock(ds);
  if ((lock & JAWT_LOCK_ERROR) != 0) {
    std::cerr << "AwtHelper: Failed to lock drawing surface" << std::endl;
    awt.FreeDrawingSurface(ds);
    return nullptr;
  }

  JAWT_DrawingSurfaceInfo *dsi = ds->GetDrawingSurfaceInfo(ds);
  if (dsi == nullptr) {
    std::cerr << "AwtHelper: Failed to get drawing surface info" << std::endl;
    ds->Unlock(ds);
    awt.FreeDrawingSurface(ds);
    return nullptr;
  }

  void *result = nullptr;

#if defined(_WIN32)
  auto *winInfo = (JAWT_Win32DrawingSurfaceInfo *)dsi->platformInfo;
  if (winInfo != nullptr) {
    result = winInfo->hwnd;
  }
#elif defined(__linux__)
  auto *x11Info = (JAWT_X11DrawingSurfaceInfo *)dsi->platformInfo;
  if (x11Info != nullptr) {
    result = (void *)x11Info->drawable;
  }
#elif defined(__APPLE__)
  result = dsi->platformInfo;
#endif

  ds->FreeDrawingSurfaceInfo(dsi);
  ds->Unlock(ds);
  awt.FreeDrawingSurface(ds);

  if (result == nullptr) {
    std::cerr << "AwtHelper: Failed to extract native window handle (Platform "
                 "not supported or surface not ready)"
              << std::endl;
  }

  return result;
}

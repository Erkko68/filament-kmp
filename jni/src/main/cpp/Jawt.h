#pragma once

#include <jni.h>
#include <jawt_md.h>
#ifdef _WIN32
#include <windows.h>
#else
#include <dlfcn.h>
#endif

// The JVM has already loaded libjawt (the Kotlin helpers load it), so look JAWT_GetAWT up rather than
// link it: a load-time dependency wouldn't resolve, the JDK's lib dir isn't on the loader's path.
inline JAWT* filaJawt(JNIEnv* env) {
    using GetAwt = jboolean (JNICALL*)(JNIEnv*, JAWT*);
    static JAWT instance{};
    static bool ready = false;
    if (!ready) {
#if defined(_WIN32)
        HMODULE lib = GetModuleHandleA("jawt.dll");
        auto getAwt = lib ? (GetAwt) GetProcAddress(lib, "JAWT_GetAWT") : nullptr;
        instance.version = JAWT_VERSION_1_4;
#elif defined(__APPLE__)
        auto getAwt = (GetAwt) dlsym(RTLD_DEFAULT, "JAWT_GetAWT");
        instance.version = JAWT_VERSION_1_4 | JAWT_MACOSX_USE_CALAYER;
#else
        void* lib = dlopen("libjawt.so", RTLD_LAZY | RTLD_NOLOAD);
        auto getAwt = lib ? (GetAwt) dlsym(lib, "JAWT_GetAWT") : nullptr;
        instance.version = JAWT_VERSION_1_4;
#endif
        if (!getAwt) return nullptr;
        ready = getAwt(env, &instance) == JNI_TRUE;
    }
    return ready ? &instance : nullptr;
}

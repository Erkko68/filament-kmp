// Hand-written half of the JNI layer: what the generated forwarders can't express.
// Backs io.github.erkko68.filament.jni.FilaJni.

#include <jni.h>
#include <android/native_window_jni.h>

#include <cstdint>
#include <cstdlib>

// Private upstream header (backend/include/private/backend/VirtualMachineEnv.h); only this entry is needed.
namespace filament {
class VirtualMachineEnv {
public:
    static jint JNI_OnLoad(JavaVM* vm);
};
} // namespace filament

static JavaVM* sVm = nullptr;
static jmethodID sInvoke = nullptr; // FilaCallback.invoke(long, long)

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) return -1;
    sVm = vm;
    // Filament's Android backend (streams, EGL helpers) needs the VM, as in upstream filament-jni.
    filament::VirtualMachineEnv::JNI_OnLoad(vm);
    jclass callback = env->FindClass("io/github/erkko68/filament/jni/FilaCallback");
    sInvoke = env->GetMethodID(callback, "invoke", "(JJ)V");
    env->DeleteLocalRef(callback);
    return JNI_VERSION_1_6;
}

// Callbacks fire on Filament's driver thread; attach it once, as a daemon so it never blocks VM exit.
static JNIEnv* attachedEnv() {
    JNIEnv* env;
    if (sVm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        sVm->AttachCurrentThreadAsDaemon(&env, nullptr);
    }
    return env;
}

// The userData behind every trampoline (FilaJni.newCallback).
struct Callback {
    jobject target; // global ref to a FilaCallback
    bool once;
};

static void release(JNIEnv* env, Callback* callback) {
    env->DeleteGlobalRef(callback->target);
    delete callback;
}

static void dispatch(void* userData, jlong a, jlong b) {
    JNIEnv* env = attachedEnv();
    auto callback = static_cast<Callback*>(userData);
    env->CallVoidMethod(callback->target, sInvoke, a, b);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
    if (callback->once) release(env, callback);
}

// One trampoline per C callback shape, matching web's Callbacks (userOnly, argUser, keepBuffer, freeBuffer).
static void userOnly(void* userData) { dispatch(userData, 0, 0); }
static void argUser(void* arg, void* userData) { dispatch(userData, reinterpret_cast<jlong>(arg), 0); }
static void keepBuffer(void* buffer, size_t size, void* userData) {
    dispatch(userData, reinterpret_cast<jlong>(buffer), static_cast<jlong>(size));
}
// For uploads copied into native memory: frees the copy once Filament has consumed it.
static void freeBuffer(void* buffer, size_t size, void* userData) {
    std::free(buffer);
    if (userData) dispatch(userData, reinterpret_cast<jlong>(buffer), static_cast<jlong>(size));
}

#define FILA_JNI(ret, name) extern "C" JNIEXPORT ret JNICALL Java_io_github_erkko68_filament_jni_FilaJni_##name

FILA_JNI(jlong, alloc)(JNIEnv*, jclass, jlong size) {
    return reinterpret_cast<jlong>(std::calloc(1, static_cast<size_t>(size)));
}

FILA_JNI(void, free)(JNIEnv*, jclass, jlong ptr) {
    std::free(reinterpret_cast<void*>(ptr));
}

FILA_JNI(jlong, address)(JNIEnv* env, jclass, jobject buffer) {
    return reinterpret_cast<jlong>(env->GetDirectBufferAddress(buffer));
}

FILA_JNI(jobject, view)(JNIEnv* env, jclass, jlong ptr, jlong size) {
    return env->NewDirectByteBuffer(reinterpret_cast<void*>(ptr), size);
}

FILA_JNI(jlong, windowFromSurface)(JNIEnv* env, jclass, jobject surface) {
    return reinterpret_cast<jlong>(ANativeWindow_fromSurface(env, surface));
}

FILA_JNI(void, releaseWindow)(JNIEnv*, jclass, jlong window) {
    ANativeWindow_release(reinterpret_cast<ANativeWindow*>(window));
}

FILA_JNI(jlong, newCallback)(JNIEnv* env, jclass, jobject target, jboolean once) {
    return reinterpret_cast<jlong>(new Callback { env->NewGlobalRef(target), once == JNI_TRUE });
}

FILA_JNI(void, releaseCallback)(JNIEnv* env, jclass, jlong userData) {
    release(env, reinterpret_cast<Callback*>(userData));
}

FILA_JNI(jlong, userOnly)(JNIEnv*, jclass) { return reinterpret_cast<jlong>(&userOnly); }
FILA_JNI(jlong, argUser)(JNIEnv*, jclass) { return reinterpret_cast<jlong>(&argUser); }
FILA_JNI(jlong, keepBuffer)(JNIEnv*, jclass) { return reinterpret_cast<jlong>(&keepBuffer); }
FILA_JNI(jlong, freeBuffer)(JNIEnv*, jclass) { return reinterpret_cast<jlong>(&freeBuffer); }

// Peek/poke for the pointer-based views (F32Array, IntVar, ...), matching :web's fila.getF32 & co.
#define FILA_PEEK(name, jtype, ctype) \
    FILA_JNI(jtype, get##name)(JNIEnv*, jclass, jlong ptr) { return static_cast<jtype>(*reinterpret_cast<const ctype*>(ptr)); } \
    FILA_JNI(void, set##name)(JNIEnv*, jclass, jlong ptr, jtype value) { *reinterpret_cast<ctype*>(ptr) = static_cast<ctype>(value); }
FILA_PEEK(F32, jfloat, float)
FILA_PEEK(F64, jdouble, double)
FILA_PEEK(I32, jint, int32_t)
FILA_PEEK(I64, jlong, int64_t)
FILA_PEEK(U8, jint, uint8_t)

FILA_JNI(jstring, readString)(JNIEnv* env, jclass, jlong ptr) {
    return ptr ? env->NewStringUTF(reinterpret_cast<const char*>(ptr)) : nullptr;
}

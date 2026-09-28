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
static jmethodID sInvoke = nullptr; // FilaCallback.invoke(long)

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) return -1;
    sVm = vm;
    // Filament's Android backend (streams, EGL helpers) needs the VM, as in upstream filament-jni.
    filament::VirtualMachineEnv::JNI_OnLoad(vm);
    jclass callback = env->FindClass("io/github/erkko68/filament/jni/FilaCallback");
    sInvoke = env->GetMethodID(callback, "invoke", "(J)V");
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

static void dispatch(void* arg, void* userData) {
    JNIEnv* env = attachedEnv();
    auto callback = static_cast<Callback*>(userData);
    env->CallVoidMethod(callback->target, sInvoke, reinterpret_cast<jlong>(arg));
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
    if (callback->once) release(env, callback);
}

static void bufferTrampoline(void* buffer, size_t, void* userData) { dispatch(buffer, userData); }
static void userDataTrampoline(void* userData) { dispatch(nullptr, userData); }
static void pointerTrampoline(void* arg, void* userData) { dispatch(arg, userData); }

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

FILA_JNI(jlong, bufferCallback)(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&bufferTrampoline);
}

FILA_JNI(jlong, userDataCallback)(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&userDataTrampoline);
}

FILA_JNI(jlong, pointerCallback)(JNIEnv*, jclass) {
    return reinterpret_cast<jlong>(&pointerTrampoline);
}

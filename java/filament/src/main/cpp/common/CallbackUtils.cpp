#include "common/CallbackUtils.h"
#include "common/VirtualMachineEnv.h"

void acquireCallbackJni(JNIEnv* env, CallbackJni& callbackUtils) {
    callbackUtils.executorClass = env->FindClass("java/util/concurrent/Executor");
    callbackUtils.executorClass = (jclass) env->NewGlobalRef(callbackUtils.executorClass);
    callbackUtils.execute = env->GetMethodID(callbackUtils.executorClass,
                                              "execute", "(Ljava/lang/Runnable;)V");
}

void releaseCallbackJni(JNIEnv* env, CallbackJni callbackUtils, jobject handler, jobject callback) {
    if (handler && callback) {
        if (env->IsInstanceOf(handler, callbackUtils.executorClass)) {
            env->CallVoidMethod(handler, callbackUtils.execute, callback);
        }
    }
    env->DeleteGlobalRef(handler);
    env->DeleteGlobalRef(callback);
    env->DeleteGlobalRef(callbackUtils.executorClass);
}

JniCallback* JniCallback::make(JNIEnv* env, jobject handler, jobject callback) {
    return new JniCallback(env, handler, callback);
}

JniCallback::JniCallback(JNIEnv* env, jobject handler, jobject callback)
        : mHandler(env->NewGlobalRef(handler)),
          mCallback(env->NewGlobalRef(callback)) {
    acquireCallbackJni(env, mCallbackUtils);
}

JniCallback::~JniCallback() = default;

void JniCallback::post(void* user, filament::backend::CallbackHandler::Callback callback) {
    callback(user);
}

void JniCallback::postToJavaAndDestroy(JniCallback* callback) {
    JNIEnv* env = filament::VirtualMachineEnv::get().getEnvironment();
    releaseCallbackJni(env, callback->mCallbackUtils, callback->mHandler, callback->mCallback);
    delete callback;
}

JniBufferCallback* JniBufferCallback::make(filament::Engine*,
        JNIEnv* env, jobject handler, jobject callback, AutoBuffer&& buffer) {
    return new JniBufferCallback(env, handler, callback, std::move(buffer));
}

JniBufferCallback::JniBufferCallback(JNIEnv* env, jobject handler, jobject callback,
        AutoBuffer&& buffer)
        : JniCallback(env, handler, callback),
        mBuffer(std::move(buffer)) {
}

JniBufferCallback::~JniBufferCallback() = default;

void JniBufferCallback::postToJavaAndDestroy(void*, size_t, void* user) {
    JniBufferCallback* callback = (JniBufferCallback*)user;
    JNIEnv* env = filament::VirtualMachineEnv::get().getEnvironment();
    callback->mBuffer.attachToJniThread(env);
    releaseCallbackJni(env, callback->mCallbackUtils, callback->mHandler, callback->mCallback);
    delete callback;
}

JniImageCallback* JniImageCallback::make(filament::Engine*,
        JNIEnv* env, jobject handler, jobject callback, long) {
    return new JniImageCallback(env, handler, callback);
}

void JniImageCallback::postToJavaAndDestroy(void*, void* user) {
    JniImageCallback* callback = (JniImageCallback*)user;
    JniCallback::postToJavaAndDestroy(callback);
}

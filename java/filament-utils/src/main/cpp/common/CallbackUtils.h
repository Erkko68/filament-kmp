#pragma once

#include <jni.h>
#include "common/NioUtils.h"
#include <backend/CallbackHandler.h>
#include <filament/Engine.h>

struct CallbackJni {
    jclass executorClass = nullptr;
    jmethodID execute = nullptr;
};

void acquireCallbackJni(JNIEnv* env, CallbackJni& callbackUtils);
void releaseCallbackJni(JNIEnv* env, CallbackJni callbackUtils, jobject handler, jobject callback);

struct JniCallback : private filament::backend::CallbackHandler {
    JniCallback(JniCallback const &) = delete;
    static JniCallback* make(JNIEnv* env, jobject handler, jobject runnable);
    static void postToJavaAndDestroy(JniCallback* callback);
    void post(void* user, Callback callback) override;
    filament::backend::CallbackHandler* getHandler() noexcept { return this; }
    jobject getCallbackObject() { return mCallback; }

protected:
    JniCallback(JNIEnv* env, jobject handler, jobject runnable);
    virtual ~JniCallback();
    jobject mHandler{};
    jobject mCallback{};
    CallbackJni mCallbackUtils{};
};

struct JniBufferCallback : public JniCallback {
    static JniBufferCallback* make(filament::Engine* engine,
            JNIEnv* env, jobject handler, jobject callback, AutoBuffer&& buffer);
    static void postToJavaAndDestroy(void*, size_t, void* user);
private:
    JniBufferCallback(JNIEnv* env, jobject handler, jobject callback, AutoBuffer&& buffer);
    virtual ~JniBufferCallback();
    AutoBuffer mBuffer;
};

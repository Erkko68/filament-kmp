#include <jni.h>
#include <filament/Renderer.h>
#include <filament/SwapChain.h>
#include <filament/View.h>
#include <filament/Viewport.h>
#include <backend/PixelBufferDescriptor.h>
#include <math/vec4.h>

#include "common/CallbackUtils.h"
#include "common/NioUtils.h"

using namespace filament;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Renderer_nBeginFrame(JNIEnv* env, jclass clazz, jlong nativeRenderer, jlong nativeSwapChain, jlong frameTimeNanos) {
    return (jboolean) ((Renderer*) nativeRenderer)->beginFrame((SwapChain*) nativeSwapChain, (uint64_t) frameTimeNanos);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nSetDisplayInfo(JNIEnv* env, jclass clazz, jlong nativeRenderer, jfloat refreshRate) {
    ((Renderer*) nativeRenderer)->setDisplayInfo({ .refreshRate = refreshRate });
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nSetFrameRateOptions(JNIEnv* env, jclass clazz, jlong nativeRenderer, jfloat interval, jfloat headRoom, jfloat scaleRate, jint history) {
    Renderer::FrameRateOptions options;
    options.interval = interval;
    options.headRoomRatio = headRoom;
    options.scaleRate = scaleRate;
    options.history = (uint32_t) history;
    ((Renderer*) nativeRenderer)->setFrameRateOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nSetClearOptions(JNIEnv* env, jclass clazz, jlong nativeRenderer, jfloat r, jfloat g, jfloat b, jfloat a, jboolean clear, jboolean discard) {
    Renderer::ClearOptions options;
    options.clearColor = { r, g, b, a };
    options.clear = clear;
    options.discard = discard;
    ((Renderer*) nativeRenderer)->setClearOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nRender(JNIEnv* env, jclass clazz, jlong nativeRenderer, jlong nativeView) {
    ((Renderer*) nativeRenderer)->render((View*) nativeView);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nRenderStandaloneView(JNIEnv* env, jclass clazz, jlong nativeRenderer, jlong nativeView) {
    ((Renderer*) nativeRenderer)->renderStandaloneView((View*) nativeView);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nEndFrame(JNIEnv* env, jclass clazz, jlong nativeRenderer) {
    ((Renderer*) nativeRenderer)->endFrame();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nSkipFrame(JNIEnv*, jclass, jlong nativeRenderer, jlong frameTimeNanos) {
    ((Renderer*) nativeRenderer)->skipFrame((uint64_t) frameTimeNanos);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Renderer_nShouldRenderFrame(JNIEnv*, jclass, jlong nativeRenderer) {
    return (jboolean) ((Renderer*) nativeRenderer)->shouldRenderFrame();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nCopyFrame(JNIEnv*, jclass, jlong nativeRenderer, jlong nativeSwapChain,
        jint dstL, jint dstB, jint dstW, jint dstH, jint srcL, jint srcB, jint srcW, jint srcH, jint flags) {
    Renderer* renderer = (Renderer*) nativeRenderer;
    SwapChain* swapChain = (SwapChain*) nativeSwapChain;
    renderer->copyFrame(swapChain, {dstL, dstB, (uint32_t)dstW, (uint32_t)dstH}, {srcL, srcB, (uint32_t)srcW, (uint32_t)srcH}, (uint32_t)flags);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Renderer_nReadPixels(JNIEnv* env, jclass, jlong nativeRenderer, jlong nativeEngine,
        int x, int y, int w, int h, jobject storage, int remaining, int left, int top, int type, int alignment, int stride, int format, jobject handler, jobject callbackObject) {
    
    Renderer* renderer = (Renderer*) nativeRenderer;
    Engine* engine = (Engine*) nativeEngine;
    
    stride = stride ? stride : w;
    size_t sizeInBytes = backend::PixelBufferDescriptor::computeDataSize(
            (backend::PixelDataFormat) format, (backend::PixelDataType) type,
            (size_t) stride, (size_t) (h + top), (size_t) alignment);

    AutoBuffer nioBuffer(env, storage, 0);
    if (sizeInBytes > (remaining << nioBuffer.getShift())) {
        return -1;
    }

    void* buffer = nioBuffer.getData();
    auto* callback = JniBufferCallback::make(engine, env, handler, callbackObject, std::move(nioBuffer));

    backend::PixelBufferDescriptor desc(buffer, sizeInBytes, (backend::PixelDataFormat) format,
            (backend::PixelDataType) type, (uint8_t) alignment, (uint32_t) left, (uint32_t) top,
            (uint32_t) stride,
            callback->getHandler(), &JniBufferCallback::postToJavaAndDestroy, callback);

    renderer->readPixels((uint32_t)x, (uint32_t)y, (uint32_t)w, (uint32_t)h, std::move(desc));
    return 0;
}

JNIEXPORT jdouble JNICALL
Java_io_github_erkko68_filament_Renderer_nGetUserTime(JNIEnv*, jclass, jlong nativeRenderer) {
    return ((Renderer*) nativeRenderer)->getUserTime();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nResetUserTime(JNIEnv*, jclass, jlong nativeRenderer) {
    ((Renderer*) nativeRenderer)->resetUserTime();
}

} // extern "C"

#include <jni.h>
#import <Metal/Metal.h>
#import <Foundation/Foundation.h>

static id<MTLDevice> gDevice = nil;
static id<MTLCommandQueue> gQueue = nil;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nCreateMetalTexture(JNIEnv* env, jclass, jint width, jint height) {
    if (!gDevice) gDevice = MTLCreateSystemDefaultDevice();
    if (!gDevice) return 0;

    MTLTextureDescriptor* descriptor = [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatRGBA8Unorm
                                                                                           width:width
                                                                                          height:height
                                                                                       mipmapped:NO];

    descriptor.usage = MTLTextureUsageShaderRead | MTLTextureUsageRenderTarget;
    descriptor.storageMode = MTLStorageModeShared; 

    id<MTLTexture> texture = [gDevice newTextureWithDescriptor:descriptor];
    if (!texture) return 0;

    // We'll trust Filament/Skia for now, removing native clear to avoid noise
    return (jlong)CFBridgingRetain(texture);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMetalDevice(JNIEnv* env, jclass, jlong nativeEngine) {
    if (!gDevice) gDevice = MTLCreateSystemDefaultDevice();
    return (jlong)(__bridge void*)gDevice;
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMetalQueue(JNIEnv* env, jclass, jlong nativeEngine) {
    if (!gDevice) gDevice = MTLCreateSystemDefaultDevice();
    if (!gQueue) gQueue = [gDevice newCommandQueue];
    return (jlong)(__bridge void*)gQueue;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nReleaseMetalTexture(JNIEnv* env, jclass, jlong handle) {
    if (handle != 0) {
        CFRelease((CFTypeRef)handle);
    }
}

} // extern "C"


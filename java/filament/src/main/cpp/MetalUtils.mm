#include <jni.h>
#import <Metal/Metal.h>
#import <Foundation/Foundation.h>

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nCreateMetalTexture(JNIEnv* env, jclass, jint width, jint height) {
    id<MTLDevice> device = MTLCreateSystemDefaultDevice();
    if (!device) return 0;

    MTLTextureDescriptor* descriptor = [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatRGBA8Unorm
                                                                                          width:width
                                                                                         height:height
                                                                                      mipmapped:NO];
    descriptor.usage = MTLTextureUsageShaderRead | MTLTextureUsageRenderTarget;
    descriptor.storageMode = MTLStorageModeShared; // Shared for CPU/GPU access if needed, or Private for better performance

    id<MTLTexture> texture = [device newTextureWithDescriptor:descriptor];
    if (!texture) return 0;

    // Transfer ownership to the caller (JVM). 
    // The caller must release this later (e.g. in Filament destruction or Skia finalizer).
    return (jlong)CFBridgingRetain(texture);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMetalDevice(JNIEnv* env, jclass, jlong nativeEngine) {
    // For now, we return the system default device.
    // Ideally, we should get this from the engine's platform.
    id<MTLDevice> device = MTLCreateSystemDefaultDevice();
    return (jlong)CFBridgingRetain(device);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMetalQueue(JNIEnv* env, jclass, jlong nativeEngine) {
    id<MTLDevice> device = MTLCreateSystemDefaultDevice();
    id<MTLCommandQueue> queue = [device newCommandQueue];
    return (jlong)CFBridgingRetain(queue);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nReleaseMetalTexture(JNIEnv* env, jclass, jlong handle) {
    if (handle != 0) {
        CFRelease((CFTypeRef)handle);
    }
}

} // extern "C"

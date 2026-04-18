#include <jni.h>
#import <Metal/Metal.h>
#import <Foundation/Foundation.h>

static id<MTLDevice> gDevice = nil;
static id<MTLCommandQueue> gQueue = nil;

static void ensureMetalInitialized() {
    if (!gDevice) {
        gDevice = MTLCreateSystemDefaultDevice();
        if (gDevice) {
            gQueue = [gDevice newCommandQueue];
        }
    }
}

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nCreateMetalTexture(JNIEnv* env, jclass, jint width, jint height) {
    ensureMetalInitialized();
    if (!gDevice) return 0;

    // Use BGRA8Unorm - the native format for macOS surfaces
    MTLTextureDescriptor* descriptor = [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
                                                                                           width:width
                                                                                          height:height
                                                                                       mipmapped:NO];

    descriptor.usage = MTLTextureUsageShaderRead | MTLTextureUsageRenderTarget | MTLTextureUsageShaderWrite;
    descriptor.storageMode = MTLStorageModeShared; 

    id<MTLTexture> texture = [gDevice newTextureWithDescriptor:descriptor];
    if (!texture) return 0;

    // Return the actual pointer
    return (jlong)(__bridge_retained void*)texture;
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMetalDevice(JNIEnv* env, jclass, jlong nativeEngine) {
    ensureMetalInitialized();
    return (jlong)(__bridge void*)gDevice;
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMetalQueue(JNIEnv* env, jclass, jlong nativeEngine) {
    ensureMetalInitialized();
    return (jlong)(__bridge void*)gQueue;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nReleaseMetalTexture(JNIEnv* env, jclass, jlong handle) {
    if (handle != 0) {
        id<MTLTexture> texture = (__bridge_transfer id<MTLTexture>)(void*)handle;
        texture = nil;
    }
}

} // extern "C"

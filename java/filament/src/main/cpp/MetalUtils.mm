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

    MTLTextureDescriptor* descriptor = [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
                                                                                           width:width
                                                                                          height:height
                                                                                       mipmapped:NO];

    descriptor.usage = MTLTextureUsageShaderRead | MTLTextureUsageRenderTarget;
    descriptor.storageMode = MTLStorageModeShared; 

    id<MTLTexture> texture = [gDevice newTextureWithDescriptor:descriptor];
    if (!texture) return 0;

    // Clear texture to cyan to verify it reaches Skia
    id<MTLCommandBuffer> commandBuffer = [gQueue commandBuffer];
    MTLRenderPassDescriptor *passDescriptor = [MTLRenderPassDescriptor renderPassDescriptor];
    passDescriptor.colorAttachments[0].texture = texture;
    passDescriptor.colorAttachments[0].loadAction = MTLLoadActionClear;
    passDescriptor.colorAttachments[0].clearColor = MTLClearColorMake(0.0, 1.0, 1.0, 1.0); // CYAN
    passDescriptor.colorAttachments[0].storeAction = MTLStoreActionStore;
    id<MTLRenderCommandEncoder> encoder = [commandBuffer renderCommandEncoderWithDescriptor:passDescriptor];
    [encoder endEncoding];
    [commandBuffer commit];
    [commandBuffer waitUntilCompleted];

    // Use CFBridgingRetain to transfer ownership to the JVM.

    // The texture will have a ref count of 1 and will NOT be released by ARC.
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


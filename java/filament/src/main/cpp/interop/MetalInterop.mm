#import <Metal/Metal.h>
#include <jni.h>

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_GraphicsInterop_nCreateMetalTexture(
        JNIEnv*, jclass, jlong devicePtr, jint width, jint height) {
    id<MTLDevice> device = (__bridge id<MTLDevice>)(void*) devicePtr;
    if (!device || width <= 0 || height <= 0) return 0;

    MTLTextureDescriptor* desc =
        [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
                                                           width:(NSUInteger) width
                                                          height:(NSUInteger) height
                                                       mipmapped:NO];
    desc.usage = MTLTextureUsageShaderRead | MTLTextureUsageRenderTarget | MTLTextureUsageShaderWrite;
    desc.storageMode = MTLStorageModePrivate;

    id<MTLTexture> texture = [device newTextureWithDescriptor:desc];
    if (!texture) return 0;
    // Ownership transferred to the JVM caller; balanced by nReleaseMetalTexture.
    return (jlong)(__bridge_retained void*) texture;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_GraphicsInterop_nReleaseMetalTexture(JNIEnv*, jclass, jlong handle) {
    if (!handle) return;
    id<MTLTexture> texture = (__bridge_transfer id<MTLTexture>)(void*) handle;
    texture = nil;
}

#import <Metal/Metal.h>
#include <jni.h>

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nCreateSharedTexture(JNIEnv *, jclass,
                                                                   jlong devicePtr, jlong /*physDevicePtr*/,
                                                                   jint width, jint height) {
    id<MTLDevice> device = (__bridge id<MTLDevice>)(void *)devicePtr;
    if (!device) return 0;

    MTLTextureDescriptor *desc =
        [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
                                                          width:width height:height mipmapped:NO];
    desc.usage       = MTLTextureUsageShaderRead | MTLTextureUsageRenderTarget | MTLTextureUsageShaderWrite;
    desc.storageMode = MTLStorageModeShared;

    id<MTLTexture> texture = [device newTextureWithDescriptor:desc];
    if (!texture) return 0;
    // __bridge_retained transfers ownership to the JVM caller; balanced by nReleaseSharedTexture.
    return (jlong)(__bridge_retained void *)texture;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nReleaseSharedTexture(JNIEnv *, jclass, jlong handle) {
    if (handle) {
        // __bridge_transfer returns ownership to ARC, which releases the object.
        id<MTLTexture> texture = (__bridge_transfer id<MTLTexture>)(void *)handle;
        texture = nil;
    }
}

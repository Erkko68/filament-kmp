#import <Metal/Metal.h>
#include <jni.h>

// Creates a MTLTexture on the given device, suitable for Filament rendering and Skia reading.
// The texture is BGRA8Unorm with ShaderRead|RenderTarget|ShaderWrite and MTLStorageModeShared
// so both Filament (render target) and Skia (sampled image) can access it on the same GPU.
// Caller takes ownership — must call nReleaseMetalTexture when done.
extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nCreateMetalTexture(JNIEnv *, jclass,
                                                                  jlong devicePtr,
                                                                  jint width, jint height) {
    id<MTLDevice> device = (__bridge id<MTLDevice>)(void *)devicePtr;
    if (!device) return 0;

    MTLTextureDescriptor *desc =
        [MTLTextureDescriptor texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
                                                          width:width
                                                         height:height
                                                      mipmapped:NO];
    desc.usage       = MTLTextureUsageShaderRead | MTLTextureUsageRenderTarget | MTLTextureUsageShaderWrite;
    desc.storageMode = MTLStorageModeShared;

    id<MTLTexture> texture = [device newTextureWithDescriptor:desc];
    if (!texture) return 0;

    // __bridge_retained: transfers ownership to the JVM caller; balanced by nReleaseMetalTexture.
    return (jlong)(__bridge_retained void *)texture;
}

// Releases a texture handle previously returned by nCreateMetalTexture.
extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nReleaseMetalTexture(JNIEnv *, jclass, jlong handle) {
    if (handle != 0) {
        // __bridge_transfer returns ownership to ARC, which then releases the object.
        id<MTLTexture> texture = (__bridge_transfer id<MTLTexture>)(void *)handle;
        texture = nil;
    }
}

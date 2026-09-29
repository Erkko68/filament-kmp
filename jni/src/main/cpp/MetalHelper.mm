#include <jni.h>

#ifdef __APPLE__
#import <Metal/Metal.h>

#define METAL_JNI(ret, name) extern "C" JNIEXPORT ret JNICALL Java_io_github_erkko68_filament_jni_MetalHelper_##name

// Returns a +1 retained BGRA8 texture on devicePtr; Filament's importTexture takes that reference.
METAL_JNI(jlong, nCreateMetalTexture)(JNIEnv*, jclass, jlong devicePtr, jint width, jint height) {
    @autoreleasepool {
        id<MTLDevice> device = (id<MTLDevice>)(void*)devicePtr;
        MTLTextureDescriptor* desc = [MTLTextureDescriptor
            texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
            width:width
            height:height
            mipmapped:NO];
        desc.usage = MTLTextureUsageRenderTarget | MTLTextureUsageShaderRead;
        desc.storageMode = MTLStorageModePrivate;
        return (jlong)(void*)[device newTextureWithDescriptor:desc];
    }
}

// Filament's Metal backend renders on MTLCreateSystemDefaultDevice().
METAL_JNI(jboolean, nIsSystemDefaultDevice)(JNIEnv*, jclass, jlong devicePtr) {
    id<MTLDevice> device = (id<MTLDevice>)(void*)devicePtr;
    id<MTLDevice> systemDefault = MTLCreateSystemDefaultDevice();
    const BOOL same = device.registryID == systemDefault.registryID;
    [systemDefault release];
    return same;
}
#endif

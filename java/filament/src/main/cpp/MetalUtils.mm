#import <Foundation/Foundation.h>
#import <Metal/Metal.h>
#include <filament/Engine.h>
#include <jni.h>

static id<MTLDevice> gDevice = nil;
static id<MTLCommandQueue> gQueue = nil;
static bool gExternalContext = false;
static filament::Engine *gEngine = nullptr;

static void ensureMetalInitialized() {
  if (gExternalContext)
    return;
  if (!gDevice) {
    gDevice = MTLCreateSystemDefaultDevice();
    if (gDevice) {
      gQueue = [gDevice newCommandQueue];
    }
  }
}

static void releaseExternalContext() {
  if (gExternalContext && gDevice) {
    // Balance the __bridge_retained calls
    CFRelease((__bridge CFTypeRef)gDevice);
    if (gQueue)
      CFRelease((__bridge CFTypeRef)gQueue);
    gDevice = nil;
    gQueue = nil;
    gExternalContext = false;
  }
}

extern "C" {

// In nSetMetalContext — allow queuePtr to be 0, create queue from shared device
JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetMetalContext(JNIEnv *, jclass,
                                                            jlong devicePtr,
                                                            jlong queuePtr) {

  if (devicePtr == 0) {
    NSLog(@"[Filament] nSetMetalContext: devicePtr is null, ignoring");
    return;
  }

  releaseExternalContext();

  gDevice = (__bridge_retained id<MTLDevice>)(void *)devicePtr;

  if (queuePtr != 0) {
    gQueue = (__bridge_retained id<MTLCommandQueue>)(void *)queuePtr;
    NSLog(@"[Filament] Using shared Skiko queue: %p", (void *)queuePtr);
  } else {
    // Create our own queue on Skiko's device — same GPU, flushAndWait() ensures
    // sync
    gQueue = [gDevice newCommandQueue];
    NSLog(@"[Filament] Created new queue on shared device: %p",
          (void *)(__bridge void *)gQueue);
  }

  gExternalContext = true;
  NSLog(@"[Filament] Metal context set: device=%p queue=%p", (void *)devicePtr,
        (void *)(__bridge void *)gQueue);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateEngine(JNIEnv *, jclass) {
  ensureMetalInitialized();
  if (!gDevice || !gQueue) {
    NSLog(@"[Filament] nCreateEngine: Metal not initialized");
    return 0;
  }

  // Pass the shared MTLCommandQueue as sharedContext so Filament
  // enqueues onto the same GPU timeline as Skiko
  void *sharedCtx = (__bridge void *)gQueue;
  gEngine = filament::Engine::Builder()
                .backend(filament::Engine::Backend::METAL)
                .sharedContext(sharedCtx)
                .build();

  NSLog(@"[Filament] Engine created with shared queue: %p", sharedCtx);
  return (jlong)(void *)gEngine;
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nCreateMetalTexture(JNIEnv *,
                                                                jclass,
                                                                jint width,
                                                                jint height) {

  ensureMetalInitialized();
  if (!gDevice)
    return 0;

  MTLTextureDescriptor *desc = [MTLTextureDescriptor
      texture2DDescriptorWithPixelFormat:MTLPixelFormatBGRA8Unorm
                                   width:width
                                  height:height
                               mipmapped:NO];
  // MTLStorageModeShared: CPU+GPU accessible — correct for readback
  desc.usage = MTLTextureUsageShaderRead | MTLTextureUsageRenderTarget |
               MTLTextureUsageShaderWrite;
  desc.storageMode = MTLStorageModeShared;

  id<MTLTexture> texture = [gDevice newTextureWithDescriptor:desc];
  if (!texture)
    return 0;

  // __bridge_retained: caller owns this pointer, must call nReleaseMetalTexture
  return (jlong)(__bridge_retained void *)texture;
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMetalDevice(JNIEnv *, jclass,
                                                           jlong) {
  ensureMetalInitialized();
  // __bridge: we don't transfer ownership, just expose the pointer
  return (jlong)(__bridge void *)gDevice;
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMetalQueue(JNIEnv *, jclass,
                                                          jlong) {
  ensureMetalInitialized();
  return (jlong)(__bridge void *)gQueue;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nReleaseMetalTexture(JNIEnv *,
                                                                 jclass,
                                                                 jlong handle) {
  if (handle != 0) {
    // __bridge_transfer: gives ownership back to ARC, which then releases
    id<MTLTexture> texture = (__bridge_transfer id<MTLTexture>)(void *)handle;
    texture = nil;
  }
}

} // extern "C"
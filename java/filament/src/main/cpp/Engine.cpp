#include <jni.h>
#include <filament/Engine.h>
#include <filament/Renderer.h>
#include <filament/SwapChain.h>
#include <filament/View.h>
#include <filament/Scene.h>
#include <filament/Camera.h>
#include <filament/VertexBuffer.h>
#include <filament/IndexBuffer.h>
#include <filament/Material.h>
#include <filament/Texture.h>
#include <filament/RenderableManager.h>
#include <filament/LightManager.h>
#include <filament/TransformManager.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateEngine(JNIEnv* env, jclass, jlong nativeBackend, jlong nativeSharedContext) {
    // For now we use default backend. In production we might want to specify it.
    Engine* engine = Engine::create();
    return (jlong) engine;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyEngine(JNIEnv* env, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    if (engine) {
        Engine::destroy(&engine);
    }
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateRenderer(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) ((Engine*) nativeEngine)->createRenderer();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyRenderer(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeRenderer) {
    ((Engine*) nativeEngine)->destroy((Renderer*) nativeRenderer);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateSwapChain(JNIEnv* env, jclass, jlong nativeEngine, jint width, jint height, jlong flags) {
    // This is for headless or specific platform surfaces. For now we just create a dummy/headless one if window is 0.
    return (jlong) ((Engine*) nativeEngine)->createSwapChain((uint32_t) width, (uint32_t) height, (uint64_t) flags);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroySwapChain(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeSwapChain) {
    ((Engine*) nativeEngine)->destroy((SwapChain*) nativeSwapChain);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateView(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) ((Engine*) nativeEngine)->createView();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyView(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeView) {
    ((Engine*) nativeEngine)->destroy((View*) nativeView);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateScene(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) ((Engine*) nativeEngine)->createScene();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyScene(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeScene) {
    ((Engine*) nativeEngine)->destroy((Scene*) nativeScene);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateCamera(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) ((Engine*) nativeEngine)->createCamera(utils::EntityManager::get().create());
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyCamera(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeCamera) {
    Camera* camera = (Camera*) nativeCamera;
    utils::Entity e = camera->getEntity();
    ((Engine*) nativeEngine)->destroy(camera);
    utils::EntityManager::get().destroy(e);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyVertexBuffer(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeVertexBuffer) {
    ((Engine*) nativeEngine)->destroy((VertexBuffer*) nativeVertexBuffer);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyIndexBuffer(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeIndexBuffer) {
    ((Engine*) nativeEngine)->destroy((IndexBuffer*) nativeIndexBuffer);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyMaterial(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeMaterial) {
    ((Engine*) nativeEngine)->destroy((Material*) nativeMaterial);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyTexture(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeTexture) {
    ((Engine*) nativeEngine)->destroy((Texture*) nativeTexture);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyEntity(JNIEnv* env, jclass, jlong nativeEngine, jint entity) {
    ((Engine*) nativeEngine)->destroy((utils::Entity&) entity);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nGetRenderableManager(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) &((Engine*) nativeEngine)->getRenderableManager();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nGetLightManager(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) &((Engine*) nativeEngine)->getLightManager();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nGetTransformManager(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) &((Engine*) nativeEngine)->getTransformManager();
}

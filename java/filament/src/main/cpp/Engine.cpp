/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>

#include <filament/Camera.h>
#include <filament/Engine.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/Material.h>
#include <filament/View.h>
#include <filament/Renderer.h>
#include <filament/Scene.h>
#include <filament/Fence.h>
#include <filament/Stream.h>
#include <filament/IndexBuffer.h>
#include <filament/VertexBuffer.h>
#include <filament/SkinningBuffer.h>
#include <filament/IndirectLight.h>
#include <filament/Skybox.h>
#include <filament/ColorGrading.h>
#include <filament/Texture.h>
#include <filament/RenderTarget.h>

#include <utils/Entity.h>
#include <utils/EntityManager.h>
#include <utils/tribool.h>

#include "common/CallbackUtils.h"
#include "common/VirtualMachineEnv.h"

using namespace filament;
using namespace utils;

// ------------------------------------------------------------------------------------------------
// Platform-specific surface handling
// ------------------------------------------------------------------------------------------------

// For KMP on Desktop, we might need to handle various surface types.
// For now, we provide a simple implementation that handles pointers as Long objects.
static void* getNativeWindow(JNIEnv* env, jclass, jobject surface) {
    if (surface == nullptr) return nullptr;
    
    // Check if it's a Long (primitive wrapper) - common way to pass pointers from Kotlin
    jclass longClass = env->FindClass("java/lang/Long");
    if (env->IsInstanceOf(surface, longClass)) {
        jmethodID longValueMethod = env->GetMethodID(longClass, "longValue", "()J");
        return (void*) env->CallLongMethod(surface, longValueMethod);
    }
    
    // On Android, this would use ANativeWindow_fromSurface.
    // On Desktop, this depends on the windowing system. 
    // For now, we return the object itself if we can't identify it, 
    // but this is likely to fail unless the backend knows how to handle it.
    return (void*) surface;
}

// ------------------------------------------------------------------------------------------------
// JNI Implementation
// ------------------------------------------------------------------------------------------------

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    VirtualMachineEnv::get().setJvm(vm);
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyEngine(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    Engine::destroy(&engine);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetBackend(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) engine->getBackend();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateSwapChain(JNIEnv* env, jclass klass, jlong nativeEngine, jobject surface, jlong flags) {
    Engine* engine = (Engine*) nativeEngine;
    void* win = getNativeWindow(env, klass, surface);
    return (jlong) engine->createSwapChain(win, (uint64_t) flags);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateSwapChainHeadless(JNIEnv*, jclass, jlong nativeEngine, jint width, jint height, jlong flags) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) engine->createSwapChain((uint32_t) width, (uint32_t) height, (uint64_t) flags);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateSwapChainFromRawPointer(JNIEnv*, jclass, jlong nativeEngine, jlong pointer, jlong flags) {
     Engine* engine = (Engine*) nativeEngine;
     return (jlong) engine->createSwapChain((void*) pointer, (uint64_t) flags);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroySwapChain(JNIEnv*, jclass, jlong nativeEngine, jlong nativeSwapChain) {
    Engine* engine = (Engine*) nativeEngine;
    SwapChain* swapChain = (SwapChain*) nativeSwapChain;
    engine->destroy(swapChain);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateView(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) engine->createView();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyView(JNIEnv*, jclass, jlong nativeEngine, jlong nativeView) {
    Engine* engine = (Engine*) nativeEngine;
    View* view = (View*) nativeView;
    engine->destroy(view);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateRenderer(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) engine->createRenderer();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyRenderer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeRenderer) {
    Engine* engine = (Engine*) nativeEngine;
    Renderer* renderer = (Renderer*) nativeRenderer;
    engine->destroy(renderer);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateCamera(JNIEnv*, jclass, jlong nativeEngine, jint entity_) {
    Engine* engine = (Engine*) nativeEngine;
    Entity& entity = *reinterpret_cast<Entity*>(&entity_);
    return (jlong) engine->createCamera(entity);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetCameraComponent(JNIEnv*, jclass, jlong nativeEngine, jint entity_) {
    Engine* engine = (Engine*) nativeEngine;
    Entity& entity = *reinterpret_cast<Entity*>(&entity_);
    return (jlong) engine->getCameraComponent(entity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyCameraComponent(JNIEnv*, jclass, jlong nativeEngine, jint entity_) {
    Engine* engine = (Engine*) nativeEngine;
    Entity& entity = *reinterpret_cast<Entity*>(&entity_);
    engine->destroyCameraComponent(entity);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateScene(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) engine->createScene();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyScene(JNIEnv*, jclass, jlong nativeEngine, jlong nativeScene) {
    Engine* engine = (Engine*) nativeEngine;
    Scene* scene = (Scene*) nativeScene;
    engine->destroy(scene);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateFence(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) engine->createFence();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyFence(JNIEnv*, jclass, jlong nativeEngine, jlong nativeFence) {
    Engine* engine = (Engine*) nativeEngine;
    Fence* fence = (Fence*) nativeFence;
    engine->destroy(fence);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyStream(JNIEnv*, jclass, jlong nativeEngine, jlong nativeStream) {
    Engine* engine = (Engine*) nativeEngine;
    Stream* stream = (Stream*) nativeStream;
    engine->destroy(stream);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyIndexBuffer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeIndexBuffer) {
    Engine* engine = (Engine*) nativeEngine;
    IndexBuffer* indexBuffer = (IndexBuffer*) nativeIndexBuffer;
    engine->destroy(indexBuffer);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyVertexBuffer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeVertexBuffer) {
    Engine* engine = (Engine*) nativeEngine;
    VertexBuffer* vertexBuffer = (VertexBuffer*) nativeVertexBuffer;
    engine->destroy(vertexBuffer);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroySkinningBuffer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeSkinningBuffer) {
    Engine* engine = (Engine*) nativeEngine;
    SkinningBuffer* skinningBuffer = (SkinningBuffer*) nativeSkinningBuffer;
    engine->destroy(skinningBuffer);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyMorphTargetBuffer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeMorphTargetBuffer) {
    Engine* engine = (Engine*) nativeEngine;
    MorphTargetBuffer* mtb = (MorphTargetBuffer*) nativeMorphTargetBuffer;
    engine->destroy(mtb);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyIndirectLight(JNIEnv*, jclass, jlong nativeEngine, jlong nativeIndirectLight) {
    Engine* engine = (Engine*) nativeEngine;
    IndirectLight* indirectLight = (IndirectLight*) nativeIndirectLight;
    engine->destroy(indirectLight);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyMaterial(JNIEnv*, jclass, jlong nativeEngine, jlong nativeMaterial) {
    Engine* engine = (Engine*) nativeEngine;
    Material* material = (Material*) nativeMaterial;
    engine->destroy(material);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyMaterialInstance(JNIEnv*, jclass, jlong nativeEngine, jlong nativeMaterialInstance) {
    Engine* engine = (Engine*) nativeEngine;
    MaterialInstance* materialInstance = (MaterialInstance*) nativeMaterialInstance;
    engine->destroy(materialInstance);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroySkybox(JNIEnv*, jclass, jlong nativeEngine, jlong nativeSkybox) {
    Engine* engine = (Engine*) nativeEngine;
    Skybox* skybox = (Skybox*) nativeSkybox;
    engine->destroy(skybox);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyColorGrading(JNIEnv*, jclass, jlong nativeEngine, jlong nativeColorGrading) {
    Engine* engine = (Engine*) nativeEngine;
    ColorGrading* colorGrading = (ColorGrading*) nativeColorGrading;
    engine->destroy(colorGrading);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyTexture(JNIEnv*, jclass, jlong nativeEngine, jlong nativeTexture) {
    Engine* engine = (Engine*) nativeEngine;
    Texture* texture = (Texture*) nativeTexture;
    engine->destroy(texture);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyRenderTarget(JNIEnv*, jclass, jlong nativeEngine, jlong nativeTarget) {
    Engine* engine = (Engine*) nativeEngine;
    RenderTarget* target = (RenderTarget*) nativeTarget;
    engine->destroy(target);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyEntity(JNIEnv*, jclass, jlong nativeEngine, jint entity_) {
    Engine* engine = (Engine*) nativeEngine;
    Entity& entity = *reinterpret_cast<Entity*>(&entity_);
    engine->destroy(entity);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidRenderer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeRenderer) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((Renderer*)nativeRenderer);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidView(JNIEnv*, jclass, jlong nativeEngine, jlong nativeView) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((View*)nativeView);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidScene(JNIEnv*, jclass, jlong nativeEngine, jlong nativeScene) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((Scene*)nativeScene);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidFence(JNIEnv*, jclass, jlong nativeEngine, jlong nativeFence) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((Fence*)nativeFence);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidStream(JNIEnv*, jclass, jlong nativeEngine, jlong nativeStream) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((Stream*)nativeStream);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidIndexBuffer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeIndexBuffer) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((IndexBuffer*)nativeIndexBuffer);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidVertexBuffer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeVertexBuffer) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((VertexBuffer*)nativeVertexBuffer);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidSkinningBuffer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeSkinningBuffer) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((SkinningBuffer*)nativeSkinningBuffer);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidMorphTargetBuffer(JNIEnv*, jclass, jlong nativeEngine, jlong nativeMorphTargetBuffer) {
    Engine* engine = (Engine*) nativeEngine;
    return (jboolean) engine->isValid((MorphTargetBuffer*) nativeMorphTargetBuffer);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidIndirectLight(JNIEnv*, jclass, jlong nativeEngine, jlong nativeIndirectLight) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((IndirectLight*)nativeIndirectLight);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidMaterial(JNIEnv*, jclass, jlong nativeEngine, jlong nativeMaterial) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((Material*)nativeMaterial);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidMaterialInstance(JNIEnv*, jclass, jlong nativeEngine, jlong nativeMaterial, jlong nativeMaterialInstance) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((Material*)nativeMaterial, (MaterialInstance*)nativeMaterialInstance);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidExpensiveMaterialInstance(JNIEnv*, jclass, jlong nativeEngine, jlong nativeMaterialInstance) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValidExpensive((MaterialInstance*)nativeMaterialInstance);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidSkybox(JNIEnv*, jclass, jlong nativeEngine, jlong nativeSkybox) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((Skybox*)nativeSkybox);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidColorGrading(JNIEnv*, jclass, jlong nativeEngine, jlong nativeColorGrading) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((ColorGrading*)nativeColorGrading);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidTexture(JNIEnv*, jclass, jlong nativeEngine, jlong nativeTexture) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((Texture*)nativeTexture);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidRenderTarget(JNIEnv*, jclass, jlong nativeEngine, jlong nativeTarget) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((RenderTarget*)nativeTarget);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsValidSwapChain(JNIEnv*, jclass, jlong nativeEngine, jlong nativeSwapChain) {
    Engine* engine = (Engine *)nativeEngine;
    return (jboolean)engine->isValid((SwapChain*)nativeSwapChain);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nFlushAndWait(JNIEnv*, jclass, jlong nativeEngine, jlong timeout) {
    Engine* engine = (Engine*) nativeEngine;
    return engine->flushAndWait((uint64_t) timeout);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nFlush(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    engine->flush();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsPaused(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jboolean) engine->isPaused();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetPaused(JNIEnv*, jclass, jlong nativeEngine, jboolean paused) {
    Engine* engine = (Engine*) nativeEngine;
    engine->setPaused(paused);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nUnprotected(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    engine->unprotected();
}


JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCompile(JNIEnv* env, jclass,
        jlong nativeEngine, jint priority, jlong nativeMaterial, jlong nativeView,
        jint shadowReceiver, jint skinning, jobject handler, jobject runnable) {
    Engine* engine = (Engine*) nativeEngine;
    Material* material = (Material*) nativeMaterial;
    View* view = (View*) nativeView;
    JniCallback* jniCallback = JniCallback::make(env, handler, runnable);
    engine->compile(
            (backend::CompilerPriorityQueue) priority,
            material, view,
            (utils::tribool) shadowReceiver,
            (utils::tribool) skinning,
            jniCallback->getHandler(), [jniCallback](Material*){
                JniCallback::postToJavaAndDestroy(jniCallback);
            });
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetTransformManager(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) &engine->getTransformManager();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetLightManager(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) &engine->getLightManager();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetRenderableManager(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) &engine->getRenderableManager();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetJobSystem(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) &engine->getJobSystem();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetEntityManager(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) &engine->getEntityManager();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetAutomaticInstancingEnabled(JNIEnv*, jclass, jlong nativeEngine, jboolean enable) {
    Engine* engine = (Engine*) nativeEngine;
    engine->setAutomaticInstancingEnabled(enable);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nIsAutomaticInstancingEnabled(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jboolean) engine->isAutomaticInstancingEnabled();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetMaxStereoscopicEyes(JNIEnv*, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) engine->getMaxStereoscopicEyes();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetSupportedFeatureLevel(JNIEnv *, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jint) engine->getSupportedFeatureLevel();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetActiveFeatureLevel(JNIEnv *, jclass, jlong nativeEngine, jint ordinal) {
    Engine* engine = (Engine*) nativeEngine;
    return (jint) engine->setActiveFeatureLevel((Engine::FeatureLevel) ordinal);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetActiveFeatureLevel(JNIEnv *, jclass, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jint) engine->getActiveFeatureLevel();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nHasFeatureFlag(JNIEnv *env, jclass, jlong nativeEngine, jstring name_) {
    Engine* engine = (Engine*) nativeEngine;
    const char *name = env->GetStringUTFChars(name_, 0);
    bool result = engine->hasFeatureFlag(name);
    env->ReleaseStringUTFChars(name_, name);
    return (jboolean) result;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetFeatureFlag(JNIEnv *env, jclass, jlong nativeEngine, jstring name_, jboolean value) {
    Engine* engine = (Engine*) nativeEngine;
    const char *name = env->GetStringUTFChars(name_, 0);
    bool result = engine->setFeatureFlag(name, (bool) value);
    env->ReleaseStringUTFChars(name_, name);
    return (jboolean) result;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Engine_nGetFeatureFlag(JNIEnv *env, jclass, jlong nativeEngine, jstring name_) {
    Engine* engine = (Engine*) nativeEngine;
    const char *name = env->GetStringUTFChars(name_, 0);
    bool result = engine->getFeatureFlag(name).value_or(false);
    env->ReleaseStringUTFChars(name_, name);
    return (jboolean) result;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new Engine::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (Engine::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetBuilderBackend(JNIEnv*, jclass, jlong nativeBuilder, jint backend) {
    ((Engine::Builder*) nativeBuilder)->backend((Engine::Backend) backend);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetBuilderConfig(JNIEnv*, jclass, jlong nativeBuilder, jlong commandBufferSizeMB, jlong perRenderPassArenaSizeMB, jlong driverHandleArenaSizeMB, jlong minCommandBufferSizeMB, jlong perFrameCommandsSizeMB, jlong jobSystemThreadCount, jboolean disableParallelShaderCompile, jint stereoscopicType, jlong stereoscopicEyeCount, jlong resourceAllocatorCacheSizeMB, jlong resourceAllocatorCacheMaxAge, jboolean disableHandleUseAfterFreeCheck, jint preferredShaderLanguage, jboolean forceGLES2Context, jboolean assertNativeWindowIsValid, jint gpuContextPriority, jlong sharedUboInitialSizeInBytes) {
    Engine::Config config;
    config.commandBufferSizeMB = (uint32_t) commandBufferSizeMB;
    config.perRenderPassArenaSizeMB = (uint32_t) perRenderPassArenaSizeMB;
    config.driverHandleArenaSizeMB = (uint32_t) driverHandleArenaSizeMB;
    config.minCommandBufferSizeMB = (uint32_t) minCommandBufferSizeMB;
    config.perFrameCommandsSizeMB = (uint32_t) perFrameCommandsSizeMB;
    config.jobSystemThreadCount = (uint32_t) jobSystemThreadCount;
    config.disableParallelShaderCompile = (bool) disableParallelShaderCompile;
    config.stereoscopicType = (Engine::StereoscopicType) stereoscopicType;
    config.stereoscopicEyeCount = (uint8_t) stereoscopicEyeCount;
    config.resourceAllocatorCacheSizeMB = (uint32_t) resourceAllocatorCacheSizeMB;
    config.resourceAllocatorCacheMaxAge = (uint8_t) resourceAllocatorCacheMaxAge;
    config.disableHandleUseAfterFreeCheck = (bool) disableHandleUseAfterFreeCheck;
    config.preferredShaderLanguage = (Engine::Config::ShaderLanguage) preferredShaderLanguage;
    config.forceGLES2Context = (bool) forceGLES2Context;
    config.assertNativeWindowIsValid = (bool) assertNativeWindowIsValid;
    config.gpuContextPriority = (Engine::GpuContextPriority) gpuContextPriority;
    config.sharedUboInitialSizeInBytes = (uint32_t) sharedUboInitialSizeInBytes;
    ((Engine::Builder*) nativeBuilder)->config(&config);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetBuilderFeatureLevel(JNIEnv*, jclass, jlong nativeBuilder, jint featureLevel) {
    ((Engine::Builder*) nativeBuilder)->featureLevel((Engine::FeatureLevel) featureLevel);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetBuilderSharedContext(JNIEnv* env, jclass klass, jlong nativeBuilder, jobject sharedContext) {
     void* ctx = getNativeWindow(env, klass, sharedContext);
    ((Engine::Builder*) nativeBuilder)->sharedContext(ctx);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetBuilderPaused(JNIEnv*, jclass, jlong nativeBuilder, jboolean paused) {
    ((Engine::Builder*) nativeBuilder)->paused((bool) paused);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Engine_nSetBuilderFeature(JNIEnv *env, jclass, jlong nativeBuilder, jstring name_, jboolean value) {
    const char *name = env->GetStringUTFChars(name_, 0);
    ((Engine::Builder*) nativeBuilder)->feature(name, (bool) value);
    env->ReleaseStringUTFChars(name_, name);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder) {
    return (jlong) ((Engine::Builder*) nativeBuilder)->build();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Engine_getSteadyClockTimeNano(JNIEnv *, jclass) {
    return (jlong) Engine::getSteadyClockTimeNano();
}

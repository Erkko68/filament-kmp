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
#include <filament/MaterialInstance.h>
#include <filament/Texture.h>
#include <filament/TextureSampler.h>
#include <math/mat3.h>
#include <math/mat4.h>
#include <math/vec2.h>
#include <math/vec3.h>
#include <math/vec4.h>

using namespace filament;
using namespace filament::math;

namespace filament::JniUtils {
    extern TextureSampler from_long(jlong params) noexcept;
}

template<typename T>
static void setParameter(JNIEnv* env, jlong nativeMaterialInstance, jstring name_, T v) {
    MaterialInstance* instance = (MaterialInstance*) nativeMaterialInstance;
    const char *name = env->GetStringUTFChars(name_, 0);
    instance->setParameter(name, v);
    env->ReleaseStringUTFChars(name_, name);
}

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterBool(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jboolean x) {
    setParameter(env, nativeMaterialInstance, name_, bool(x));
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterBool2(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jboolean x, jboolean y) {
    setParameter(env, nativeMaterialInstance, name_, bool2{x, y});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterBool3(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jboolean x, jboolean y, jboolean z) {
    setParameter(env, nativeMaterialInstance, name_, bool3{x, y, z});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterBool4(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jboolean x, jboolean y, jboolean z, jboolean w) {
    setParameter(env, nativeMaterialInstance, name_, bool4{x, y, z, w});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterInt(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jint x) {
    setParameter(env, nativeMaterialInstance, name_, int32_t(x));
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterInt2(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jint x, jint y) {
    setParameter(env, nativeMaterialInstance, name_, int2{x, y});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterInt3(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jint x, jint y, jint z) {
    setParameter(env, nativeMaterialInstance, name_, int3{x, y, z});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterInt4(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jint x, jint y, jint z, jint w) {
    setParameter(env, nativeMaterialInstance, name_, int4{x, y, z, w});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterFloat(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jfloat x) {
    setParameter(env, nativeMaterialInstance, name_, float(x));
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterFloat2(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jfloat x, jfloat y) {
    setParameter(env, nativeMaterialInstance, name_, float2{x, y});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterFloat3(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jfloat x, jfloat y, jfloat z) {
    setParameter(env, nativeMaterialInstance, name_, float3{x, y, z});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterFloat4(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jfloat x, jfloat y, jfloat z, jfloat w) {
    setParameter(env, nativeMaterialInstance, name_, float4{x, y, z, w});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetBooleanParameterArray(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jint element, jbooleanArray v_, jint offset, jint count) {
    MaterialInstance* instance = (MaterialInstance*) nativeMaterialInstance;
    const char* name = env->GetStringUTFChars(name_, 0);
    jboolean* v = env->GetBooleanArrayElements(v_, NULL);
    switch (element) {
        case 0: instance->setParameter(name, ((const bool*) v) + offset, count); break;
        case 1: instance->setParameter(name, ((const bool2*) v) + offset, count); break;
        case 2: instance->setParameter(name, ((const bool3*) v) + offset, count); break;
        case 3: instance->setParameter(name, ((const bool4*) v) + offset, count); break;
    }
    env->ReleaseBooleanArrayElements(v_, v, JNI_ABORT);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetIntParameterArray(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jint element, jintArray v_, jint offset, jint count) {
    MaterialInstance* instance = (MaterialInstance*) nativeMaterialInstance;
    const char* name = env->GetStringUTFChars(name_, 0);
    jint* v = env->GetIntArrayElements(v_, NULL);
    switch (element) {
        case 0: instance->setParameter(name, ((const int32_t*) v) + offset, count); break;
        case 1: instance->setParameter(name, ((const int2*) v) + offset, count); break;
        case 2: instance->setParameter(name, ((const int3*) v) + offset, count); break;
        case 3: instance->setParameter(name, ((const int4*) v) + offset, count); break;
    }
    env->ReleaseIntArrayElements(v_, v, JNI_ABORT);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetFloatParameterArray(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jint element, jfloatArray v_, jint offset, jint count) {
    MaterialInstance* instance = (MaterialInstance*) nativeMaterialInstance;
    const char* name = env->GetStringUTFChars(name_, 0);
    jfloat* v = env->GetFloatArrayElements(v_, NULL);
    switch (element) {
        case 0: instance->setParameter(name, ((const float*) v) + offset, count); break;
        case 1: instance->setParameter(name, ((const float2*) v) + offset, count); break;
        case 2: instance->setParameter(name, ((const float3*) v) + offset, count); break;
        case 3: instance->setParameter(name, ((const float4*) v) + offset, count); break;
        case 4: instance->setParameter(name, ((const mat3f*) v) + offset, count); break;
        case 5: instance->setParameter(name, ((const mat4f*) v) + offset, count); break;
    }
    env->ReleaseFloatArrayElements(v_, v, JNI_ABORT);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetParameterTexture(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name_, jlong nativeTexture, jint sampler) {
    MaterialInstance* instance = (MaterialInstance*) nativeMaterialInstance;
    const char* name = env->GetStringUTFChars(name_, 0);
    instance->setParameter(name, (Texture*) nativeTexture, JniUtils::from_long(sampler));
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetScissor(JNIEnv*, jclass, jlong nativeMaterialInstance, jint left, jint bottom, jint width, jint height) {
    ((MaterialInstance*) nativeMaterialInstance)->setScissor(left, bottom, width, height);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nUnsetScissor(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    ((MaterialInstance*) nativeMaterialInstance)->unsetScissor();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetPolygonOffset(JNIEnv*, jclass, jlong nativeMaterialInstance, jfloat scale, jfloat constant) {
    ((MaterialInstance*) nativeMaterialInstance)->setPolygonOffset(scale, constant);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetMaskThreshold(JNIEnv*, jclass, jlong nativeMaterialInstance, jfloat threshold) {
    ((MaterialInstance*) nativeMaterialInstance)->setMaskThreshold(threshold);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetMaskThreshold(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return ((MaterialInstance*) nativeMaterialInstance)->getMaskThreshold();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetSpecularAntiAliasingVariance(JNIEnv*, jclass, jlong nativeMaterialInstance, jfloat variance) {
    ((MaterialInstance*) nativeMaterialInstance)->setSpecularAntiAliasingVariance(variance);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetSpecularAntiAliasingVariance(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return ((MaterialInstance*) nativeMaterialInstance)->getSpecularAntiAliasingVariance();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetSpecularAntiAliasingThreshold(JNIEnv*, jclass, jlong nativeMaterialInstance, jfloat threshold) {
    ((MaterialInstance*) nativeMaterialInstance)->setSpecularAntiAliasingThreshold(threshold);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetSpecularAntiAliasingThreshold(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return ((MaterialInstance*) nativeMaterialInstance)->getSpecularAntiAliasingThreshold();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetDoubleSided(JNIEnv*, jclass, jlong nativeMaterialInstance, jboolean doubleSided) {
    ((MaterialInstance*) nativeMaterialInstance)->setDoubleSided(doubleSided);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetTransparencyMode(JNIEnv*, jclass, jlong nativeMaterialInstance, jint mode) {
    ((MaterialInstance*) nativeMaterialInstance)->setTransparencyMode((MaterialInstance::TransparencyMode) mode);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nIsDoubleSided(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jboolean) ((MaterialInstance*) nativeMaterialInstance)->isDoubleSided();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetTransparencyMode(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jint) ((MaterialInstance*) nativeMaterialInstance)->getTransparencyMode();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetCullingMode(JNIEnv*, jclass, jlong nativeMaterialInstance, jint mode) {
    ((MaterialInstance*) nativeMaterialInstance)->setCullingMode((MaterialInstance::CullingMode) mode);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetCullingModeSeparate(JNIEnv*, jclass, jlong nativeMaterialInstance, jint colorMode, jint shadowMode) {
    ((MaterialInstance*) nativeMaterialInstance)->setCullingMode((MaterialInstance::CullingMode) colorMode, (MaterialInstance::CullingMode) shadowMode);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetCullingMode(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jint) ((MaterialInstance*) nativeMaterialInstance)->getCullingMode();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetShadowCullingMode(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jint) ((MaterialInstance*) nativeMaterialInstance)->getShadowCullingMode();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetColorWrite(JNIEnv*, jclass, jlong nativeMaterialInstance, jboolean enable) {
    ((MaterialInstance*) nativeMaterialInstance)->setColorWrite(enable);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nIsColorWriteEnabled(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jboolean) ((MaterialInstance*) nativeMaterialInstance)->isColorWriteEnabled();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetDepthWrite(JNIEnv*, jclass, jlong nativeMaterialInstance, jboolean enable) {
    ((MaterialInstance*) nativeMaterialInstance)->setDepthWrite(enable);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nIsDepthWriteEnabled(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jboolean) ((MaterialInstance*) nativeMaterialInstance)->isDepthWriteEnabled();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetStencilWrite(JNIEnv*, jclass, jlong nativeMaterialInstance, jboolean enable) {
    ((MaterialInstance*) nativeMaterialInstance)->setStencilWrite(enable);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nIsStencilWriteEnabled(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jboolean) ((MaterialInstance*) nativeMaterialInstance)->isStencilWriteEnabled();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetDepthCulling(JNIEnv*, jclass, jlong nativeMaterialInstance, jboolean enable) {
    ((MaterialInstance*) nativeMaterialInstance)->setDepthCulling(enable);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetDepthFunc(JNIEnv*, jclass, jlong nativeMaterialInstance, jint func) {
    ((MaterialInstance*) nativeMaterialInstance)->setDepthFunc((MaterialInstance::DepthFunc) func);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nIsDepthCullingEnabled(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jboolean) ((MaterialInstance*) nativeMaterialInstance)->isDepthCullingEnabled();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetDepthFunc(JNIEnv*, jclass, jlong nativeMaterialInstance) {
    return (jint) ((MaterialInstance*) nativeMaterialInstance)->getDepthFunc();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetStencilCompareFunction(JNIEnv*, jclass, jlong nativeMaterialInstance, jint func, jint face) {
    ((MaterialInstance*) nativeMaterialInstance)->setStencilCompareFunction((MaterialInstance::StencilCompareFunc) func, (MaterialInstance::StencilFace) face);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetStencilOpStencilFail(JNIEnv*, jclass, jlong nativeMaterialInstance, jint op, jint face) {
    ((MaterialInstance*) nativeMaterialInstance)->setStencilOpStencilFail((MaterialInstance::StencilOperation) op, (MaterialInstance::StencilFace) face);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetStencilOpDepthFail(JNIEnv*, jclass, jlong nativeMaterialInstance, jint op, jint face) {
    ((MaterialInstance*) nativeMaterialInstance)->setStencilOpDepthFail((MaterialInstance::StencilOperation) op, (MaterialInstance::StencilFace) face);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetStencilOpDepthStencilPass(JNIEnv*, jclass, jlong nativeMaterialInstance, jint op, jint face) {
    ((MaterialInstance*) nativeMaterialInstance)->setStencilOpDepthStencilPass((MaterialInstance::StencilOperation) op, (MaterialInstance::StencilFace) face);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetStencilReference(JNIEnv*, jclass, jlong nativeMaterialInstance, jint value, jint face) {
    ((MaterialInstance*) nativeMaterialInstance)->setStencilReferenceValue(value, (MaterialInstance::StencilFace) face);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetStencilReadMask(JNIEnv*, jclass, jlong nativeMaterialInstance, jint mask, jint face) {
    ((MaterialInstance*) nativeMaterialInstance)->setStencilReadMask(mask, (MaterialInstance::StencilFace) face);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nSetStencilWriteMask(JNIEnv*, jclass, jlong nativeMaterialInstance, jint mask, jint face) {
    ((MaterialInstance*) nativeMaterialInstance)->setStencilWriteMask(mask, (MaterialInstance::StencilFace) face);
}

JNIEXPORT jstring JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetName(JNIEnv* env, jclass, jlong nativeInstance) {
    return env->NewStringUTF(((MaterialInstance*) nativeInstance)->getName());
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nGetMaterial(JNIEnv*, jclass, jlong nativeInstance) {
    return (jlong) ((MaterialInstance*) nativeInstance)->getMaterial();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_MaterialInstance_nDuplicate(JNIEnv* env, jclass, jlong nativeInstance, jstring name_) {
    const char* name = name_ ? env->GetStringUTFChars(name_, 0) : nullptr;
    jlong dup = (jlong) MaterialInstance::duplicate((MaterialInstance*) nativeInstance, name);
    if (name) env->ReleaseStringUTFChars(name_, name);
    return dup;
}

} // extern "C"

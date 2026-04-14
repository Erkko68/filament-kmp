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
#include <filament/Material.h>
#include <filament/Engine.h>
#include <filament/TextureSampler.h>

#include "common/NioUtils.h"
#include "common/CallbackUtils.h"

using namespace filament;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Material_nBuilderBuild(JNIEnv* env, jclass, jlong nativeEngine, jobject buffer_, jsize size, jint shBandCount, jint shadowQuality, jint uboBatchingMode) {
    Engine* engine = (Engine*) nativeEngine;
    AutoBuffer buffer(env, buffer_, size);
    auto builder = Material::Builder();
    if (shBandCount) {
        builder.sphericalHarmonicsBandCount(shBandCount);
    }
    builder.shadowSamplingQuality((Material::Builder::ShadowSamplingQuality) shadowQuality);
    builder.uboBatching((Material::UboBatchingMode) uboBatchingMode);
    return (jlong) builder.package(buffer.getData(), buffer.getSize()).build(*engine);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Material_nGetDefaultInstance(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jlong) ((Material*) nativeMaterial)->getDefaultInstance();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Material_nCreateInstance(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jlong) ((Material*) nativeMaterial)->createInstance();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Material_nCreateInstanceWithName(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_) {
    const char* name = env->GetStringUTFChars(name_, 0);
    jlong instance = (jlong) ((Material*) nativeMaterial)->createInstance(name);
    env->ReleaseStringUTFChars(name_, name);
    return instance;
}

JNIEXPORT jstring JNICALL
Java_io_github_erkko68_filament_Material_nGetName(JNIEnv* env, jclass, jlong nativeMaterial) {
    return env->NewStringUTF(((Material*) nativeMaterial)->getName());
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetShading(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getShading();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetInterpolation(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getInterpolation();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetBlendingMode(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getBlendingMode();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetTransparencyMode(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getTransparencyMode();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetRefractionMode(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getRefractionMode();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetRefractionType(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getRefractionType();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetReflectionMode(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getReflectionMode();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetFeatureLevel(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getFeatureLevel();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetVertexDomain(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getVertexDomain();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetCullingMode(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getCullingMode();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Material_nIsColorWriteEnabled(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jboolean) ((Material*) nativeMaterial)->isColorWriteEnabled();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Material_nIsDepthWriteEnabled(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jboolean) ((Material*) nativeMaterial)->isDepthWriteEnabled();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Material_nIsDepthCullingEnabled(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jboolean) ((Material*) nativeMaterial)->isDepthCullingEnabled();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Material_nIsDoubleSided(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jboolean) ((Material*) nativeMaterial)->isDoubleSided();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Material_nIsAlphaToCoverageEnabled(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jboolean) ((Material*) nativeMaterial)->isAlphaToCoverageEnabled();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Material_nGetMaskThreshold(JNIEnv*, jclass, jlong nativeMaterial) {
    return ((Material*) nativeMaterial)->getMaskThreshold();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Material_nGetSpecularAntiAliasingVariance(JNIEnv*, jclass, jlong nativeMaterial) {
    return ((Material*) nativeMaterial)->getSpecularAntiAliasingVariance();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Material_nGetSpecularAntiAliasingThreshold(JNIEnv*, jclass, jlong nativeMaterial) {
    return ((Material*) nativeMaterial)->getSpecularAntiAliasingThreshold();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetRequiredAttributes(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getRequiredAttributes().getValue();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Material_nHasParameter(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_) {
    const char* name = env->GetStringUTFChars(name_, 0);
    bool has = ((Material*) nativeMaterial)->hasParameter(name);
    env->ReleaseStringUTFChars(name_, name);
    return (jboolean) has;
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Material_nGetParameterCount(JNIEnv*, jclass, jlong nativeMaterial) {
    return (jint) ((Material*) nativeMaterial)->getParameterCount();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nGetParameters(JNIEnv* env, jclass, jlong nativeMaterial, jobject parameters) {
    Material* material = (Material*) nativeMaterial;
    size_t count = material->getParameterCount();
    Material::ParameterInfo* info = new Material::ParameterInfo[count];
    material->getParameters(info, count);

    jclass parameterClass = env->FindClass("io/github/erkko68/filament/Material$Parameter");
    jmethodID parameterAdd = env->GetStaticMethodID(parameterClass, "add", "(Ljava/util/List;Ljava/lang/String;III)V");

    for (size_t i = 0; i < count; i++) {
        jint type;
        if (info[i].isSampler) {
            type = (jint) info[i].samplerType + 18; // 18 is Type.SAMPLER_2D ordinal (see Parameter.Type)
        } else if (info[i].isSubpass) {
            type = 23; // 23 is Type.SUBPASS_INPUT
        } else {
            type = (jint) info[i].type;
        }

        env->CallStaticVoidMethod(parameterClass, parameterAdd, parameters, env->NewStringUTF(info[i].name), type, (jint) info[i].precision, (jint) info[i].count);
    }
    delete[] info;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nCompile(JNIEnv* env, jclass, jlong nativeMaterial, jint priority, jint variants, jobject handler, jobject runnable) {
    Material* material = (Material*) nativeMaterial;
    JniCallback* jniCallback = JniCallback::make(env, handler, runnable);
    material->compile((Material::CompilerPriorityQueue) priority, (UserVariantFilterBit) variants, jniCallback->getHandler(), [jniCallback](Material*){
        JniCallback::postToJavaAndDestroy(jniCallback);
    });
}

JNIEXPORT jstring JNICALL
Java_io_github_erkko68_filament_Material_nGetParameterTransformName(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_) {
    const char* name = env->GetStringUTFChars(name_, 0);
    jstring transformName = env->NewStringUTF(((Material*) nativeMaterial)->getParameterTransformName(name));
    env->ReleaseStringUTFChars(name_, name);
    return transformName;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterBool(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jboolean x) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, (bool) x);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterFloat(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jfloat x) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, x);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterInt(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jint x) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, (int32_t) x);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterBool2(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jboolean x, jboolean y) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::bool2{x, y});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterFloat2(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jfloat x, jfloat y) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::float2{x, y});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterInt2(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jint x, jint y) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::int2{x, y});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterBool3(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jboolean x, jboolean y, jboolean z) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::bool3{x, y, z});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterFloat3(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jfloat x, jfloat y, jfloat z) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::float3{x, y, z});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterInt3(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jint x, jint y, jint z) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::int3{x, y, z});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterBool4(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jboolean x, jboolean y, jboolean z, jboolean w) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::bool4{x, y, z, w});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterFloat4(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jfloat x, jfloat y, jfloat z, jfloat w) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::float4{x, y, z, w});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterInt4(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jint x, jint y, jint z, jint w) {
    const char* name = env->GetStringUTFChars(name_, 0);
    ((Material*) nativeMaterial)->setDefaultParameter(name, filament::math::int4{x, y, z, w});
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultBooleanParameterArray(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jint type, jbooleanArray v, jint offset, jint count) {
    const char* name = env->GetStringUTFChars(name_, 0);
    jboolean* values = env->GetBooleanArrayElements(v, NULL);
    ((Material*) nativeMaterial)->getDefaultInstance()->setParameter(name, (const bool*) (values + offset), (size_t) count);
    env->ReleaseBooleanArrayElements(v, values, JNI_ABORT);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultIntParameterArray(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jint type, jintArray v, jint offset, jint count) {
    const char* name = env->GetStringUTFChars(name_, 0);
    jint* values = env->GetIntArrayElements(v, NULL);
    ((Material*) nativeMaterial)->getDefaultInstance()->setParameter(name, (const int32_t*) (values + offset), (size_t) count);
    env->ReleaseIntArrayElements(v, values, JNI_ABORT);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultFloatParameterArray(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jint type, jfloatArray v, jint offset, jint count) {
    const char* name = env->GetStringUTFChars(name_, 0);
    jfloat* values = env->GetFloatArrayElements(v, NULL);
    ((Material*) nativeMaterial)->getDefaultInstance()->setParameter(name, (const float*) (values + offset), (size_t) count);
    env->ReleaseFloatArrayElements(v, values, JNI_ABORT);
    env->ReleaseStringUTFChars(name_, name);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Material_nSetDefaultParameterTexture(JNIEnv* env, jclass, jlong nativeMaterial, jstring name_, jlong nativeTexture, jint sampler) {
    const char* name = env->GetStringUTFChars(name_, 0);
    backend::SamplerParams params;
    *reinterpret_cast<uint32_t*>(&params) = (uint32_t) sampler;
    ((Material*) nativeMaterial)->setDefaultParameter(name, (Texture*) nativeTexture, TextureSampler(params));
    env->ReleaseStringUTFChars(name_, name);
}

} // extern "C"

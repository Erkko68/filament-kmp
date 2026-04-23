#include <jni.h>
#include <gltfio/MaterialProvider.h>
#include "MaterialKey.h"
#include <algorithm>

using namespace filament::gltfio;

MaterialKeyHelper& MaterialKeyHelper::get() {
    static MaterialKeyHelper helper;
    return helper;
}

void MaterialKeyHelper::init(JNIEnv* env) {
    const jclass materialKeyClass = env->FindClass(JAVA_MATERIAL_KEY);
    auto field = [materialKeyClass, env](const char* fieldName, const char* signature) {
        return env->GetFieldID(materialKeyClass, fieldName, signature);
    };
    doubleSided = field("doubleSided", "Z");
    unlit = field("unlit", "Z");
    hasVertexColors = field("hasVertexColors", "Z");
    hasBaseColorTexture = field("hasBaseColorTexture", "Z");
    hasNormalTexture = field("hasNormalTexture", "Z");
    hasOcclusionTexture = field("hasOcclusionTexture", "Z");
    hasEmissiveTexture = field("hasEmissiveTexture", "Z");
    useSpecularGlossiness = field("useSpecularGlossiness", "Z");
    alphaMode = field("alphaMode", "I");
    enableDiagnostics = field("enableDiagnostics", "Z");
    hasMetallicRoughnessTexture = field("hasMetallicRoughnessTexture", "Z");
    metallicRoughnessUV = field("metallicRoughnessUV", "I");
    baseColorUV = field("baseColorUV", "I");
    hasClearCoatTexture = field("hasClearCoatTexture", "Z");
    clearCoatUV = field("clearCoatUV", "I");
    hasClearCoatRoughnessTexture = field("hasClearCoatRoughnessTexture", "Z");
    clearCoatRoughnessUV = field("clearCoatRoughnessUV", "I");
    hasClearCoatNormalTexture = field("hasClearCoatNormalTexture", "Z");
    clearCoatNormalUV = field("clearCoatNormalUV", "I");
    hasClearCoat = field("hasClearCoat", "Z");
    hasTransmission = field("hasTransmission", "Z");
    hasTextureTransforms = field("hasTextureTransforms", "Z");
    emissiveUV = field("emissiveUV", "I");
    aoUV = field("aoUV", "I");
    normalUV = field("normalUV", "I");
    hasTransmissionTexture = field("hasTransmissionTexture", "Z");
    transmissionUV = field("transmissionUV", "I");
    hasSheenColorTexture = field("hasSheenColorTexture", "Z");
    sheenColorUV = field("sheenColorUV", "I");
    hasSheenRoughnessTexture = field("hasSheenRoughnessTexture", "Z");
    sheenRoughnessUV = field("sheenRoughnessUV", "I");
    hasVolumeThicknessTexture = field("hasVolumeThicknessTexture", "Z");
    volumeThicknessUV = field("volumeThicknessUV", "I");
    hasSheen = field("hasSheen", "Z");
    hasIOR = field("hasIOR", "Z");
    hasVolume = field("hasVolume", "Z");
    hasDispersion = field("hasDispersion", "Z");
    hasSpecular = field("hasSpecular", "Z");
    hasSpecularTexture = field("hasSpecularTexture", "Z");
    hasSpecularColorTexture = field("hasSpecularColorTexture", "Z");
    specularTextureUV = field("specularTextureUV", "I");
    specularColorTextureUV = field("specularColorTextureUV", "I");
}

void MaterialKeyHelper::copy(JNIEnv* env, MaterialKey& dst, jobject src) {
    dst.doubleSided = env->GetBooleanField(src, doubleSided);
    dst.unlit = env->GetBooleanField(src, unlit);
    dst.hasVertexColors = env->GetBooleanField(src, hasVertexColors);
    dst.hasBaseColorTexture = env->GetBooleanField(src, hasBaseColorTexture);
    dst.hasNormalTexture = env->GetBooleanField(src, hasNormalTexture);
    dst.hasOcclusionTexture = env->GetBooleanField(src, hasOcclusionTexture);
    dst.hasEmissiveTexture = env->GetBooleanField(src, hasEmissiveTexture);
    dst.useSpecularGlossiness = env->GetBooleanField(src, useSpecularGlossiness);
    dst.alphaMode = (AlphaMode) env->GetIntField(src, alphaMode);
    dst.enableDiagnostics = env->GetBooleanField(src, enableDiagnostics);
    dst.hasMetallicRoughnessTexture = env->GetBooleanField(src, hasMetallicRoughnessTexture);
    dst.metallicRoughnessUV = (uint8_t) env->GetIntField(src, metallicRoughnessUV);
    dst.baseColorUV = (uint8_t) env->GetIntField(src, baseColorUV);
    dst.hasClearCoatTexture = env->GetBooleanField(src, hasClearCoatTexture);
    dst.clearCoatUV = (uint8_t) env->GetIntField(src, clearCoatUV);
    dst.hasClearCoatRoughnessTexture = env->GetBooleanField(src, hasClearCoatRoughnessTexture);
    dst.clearCoatRoughnessUV = (uint8_t) env->GetIntField(src, clearCoatRoughnessUV);
    dst.hasClearCoatNormalTexture = env->GetBooleanField(src, hasClearCoatNormalTexture);
    dst.clearCoatNormalUV = (uint8_t) env->GetIntField(src, clearCoatNormalUV);
    dst.hasClearCoat = env->GetBooleanField(src, hasClearCoat);
    dst.hasTransmission = env->GetBooleanField(src, hasTransmission);
    dst.hasTextureTransforms = env->GetBooleanField(src, hasTextureTransforms);
    dst.emissiveUV = (uint8_t) env->GetIntField(src, emissiveUV);
    dst.aoUV = (uint8_t) env->GetIntField(src, aoUV);
    dst.normalUV = (uint8_t) env->GetIntField(src, normalUV);
    dst.hasTransmissionTexture = env->GetBooleanField(src, hasTransmissionTexture);
    dst.transmissionUV = (uint8_t) env->GetIntField(src, transmissionUV);
    dst.hasSheenColorTexture = env->GetBooleanField(src, hasSheenColorTexture);
    dst.sheenColorUV = (uint8_t) env->GetIntField(src, sheenColorUV);
    dst.hasSheenRoughnessTexture = env->GetBooleanField(src, hasSheenRoughnessTexture);
    dst.sheenRoughnessUV = (uint8_t) env->GetIntField(src, sheenRoughnessUV);
    dst.hasVolumeThicknessTexture = env->GetBooleanField(src, hasVolumeThicknessTexture);
    dst.volumeThicknessUV = (uint8_t) env->GetIntField(src, volumeThicknessUV);
    dst.hasSheen = env->GetBooleanField(src, hasSheen);
    dst.hasIOR = env->GetBooleanField(src, hasIOR);
    dst.hasVolume = env->GetBooleanField(src, hasVolume);
    dst.hasDispersion = env->GetBooleanField(src, hasDispersion);
    dst.hasSpecular = env->GetBooleanField(src, hasSpecular);
    dst.hasSpecularTexture = env->GetBooleanField(src, hasSpecularTexture);
    dst.hasSpecularColorTexture = env->GetBooleanField(src, hasSpecularColorTexture);
    dst.specularTextureUV = (uint8_t) env->GetIntField(src, specularTextureUV);
    dst.specularColorTextureUV = (uint8_t) env->GetIntField(src, specularColorTextureUV);
}

void MaterialKeyHelper::copy(JNIEnv* env, jobject dst, const MaterialKey& src) {
    env->SetBooleanField(dst, doubleSided, src.doubleSided);
    env->SetBooleanField(dst, unlit, src.unlit);
    env->SetBooleanField(dst, hasVertexColors, src.hasVertexColors);
    env->SetBooleanField(dst, hasBaseColorTexture, src.hasBaseColorTexture);
    env->SetBooleanField(dst, hasNormalTexture, src.hasNormalTexture);
    env->SetBooleanField(dst, hasOcclusionTexture, src.hasOcclusionTexture);
    env->SetBooleanField(dst, hasEmissiveTexture, src.hasEmissiveTexture);
    env->SetBooleanField(dst, useSpecularGlossiness, src.useSpecularGlossiness);
    env->SetIntField(dst, alphaMode, (int) src.alphaMode);
    env->SetBooleanField(dst, enableDiagnostics, src.enableDiagnostics);
    env->SetBooleanField(dst, hasMetallicRoughnessTexture, src.hasMetallicRoughnessTexture);
    env->SetIntField(dst, metallicRoughnessUV, src.metallicRoughnessUV);
    env->SetIntField(dst, baseColorUV, src.baseColorUV);
    env->SetBooleanField(dst, hasClearCoatTexture, src.hasClearCoatTexture);
    env->SetIntField(dst, clearCoatUV, src.clearCoatUV);
    env->SetBooleanField(dst, hasClearCoatRoughnessTexture, src.hasClearCoatRoughnessTexture);
    env->SetIntField(dst, clearCoatRoughnessUV, src.clearCoatRoughnessUV);
    env->SetBooleanField(dst, hasClearCoatNormalTexture, src.hasClearCoatNormalTexture);
    env->SetIntField(dst, clearCoatNormalUV, src.clearCoatNormalUV);
    env->SetBooleanField(dst, hasClearCoat, src.hasClearCoat);
    env->SetBooleanField(dst, hasTransmission, src.hasTransmission);
    env->SetBooleanField(dst, hasTextureTransforms, src.hasTextureTransforms);
    env->SetIntField(dst, emissiveUV, src.emissiveUV);
    env->SetIntField(dst, aoUV, src.aoUV);
    env->SetIntField(dst, normalUV, src.normalUV);
    env->SetBooleanField(dst, hasTransmissionTexture, src.hasTransmissionTexture);
    env->SetIntField(dst, transmissionUV, src.transmissionUV);
    env->SetBooleanField(dst, hasSheenColorTexture, src.hasSheenColorTexture);
    env->SetIntField(dst, sheenColorUV, src.sheenColorUV);
    env->SetBooleanField(dst, hasSheenRoughnessTexture, src.hasSheenRoughnessTexture);
    env->SetIntField(dst, sheenRoughnessUV, src.sheenRoughnessUV);
    env->SetBooleanField(dst, hasVolumeThicknessTexture, src.hasVolumeThicknessTexture);
    env->SetIntField(dst, volumeThicknessUV, src.volumeThicknessUV);
    env->SetBooleanField(dst, hasSheen, src.hasSheen);
    env->SetBooleanField(dst, hasIOR, src.hasIOR);
    env->SetBooleanField(dst, hasVolume, src.hasVolume);
    env->SetBooleanField(dst, hasDispersion, src.hasDispersion);
    env->SetBooleanField(dst, hasSpecular, src.hasSpecular);
    env->SetBooleanField(dst, hasSpecularTexture, src.hasSpecularTexture);
    env->SetBooleanField(dst, hasSpecularColorTexture, src.hasSpecularColorTexture);
    env->SetIntField(dst, specularTextureUV, src.specularTextureUV);
    env->SetIntField(dst, specularColorTextureUV, src.specularColorTextureUV);
}


extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_MaterialProvider_00024MaterialKey_nGlobalInit(JNIEnv* env, jclass) {
    MaterialKeyHelper::get().init(env);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_MaterialProvider_00024MaterialKey_nConstrainMaterial(JNIEnv* env, jclass, jobject config, jintArray uvmap) {
    MaterialKey nativeKey = {};
    MaterialKeyHelper::get().copy(env, nativeKey, config);
    jint* uvmapData = env->GetIntArrayElements(uvmap, nullptr);
    constrainMaterial(&nativeKey, (UvMap*) uvmapData);
    env->ReleaseIntArrayElements(uvmap, uvmapData, 0);
    MaterialKeyHelper::get().copy(env, config, nativeKey);
}

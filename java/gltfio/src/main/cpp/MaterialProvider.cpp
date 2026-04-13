#include <jni.h>
#include <gltfio/MaterialProvider.h>
#include <filament/MaterialInstance.h>
#include <filament/Material.h>
#include <filament/Engine.h>
#include <vector>

using namespace filament;
using namespace gltfio;

static void mapMaterialKey(JNIEnv* env, jobject config, MaterialKey& nativeKey) {
    jclass cls = env->GetObjectClass(config);
    nativeKey.doubleSided = env->GetBooleanField(config, env->GetFieldID(cls, "doubleSided", "Z"));
    nativeKey.unlit = env->GetBooleanField(config, env->GetFieldID(cls, "unlit", "Z"));
    nativeKey.hasVertexColors = env->GetBooleanField(config, env->GetFieldID(cls, "hasVertexColors", "Z"));
    nativeKey.hasBaseColorTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasBaseColorTexture", "Z"));
    nativeKey.hasNormalTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasNormalTexture", "Z"));
    nativeKey.hasOcclusionTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasOcclusionTexture", "Z"));
    nativeKey.hasEmissiveTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasEmissiveTexture", "Z"));
    nativeKey.useSpecularGlossiness = env->GetBooleanField(config, env->GetFieldID(cls, "useSpecularGlossiness", "Z"));
    nativeKey.alphaMode = (AlphaMode) env->GetIntField(config, env->GetFieldID(cls, "alphaMode", "I"));
    nativeKey.enableDiagnostics = env->GetBooleanField(config, env->GetFieldID(cls, "enableDiagnostics", "Z"));
    nativeKey.hasMetallicRoughnessTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasMetallicRoughnessTexture", "Z"));
    nativeKey.metallicRoughnessUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "metallicRoughnessUV", "I"));
    nativeKey.baseColorUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "baseColorUV", "I"));
    nativeKey.hasClearCoatTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasClearCoatTexture", "Z"));
    nativeKey.clearCoatUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "clearCoatUV", "I"));
    nativeKey.hasClearCoatRoughnessTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasClearCoatRoughnessTexture", "Z"));
    nativeKey.clearCoatRoughnessUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "clearCoatRoughnessUV", "I"));
    nativeKey.hasClearCoatNormalTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasClearCoatNormalTexture", "Z"));
    nativeKey.clearCoatNormalUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "clearCoatNormalUV", "I"));
    nativeKey.hasClearCoat = env->GetBooleanField(config, env->GetFieldID(cls, "hasClearCoat", "Z"));
    nativeKey.hasTransmission = env->GetBooleanField(config, env->GetFieldID(cls, "hasTransmission", "Z"));
    nativeKey.hasTextureTransforms = env->GetBooleanField(config, env->GetFieldID(cls, "hasTextureTransforms", "Z"));
    nativeKey.emissiveUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "emissiveUV", "I"));
    nativeKey.aoUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "aoUV", "I"));
    nativeKey.normalUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "normalUV", "I"));
    nativeKey.hasTransmissionTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasTransmissionTexture", "Z"));
    nativeKey.transmissionUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "transmissionUV", "I"));
    nativeKey.hasSheenColorTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasSheenColorTexture", "Z"));
    nativeKey.sheenColorUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "sheenColorUV", "I"));
    nativeKey.hasSheenRoughnessTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasSheenRoughnessTexture", "Z"));
    nativeKey.sheenRoughnessUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "sheenRoughnessUV", "I"));
    nativeKey.hasVolumeThicknessTexture = env->GetBooleanField(config, env->GetFieldID(cls, "hasVolumeThicknessTexture", "Z"));
    nativeKey.volumeThicknessUV = (uint8_t) env->GetIntField(config, env->GetFieldID(cls, "volumeThicknessUV", "I"));
    nativeKey.hasSheen = env->GetBooleanField(config, env->GetFieldID(cls, "hasSheen", "Z"));
    nativeKey.hasIOR = env->GetBooleanField(config, env->GetFieldID(cls, "hasIOR", "Z"));
    nativeKey.padding = 0;
    nativeKey.padding2 = 0;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nCreateUbershaderProvider(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) createUbershaderProvider((Engine*) nativeEngine, nullptr, 0);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_JitShaderProvider_nCreateJitShaderProvider(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) createJitShaderProvider((Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nDestroy(JNIEnv* env, jclass, jlong nativeProvider) {
    delete (MaterialProvider*) nativeProvider;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_JitShaderProvider_nDestroyJitShaderProvider(JNIEnv* env, jclass, jlong nativeProvider) {
    delete (MaterialProvider*) nativeProvider;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nCreateMaterialInstance(JNIEnv* env, jclass, jlong nativeProvider, jobject config, jintArray uvmap, jstring label, jstring extras) {
    MaterialProvider* provider = (MaterialProvider*) nativeProvider;
    MaterialKey nativeKey = {};
    mapMaterialKey(env, config, nativeKey);
    
    const char* nativeLabel = label ? env->GetStringUTFChars(label, nullptr) : nullptr;
    const char* nativeExtras = extras ? env->GetStringUTFChars(extras, nullptr) : nullptr;
    
    jint* uvmapData = env->GetIntArrayElements(uvmap, nullptr);
    
    MaterialInstance* instance = provider->createMaterialInstance(&nativeKey, (UvMap*) uvmapData, nativeLabel, nativeExtras);
    
    env->ReleaseIntArrayElements(uvmap, uvmapData, 0);
    if (nativeLabel) env->ReleaseStringUTFChars(label, nativeLabel);
    if (nativeExtras) env->ReleaseStringUTFChars(extras, nativeExtras);
    
    return (jlong) instance;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_JitShaderProvider_nCreateMaterialInstance(JNIEnv* env, jclass, jlong nativeProvider, jobject config, jintArray uvmap, jstring label, jstring extras) {
    return Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nCreateMaterialInstance(env, nullptr, nativeProvider, config, uvmap, label, extras);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nGetMaterial(JNIEnv* env, jclass, jlong nativeProvider, jobject config, jintArray uvmap, jstring label) {
    MaterialProvider* provider = (MaterialProvider*) nativeProvider;
    MaterialKey nativeKey = {};
    mapMaterialKey(env, config, nativeKey);
    const char* nativeLabel = label ? env->GetStringUTFChars(label, nullptr) : nullptr;
    jint* uvmapData = env->GetIntArrayElements(uvmap, nullptr);
    
    Material* material = provider->getMaterial(&nativeKey, (UvMap*) uvmapData, nativeLabel);
    
    env->ReleaseIntArrayElements(uvmap, uvmapData, 0);
    if (nativeLabel) env->ReleaseStringUTFChars(label, nativeLabel);
    
    return (jlong) material;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_JitShaderProvider_nGetMaterial(JNIEnv* env, jclass, jlong nativeProvider, jobject config, jintArray uvmap, jstring label) {
    return Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nGetMaterial(env, nullptr, nativeProvider, config, uvmap, label);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nGetMaterialCount(JNIEnv* env, jclass, jlong nativeProvider) {
    return (jint) ((MaterialProvider*) nativeProvider)->getMaterialsCount();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_JitShaderProvider_nGetMaterialCount(JNIEnv* env, jclass, jlong nativeProvider) {
    return (jint) ((MaterialProvider*) nativeProvider)->getMaterialsCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nGetMaterials(JNIEnv* env, jclass, jlong nativeProvider, jlongArray result) {
    MaterialProvider* provider = (MaterialProvider*) nativeProvider;
    size_t count = provider->getMaterialsCount();
    const Material* const* materials = provider->getMaterials();
    
    jlong* data = env->GetLongArrayElements(result, nullptr);
    for (size_t i = 0; i < count; i++) {
        data[i] = (jlong) materials[i];
    }
    env->ReleaseLongArrayElements(result, data, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_JitShaderProvider_nGetMaterials(JNIEnv* env, jclass, jlong nativeProvider, jlongArray result) {
    Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nGetMaterials(env, nullptr, nativeProvider, result);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nNeedsDummyData(JNIEnv* env, jclass, jlong nativeProvider, jint attrib) {
    return (jboolean) ((MaterialProvider*) nativeProvider)->needsDummyData((VertexAttribute) attrib);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_JitShaderProvider_nNeedsDummyData(JNIEnv* env, jclass, jlong nativeProvider, jint attrib) {
    return (jboolean) ((MaterialProvider*) nativeProvider)->needsDummyData((VertexAttribute) attrib);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_UbershaderProvider_nDestroyMaterials(JNIEnv* env, jclass, jlong nativeProvider) {
    ((MaterialProvider*) nativeProvider)->destroyMaterials();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_JitShaderProvider_nDestroyMaterials(JNIEnv* env, jclass, jlong nativeProvider) {
    ((MaterialProvider*) nativeProvider)->destroyMaterials();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_MaterialProvider_00024MaterialKey_nConstrainMaterial(JNIEnv* env, jclass, jobject key, jintArray uvmap) {
    MaterialKey nativeKey = {};
    mapMaterialKey(env, key, nativeKey);
    jint* uvmapData = env->GetIntArrayElements(uvmap, nullptr);
    constrainMaterial(&nativeKey, (UvMap*) uvmapData);
    env->ReleaseIntArrayElements(uvmap, uvmapData, 0);
}

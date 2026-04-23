#pragma once

#include <jni.h>
#include <gltfio/MaterialProvider.h>

#define JAVA_MATERIAL_KEY "io/github/erkko68/filament/gltfio/jni/MaterialProvider$MaterialKey"

class MaterialKeyHelper {
public:
    static MaterialKeyHelper& get();
    void init(JNIEnv* env);
    void copy(JNIEnv* env, filament::gltfio::MaterialKey& dst, jobject src);
    void copy(JNIEnv* env, jobject dst, const filament::gltfio::MaterialKey& src);

private:
    jfieldID doubleSided, unlit, hasVertexColors, hasBaseColorTexture, hasNormalTexture,
             hasOcclusionTexture, hasEmissiveTexture, useSpecularGlossiness, alphaMode,
             enableDiagnostics, hasMetallicRoughnessTexture, metallicRoughnessUV, baseColorUV,
             hasClearCoatTexture, clearCoatUV, hasClearCoatRoughnessTexture, clearCoatRoughnessUV,
             hasClearCoatNormalTexture, clearCoatNormalUV, hasClearCoat, hasTransmission,
             hasTextureTransforms, emissiveUV, aoUV, normalUV, hasTransmissionTexture,
             transmissionUV, hasSheenColorTexture, sheenColorUV, hasSheenRoughnessTexture,
             sheenRoughnessUV, hasVolumeThicknessTexture, volumeThicknessUV, hasSheen, hasIOR,
             hasVolume, hasDispersion, hasSpecular, hasSpecularTexture, hasSpecularColorTexture,
             specularTextureUV, specularColorTextureUV;
};


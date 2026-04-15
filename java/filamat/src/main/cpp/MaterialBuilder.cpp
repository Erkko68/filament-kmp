#include <jni.h>
#include <filamat/MaterialBuilder.h>
#include <filament/Engine.h>
#include <utils/JobSystem.h>
#include <backend/DriverEnums.h>

using namespace filamat;
using namespace filament;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderInit(JNIEnv*, jclass) {
    MaterialBuilder::init();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderShutdown(JNIEnv*, jclass) {
    MaterialBuilder::shutdown();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nCreateMaterialBuilder(JNIEnv*, jclass) {
    return (jlong) new MaterialBuilder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nDestroyMaterialBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (MaterialBuilder*) nativeBuilder;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    MaterialBuilder* builder = (MaterialBuilder*) nativeBuilder;
    utils::JobSystem* jobSystem = nullptr;
    if (nativeEngine != 0) {
        Engine* engine = (Engine*) nativeEngine;
        jobSystem = &engine->getJobSystem();
    }

    bool ownJobSystem = false;
    if (jobSystem == nullptr) {
        jobSystem = new utils::JobSystem;
        jobSystem->adopt();
        ownJobSystem = true;
    }

    Package* pkg = new Package(builder->build(*jobSystem));

    if (ownJobSystem) {
        jobSystem->emancipate();
        delete jobSystem;
    }

    return (jlong) pkg;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nGetPackageBytes(JNIEnv* env, jclass, jlong nativePackage) {
    Package* pkg = (Package*) nativePackage;
    jbyteArray result = env->NewByteArray(pkg->getSize());
    env->SetByteArrayRegion(result, 0, pkg->getSize(), (const jbyte*) pkg->getData());
    return result;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nGetPackageIsValid(JNIEnv* env, jclass, jlong nativePackage) {
    return (jboolean) ((Package*) nativePackage)->isValid();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nDestroyPackage(JNIEnv* env, jclass, jlong nativePackage) {
    delete (Package*) nativePackage;
}

// Configuration Methods

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderName(JNIEnv* env, jclass, jlong nativeBuilder, jstring name) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialBuilder*) nativeBuilder)->name(nativeName);
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderShading(JNIEnv*, jclass, jlong nativeBuilder, jint shading) {
    ((MaterialBuilder*) nativeBuilder)->shading((MaterialBuilder::Shading) shading);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderInterpolation(JNIEnv*, jclass, jlong nativeBuilder, jint interpolation) {
    ((MaterialBuilder*) nativeBuilder)->interpolation((MaterialBuilder::Interpolation) interpolation);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderBlending(JNIEnv*, jclass, jlong nativeBuilder, jint mode) {
    ((MaterialBuilder*) nativeBuilder)->blending((MaterialBuilder::BlendingMode) mode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderPostLightingBlending(JNIEnv*, jclass, jlong nativeBuilder, jint mode) {
    ((MaterialBuilder*) nativeBuilder)->postLightingBlending((MaterialBuilder::BlendingMode) mode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderMaterialDomain(JNIEnv*, jclass, jlong nativeBuilder, jint domain) {
    ((MaterialBuilder*) nativeBuilder)->materialDomain((MaterialBuilder::MaterialDomain) domain);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderVertexDomain(JNIEnv*, jclass, jlong nativeBuilder, jint domain) {
    ((MaterialBuilder*) nativeBuilder)->vertexDomain((MaterialBuilder::VertexDomain) domain);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderMaterial(JNIEnv* env, jclass, jlong nativeBuilder, jstring code) {
    const char* nativeCode = env->GetStringUTFChars(code, nullptr);
    ((MaterialBuilder*) nativeBuilder)->material(nativeCode);
    env->ReleaseStringUTFChars(code, nativeCode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderMaterialVertex(JNIEnv* env, jclass, jlong nativeBuilder, jstring code) {
    const char* nativeCode = env->GetStringUTFChars(code, nullptr);
    ((MaterialBuilder*) nativeBuilder)->materialVertex(nativeCode);
    env->ReleaseStringUTFChars(code, nativeCode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderTargetApi(JNIEnv*, jclass, jlong nativeBuilder, jint api) {
    ((MaterialBuilder*) nativeBuilder)->targetApi((MaterialBuilder::TargetApi) api);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderOptimization(JNIEnv*, jclass, jlong nativeBuilder, jint optimization) {
    ((MaterialBuilder*) nativeBuilder)->optimization((MaterialBuilder::Optimization) optimization);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderPlatform(JNIEnv*, jclass, jlong nativeBuilder, jint platform) {
    ((MaterialBuilder*) nativeBuilder)->platform((MaterialBuilder::Platform) platform);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderUniformParameter(JNIEnv* env, jclass, jlong nativeBuilder, jint type, jint precision, jstring name) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialBuilder*) nativeBuilder)->parameter(nativeName, (MaterialBuilder::UniformType) type, (MaterialBuilder::Precision) precision);
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderUniformParameterArray(JNIEnv* env, jclass, jlong nativeBuilder, jint type, jint size, jint precision, jstring name) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialBuilder*) nativeBuilder)->parameter(nativeName, (size_t) size, (MaterialBuilder::UniformType) type, (MaterialBuilder::Precision) precision);
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderSamplerParameter(JNIEnv* env, jclass, jlong nativeBuilder, jint type, jint format, jint precision, jstring name) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialBuilder*) nativeBuilder)->parameter(nativeName, (MaterialBuilder::SamplerType) type, (MaterialBuilder::SamplerFormat) format, (MaterialBuilder::Precision) precision);
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderVariable(JNIEnv* env, jclass, jlong nativeBuilder, jint variable, jstring name) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialBuilder*) nativeBuilder)->variable((MaterialBuilder::Variable) variable, nativeName);
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderRequire(JNIEnv*, jclass, jlong nativeBuilder, jint attribute) {
    ((MaterialBuilder*) nativeBuilder)->require((MaterialBuilder::VertexAttribute) attribute);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderColorWrite(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->colorWrite((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderDepthWrite(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->depthWrite((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderDepthCulling(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->depthCulling((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderDoubleSided(JNIEnv*, jclass, jlong nativeBuilder, jboolean doubleSided) {
    ((MaterialBuilder*) nativeBuilder)->doubleSided((bool) doubleSided);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderCulling(JNIEnv*, jclass, jlong nativeBuilder, jint mode) {
    ((MaterialBuilder*) nativeBuilder)->culling((MaterialBuilder::CullingMode) mode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderTransparencyMode(JNIEnv*, jclass, jlong nativeBuilder, jint mode) {
    ((MaterialBuilder*) nativeBuilder)->transparencyMode((MaterialBuilder::TransparencyMode) mode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderMaskThreshold(JNIEnv*, jclass, jlong nativeBuilder, jfloat threshold) {
    ((MaterialBuilder*) nativeBuilder)->maskThreshold(threshold);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderAlphaToCoverage(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->alphaToCoverage((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderShadowMultiplier(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->shadowMultiplier((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderTransparentShadow(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->transparentShadow((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderSpecularAntiAliasing(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->specularAntiAliasing((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderSpecularAntiAliasingVariance(JNIEnv*, jclass, jlong nativeBuilder, jfloat variance) {
    ((MaterialBuilder*) nativeBuilder)->specularAntiAliasingVariance(variance);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderSpecularAntiAliasingThreshold(JNIEnv*, jclass, jlong nativeBuilder, jfloat threshold) {
    ((MaterialBuilder*) nativeBuilder)->specularAntiAliasingThreshold(threshold);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderRefractionMode(JNIEnv*, jclass, jlong nativeBuilder, jint mode) {
    ((MaterialBuilder*) nativeBuilder)->refractionMode((MaterialBuilder::RefractionMode) mode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderReflectionMode(JNIEnv*, jclass, jlong nativeBuilder, jint mode) {
    ((MaterialBuilder*) nativeBuilder)->reflectionMode((MaterialBuilder::ReflectionMode) mode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderRefractionType(JNIEnv*, jclass, jlong nativeBuilder, jint type) {
    ((MaterialBuilder*) nativeBuilder)->refractionType((MaterialBuilder::RefractionType) type);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderClearCoatIorChange(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->clearCoatIorChange((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderFlipUV(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->flipUV((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderCustomSurfaceShading(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->customSurfaceShading((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderMultiBounceAmbientOcclusion(JNIEnv*, jclass, jlong nativeBuilder, jboolean enable) {
    ((MaterialBuilder*) nativeBuilder)->multiBounceAmbientOcclusion((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderSpecularAmbientOcclusion(JNIEnv*, jclass, jlong nativeBuilder, jint sao) {
    ((MaterialBuilder*) nativeBuilder)->specularAmbientOcclusion((MaterialBuilder::SpecularAmbientOcclusion) sao);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderVariantFilter(JNIEnv*, jclass, jlong nativeBuilder, jint variantFilter) {
    ((MaterialBuilder*) nativeBuilder)->variantFilter((filament::UserVariantFilterMask) variantFilter);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_jni_MaterialBuilder_nMaterialBuilderUseLegacyMorphing(JNIEnv*, jclass, jlong nativeBuilder) {
    ((MaterialBuilder*) nativeBuilder)->useLegacyMorphing();
}

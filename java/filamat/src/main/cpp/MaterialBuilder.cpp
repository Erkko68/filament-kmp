#include <jni.h>
#include <filamat/MaterialBuilder.h>
#include <utils/JobSystem.h>

using namespace filamat;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nMaterialBuilderInit(JNIEnv*, jclass) {
    MaterialBuilder::init();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nMaterialBuilderShutdown(JNIEnv*, jclass) {
    MaterialBuilder::shutdown();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nCreateMaterialBuilder(JNIEnv*, jclass) {
    return (jlong) new MaterialBuilder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nDestroyMaterialBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (MaterialBuilder*) nativeBuilder;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder) {
    // For now we use a default JobSystem or just synchronous build
    // Filament's MaterialBuilder::build returns a MaterialPackage object (by value/move)
    MaterialPackage* pkg = new MaterialPackage(((MaterialBuilder*) nativeBuilder)->build());
    return (jlong) pkg;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nGetPackageBytes(JNIEnv* env, jclass, jlong nativePackage) {
    MaterialPackage* pkg = (MaterialPackage*) nativePackage;
    jbyteArray result = env->NewByteArray(pkg->getDataSize());
    env->SetByteArrayRegion(result, 0, pkg->getDataSize(), (const jbyte*) pkg->getData());
    return result;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nGetPackageIsValid(JNIEnv* env, jclass, jlong nativePackage) {
    return (jboolean) ((MaterialPackage*) nativePackage)->isValid();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nDestroyPackage(JNIEnv* env, jclass, jlong nativePackage) {
    delete (MaterialPackage*) nativePackage;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nMaterialBuilderName(JNIEnv* env, jclass, jlong nativeBuilder, jstring name) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialBuilder*) nativeBuilder)->name(nativeName);
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nMaterialBuilderShading(JNIEnv*, jclass, jlong nativeBuilder, jint shading) {
    ((MaterialBuilder*) nativeBuilder)->shading((MaterialBuilder::Shading) shading);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nMaterialBuilderBlending(JNIEnv*, jclass, jlong nativeBuilder, jint mode) {
    ((MaterialBuilder*) nativeBuilder)->blending((MaterialBuilder::BlendingMode) mode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nMaterialBuilderMaterialDomain(JNIEnv*, jclass, jlong nativeBuilder, jint domain) {
    ((MaterialBuilder*) nativeBuilder)->materialDomain((MaterialBuilder::MaterialDomain) domain);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nMaterialBuilderMaterial(JNIEnv* env, jclass, jlong nativeBuilder, jstring code) {
    const char* nativeCode = env->GetStringUTFChars(code, nullptr);
    ((MaterialBuilder*) nativeBuilder)->material(nativeCode);
    env->ReleaseStringUTFChars(code, nativeCode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_filamat_MaterialBuilder_nMaterialBuilderTargetApi(JNIEnv*, jclass, jlong nativeBuilder, jint api) {
    ((MaterialBuilder*) nativeBuilder)->targetApi((MaterialBuilder::TargetApi) api);
}

#include <jni.h>
#include <filament/ToneMapper.h>

using namespace filament;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_ToneMapper_nDestroyToneMapper(JNIEnv* env, jclass, jlong nativeToneMapper) {
    delete (ToneMapper*) nativeToneMapper;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_ToneMapper_nCreateLinearToneMapper(JNIEnv* env, jclass) {
    return (jlong) new LinearToneMapper();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_ToneMapper_nCreateACESToneMapper(JNIEnv* env, jclass) {
    return (jlong) new ACESToneMapper();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_ToneMapper_nCreateACESLegacyToneMapper(JNIEnv* env, jclass) {
    return (jlong) new ACESLegacyToneMapper();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_ToneMapper_nCreateFilmicToneMapper(JNIEnv* env, jclass) {
    return (jlong) new FilmicToneMapper();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_ToneMapper_nCreatePBRNeutralToneMapper(JNIEnv* env, jclass) {
    return (jlong) new PBRNeutralToneMapper();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_ToneMapper_nCreateGT7ToneMapper(JNIEnv* env, jclass) {
    return (jlong) new GT7ToneMapper();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_ToneMapper_nCreateAgxToneMapper(JNIEnv* env, jclass, jint look) {
    return (jlong) new AgXToneMapper((AgXToneMapper::AgXLook) look);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_ToneMapper_nCreateGenericToneMapper(JNIEnv* env, jclass, jfloat contrast, jfloat midGrayIn, jfloat midGrayOut, jfloat hdrMax) {
    return (jlong) new GenericToneMapper(contrast, midGrayIn, midGrayOut, hdrMax);
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_ToneMapper_nGenericGetContrast(JNIEnv* env, jclass, jlong nativeObject) {
    return ((GenericToneMapper*) nativeObject)->getContrast();
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_ToneMapper_nGenericGetMidGrayIn(JNIEnv* env, jclass, jlong nativeObject) {
    return ((GenericToneMapper*) nativeObject)->getMidGrayIn();
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_ToneMapper_nGenericGetMidGrayOut(JNIEnv* env, jclass, jlong nativeObject) {
    return ((GenericToneMapper*) nativeObject)->getMidGrayOut();
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_ToneMapper_nGenericGetHdrMax(JNIEnv* env, jclass, jlong nativeObject) {
    return ((GenericToneMapper*) nativeObject)->getHdrMax();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_ToneMapper_nGenericSetContrast(JNIEnv* env, jclass, jlong nativeObject, jfloat contrast) {
    ((GenericToneMapper*) nativeObject)->setContrast(contrast);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_ToneMapper_nGenericSetMidGrayIn(JNIEnv* env, jclass, jlong nativeObject, jfloat midGrayIn) {
    ((GenericToneMapper*) nativeObject)->setMidGrayIn(midGrayIn);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_ToneMapper_nGenericSetMidGrayOut(JNIEnv* env, jclass, jlong nativeObject, jfloat midGrayOut) {
    ((GenericToneMapper*) nativeObject)->setMidGrayOut(midGrayOut);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_ToneMapper_nGenericSetHdrMax(JNIEnv* env, jclass, jlong nativeObject, jfloat hdrMax) {
    ((GenericToneMapper*) nativeObject)->setHdrMax(hdrMax);
}

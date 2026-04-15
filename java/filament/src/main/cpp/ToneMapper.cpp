#include <jni.h>
#include <filament/ToneMapper.h>

using namespace filament;

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nDestroyToneMapper(JNIEnv*, jclass, jlong nativeObject) {
    delete (ToneMapper*) nativeObject;
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreateLinearToneMapper(JNIEnv*, jclass) {
    return (jlong) new LinearToneMapper();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreateACESToneMapper(JNIEnv*, jclass) {
    return (jlong) new ACESToneMapper();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreateACESLegacyToneMapper(JNIEnv*, jclass) {
    return (jlong) new ACESLegacyToneMapper();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreateFilmicToneMapper(JNIEnv*, jclass) {
    return (jlong) new FilmicToneMapper();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreatePBRNeutralToneMapper(JNIEnv*, jclass) {
    return (jlong) new PBRNeutralToneMapper();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreateGT7ToneMapper(JNIEnv*, jclass) {
    return (jlong) new GT7ToneMapper();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreateAgxToneMapper(JNIEnv*, jclass, jint look) {
    return (jlong) new AgxToneMapper((AgxToneMapper::AgxLook) look);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreateGenericToneMapper(JNIEnv*, jclass, jfloat contrast, jfloat midGrayIn, jfloat midGrayOut, jfloat hdrMax) {
    return (jlong) new GenericToneMapper(contrast, midGrayIn, midGrayOut, hdrMax);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nGenericGetContrast(JNIEnv*, jclass, jlong nativeObject) {
    return ((GenericToneMapper*) nativeObject)->getContrast();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nGenericGetMidGrayIn(JNIEnv*, jclass, jlong nativeObject) {
    return ((GenericToneMapper*) nativeObject)->getMidGrayIn();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nGenericGetMidGrayOut(JNIEnv*, jclass, jlong nativeObject) {
    return ((GenericToneMapper*) nativeObject)->getMidGrayOut();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nGenericGetHdrMax(JNIEnv*, jclass, jlong nativeObject) {
    return ((GenericToneMapper*) nativeObject)->getHdrMax();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nGenericSetContrast(JNIEnv*, jclass, jlong nativeObject, jfloat contrast) {
    ((GenericToneMapper*) nativeObject)->setContrast(contrast);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nGenericSetMidGrayIn(JNIEnv*, jclass, jlong nativeObject, jfloat midGrayIn) {
    ((GenericToneMapper*) nativeObject)->setMidGrayIn(midGrayIn);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nGenericSetMidGrayOut(JNIEnv*, jclass, jlong nativeObject, jfloat midGrayOut) {
    ((GenericToneMapper*) nativeObject)->setMidGrayOut(midGrayOut);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nGenericSetHdrMax(JNIEnv*, jclass, jlong nativeObject, jfloat hdrMax) {
    ((GenericToneMapper*) nativeObject)->setHdrMax(hdrMax);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ToneMapper_nCreateDisplayRangeToneMapper(JNIEnv*, jclass) {
    return (jlong) new DisplayRangeToneMapper();
}

} // extern "C"

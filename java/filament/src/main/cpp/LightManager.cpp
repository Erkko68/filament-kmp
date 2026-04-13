#include <jni.h>
#include <filament/LightManager.h>
#include <filament/Engine.h>

using namespace filament;

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_LightManager_nGetInstance(JNIEnv* env, jclass, jlong nativeLightManager, jint entity) {
    return (jint) ((LightManager*) nativeLightManager)->getInstance((utils::Entity&) entity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetIntensity(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat intensity) {
    ((LightManager*) nativeLightManager)->setIntensity((LightManager::Instance) instance, (float) intensity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetColor(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat r, jfloat g, jfloat b) {
    ((LightManager*) nativeLightManager)->setColor((LightManager::Instance) instance, {r, g, b});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetDirection(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat x, jfloat y, jfloat z) {
    ((LightManager*) nativeLightManager)->setDirection((LightManager::Instance) instance, {x, y, z});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetPosition(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat x, jfloat y, jfloat z) {
    ((LightManager*) nativeLightManager)->setPosition((LightManager::Instance) instance, {x, y, z});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetFalloff(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat falloff) {
    ((LightManager*) nativeLightManager)->setFalloff((LightManager::Instance) instance, (float) falloff);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetSunAngularRadius(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat radius) {
    ((LightManager*) nativeLightManager)->setSunAngularRadius((LightManager::Instance) instance, (float) radius);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetSunHaloSize(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat size) {
    ((LightManager*) nativeLightManager)->setSunHaloSize((LightManager::Instance) instance, (float) size);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetSunHaloFalloff(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat falloff) {
    ((LightManager*) nativeLightManager)->setSunHaloFalloff((LightManager::Instance) instance, (float) falloff);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nDestroy(JNIEnv* env, jclass, jlong nativeLightManager, jint entity) {
    ((LightManager*) nativeLightManager)->destroy((utils::Entity&) entity);
}

extern "C" JNIEXPORT long JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nCreateBuilder(JNIEnv* env, jclass, jint type) {
    return (jlong) new LightManager::Builder((LightManager::Type) type);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (LightManager::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderCastShadows(JNIEnv* env, jclass, jlong nativeBuilder, jboolean enabled) {
    ((LightManager::Builder*) nativeBuilder)->castShadows((bool) enabled);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderShadowOptions(JNIEnv* env, jclass, jlong nativeBuilder,
    jint mapSize, jint shadowCascades, jfloatArray cascadeSplitPositions,
    jfloat constantBias, jfloat normalBias, jfloat shadowFar, jfloat shadowNearHint,
    jfloat shadowFarHint, jboolean stable, jboolean lispsm,
    jfloat polygonOffsetConstant, jfloat polygonOffsetSlope,
    jboolean screenSpaceContactShadows,
    jint stepCount, jfloat maxShadowDistance,
    jboolean elvsm, jfloat blurWidth, jfloat shadowBulbRadius, jfloatArray transform) {

    LightManager::ShadowOptions options;
    options.mapSize = (uint32_t) mapSize;
    options.shadowCascades = (uint8_t) shadowCascades;

    jfloat* splits = env->GetFloatArrayElements(cascadeSplitPositions, nullptr);
    options.cascadeSplitPositions[0] = splits[0];
    options.cascadeSplitPositions[1] = splits[1];
    options.cascadeSplitPositions[2] = splits[2];
    env->ReleaseFloatArrayElements(cascadeSplitPositions, splits, JNI_ABORT);

    options.constantBias = constantBias;
    options.normalBias = normalBias;
    options.shadowFar = shadowFar;
    options.shadowNearHint = shadowNearHint;
    options.shadowFarHint = shadowFarHint;
    options.stable = (bool) stable;
    options.lispsm = (bool) lispsm;
    options.polygonOffsetConstant = polygonOffsetConstant;
    options.polygonOffsetSlope = polygonOffsetSlope;
    options.screenSpaceContactShadows = (bool) screenSpaceContactShadows;
    options.stepCount = (uint8_t) stepCount;
    options.maxShadowDistance = maxShadowDistance;
    options.vsm.elvsm = (bool) elvsm;
    options.vsm.blurWidth = blurWidth;
    options.shadowBulbRadius = shadowBulbRadius;

    jfloat* quat = env->GetFloatArrayElements(transform, nullptr);
    options.transform = { quat[3], { quat[0], quat[1], quat[2] } }; // math::quatf{w, {x, y, z}}
    env->ReleaseFloatArrayElements(transform, quat, JNI_ABORT);

    ((LightManager::Builder*) nativeBuilder)->shadowOptions(options);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderColor(JNIEnv* env, jclass, jlong nativeBuilder, jfloat r, jfloat g, jfloat b) {
    ((LightManager::Builder*) nativeBuilder)->color({r, g, b});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderIntensity(JNIEnv* env, jclass, jlong nativeBuilder, jfloat intensity) {
    ((LightManager::Builder*) nativeBuilder)->intensity((float) intensity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderDirection(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((LightManager::Builder*) nativeBuilder)->direction({x, y, z});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderPosition(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((LightManager::Builder*) nativeBuilder)->position({x, y, z});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderFalloff(JNIEnv* env, jclass, jlong nativeBuilder, jfloat radius) {
    ((LightManager::Builder*) nativeBuilder)->falloff(radius);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderSpotLightCone(JNIEnv* env, jclass, jlong nativeBuilder, jfloat inner, jfloat outer) {
    ((LightManager::Builder*) nativeBuilder)->spotLightCone(inner, outer);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderSunAngularRadius(JNIEnv* env, jclass, jlong nativeBuilder, jfloat radius) {
    ((LightManager::Builder*) nativeBuilder)->sunAngularRadius(radius);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderSunHaloSize(JNIEnv* env, jclass, jlong nativeBuilder, jfloat size) {
    ((LightManager::Builder*) nativeBuilder)->sunHaloSize(size);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderSunHaloFalloff(JNIEnv* env, jclass, jlong nativeBuilder, jfloat falloff) {
    ((LightManager::Builder*) nativeBuilder)->sunHaloFalloff(falloff);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine, jint entity) {
    ((LightManager::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine, (utils::Entity&) entity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nComputeUniformSplits(JNIEnv* env, jclass, jfloatArray splitPositions, jint cascades) {
    jfloat* splits = env->GetFloatArrayElements(splitPositions, nullptr);
    LightManager::ShadowCascades::computeUniformSplits(splits, (uint8_t) cascades);
    env->ReleaseFloatArrayElements(splitPositions, splits, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nComputeLogSplits(JNIEnv* env, jclass, jfloatArray splitPositions, jint cascades, jfloat near, jfloat far) {
    jfloat* splits = env->GetFloatArrayElements(splitPositions, nullptr);
    LightManager::ShadowCascades::computeLogSplits(splits, (uint8_t) cascades, near, far);
    env->ReleaseFloatArrayElements(splitPositions, splits, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nComputePracticalSplits(JNIEnv* env, jclass, jfloatArray splitPositions, jint cascades, jfloat near, jfloat far, jfloat lambda) {
    jfloat* splits = env->GetFloatArrayElements(splitPositions, nullptr);
    LightManager::ShadowCascades::computePracticalSplits(splits, (uint8_t) cascades, near, far, lambda);
    env->ReleaseFloatArrayElements(splitPositions, splits, 0);
}

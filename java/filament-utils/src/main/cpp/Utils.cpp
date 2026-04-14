#include <filament/Engine.h>
#include <jni.h>
#include <utils/Log.h>

extern "C" {
// HDRLoader
extern jlong Java_io_github_erkko68_filament_utils_HDRLoader_nCreateHDRTexture(
    JNIEnv *, jclass, jlong, jbyteArray, jint);

// Manipulator
extern jlong
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nCreateBuilder(
    JNIEnv *, jclass);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nDestroyBuilder(
    JNIEnv *, jclass, jlong);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderViewport(
    JNIEnv *, jclass, jlong, jint, jint);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderTargetPosition(
    JNIEnv *, jclass, jlong, jfloat, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderUpVector(
    JNIEnv *, jclass, jlong, jfloat, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderZoomSpeed(
    JNIEnv *, jclass, jlong, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderOrbitHomePosition(
    JNIEnv *, jclass, jlong, jfloat, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderOrbitSpeed(
    JNIEnv *, jclass, jlong, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFovDirection(
    JNIEnv *, jclass, jlong, jint);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFovDegrees(
    JNIEnv *, jclass, jlong, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFarPlane(
    JNIEnv *, jclass, jlong, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderMapExtent(
    JNIEnv *, jclass, jlong, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderMapMinDistance(
    JNIEnv *, jclass, jlong, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightStartPosition(
    JNIEnv *, jclass, jlong, jfloat, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightStartOrientation(
    JNIEnv *, jclass, jlong, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightMaxMoveSpeed(
    JNIEnv *, jclass, jlong, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightSpeedSteps(
    JNIEnv *, jclass, jlong, jint);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightPanSpeed(
    JNIEnv *, jclass, jlong, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightMoveDamping(
    JNIEnv *, jclass, jlong, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderGroundPlane(
    JNIEnv *, jclass, jlong, jfloat, jfloat, jfloat, jfloat);
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderPanning(
    JNIEnv *, jclass, jlong, jboolean);
extern jlong
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderBuild(
    JNIEnv *, jclass, jlong, jint);

extern void
Java_io_github_erkko68_filament_utils_Manipulator_nDestroyManipulator(JNIEnv *,
                                                                      jclass,
                                                                      jlong);
extern jint Java_io_github_erkko68_filament_utils_Manipulator_nGetMode(JNIEnv *,
                                                                       jclass,
                                                                       jlong);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nSetViewport(
    JNIEnv *, jclass, jlong, jint, jint);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nGetLookAt(
    JNIEnv *, jclass, jlong, jfloatArray, jfloatArray, jfloatArray);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nRaycast(
    JNIEnv *, jclass, jlong, jint, jint, jfloatArray);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nGrabBegin(
    JNIEnv *, jclass, jlong, jint, jint, jboolean);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nGrabUpdate(
    JNIEnv *, jclass, jlong, jint, jint);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nGrabEnd(JNIEnv *,
                                                                       jclass,
                                                                       jlong);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nKeyDown(JNIEnv *,
                                                                       jclass,
                                                                       jlong,
                                                                       jint);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nKeyUp(JNIEnv *,
                                                                     jclass,
                                                                     jlong,
                                                                     jint);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nScroll(
    JNIEnv *, jclass, jlong, jint, jint, jfloat);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nUpdate(JNIEnv *,
                                                                      jclass,
                                                                      jlong,
                                                                      jfloat);
extern jlong
Java_io_github_erkko68_filament_utils_Manipulator_nGetCurrentBookmark(JNIEnv *,
                                                                      jclass,
                                                                      jlong);
extern jlong Java_io_github_erkko68_filament_utils_Manipulator_nGetHomeBookmark(
    JNIEnv *, jclass, jlong);
extern void Java_io_github_erkko68_filament_utils_Manipulator_nJumpToBookmark(
    JNIEnv *, jclass, jlong, jlong);

// Bookmark
extern void
Java_io_github_erkko68_filament_utils_Manipulator_00024Bookmark_nDestroyBookmark(
    JNIEnv *, jclass, jlong);
}

static const JNINativeMethod HDRLOADER_METHODS[] = {
    {(char *)"nCreateHDRTexture", (char *)"(J[BI)J",
     (void *)Java_io_github_erkko68_filament_utils_HDRLoader_nCreateHDRTexture},
};

static const JNINativeMethod MANIPULATOR_BUILDER_METHODS[] = {
    {(char *)"nCreateBuilder", (char *)"()J",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nCreateBuilder},
    {(char *)"nDestroyBuilder", (char *)"(J)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nDestroyBuilder},
    {(char *)"nBuilderViewport", (char *)"(JII)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderViewport},
    {(char *)"nBuilderTargetPosition", (char *)"(JFFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderTargetPosition},
    {(char *)"nBuilderUpVector", (char *)"(JFFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderUpVector},
    {(char *)"nBuilderZoomSpeed", (char *)"(JF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderZoomSpeed},
    {(char *)"nBuilderOrbitHomePosition", (char *)"(JFFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderOrbitHomePosition},
    {(char *)"nBuilderOrbitSpeed", (char *)"(JFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderOrbitSpeed},
    {(char *)"nBuilderFovDirection", (char *)"(JI)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFovDirection},
    {(char *)"nBuilderFovDegrees", (char *)"(JF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFovDegrees},
    {(char *)"nBuilderFarPlane", (char *)"(JF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFarPlane},
    {(char *)"nBuilderMapExtent", (char *)"(JFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderMapExtent},
    {(char *)"nBuilderMapMinDistance", (char *)"(JF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderMapMinDistance},
    {(char *)"nBuilderFlightStartPosition", (char *)"(JFFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightStartPosition},
    {(char *)"nBuilderFlightStartOrientation", (char *)"(JFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightStartOrientation},
    {(char *)"nBuilderFlightMaxMoveSpeed", (char *)"(JF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightMaxMoveSpeed},
    {(char *)"nBuilderFlightSpeedSteps", (char *)"(JI)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightSpeedSteps},
    {(char *)"nBuilderFlightPanSpeed", (char *)"(JFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightPanSpeed},
    {(char *)"nBuilderFlightMoveDamping", (char *)"(JF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightMoveDamping},
    {(char *)"nBuilderGroundPlane", (char *)"(JFFFF)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderGroundPlane},
    {(char *)"nBuilderPanning", (char *)"(JZ)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderPanning},
    {(char *)"nBuilderBuild", (char *)"(JI)J",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderBuild},
};

static const JNINativeMethod MANIPULATOR_METHODS[] = {
    {(char *)"nDestroyManipulator", (char *)"(J)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_nDestroyManipulator},
    {(char *)"nGetMode", (char *)"(J)I",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nGetMode},
    {(char *)"nSetViewport", (char *)"(JII)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nSetViewport},
    {(char *)"nGetLookAt", (char *)"(J[F[F[F)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nGetLookAt},
    {(char *)"nRaycast", (char *)"(JII[F)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nRaycast},
    {(char *)"nGrabBegin", (char *)"(JIIZ)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nGrabBegin},
    {(char *)"nGrabUpdate", (char *)"(JII)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nGrabUpdate},
    {(char *)"nGrabEnd", (char *)"(J)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nGrabEnd},
    {(char *)"nKeyDown", (char *)"(JI)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nKeyDown},
    {(char *)"nKeyUp", (char *)"(JI)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nKeyUp},
    {(char *)"nScroll", (char *)"(JIIF)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nScroll},
    {(char *)"nUpdate", (char *)"(JF)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nUpdate},
    {(char *)"nGetCurrentBookmark", (char *)"(J)J",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_nGetCurrentBookmark},
    {(char *)"nGetHomeBookmark", (char *)"(J)J",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_nGetHomeBookmark},
    {(char *)"nJumpToBookmark", (char *)"(JJ)V",
     (void *)Java_io_github_erkko68_filament_utils_Manipulator_nJumpToBookmark},
};

static const JNINativeMethod BOOKMARK_METHODS[] = {
    {(char *)"nDestroyBookmark", (char *)"(J)V",
     (void *)
         Java_io_github_erkko68_filament_utils_Manipulator_00024Bookmark_nDestroyBookmark},
};

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
  JNIEnv *env;
  if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK)
    return -1;

  auto registerNativeMethods = [env](const char *className,
                                     const JNINativeMethod *methods,
                                     int numMethods) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr)
      return false;
    if (env->RegisterNatives(clazz, methods, numMethods) < 0)
      return false;
    return true;
  };

  if (!registerNativeMethods(
          "io/github/erkko68/filament/utils/HDRLoader", HDRLOADER_METHODS,
          sizeof(HDRLOADER_METHODS) / sizeof(JNINativeMethod)))
    return -1;
  if (!registerNativeMethods(
          "io/github/erkko68/filament/utils/Manipulator", MANIPULATOR_METHODS,
          sizeof(MANIPULATOR_METHODS) / sizeof(JNINativeMethod)))
    return -1;
  if (!registerNativeMethods(
          "io/github/erkko68/filament/utils/Manipulator$Builder",
          MANIPULATOR_BUILDER_METHODS,
          sizeof(MANIPULATOR_BUILDER_METHODS) / sizeof(JNINativeMethod)))
    return -1;
  if (!registerNativeMethods(
          "io/github/erkko68/filament/utils/Manipulator$Bookmark",
          BOOKMARK_METHODS, sizeof(BOOKMARK_METHODS) / sizeof(JNINativeMethod)))
    return -1;

  return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Utils_nInit(JNIEnv *, jclass) {
  // Initialization logic if needed
}

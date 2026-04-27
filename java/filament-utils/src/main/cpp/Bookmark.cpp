#include <jni.h>
#include <camutils/Bookmark.h>
#include <camutils/Manipulator.h>

using namespace filament::camutils;
using CameraManipulator = Manipulator<float>;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_jni_Manipulator_00024Bookmark_nDestroyBookmark(JNIEnv*, jclass, jlong nativeObject) {
    delete (CameraManipulator::Bookmark*) nativeObject;
}

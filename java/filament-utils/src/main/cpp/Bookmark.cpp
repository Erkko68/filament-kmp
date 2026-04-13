#include <jni.h>
#include <camutils/Bookmark.h>

using namespace filament::camutils;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Bookmark_nDestroyBookmark(JNIEnv* env, jclass, jlong nativeObject) {
    delete (Bookmark<float>*) nativeObject;
}

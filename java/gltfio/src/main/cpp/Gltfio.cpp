#include <jni.h>
#include "MaterialKey.h"

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_Gltfio_nInit(JNIEnv* env, jclass) {
    MaterialKeyHelper::get().init(env);
}

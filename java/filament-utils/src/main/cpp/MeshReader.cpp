#include <jni.h>
#include <filament/Engine.h>
#include <filament/VertexBuffer.h>
#include <filament/IndexBuffer.h>
#include <filament/MaterialInstance.h>
#include <filameshio/MeshReader.h>

using namespace filament;
using namespace filamesh;

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_utils_MeshReader_nLoadMeshFromBuffer(JNIEnv* env, jclass, jlong nativeEngine, jobject buffer, jint remaining, jlong nativeDefaultMaterial, jlongArray outVbIb) {
    Engine* engine = (Engine*) nativeEngine;
    void* data = env->GetDirectBufferAddress(buffer);
    MaterialInstance* defaultMaterial = (MaterialInstance*) nativeDefaultMaterial;

    MeshReader::Mesh mesh = MeshReader::loadMeshFromBuffer(engine, data, nullptr, nullptr, defaultMaterial);
    
    if (mesh.renderable.isNull()) {
        return 0;
    }

    jlong* out = env->GetLongArrayElements(outVbIb, nullptr);
    out[0] = (jlong) mesh.vertexBuffer;
    out[1] = (jlong) mesh.indexBuffer;
    env->ReleaseLongArrayElements(outVbIb, out, 0);

    return (jint) mesh.renderable.getId();
}

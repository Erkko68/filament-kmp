#include <jni.h>
#include <geometry/SurfaceOrientation.h>
#include <math/mat3.h>
#include <math/vec3.h>
#include <math/vec4.h>
#include <math/norm.h>

using namespace filament;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MathUtils_nPackTangentFrame(JNIEnv* env, jclass, 
    jfloat tx, jfloat ty, jfloat tz,
    jfloat bx, jfloat by, jfloat bz,
    jfloat nx, jfloat ny, jfloat nz,
    jfloatArray quaternion, jint offset) {
    
    math::float3 tangent(tx, ty, tz);
    math::float3 bitangent(bx, by, bz);
    math::float3 normal(nx, ny, nz);
    
    math::mat3f m;
    m[0] = tangent;
    m[1] = bitangent;
    m[2] = normal;
    
    math::quatf q = math::mat3f::packTangentFrame(m);
    env->SetFloatArrayRegion(quaternion, offset, 4, (jfloat*) &q);
}

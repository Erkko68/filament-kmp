#include <jni.h>
#include <filament/Engine.h>
#include <filament/Texture.h>
#include <filament/IndirectLight.h>
#include <filament/Skybox.h>
#include <ktxreader/Ktx1Reader.h>
#include <image/Ktx1Bundle.h>

using namespace filament;
using namespace ktxreader;
using namespace image;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_KtxLoader_nCreateTexture(JNIEnv* env, jclass, jlong nativeEngine, jobject buffer, jint remaining, jboolean srgb) {
    Engine* engine = (Engine*) nativeEngine;
    uint8_t* data = (uint8_t*) env->GetDirectBufferAddress(buffer);
    Ktx1Bundle* bundle = new Ktx1Bundle(data, (uint32_t) remaining);
    Texture* texture = ktxreader::Ktx1Reader::createTexture(engine, bundle, (bool) srgb);
    // Ktx1Reader::createTexture(engine, Ktx1Bundle*, bool) takes ownership and destroys the bundle
    return (jlong) texture;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_KtxLoader_nCreateIndirectLight(JNIEnv* env, jclass, jlong nativeEngine, jobject buffer, jint remaining) {
    Engine* engine = (Engine*) nativeEngine;
    uint8_t* data = (uint8_t*) env->GetDirectBufferAddress(buffer);
    Ktx1Bundle* bundle = new Ktx1Bundle(data, (uint32_t) remaining);
    
    // For IndirectLight, we usually need the spherical harmonics if present
    math::float3 sh[9];
    if (bundle->getSphericalHarmonics(sh)) {
        Texture* cubemap = ktxreader::Ktx1Reader::createTexture(engine, bundle, true);
        IndirectLight* ibl = IndirectLight::Builder()
            .reflections(cubemap)
            .irradiance(3, sh)
            .build(*engine);
        return (jlong) ibl;
    } else {
        Texture* cubemap = ktxreader::Ktx1Reader::createTexture(engine, bundle, true);
        IndirectLight* ibl = IndirectLight::Builder()
            .reflections(cubemap)
            .build(*engine);
        return (jlong) ibl;
    }
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_KtxLoader_nCreateSkybox(JNIEnv* env, jclass, jlong nativeEngine, jobject buffer, jint remaining) {
    Engine* engine = (Engine*) nativeEngine;
    uint8_t* data = (uint8_t*) env->GetDirectBufferAddress(buffer);
    Ktx1Bundle* bundle = new Ktx1Bundle(data, (uint32_t) remaining);
    Texture* cubemap = ktxreader::Ktx1Reader::createTexture(engine, bundle, true);
    Skybox* skybox = Skybox::Builder().environment(cubemap).build(*engine);
    return (jlong) skybox;
}

#include <jni.h>
#include <filament/TextureSampler.h>

using namespace filament;

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_TextureSampler_nCreateSampler(JNIEnv* env, jclass, jint min, jint mag, jint s, jint t, jint r) {
    TextureSampler sampler((TextureSampler::MinFilter) min, (TextureSampler::MagFilter) mag, (TextureSampler::WrapMode) s, (TextureSampler::WrapMode) t, (TextureSampler::WrapMode) r);
    return (jint) sampler.getSamplerParams();
}

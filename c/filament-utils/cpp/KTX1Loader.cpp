#include "KTX1Loader.h"

#include <filament/Engine.h>
#include <filament/IndirectLight.h>
#include <filament/Skybox.h>
#include <filament/Texture.h>
#include <ktxreader/Ktx1Reader.h>

#include <math/vec3.h>

using namespace filament;
using namespace ktxreader;
using namespace filament::math;

extern "C" {

FilaTexture* FilaKTX1Loader_createTexture(FilaEngine* engine, const void* buffer, size_t size, bool srgb) {
    Engine* nativeEngine = reinterpret_cast<Engine*>(engine);
    Ktx1Bundle* bundle = new Ktx1Bundle(reinterpret_cast<const uint8_t*>(buffer), size);
    
    // Note: Ktx1Reader::createTexture returns a Texture* that the user is responsible for.
    // The bundle can be deleted after the texture is created.
    Texture* texture = Ktx1Reader::createTexture(nativeEngine, bundle, srgb);
    delete bundle;
    
    return reinterpret_cast<FilaTexture*>(texture);
}

FilaIndirectLight* FilaKTX1Loader_createIndirectLight(FilaEngine* engine, FilaTexture* texture, const FilaFloat3* sh) {
    Engine* nativeEngine = reinterpret_cast<Engine*>(engine);
    Texture* nativeTexture = reinterpret_cast<Texture*>(texture);
    
    IndirectLight* indirectLight = IndirectLight::Builder()
            .reflections(nativeTexture)
            .irradiance(3, reinterpret_cast<const float3*>(sh))
            .build(*nativeEngine);
            
    return reinterpret_cast<FilaIndirectLight*>(indirectLight);
}

FilaSkybox* FilaKTX1Loader_createSkybox(FilaEngine* engine, FilaTexture* texture) {
    Engine* nativeEngine = reinterpret_cast<Engine*>(engine);
    Texture* nativeTexture = reinterpret_cast<Texture*>(texture);
    
    Skybox* skybox = Skybox::Builder()
            .environment(nativeTexture)
            .build(*nativeEngine);
            
    return reinterpret_cast<FilaSkybox*>(skybox);
}

bool FilaKTX1Loader_getSphericalHarmonics(const void* buffer, size_t size, FilaFloat3* outSh) {
    Ktx1Bundle bundle(reinterpret_cast<const uint8_t*>(buffer), size);
    return bundle.getSphericalHarmonics(reinterpret_cast<float3*>(outSh));
}

}

#include <gltfio/MaterialProvider.h>
#include <gltfio/materials/uberarchive.h>
#include "../c/MaterialProvider.h"

#include <cstring>

using namespace filament;
using namespace filament::gltfio;

namespace {

// FILA_MATERIAL_KEY_FIELD_COUNT fields, in the flattened order (see GltfioTypes.h).
#define FILA_MATERIAL_KEY_FIELDS(X) \
    X(doubleSided) \
    X(unlit) \
    X(hasVertexColors) \
    X(hasBaseColorTexture) \
    X(hasNormalTexture) \
    X(hasOcclusionTexture) \
    X(hasEmissiveTexture) \
    X(useSpecularGlossiness) \
    X(alphaMode) \
    X(enableDiagnostics) \
    X(hasMetallicRoughnessTexture) \
    X(metallicRoughnessUV) \
    X(baseColorUV) \
    X(hasClearCoatTexture) \
    X(clearCoatUV) \
    X(hasClearCoatRoughnessTexture) \
    X(clearCoatRoughnessUV) \
    X(hasClearCoatNormalTexture) \
    X(clearCoatNormalUV) \
    X(hasClearCoat) \
    X(hasTransmission) \
    X(hasTextureTransforms) \
    X(emissiveUV) \
    X(aoUV) \
    X(normalUV) \
    X(hasTransmissionTexture) \
    X(transmissionUV) \
    X(hasSheenColorTexture) \
    X(sheenColorUV) \
    X(hasSheenRoughnessTexture) \
    X(sheenRoughnessUV) \
    X(hasVolumeThicknessTexture) \
    X(volumeThicknessUV) \
    X(hasSheen) \
    X(hasIOR)

MaterialKey unpackKey(const int32_t* key) {
    MaterialKey mk;
    memset(&mk, 0, sizeof(mk)); // gltfio hashes the raw bytes, padding included
    int i = 0;
#define X(f) mk.f = decltype(mk.f)(key[i++]);
    FILA_MATERIAL_KEY_FIELDS(X)
#undef X
    return mk;
}

void packKey(const MaterialKey& mk, int32_t* key) {
    int i = 0;
#define X(f) key[i++] = int32_t(mk.f);
    FILA_MATERIAL_KEY_FIELDS(X)
#undef X
}

#define X(f) + 1
static_assert(0 FILA_MATERIAL_KEY_FIELDS(X) == FILA_MATERIAL_KEY_FIELD_COUNT, "MaterialKey field list out of sync");
#undef X

} // namespace

extern "C" {

void FilaMaterialProvider_destroy(FilaMaterialProvider* provider) {
    delete (MaterialProvider*) provider;
}

FilaMaterialProvider* FilaMaterialProvider_createUbershaderProvider(FilaEngine* engine, const void* archive, uint32_t archiveByteCount) {
    if (archive == nullptr) {
        archive = UBERARCHIVE_DEFAULT_DATA;
        archiveByteCount = UBERARCHIVE_DEFAULT_SIZE;
    }
    return (FilaMaterialProvider*) createUbershaderProvider((Engine*) engine, archive, archiveByteCount);
}


void FilaMaterialProvider_destroyMaterials(FilaMaterialProvider* provider) {
    ((MaterialProvider*) provider)->destroyMaterials();
}

uint32_t FilaMaterialProvider_getMaterialsCount(FilaMaterialProvider* provider) {
    return ((MaterialProvider*) provider)->getMaterialsCount();
}

FilaMaterial* FilaMaterialProvider_getMaterialAt(FilaMaterialProvider* provider, uint32_t index) {
    return (FilaMaterial*) ((MaterialProvider*) provider)->getMaterials()[index];
}

bool FilaMaterialProvider_needsDummyData(FilaMaterialProvider* provider, int attrib) {
    return ((MaterialProvider*) provider)->needsDummyData((VertexAttribute) attrib);
}

FilaMaterialInstance* FilaMaterialProvider_createMaterialInstance(FilaMaterialProvider* provider,
    const int32_t* key, const uint8_t* uvmap, const char* label, const char* extras) {
    MaterialKey mk = unpackKey(key);
    return (FilaMaterialInstance*) ((MaterialProvider*) provider)->createMaterialInstance(&mk, (UvMap*) uvmap, label, extras);
}

FilaMaterial* FilaMaterialProvider_getMaterial(FilaMaterialProvider* provider,
    const int32_t* key, const uint8_t* uvmap, const char* label) {
    MaterialKey mk = unpackKey(key);
    return (FilaMaterial*) ((MaterialProvider*) provider)->getMaterial(&mk, (UvMap*) uvmap, label);
}

void FilaMaterialKey_constrainMaterial(int32_t* key, uint8_t* uvmap) {
    MaterialKey mk = unpackKey(key);
    constrainMaterial(&mk, (UvMap*) uvmap);
    packKey(mk, key);
}

}

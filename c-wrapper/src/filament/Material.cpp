#include <filament/Engine.h>
#include <filament/Material.h>
#include <filament/MaterialInstance.h>
#include <filament/Texture.h>
#include <filament/TextureSampler.h>

#include <math/mat3.h>
#include <math/mat4.h>
#include <math/vec2.h>
#include <math/vec3.h>
#include <math/vec4.h>

#include <mutex>
#include <string>
#include <unordered_map>
#include <vector>

#include "../../include/filament/Material.h"
#include "../../include/filament/MaterialInstance.h"

namespace {
using MaterialBuilder = filament::Material::Builder;

struct CachedBoolParameter {
    uint8_t arity;
    bool value[4];
};

std::mutex gBoolParameterCacheMutex;
std::unordered_map<const filament::MaterialInstance*, std::unordered_map<std::string, CachedBoolParameter>> gBoolParameterCache;

bool canSetParameter(const filament::MaterialInstance* materialInstance, const char* name) {
    if (!materialInstance || !name || name[0] == '\0') {
        return false;
    }
    const filament::Material* material = materialInstance->getMaterial();
    return material && material->hasParameter(name);
}

bool canGetParameter(const filament::MaterialInstance* materialInstance, const char* name) {
    return canSetParameter(materialInstance, name);
}

filament::math::mat3f toMat3f(const float m[9]) {
    return filament::math::mat3f(
            m[0], m[1], m[2],
            m[3], m[4], m[5],
            m[6], m[7], m[8]);
}

filament::math::mat4f toMat4f(const float m[16]) {
    return filament::math::mat4f(
            m[0], m[1], m[2], m[3],
            m[4], m[5], m[6], m[7],
            m[8], m[9], m[10], m[11],
            m[12], m[13], m[14], m[15]);
}

void fromMat3f(const filament::math::mat3f& in, float out[9]) {
    for (size_t c = 0; c < 3; ++c) {
        for (size_t r = 0; r < 3; ++r) {
            out[c * 3 + r] = in[c][r];
        }
    }
}

void fromMat4f(const filament::math::mat4f& in, float out[16]) {
    for (size_t c = 0; c < 4; ++c) {
        for (size_t r = 0; r < 4; ++r) {
            out[c * 4 + r] = in[c][r];
        }
    }
}

void cacheBoolParameter(const filament::MaterialInstance* materialInstance, const char* name,
        uint8_t arity, bool x, bool y, bool z, bool w) {
    if (!materialInstance || !name || arity == 0u || arity > 4u) {
        return;
    }
    CachedBoolParameter cached = {arity, {x, y, z, w}};
    std::lock_guard<std::mutex> lock(gBoolParameterCacheMutex);
    gBoolParameterCache[materialInstance][name] = cached;
}

bool readCachedBoolParameter(const filament::MaterialInstance* materialInstance, const char* name,
        uint8_t expectedArity, bool* outValue) {
    if (!materialInstance || !name || expectedArity == 0u || expectedArity > 4u || !outValue) {
        return false;
    }
    std::lock_guard<std::mutex> lock(gBoolParameterCacheMutex);
    const auto byInstance = gBoolParameterCache.find(materialInstance);
    if (byInstance == gBoolParameterCache.end()) {
        return false;
    }
    const auto byName = byInstance->second.find(name);
    if (byName == byInstance->second.end() || byName->second.arity != expectedArity) {
        return false;
    }
    for (uint8_t i = 0u; i < expectedArity; ++i) {
        outValue[i] = byName->second.value[i];
    }
    return true;
}
} // namespace

void FilaMaterialInstance_clearCachedBoolParameters(const filament::MaterialInstance* materialInstance) {
    if (!materialInstance) {
        return;
    }
    std::lock_guard<std::mutex> lock(gBoolParameterCacheMutex);
    gBoolParameterCache.erase(materialInstance);
}

void FilaMaterialInstance_clearAllCachedBoolParameters(void) {
    std::lock_guard<std::mutex> lock(gBoolParameterCacheMutex);
    gBoolParameterCache.clear();
}

extern "C" {

FilaMaterialBuilder* FilaMaterialBuilder_create(void) {
    auto builder = new MaterialBuilder();
    return reinterpret_cast<FilaMaterialBuilder*>(builder);
}

void FilaMaterialBuilder_destroy(FilaMaterialBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<MaterialBuilder*>(builder);
    delete cppBuilder;
}

void FilaMaterialBuilder_package(FilaMaterialBuilder* builder, const void* payload, size_t size) {
    if (!builder || !payload || size == 0) {
        return;
    }
    auto cppBuilder = reinterpret_cast<MaterialBuilder*>(builder);
    cppBuilder->package(payload, size);
}

FilaMaterial* FilaMaterialBuilder_build(const FilaMaterialBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<const MaterialBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaMaterial*>(cppBuilder->build(*cppEngine));
}

FilaMaterialInstance* FilaMaterial_createInstance(const FilaMaterial* material) {
    if (!material) {
        return nullptr;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return reinterpret_cast<FilaMaterialInstance*>(cppMaterial->createInstance());
}

FilaMaterialInstance* FilaMaterial_createInstanceNamed(const FilaMaterial* material, const char* name) {
    if (!material) {
        return nullptr;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return reinterpret_cast<FilaMaterialInstance*>(cppMaterial->createInstance(name));
}

const FilaMaterial* FilaMaterialInstance_getMaterial(const FilaMaterialInstance* materialInstance) {
    if (!materialInstance) {
        return nullptr;
    }
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return reinterpret_cast<const FilaMaterial*>(cppMaterialInstance->getMaterial());
}

const char* FilaMaterial_getName(const FilaMaterial* material) {
    if (!material) {
        return nullptr;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->getName();
}

bool FilaMaterial_hasParameter(const FilaMaterial* material, const char* name) {
    if (!material || !name || name[0] == '\0') {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->hasParameter(name);
}

bool FilaMaterial_isSampler(const FilaMaterial* material, const char* name) {
    if (!material || !name || name[0] == '\0') {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->isSampler(name);
}

size_t FilaMaterial_getParameterCount(const FilaMaterial* material) {
    if (!material) {
        return 0;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->getParameterCount();
}

size_t FilaMaterial_getParameters(
        const FilaMaterial* material,
        FilaMaterialParameterInfo* outParameters,
        size_t outParameterCount) {
    if (!material || !outParameters || outParameterCount == 0u) {
        return 0u;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    std::vector<filament::Material::ParameterInfo> infos(outParameterCount);
    const size_t written = cppMaterial->getParameters(infos.data(), outParameterCount);
    for (size_t i = 0; i < written; ++i) {
        outParameters[i].name = infos[i].name;
        outParameters[i].isSampler = infos[i].isSampler;
        outParameters[i].isSubpass = infos[i].isSubpass;
        outParameters[i].type = FILA_BACKEND_UNIFORM_TYPE_FLOAT;
        outParameters[i].samplerType = FILA_BACKEND_SAMPLER_TYPE_2D;
        outParameters[i].subpassType = FILA_BACKEND_SUBPASS_TYPE_SUBPASS_INPUT;
        if (infos[i].isSubpass) {
            outParameters[i].subpassType = static_cast<FilaBackendSubpassType>(infos[i].subpassType);
        } else if (infos[i].isSampler) {
            outParameters[i].samplerType = static_cast<FilaBackendSamplerType>(infos[i].samplerType);
        } else {
            outParameters[i].type = static_cast<FilaBackendUniformType>(infos[i].type);
        }
        outParameters[i].count = infos[i].count;
        outParameters[i].precision = static_cast<FilaBackendPrecision>(infos[i].precision);
    }
    return written;
}

FilaMaterialShading FilaMaterial_getShading(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_SHADING_UNLIT;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialShading>(cppMaterial->getShading());
}

FilaMaterialInterpolation FilaMaterial_getInterpolation(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_INTERPOLATION_SMOOTH;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialInterpolation>(cppMaterial->getInterpolation());
}

FilaMaterialBlendingMode FilaMaterial_getBlendingMode(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_BLENDING_MODE_OPAQUE;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialBlendingMode>(cppMaterial->getBlendingMode());
}

FilaMaterialVertexDomain FilaMaterial_getVertexDomain(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_VERTEX_DOMAIN_OBJECT;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialVertexDomain>(cppMaterial->getVertexDomain());
}

uint8_t FilaMaterial_getSupportedVariants(const FilaMaterial* material) {
    if (!material) {
        return 0u;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<uint8_t>(cppMaterial->getSupportedVariants());
}

FilaMaterialDomain FilaMaterial_getMaterialDomain(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_DOMAIN_SURFACE;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialDomain>(cppMaterial->getMaterialDomain());
}

FilaBackendCullingMode FilaMaterial_getCullingMode(const FilaMaterial* material) {
    if (!material) {
        return FILA_BACKEND_CULLING_MODE_NONE;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaBackendCullingMode>(cppMaterial->getCullingMode());
}

FilaMaterialTransparencyMode FilaMaterial_getTransparencyMode(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_TRANSPARENCY_MODE_DEFAULT;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialTransparencyMode>(cppMaterial->getTransparencyMode());
}

bool FilaMaterial_isColorWriteEnabled(const FilaMaterial* material) {
    if (!material) {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->isColorWriteEnabled();
}

bool FilaMaterial_isDepthWriteEnabled(const FilaMaterial* material) {
    if (!material) {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->isDepthWriteEnabled();
}

bool FilaMaterial_isDepthCullingEnabled(const FilaMaterial* material) {
    if (!material) {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->isDepthCullingEnabled();
}

bool FilaMaterial_isDoubleSided(const FilaMaterial* material) {
    if (!material) {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->isDoubleSided();
}

bool FilaMaterial_isAlphaToCoverageEnabled(const FilaMaterial* material) {
    if (!material) {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->isAlphaToCoverageEnabled();
}

float FilaMaterial_getMaskThreshold(const FilaMaterial* material) {
    if (!material) {
        return 0.0f;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->getMaskThreshold();
}

bool FilaMaterial_hasShadowMultiplier(const FilaMaterial* material) {
    if (!material) {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->hasShadowMultiplier();
}

bool FilaMaterial_hasSpecularAntiAliasing(const FilaMaterial* material) {
    if (!material) {
        return false;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->hasSpecularAntiAliasing();
}

float FilaMaterial_getSpecularAntiAliasingVariance(const FilaMaterial* material) {
    if (!material) {
        return 0.0f;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->getSpecularAntiAliasingVariance();
}

float FilaMaterial_getSpecularAntiAliasingThreshold(const FilaMaterial* material) {
    if (!material) {
        return 0.0f;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->getSpecularAntiAliasingThreshold();
}

uint32_t FilaMaterial_getRequiredAttributes(const FilaMaterial* material) {
    if (!material) {
        return 0u;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->getRequiredAttributes().getValue();
}

FilaMaterialRefractionMode FilaMaterial_getRefractionMode(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_REFRACTION_MODE_NONE;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialRefractionMode>(cppMaterial->getRefractionMode());
}

FilaMaterialRefractionType FilaMaterial_getRefractionType(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_REFRACTION_TYPE_SOLID;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialRefractionType>(cppMaterial->getRefractionType());
}

FilaMaterialReflectionMode FilaMaterial_getReflectionMode(const FilaMaterial* material) {
    if (!material) {
        return FILA_MATERIAL_REFLECTION_MODE_DEFAULT;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaMaterialReflectionMode>(cppMaterial->getReflectionMode());
}

FilaBackendFeatureLevel FilaMaterial_getFeatureLevel(const FilaMaterial* material) {
    if (!material) {
        return FILA_BACKEND_FEATURE_LEVEL_0;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return static_cast<FilaBackendFeatureLevel>(cppMaterial->getFeatureLevel());
}

const char* FilaMaterial_getParameterTransformName(const FilaMaterial* material, const char* samplerName) {
    if (!material || !samplerName || samplerName[0] == '\0') {
        return nullptr;
    }
    auto cppMaterial = reinterpret_cast<const filament::Material*>(material);
    return cppMaterial->getParameterTransformName(samplerName);
}

FilaMaterialInstance* FilaMaterial_getDefaultInstance(FilaMaterial* material) {
    if (!material) {
        return nullptr;
    }
    auto cppMaterial = reinterpret_cast<filament::Material*>(material);
    return reinterpret_cast<FilaMaterialInstance*>(cppMaterial->getDefaultInstance());
}

void FilaMaterialInstance_setParameterFloat(FilaMaterialInstance* materialInstance, const char* name, float x) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, x);
}

void FilaMaterialInstance_setParameterFloat2(FilaMaterialInstance* materialInstance, const char* name, float x, float y) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::float2(x, y));
}

void FilaMaterialInstance_setParameterFloat3(FilaMaterialInstance* materialInstance, const char* name, float x, float y, float z) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::float3(x, y, z));
}

void FilaMaterialInstance_setParameterFloat4(FilaMaterialInstance* materialInstance, const char* name, float x, float y, float z, float w) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::float4(x, y, z, w));
}

void FilaMaterialInstance_setParameterInt(FilaMaterialInstance* materialInstance, const char* name, int32_t x) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, x);
}

void FilaMaterialInstance_setParameterInt2(FilaMaterialInstance* materialInstance, const char* name, int32_t x, int32_t y) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::int2(x, y));
}

void FilaMaterialInstance_setParameterInt3(FilaMaterialInstance* materialInstance, const char* name, int32_t x, int32_t y, int32_t z) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::int3(x, y, z));
}

void FilaMaterialInstance_setParameterInt4(FilaMaterialInstance* materialInstance, const char* name, int32_t x, int32_t y, int32_t z, int32_t w) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::int4(x, y, z, w));
}

void FilaMaterialInstance_setParameterUint(FilaMaterialInstance* materialInstance, const char* name, uint32_t x) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, x);
}

void FilaMaterialInstance_setParameterUint2(FilaMaterialInstance* materialInstance, const char* name, uint32_t x, uint32_t y) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::uint2(x, y));
}

void FilaMaterialInstance_setParameterUint3(FilaMaterialInstance* materialInstance, const char* name, uint32_t x, uint32_t y, uint32_t z) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::uint3(x, y, z));
}

void FilaMaterialInstance_setParameterUint4(FilaMaterialInstance* materialInstance, const char* name, uint32_t x, uint32_t y, uint32_t z, uint32_t w) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::uint4(x, y, z, w));
}

void FilaMaterialInstance_setParameterBool(FilaMaterialInstance* materialInstance, const char* name, bool x) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, x);
    cacheBoolParameter(cppMaterialInstance, name, 1u, x, false, false, false);
}

void FilaMaterialInstance_setParameterBool2(FilaMaterialInstance* materialInstance, const char* name, bool x, bool y) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::bool2(x, y));
    cacheBoolParameter(cppMaterialInstance, name, 2u, x, y, false, false);
}

void FilaMaterialInstance_setParameterBool3(FilaMaterialInstance* materialInstance, const char* name, bool x, bool y, bool z) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::bool3(x, y, z));
    cacheBoolParameter(cppMaterialInstance, name, 3u, x, y, z, false);
}

void FilaMaterialInstance_setParameterBool4(FilaMaterialInstance* materialInstance, const char* name, bool x, bool y, bool z, bool w) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, filament::math::bool4(x, y, z, w));
    cacheBoolParameter(cppMaterialInstance, name, 4u, x, y, z, w);
}

void FilaMaterialInstance_setParameterMat3f(FilaMaterialInstance* materialInstance, const char* name, const float value[9]) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name) || !value) {
        return;
    }
    cppMaterialInstance->setParameter(name, toMat3f(value));
}

void FilaMaterialInstance_setParameterMat4f(FilaMaterialInstance* materialInstance, const char* name, const float value[16]) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name) || !value) {
        return;
    }
    cppMaterialInstance->setParameter(name, toMat4f(value));
}

void FilaMaterialInstance_setParameterRgb(FilaMaterialInstance* materialInstance, const char* name, FilaRgbType type, float r, float g, float b) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name,
            static_cast<filament::RgbType>(type), filament::math::float3(r, g, b));
}

void FilaMaterialInstance_setParameterRgba(FilaMaterialInstance* materialInstance, const char* name, FilaRgbaType type, float r, float g, float b, float a) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name,
            static_cast<filament::RgbaType>(type), filament::math::float4(r, g, b, a));
}

void FilaMaterialInstance_setParameterTexture(
        FilaMaterialInstance* materialInstance, const char* name, const FilaTexture* texture,
        const FilaTextureParams* sampler) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name) || !sampler) {
        return;
    }
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    auto cppSampler = reinterpret_cast<const filament::TextureSampler*>(sampler);
    cppMaterialInstance->setParameter(name, cppTexture, *cppSampler);
}

bool FilaMaterialInstance_getParameterFloat(const FilaMaterialInstance* materialInstance, const char* name, float* outX) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outX) {
        return false;
    }
    *outX = cppMaterialInstance->getParameter<float>(name);
    return true;
}

bool FilaMaterialInstance_getParameterFloat2(const FilaMaterialInstance* materialInstance, const char* name, float outValue[2]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::float2 value = cppMaterialInstance->getParameter<filament::math::float2>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    return true;
}

bool FilaMaterialInstance_getParameterFloat3(const FilaMaterialInstance* materialInstance, const char* name, float outValue[3]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::float3 value = cppMaterialInstance->getParameter<filament::math::float3>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    outValue[2] = value.z;
    return true;
}

bool FilaMaterialInstance_getParameterFloat4(const FilaMaterialInstance* materialInstance, const char* name, float outValue[4]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::float4 value = cppMaterialInstance->getParameter<filament::math::float4>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    outValue[2] = value.z;
    outValue[3] = value.w;
    return true;
}

bool FilaMaterialInstance_getParameterInt(const FilaMaterialInstance* materialInstance, const char* name, int32_t* outX) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outX) {
        return false;
    }
    *outX = cppMaterialInstance->getParameter<int32_t>(name);
    return true;
}

bool FilaMaterialInstance_getParameterInt2(const FilaMaterialInstance* materialInstance, const char* name, int32_t outValue[2]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::int2 value = cppMaterialInstance->getParameter<filament::math::int2>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    return true;
}

bool FilaMaterialInstance_getParameterInt3(const FilaMaterialInstance* materialInstance, const char* name, int32_t outValue[3]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::int3 value = cppMaterialInstance->getParameter<filament::math::int3>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    outValue[2] = value.z;
    return true;
}

bool FilaMaterialInstance_getParameterInt4(const FilaMaterialInstance* materialInstance, const char* name, int32_t outValue[4]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::int4 value = cppMaterialInstance->getParameter<filament::math::int4>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    outValue[2] = value.z;
    outValue[3] = value.w;
    return true;
}

bool FilaMaterialInstance_getParameterUint(const FilaMaterialInstance* materialInstance, const char* name, uint32_t* outX) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outX) {
        return false;
    }
    *outX = cppMaterialInstance->getParameter<uint32_t>(name);
    return true;
}

bool FilaMaterialInstance_getParameterUint2(const FilaMaterialInstance* materialInstance, const char* name, uint32_t outValue[2]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::uint2 value = cppMaterialInstance->getParameter<filament::math::uint2>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    return true;
}

bool FilaMaterialInstance_getParameterUint3(const FilaMaterialInstance* materialInstance, const char* name, uint32_t outValue[3]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::uint3 value = cppMaterialInstance->getParameter<filament::math::uint3>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    outValue[2] = value.z;
    return true;
}

bool FilaMaterialInstance_getParameterUint4(const FilaMaterialInstance* materialInstance, const char* name, uint32_t outValue[4]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::uint4 value = cppMaterialInstance->getParameter<filament::math::uint4>(name);
    outValue[0] = value.x;
    outValue[1] = value.y;
    outValue[2] = value.z;
    outValue[3] = value.w;
    return true;
}

bool FilaMaterialInstance_getParameterBool(const FilaMaterialInstance* materialInstance, const char* name, bool* outX) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outX) {
        return false;
    }
    bool cached[1] = {false};
    if (!readCachedBoolParameter(cppMaterialInstance, name, 1u, cached)) {
        return false;
    }
    *outX = cached[0];
    return true;
}

bool FilaMaterialInstance_getParameterBool2(const FilaMaterialInstance* materialInstance, const char* name, bool outValue[2]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return canGetParameter(cppMaterialInstance, name) && outValue &&
            readCachedBoolParameter(cppMaterialInstance, name, 2u, outValue);
}

bool FilaMaterialInstance_getParameterBool3(const FilaMaterialInstance* materialInstance, const char* name, bool outValue[3]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return canGetParameter(cppMaterialInstance, name) && outValue &&
            readCachedBoolParameter(cppMaterialInstance, name, 3u, outValue);
}

bool FilaMaterialInstance_getParameterBool4(const FilaMaterialInstance* materialInstance, const char* name, bool outValue[4]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return canGetParameter(cppMaterialInstance, name) && outValue &&
            readCachedBoolParameter(cppMaterialInstance, name, 4u, outValue);
}


bool FilaMaterialInstance_getParameterMat3f(const FilaMaterialInstance* materialInstance, const char* name, float outValue[9]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::mat3f value = cppMaterialInstance->getParameter<filament::math::mat3f>(name);
    fromMat3f(value, outValue);
    return true;
}

bool FilaMaterialInstance_getParameterMat4f(const FilaMaterialInstance* materialInstance, const char* name, float outValue[16]) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!canGetParameter(cppMaterialInstance, name) || !outValue) {
        return false;
    }
    const filament::math::mat4f value = cppMaterialInstance->getParameter<filament::math::mat4f>(name);
    fromMat4f(value, outValue);
    return true;
}

void FilaMaterialInstance_setScissor(FilaMaterialInstance* materialInstance, uint32_t left, uint32_t bottom, uint32_t width, uint32_t height) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setScissor(left, bottom, width, height);
}

void FilaMaterialInstance_unsetScissor(FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->unsetScissor();
}

void FilaMaterialInstance_setPolygonOffset(FilaMaterialInstance* materialInstance, float scale, float constant) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setPolygonOffset(scale, constant);
}

void FilaMaterialInstance_setMaskThreshold(FilaMaterialInstance* materialInstance, float threshold) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setMaskThreshold(threshold);
}

float FilaMaterialInstance_getMaskThreshold(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppMaterialInstance ? cppMaterialInstance->getMaskThreshold() : 0.0f;
}

void FilaMaterialInstance_setSpecularAntiAliasingVariance(FilaMaterialInstance* materialInstance, float variance) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setSpecularAntiAliasingVariance(variance);
}

float FilaMaterialInstance_getSpecularAntiAliasingVariance(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppMaterialInstance ? cppMaterialInstance->getSpecularAntiAliasingVariance() : 0.0f;
}

void FilaMaterialInstance_setSpecularAntiAliasingThreshold(FilaMaterialInstance* materialInstance, float threshold) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setSpecularAntiAliasingThreshold(threshold);
}

float FilaMaterialInstance_getSpecularAntiAliasingThreshold(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppMaterialInstance ? cppMaterialInstance->getSpecularAntiAliasingThreshold() : 0.0f;
}

void FilaMaterialInstance_setDoubleSided(FilaMaterialInstance* materialInstance, bool doubleSided) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setDoubleSided(doubleSided);
}

bool FilaMaterialInstance_isDoubleSided(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppMaterialInstance ? cppMaterialInstance->isDoubleSided() : false;
}

void FilaMaterialInstance_setTransparencyMode(FilaMaterialInstance* materialInstance, FilaMaterialTransparencyMode mode) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setTransparencyMode(static_cast<filament::TransparencyMode>(mode));
}

FilaMaterialTransparencyMode FilaMaterialInstance_getTransparencyMode(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return FILA_MATERIAL_TRANSPARENCY_MODE_DEFAULT;
    }
    return static_cast<FilaMaterialTransparencyMode>(cppMaterialInstance->getTransparencyMode());
}

void FilaMaterialInstance_setCullingMode(FilaMaterialInstance* materialInstance, FilaBackendCullingMode cullingMode) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setCullingMode(static_cast<filament::backend::CullingMode>(cullingMode));
}

void FilaMaterialInstance_setCullingModeSeparate(
        FilaMaterialInstance* materialInstance,
        FilaBackendCullingMode colorPassCullingMode,
        FilaBackendCullingMode shadowPassCullingMode) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setCullingMode(
            static_cast<filament::backend::CullingMode>(colorPassCullingMode),
            static_cast<filament::backend::CullingMode>(shadowPassCullingMode));
}

FilaBackendCullingMode FilaMaterialInstance_getCullingMode(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return FILA_BACKEND_CULLING_MODE_NONE;
    }
    return static_cast<FilaBackendCullingMode>(cppMaterialInstance->getCullingMode());
}

FilaBackendCullingMode FilaMaterialInstance_getShadowCullingMode(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return FILA_BACKEND_CULLING_MODE_NONE;
    }
    return static_cast<FilaBackendCullingMode>(cppMaterialInstance->getShadowCullingMode());
}

void FilaMaterialInstance_setColorWrite(FilaMaterialInstance* materialInstance, bool enable) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setColorWrite(enable);
}

bool FilaMaterialInstance_isColorWriteEnabled(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppMaterialInstance ? cppMaterialInstance->isColorWriteEnabled() : false;
}

void FilaMaterialInstance_setDepthWrite(FilaMaterialInstance* materialInstance, bool enable) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setDepthWrite(enable);
}

bool FilaMaterialInstance_isDepthWriteEnabled(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppMaterialInstance ? cppMaterialInstance->isDepthWriteEnabled() : false;
}

void FilaMaterialInstance_setDepthCulling(FilaMaterialInstance* materialInstance, bool enable) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setDepthCulling(enable);
}

bool FilaMaterialInstance_isDepthCullingEnabled(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppMaterialInstance ? cppMaterialInstance->isDepthCullingEnabled() : false;
}

void FilaMaterialInstance_setDepthFunc(FilaMaterialInstance* materialInstance, FilaBackendSamplerCompareFunc depthFunc) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setDepthFunc(static_cast<filament::backend::SamplerCompareFunc>(depthFunc));
}

FilaBackendSamplerCompareFunc FilaMaterialInstance_getDepthFunc(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return FILA_BACKEND_SAMPLER_COMPARE_FUNC_LE;
    }
    return static_cast<FilaBackendSamplerCompareFunc>(cppMaterialInstance->getDepthFunc());
}

void FilaMaterialInstance_setStencilWrite(FilaMaterialInstance* materialInstance, bool enable) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setStencilWrite(enable);
}

bool FilaMaterialInstance_isStencilWriteEnabled(const FilaMaterialInstance* materialInstance) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return cppMaterialInstance ? cppMaterialInstance->isStencilWriteEnabled() : false;
}

void FilaMaterialInstance_setStencilCompareFunction(
        FilaMaterialInstance* materialInstance,
        FilaBackendSamplerCompareFunc func,
        FilaBackendStencilFace face) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setStencilCompareFunction(
            static_cast<filament::backend::SamplerCompareFunc>(func),
            static_cast<filament::backend::StencilFace>(face));
}

void FilaMaterialInstance_setStencilOpStencilFail(
        FilaMaterialInstance* materialInstance,
        FilaBackendStencilOperation op,
        FilaBackendStencilFace face) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setStencilOpStencilFail(
            static_cast<filament::backend::StencilOperation>(op),
            static_cast<filament::backend::StencilFace>(face));
}

void FilaMaterialInstance_setStencilOpDepthFail(
        FilaMaterialInstance* materialInstance,
        FilaBackendStencilOperation op,
        FilaBackendStencilFace face) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setStencilOpDepthFail(
            static_cast<filament::backend::StencilOperation>(op),
            static_cast<filament::backend::StencilFace>(face));
}

void FilaMaterialInstance_setStencilOpDepthStencilPass(
        FilaMaterialInstance* materialInstance,
        FilaBackendStencilOperation op,
        FilaBackendStencilFace face) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setStencilOpDepthStencilPass(
            static_cast<filament::backend::StencilOperation>(op),
            static_cast<filament::backend::StencilFace>(face));
}

void FilaMaterialInstance_setStencilReferenceValue(
        FilaMaterialInstance* materialInstance,
        uint8_t value,
        FilaBackendStencilFace face) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setStencilReferenceValue(value, static_cast<filament::backend::StencilFace>(face));
}

void FilaMaterialInstance_setStencilReadMask(
        FilaMaterialInstance* materialInstance,
        uint8_t readMask,
        FilaBackendStencilFace face) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setStencilReadMask(readMask, static_cast<filament::backend::StencilFace>(face));
}

void FilaMaterialInstance_setStencilWriteMask(
        FilaMaterialInstance* materialInstance,
        uint8_t writeMask,
        FilaBackendStencilFace face) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!cppMaterialInstance) {
        return;
    }
    cppMaterialInstance->setStencilWriteMask(writeMask, static_cast<filament::backend::StencilFace>(face));
}

void FilaMaterialInstance_commit(const FilaMaterialInstance* materialInstance, FilaEngine* engine) {
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    if (!cppMaterialInstance || !cppEngine) {
        return;
    }
    cppMaterialInstance->commit(*cppEngine);
}

} // extern "C"


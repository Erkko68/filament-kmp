#include <filament/Material.h>
#include <filament/MaterialInstance.h>
#include <filament/Engine.h>

#include <utils/Entity.h>
#include <utils/EntityManager.h>

#include <assert.h>

#include "FilaCommon.h"
#include "../c/Material.h"

using namespace filament;

extern "C" {

FilaMaterial_Builder* FilaMaterial_Builder_create() {
    return reinterpret_cast<FilaMaterial_Builder*>(new Material::Builder());
}

void FilaMaterial_Builder_destroy(FilaMaterial_Builder* builder) {
    delete reinterpret_cast<Material::Builder*>(builder);
}

void FilaMaterial_Builder_package(FilaMaterial_Builder* builder, const void* payload, size_t size) {
    reinterpret_cast<Material::Builder*>(builder)->package(payload, size);
}

void FilaMaterial_Builder_sphericalHarmonicsBandCount(FilaMaterial_Builder* builder, int count) {
    reinterpret_cast<Material::Builder*>(builder)->sphericalHarmonicsBandCount(count);
}

void FilaMaterial_Builder_shadowSamplingQuality(FilaMaterial_Builder* builder, FilaMaterialShadowSamplingQuality quality) {
    reinterpret_cast<Material::Builder*>(builder)->shadowSamplingQuality(
        static_cast<Material::ShadowSamplingQuality>(quality));
}

void FilaMaterial_Builder_uboBatching(FilaMaterial_Builder* builder, FilaMaterialUboBatchingMode mode) {
    reinterpret_cast<Material::Builder*>(builder)->uboBatching(
        static_cast<Material::UboBatchingMode>(mode));
}

FilaMaterial* FilaMaterial_Builder_build(FilaMaterial_Builder* builder, FilaEngine* engine) {
    return reinterpret_cast<FilaMaterial*>(
        reinterpret_cast<Material::Builder*>(builder)->build(*FILA_CAST(Engine, engine))
    );
}

FilaMaterialInstance* FilaMaterial_getDefaultInstance(const FilaMaterial* material) {
    return reinterpret_cast<FilaMaterialInstance*>(
        const_cast<MaterialInstance*>(FILA_CONST_CAST(Material, material)->getDefaultInstance())
    );
}

FilaMaterialInstance* FilaMaterial_createInstance(FilaMaterial* material) {
    return reinterpret_cast<FilaMaterialInstance*>(FILA_CAST(Material, material)->createInstance());
}

FilaMaterialInstance* FilaMaterial_createInstanceWithName(FilaMaterial* material, const char* name) {
    return reinterpret_cast<FilaMaterialInstance*>(FILA_CAST(Material, material)->createInstance(name));
}

const char* FilaMaterial_getName(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->getName();
}

FilaMaterialShading FilaMaterial_getShading(const FilaMaterial* material) {
    return static_cast<FilaMaterialShading>(FILA_CONST_CAST(Material, material)->getShading());
}

FilaMaterialInterpolation FilaMaterial_getInterpolation(const FilaMaterial* material) {
    return static_cast<FilaMaterialInterpolation>(FILA_CONST_CAST(Material, material)->getInterpolation());
}

FilaMaterialBlendingMode FilaMaterial_getBlendingMode(const FilaMaterial* material) {
    return static_cast<FilaMaterialBlendingMode>(FILA_CONST_CAST(Material, material)->getBlendingMode());
}

FilaMaterialTransparencyMode FilaMaterial_getTransparencyMode(const FilaMaterial* material) {
    return static_cast<FilaMaterialTransparencyMode>(FILA_CONST_CAST(Material, material)->getTransparencyMode());
}

int FilaMaterial_getRefractionMode(const FilaMaterial* material) {
    return static_cast<int>(FILA_CONST_CAST(Material, material)->getRefractionMode());
}

int FilaMaterial_getRefractionType(const FilaMaterial* material) {
    return static_cast<int>(FILA_CONST_CAST(Material, material)->getRefractionType());
}

int FilaMaterial_getReflectionMode(const FilaMaterial* material) {
    return static_cast<int>(FILA_CONST_CAST(Material, material)->getReflectionMode());
}

FilaMaterialVertexDomain FilaMaterial_getVertexDomain(const FilaMaterial* material) {
    return static_cast<FilaMaterialVertexDomain>(FILA_CONST_CAST(Material, material)->getVertexDomain());
}

FilaMaterialCullingMode FilaMaterial_getCullingMode(const FilaMaterial* material) {
    return static_cast<FilaMaterialCullingMode>(FILA_CONST_CAST(Material, material)->getCullingMode());
}

bool FilaMaterial_isColorWriteEnabled(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->isColorWriteEnabled();
}

bool FilaMaterial_isDepthWriteEnabled(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->isDepthWriteEnabled();
}

bool FilaMaterial_isDepthCullingEnabled(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->isDepthCullingEnabled();
}

bool FilaMaterial_isDoubleSided(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->isDoubleSided();
}

bool FilaMaterial_isAlphaToCoverageEnabled(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->isAlphaToCoverageEnabled();
}

float FilaMaterial_getMaskThreshold(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->getMaskThreshold();
}

float FilaMaterial_getSpecularAntiAliasingVariance(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->getSpecularAntiAliasingVariance();
}

float FilaMaterial_getSpecularAntiAliasingThreshold(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->getSpecularAntiAliasingThreshold();
}
 
FilaEngineFeatureLevel FilaMaterial_getFeatureLevel(const FilaMaterial* material) {
    return static_cast<FilaEngineFeatureLevel>(FILA_CONST_CAST(Material, material)->getFeatureLevel());
}

uint32_t FilaMaterial_getParameterCount(const FilaMaterial* material) {
    return static_cast<uint32_t>(FILA_CONST_CAST(Material, material)->getParameterCount());
}

uint32_t FilaMaterial_getRequiredAttributes(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->getRequiredAttributes().getValue();
}

void FilaMaterial_compile(FilaMaterial* material, FilaMaterialCompilerPriorityQueue priority, uint32_t variants, void* handler, FilaMaterialCompileCallback callback, void* userData) {
    FILA_CAST(Material, material)->compile(
        static_cast<Material::CompilerPriorityQueue>(priority),
        UserVariantFilterMask(variants),
        reinterpret_cast<filament::backend::CallbackHandler*>(handler),
        [callback, userData](Material* m) {
            if (callback) {
                callback(reinterpret_cast<FilaMaterial*>(m), userData);
            }
        }
    );
}

} // extern "C"

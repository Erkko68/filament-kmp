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

FilaMaterial* FilaMaterial_Builder_build(FilaEngine* engine, const void* payload, size_t size) {
    return reinterpret_cast<FilaMaterial*>(
        Material::Builder()
            .package(payload, size)
            .build(*FILA_CAST(Engine, engine))
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

int FilaMaterial_getFeatureLevel(const FilaMaterial* material) {
    return static_cast<int>(FILA_CONST_CAST(Material, material)->getFeatureLevel());
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

uint32_t FilaMaterial_getParameterCount(const FilaMaterial* material) {
    return static_cast<uint32_t>(FILA_CONST_CAST(Material, material)->getParameterCount());
}

void FilaMaterial_getParameters(const FilaMaterial* material, FilaMaterialParameterInfo* out, uint32_t count) {
    Material::ParameterInfo* info = new Material::ParameterInfo[count];
    size_t received = FILA_CONST_CAST(Material, material)->getParameters(info, (size_t)count);
    assert(received == count);

    for (uint32_t i = 0; i < count; i++) {
        out[i].name = info[i].name;
        if (info[i].isSampler) {
            out[i].type = static_cast<FilaMaterialParameterType>(static_cast<int>(info[i].samplerType) + 100);
        } else if (info[i].isSubpass) {
            out[i].type = FILA_MATERIAL_PARAMETER_TYPE_SUBPASS_INPUT;
        } else {
            out[i].type = static_cast<FilaMaterialParameterType>(info[i].type);
        }
        out[i].precision = static_cast<FilaMaterialPrecision>(info[i].precision);
        out[i].count = static_cast<uint32_t>(info[i].count);
    }

    delete[] info;
}

uint32_t FilaMaterial_getRequiredAttributes(const FilaMaterial* material) {
    return FILA_CONST_CAST(Material, material)->getRequiredAttributes().getValue();
}

bool FilaMaterial_hasParameter(const FilaMaterial* material, const char* name) {
    return FILA_CONST_CAST(Material, material)->hasParameter(name);
}

const char* FilaMaterial_getParameterTransformName(const FilaMaterial* material, const char* samplerName) {
    return FILA_CONST_CAST(Material, material)->getParameterTransformName(samplerName);
}

void FilaMaterial_compile(FilaMaterial* material, int priority, int variants, void* handler, FilaMaterialCompileCallback callback, void* userData) {
    // Note: C-wrapper needs to define how FilaCallbackHandler maps to filament::backend::CallbackHandler
    // For now, we assume the user provides a valid handler if needed.
    FILA_CAST(Material, material)->compile(
        static_cast<Material::CompilerPriorityQueue>(priority),
        static_cast<UserVariantFilterBit>(variants),
        reinterpret_cast<filament::backend::CallbackHandler*>(handler),
        [callback, userData](Material* m) {
            if (callback) {
                callback(reinterpret_cast<FilaMaterial*>(m), userData);
            }
        }
    );
}

} // extern "C"

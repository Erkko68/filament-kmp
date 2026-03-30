#include <filament/Engine.h>
#include <filament/Material.h>
#include <filament/MaterialInstance.h>

#include <math/vec2.h>
#include <math/vec3.h>
#include <math/vec4.h>

#include "filament/Material.h"

namespace {
using MaterialBuilder = filament::Material::Builder;

bool canSetParameter(const filament::MaterialInstance* materialInstance, const char* name) {
    if (!materialInstance || !name || name[0] == '\0') {
        return false;
    }
    const filament::Material* material = materialInstance->getMaterial();
    return material && material->hasParameter(name);
}
} // namespace

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

const FilaMaterial* FilaMaterialInstance_getMaterial(const FilaMaterialInstance* materialInstance) {
    if (!materialInstance) {
        return nullptr;
    }
    auto cppMaterialInstance = reinterpret_cast<const filament::MaterialInstance*>(materialInstance);
    return reinterpret_cast<const FilaMaterial*>(cppMaterialInstance->getMaterial());
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

void FilaMaterialInstance_setParameterUint(FilaMaterialInstance* materialInstance, const char* name, uint32_t x) {
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    if (!canSetParameter(cppMaterialInstance, name)) {
        return;
    }
    cppMaterialInstance->setParameter(name, x);
}

} // extern "C"


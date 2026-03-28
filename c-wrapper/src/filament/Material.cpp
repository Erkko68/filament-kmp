#include <filament/Engine.h>
#include <filament/Material.h>
#include <filament/MaterialInstance.h>

#include "../../../include/filament/Material.h"

namespace {
using MaterialBuilder = filament::Material::Builder;
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

} // extern "C"


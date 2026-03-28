#include <stddef.h>

#include "filament/Engine.h"
#include "filament/Material.h"

// Verifies Material builder and instance creation APIs are consumable from C.
void fila_material_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaMaterialBuilder* builder = FilaMaterialBuilder_create();
    const unsigned char fakePackage[4] = {0, 0, 0, 0};
    FilaMaterialBuilder_package(builder, fakePackage, sizeof(fakePackage));
    FilaMaterial* material = FilaMaterialBuilder_build(builder, engine);
    FilaMaterialInstance* materialInstance = FilaMaterial_createInstance(material);
    FilaMaterialBuilder_destroy(builder);
    FilaEngine_destroyMaterialInstance(engine, materialInstance);
    FilaEngine_destroyMaterial(engine, material);
}


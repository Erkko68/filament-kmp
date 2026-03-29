#include <stddef.h>

#include "filament/Engine.h"
#include "filament/Material.h"
#include "filament/MaterialInstance.h"

// Verifies Material builder and instance creation APIs are consumable from C.
void fila_material_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaMaterialBuilder* builder = FilaMaterialBuilder_create();
    const unsigned char fakePackage[4] = {0, 0, 0, 0};
    FilaMaterialBuilder_package(builder, fakePackage, sizeof(fakePackage));
    FilaMaterial* material = FilaMaterialBuilder_build(builder, engine);
    FilaMaterialInstance* materialInstance = FilaMaterial_createInstance(material);
    (void)FilaMaterialInstance_getMaterial(materialInstance);
    FilaMaterialInstance_setParameterFloat(materialInstance, "uFloat", 1.0f);
    FilaMaterialInstance_setParameterFloat2(materialInstance, "uFloat2", 1.0f, 2.0f);
    FilaMaterialInstance_setParameterFloat3(materialInstance, "uFloat3", 1.0f, 2.0f, 3.0f);
    FilaMaterialInstance_setParameterFloat4(materialInstance, "uFloat4", 1.0f, 2.0f, 3.0f, 4.0f);
    FilaMaterialInstance_setParameterInt(materialInstance, "uInt", -1);
    FilaMaterialInstance_setParameterUint(materialInstance, "uUint", 1u);
    FilaMaterialBuilder_destroy(builder);
    FilaEngine_destroyMaterialInstance(engine, materialInstance);
    FilaEngine_destroyMaterial(engine, material);
}


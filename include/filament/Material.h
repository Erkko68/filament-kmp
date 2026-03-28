#ifndef FILAMENT_C_MATERIAL_H
#define FILAMENT_C_MATERIAL_H

#include <stdbool.h>
#include <stddef.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaMaterialBuilder* FilaMaterialBuilder_create(void);
void FilaMaterialBuilder_destroy(FilaMaterialBuilder* builder);
void FilaMaterialBuilder_package(FilaMaterialBuilder* builder, const void* payload, size_t size);
FilaMaterial* FilaMaterialBuilder_build(const FilaMaterialBuilder* builder, FilaEngine* engine);

FilaMaterialInstance* FilaMaterial_createInstance(const FilaMaterial* material);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_MATERIAL_H


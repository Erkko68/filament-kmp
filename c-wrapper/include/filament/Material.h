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
FilaMaterialInstance* FilaMaterial_createInstanceNamed(const FilaMaterial* material, const char* name);

const char* FilaMaterial_getName(const FilaMaterial* material);
bool FilaMaterial_hasParameter(const FilaMaterial* material, const char* name);
bool FilaMaterial_isSampler(const FilaMaterial* material, const char* name);
size_t FilaMaterial_getParameterCount(const FilaMaterial* material);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_MATERIAL_H


#ifndef FILAMENT_C_MATERIAL_INSTANCE_H
#define FILAMENT_C_MATERIAL_INSTANCE_H

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns the parent material for this instance.
const FilaMaterial* FilaMaterialInstance_getMaterial(const FilaMaterialInstance* materialInstance);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_MATERIAL_INSTANCE_H


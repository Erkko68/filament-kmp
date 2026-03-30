#ifndef FILAMENT_C_DEBUGREGISTRY_H
#define FILAMENT_C_DEBUGREGISTRY_H

#include "Types.h"
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

bool FilaDebugRegistry_hasProperty(const FilaDebugRegistry* debugRegistry, const char* name);
void* FilaDebugRegistry_getPropertyAddress(FilaDebugRegistry* debugRegistry, const char* name);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_DEBUGREGISTRY_H
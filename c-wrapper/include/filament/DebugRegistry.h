#ifndef FILAMENT_C_DEBUGREGISTRY_H
#define FILAMENT_C_DEBUGREGISTRY_H

#include "Types.h"
#include <stdbool.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

bool FilaDebugRegistry_hasProperty(const FilaDebugRegistry* debugRegistry, const char* name);
void* FilaDebugRegistry_getPropertyAddress(FilaDebugRegistry* debugRegistry, const char* name);
const void* FilaDebugRegistry_getPropertyAddressConst(const FilaDebugRegistry* debugRegistry, const char* name);

bool FilaDebugRegistry_setPropertyBool(FilaDebugRegistry* debugRegistry, const char* name, bool value);
bool FilaDebugRegistry_setPropertyInt(FilaDebugRegistry* debugRegistry, const char* name, int value);
bool FilaDebugRegistry_setPropertyFloat(FilaDebugRegistry* debugRegistry, const char* name, float value);
bool FilaDebugRegistry_setPropertyFloat2(FilaDebugRegistry* debugRegistry, const char* name, const float value[2]);
bool FilaDebugRegistry_setPropertyFloat3(FilaDebugRegistry* debugRegistry, const char* name, const float value[3]);
bool FilaDebugRegistry_setPropertyFloat4(FilaDebugRegistry* debugRegistry, const char* name, const float value[4]);

bool FilaDebugRegistry_getPropertyBool(const FilaDebugRegistry* debugRegistry, const char* name, bool* outValue);
bool FilaDebugRegistry_getPropertyInt(const FilaDebugRegistry* debugRegistry, const char* name, int* outValue);
bool FilaDebugRegistry_getPropertyFloat(const FilaDebugRegistry* debugRegistry, const char* name, float* outValue);
bool FilaDebugRegistry_getPropertyFloat2(const FilaDebugRegistry* debugRegistry, const char* name, float outValue[2]);
bool FilaDebugRegistry_getPropertyFloat3(const FilaDebugRegistry* debugRegistry, const char* name, float outValue[3]);
bool FilaDebugRegistry_getPropertyFloat4(const FilaDebugRegistry* debugRegistry, const char* name, float outValue[4]);

bool FilaDebugRegistry_getDataSource(
		const FilaDebugRegistry* debugRegistry,
		const char* name,
		const void** outData,
		size_t* outCount);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_DEBUGREGISTRY_H
#ifndef FILAMENT_C_SYNC_H
#define FILAMENT_C_SYNC_H

#include "Types.h"
#include <stdbool.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef void (*FilaSyncCallback)(FilaSync* sync, void* userData);

// Requests the external sync handle and invokes callback on Filament's default callback handler.
bool FilaSync_getExternalHandle(FilaSync* sync, FilaSyncCallback callback, void* userData);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SYNC_H
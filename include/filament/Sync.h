#ifndef FILAMENT_C_SYNC_H
#define FILAMENT_C_SYNC_H

#include "Types.h"
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Sync Object - representation of filament::Sync
// Note: Callback handlers and complex cross-platform syncs are usually part of advanced parity (Batch H).
// For now we add this opaque type and a simple API surface if possible.
// Because Sync uses backend::CallbackHandler and backend::Platform::SyncCallback,
// we will just define an opaque C callback type.

typedef void (*FilaSyncCallback)(FilaSync* sync, void* userData);

// The getExternalHandle takes a backend::CallbackHandler, which is typically tied to an OS thread/looper.
// Since we don't have a C wrapper for CallbackHandler yet, we will expose this method loosely or leave it incomplete until Batch H.

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SYNC_H
#ifndef FILAMENT_C_UTILS_PANIC_H
#define FILAMENT_C_UTILS_PANIC_H

#include <stddef.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef void (*FilaUtilsPanicHandlerCallback)(void* userData, const FilaUtilsPanic* panic);

// Registers a process-global panic callback bridge.
void FilaUtilsPanic_setHandler(FilaUtilsPanicHandlerCallback callback, void* userData);

const char* FilaUtilsPanic_getType(const FilaUtilsPanic* panic);
const char* FilaUtilsPanic_getReason(const FilaUtilsPanic* panic);
const char* FilaUtilsPanic_getReasonLiteral(const FilaUtilsPanic* panic);
const char* FilaUtilsPanic_getFunction(const FilaUtilsPanic* panic);
const char* FilaUtilsPanic_getFile(const FilaUtilsPanic* panic);
int FilaUtilsPanic_getLine(const FilaUtilsPanic* panic);

size_t FilaUtilsPanic_getCallStackFrameCount(const FilaUtilsPanic* panic);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_PANIC_H


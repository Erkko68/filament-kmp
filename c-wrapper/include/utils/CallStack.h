#ifndef FILAMENT_C_UTILS_CALL_STACK_H
#define FILAMENT_C_UTILS_CALL_STACK_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaUtilsCallStack* FilaUtilsCallStack_create(void);
void FilaUtilsCallStack_destroy(FilaUtilsCallStack* callStack);

// Captures current thread frames into callStack.
void FilaUtilsCallStack_update(FilaUtilsCallStack* callStack, size_t ignoreFrames);

// Convenience constructor + capture.
FilaUtilsCallStack* FilaUtilsCallStack_unwind(size_t ignoreFrames);

size_t FilaUtilsCallStack_getFrameCount(const FilaUtilsCallStack* callStack);
bool FilaUtilsCallStack_getFramePc(const FilaUtilsCallStack* callStack, size_t index, intptr_t* outPc);

// Demangles a type name and copies into outText, returning source length.
size_t FilaUtilsCallStack_demangleTypeName(const char* mangled, char* outText, size_t outTextSize);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_CALL_STACK_H


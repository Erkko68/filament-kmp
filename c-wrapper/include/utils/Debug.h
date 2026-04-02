#ifndef FILAMENT_C_UTILS_DEBUG_H
#define FILAMENT_C_UTILS_DEBUG_H

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

// Returns true when condition holds; otherwise triggers Filament panic and does not return.
bool FilaUtilsDebug_assertInvariant(
        bool condition,
        const char* func,
        const char* file,
        int line,
        const char* assertion);

// Forwards to Filament panic implementation; this function does not return.
void FilaUtilsDebug_panic(
        const char* func,
        const char* file,
        int line,
        const char* assertion);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_DEBUG_H


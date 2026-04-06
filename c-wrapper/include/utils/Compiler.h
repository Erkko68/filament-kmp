#ifndef FILAMENT_C_UTILS_COMPILER_H
#define FILAMENT_C_UTILS_COMPILER_H

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

bool FilaUtilsCompiler_hasThreading(void);
bool FilaUtilsCompiler_hasRtti(void);
bool FilaUtilsCompiler_hasHyperThreading(void);
bool FilaUtilsCompiler_hasSanitizeThread(void);
bool FilaUtilsCompiler_hasSanitizeMemory(void);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_COMPILER_H


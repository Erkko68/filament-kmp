#ifndef FILAMENT_C_UTILS_MEMALIGN_H
#define FILAMENT_C_UTILS_MEMALIGN_H

#include <stdbool.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

void* FilaUtils_alignedAlloc(size_t size, size_t align);
void FilaUtils_alignedFree(void* ptr);
bool FilaUtils_isAligned(const void* ptr, size_t align);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_MEMALIGN_H


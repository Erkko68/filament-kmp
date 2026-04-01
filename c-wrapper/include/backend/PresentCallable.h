#ifndef FILAMENT_C_BACKEND_PRESENTCALLABLE_H
#define FILAMENT_C_BACKEND_PRESENTCALLABLE_H

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaBackendPresentCallable FilaBackendPresentCallable;

typedef void (*FilaBackendPresentFn)(bool presentFrame, void* user);

FilaBackendPresentCallable* FilaBackendPresentCallable_create(FilaBackendPresentFn fn, void* user);
void FilaBackendPresentCallable_destroy(FilaBackendPresentCallable* callable);

void FilaBackendPresentCallable_invoke(FilaBackendPresentCallable* callable, bool presentFrame);
bool FilaBackendPresentCallable_isValid(const FilaBackendPresentCallable* callable);

void* FilaBackendPresentCallable_getUser(const FilaBackendPresentCallable* callable);
FilaBackendPresentFn FilaBackendPresentCallable_getFunction(const FilaBackendPresentCallable* callable);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PRESENTCALLABLE_H


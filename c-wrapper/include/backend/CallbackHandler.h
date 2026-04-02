#ifndef FILAMENT_C_BACKEND_CALLBACKHANDLER_H
#define FILAMENT_C_BACKEND_CALLBACKHANDLER_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stdint.h>

typedef struct FilaCallbackHandler FilaCallbackHandler;

typedef void (*FilaCallbackHandlerCallback)(void* user);
typedef void (*FilaCallbackHandlerDispatcher)(
    void* callbackUser,
    FilaCallbackHandlerCallback callback,
    void* handlerUser);

typedef void (*FilaCallbackHandlerDispatcherToken)(
    uintptr_t callbackUserToken,
    FilaCallbackHandlerCallback callback,
    uintptr_t handlerUserToken);

// Creates a callback handler that forwards callback dispatch to the provided dispatcher.
FilaCallbackHandler* FilaCallbackHandler_create(
    FilaCallbackHandlerDispatcher dispatcher,
    void* handlerUser);

// Token-based variant for Kotlin/Native-friendly callback userdata.
FilaCallbackHandler* FilaCallbackHandler_createWithToken(
    FilaCallbackHandlerDispatcherToken dispatcher,
    uintptr_t handlerUserToken);

// Destroys a callback handler created with FilaCallbackHandler_create.
void FilaCallbackHandler_destroy(FilaCallbackHandler* handler);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_CALLBACKHANDLER_H


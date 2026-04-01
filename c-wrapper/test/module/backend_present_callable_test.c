#include "backend/PresentCallable.h"
#include <stddef.h>

static void module_present_callback(bool presentFrame, void* user) {
    int* value = (int*) user;
    *value += presentFrame ? 1 : 2;
}

void backend_present_callable_test(void) {
    int value = 0;
    FilaBackendPresentCallable* callable =
        FilaBackendPresentCallable_create(module_present_callback, &value);

    FilaBackendPresentCallable_isValid(callable);
    FilaBackendPresentCallable_getUser(callable);
    FilaBackendPresentCallable_getFunction(callable);

    FilaBackendPresentCallable_invoke(callable, true);
    FilaBackendPresentCallable_invoke(callable, false);

    FilaBackendPresentCallable_destroy(callable);

    callable = FilaBackendPresentCallable_create(NULL, NULL);
    FilaBackendPresentCallable_invoke(callable, true);
    FilaBackendPresentCallable_destroy(callable);
}

#include <stdio.h>

#include "utils/Panic.h"

typedef struct PanicState {
    size_t callbackCount;
} PanicState;

static void on_panic(void* userData, const FilaUtilsPanic* panic) {
    PanicState* state = (PanicState*)userData;
    if (state) {
        state->callbackCount += 1u;
    }
    (void)FilaUtilsPanic_getType(panic);
    (void)FilaUtilsPanic_getReason(panic);
    (void)FilaUtilsPanic_getReasonLiteral(panic);
    (void)FilaUtilsPanic_getFunction(panic);
    (void)FilaUtilsPanic_getFile(panic);
    (void)FilaUtilsPanic_getLine(panic);
    (void)FilaUtilsPanic_getCallStackFrameCount(panic);
}

int main(void) {
    printf("Running functionality_utils_panic...\n");

    PanicState state = {0u};
    FilaUtilsPanic_setHandler(on_panic, &state);

    if (FilaUtilsPanic_getType((const FilaUtilsPanic*)0) != (const char*)0 ||
            FilaUtilsPanic_getReason((const FilaUtilsPanic*)0) != (const char*)0 ||
            FilaUtilsPanic_getReasonLiteral((const FilaUtilsPanic*)0) != (const char*)0 ||
            FilaUtilsPanic_getFunction((const FilaUtilsPanic*)0) != (const char*)0 ||
            FilaUtilsPanic_getFile((const FilaUtilsPanic*)0) != (const char*)0 ||
            FilaUtilsPanic_getLine((const FilaUtilsPanic*)0) != -1 ||
            FilaUtilsPanic_getCallStackFrameCount((const FilaUtilsPanic*)0) != 0u) {
        printf("Panic null defaults mismatch\n");
        FilaUtilsPanic_setHandler((FilaUtilsPanicHandlerCallback)0, (void*)0);
        return 1;
    }

    FilaUtilsPanic_setHandler((FilaUtilsPanicHandlerCallback)0, (void*)0);

    printf("functionality_utils_panic completed\n");
    return 0;
}


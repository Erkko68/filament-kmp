#include "utils/Panic.h"

static void panic_callback(void* userData, const FilaUtilsPanic* panic) {
    (void)userData;
    (void)FilaUtilsPanic_getType(panic);
}

void test_headers_utils_panic(void) {
    FilaUtilsPanic_setHandler(panic_callback, (void*)0);
    FilaUtilsPanic_setHandler((FilaUtilsPanicHandlerCallback)0, (void*)0);

    (void)FilaUtilsPanic_getType((const FilaUtilsPanic*)0);
    (void)FilaUtilsPanic_getReason((const FilaUtilsPanic*)0);
    (void)FilaUtilsPanic_getReasonLiteral((const FilaUtilsPanic*)0);
    (void)FilaUtilsPanic_getFunction((const FilaUtilsPanic*)0);
    (void)FilaUtilsPanic_getFile((const FilaUtilsPanic*)0);
    (void)FilaUtilsPanic_getLine((const FilaUtilsPanic*)0);
    (void)FilaUtilsPanic_getCallStackFrameCount((const FilaUtilsPanic*)0);
}


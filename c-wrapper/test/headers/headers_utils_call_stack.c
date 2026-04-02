#include "utils/CallStack.h"

void test_headers_utils_call_stack(void) {
    FilaUtilsCallStack* callStack = FilaUtilsCallStack_create();
    intptr_t pc = 0;
    char demangled[64];

    FilaUtilsCallStack_update(callStack, 0u);
    (void)FilaUtilsCallStack_unwind(0u);
    (void)FilaUtilsCallStack_getFrameCount(callStack);
    (void)FilaUtilsCallStack_getFramePc(callStack, 0u, &pc);
    (void)FilaUtilsCallStack_demangleTypeName("i", demangled, sizeof(demangled));

    FilaUtilsCallStack_destroy(callStack);
}


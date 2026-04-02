#include <stdio.h>

#include "utils/CallStack.h"

int main(void) {
    printf("Running functionality_utils_call_stack...\n");

    FilaUtilsCallStack* callStack = FilaUtilsCallStack_create();
    if (!callStack) {
        printf("CallStack create failed\n");
        return 1;
    }

    FilaUtilsCallStack_update(callStack, 0u);
    const size_t count = FilaUtilsCallStack_getFrameCount(callStack);
    intptr_t pc = 0;
    if (count > 0u && !FilaUtilsCallStack_getFramePc(callStack, 0u, &pc)) {
        printf("CallStack frame query failed\n");
        FilaUtilsCallStack_destroy(callStack);
        return 1;
    }

    if (FilaUtilsCallStack_getFramePc((const FilaUtilsCallStack*)0, 0u, &pc) ||
            FilaUtilsCallStack_getFramePc(callStack, count, &pc)) {
        printf("CallStack null/range-safety mismatch\n");
        FilaUtilsCallStack_destroy(callStack);
        return 1;
    }

    char demangled[64] = {0};
    const size_t demangledLen = FilaUtilsCallStack_demangleTypeName("i", demangled, sizeof(demangled));
    if (demangledLen == 0u || demangled[0] == '\0') {
        printf("CallStack demangle failed\n");
        FilaUtilsCallStack_destroy(callStack);
        return 1;
    }

    FilaUtilsCallStack* unwound = FilaUtilsCallStack_unwind(0u);
    if (!unwound) {
        printf("CallStack unwind failed\n");
        FilaUtilsCallStack_destroy(callStack);
        return 1;
    }

    FilaUtilsCallStack_destroy(unwound);
    FilaUtilsCallStack_destroy(callStack);

    printf("functionality_utils_call_stack completed\n");
    return 0;
}


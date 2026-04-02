#include <stdio.h>

#include "utils/Debug.h"

int main(void) {
    printf("Running functionality_utils_debug...\n");

    if (!FilaUtilsDebug_assertInvariant(true, __func__, __FILE__, __LINE__, "always_true")) {
        printf("assertInvariant(true) should return true\n");
        return 1;
    }

    printf("functionality_utils_debug completed\n");
    return 0;
}


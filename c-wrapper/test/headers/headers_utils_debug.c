#include "utils/Debug.h"

void test_headers_utils_debug(void) {
    (void)FilaUtilsDebug_assertInvariant(true, __func__, __FILE__, __LINE__, "true");
}


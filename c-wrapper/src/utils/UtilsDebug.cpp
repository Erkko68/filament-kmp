#include <utils/debug.h>

#include "../../include/utils/Debug.h"

extern "C" {

bool FilaUtilsDebug_assertInvariant(
        bool condition,
        const char* func,
        const char* file,
        int line,
        const char* assertion) {
    if (condition) {
        return true;
    }
    if (!func) {
        func = "FilaUtilsDebug_assertInvariant";
    }
    if (!file) {
        file = "unknown";
    }
    if (!assertion) {
        assertion = "unknown";
    }
    utils::panic(func, file, line, assertion);
    return false;
}

void FilaUtilsDebug_panic(
        const char* func,
        const char* file,
        int line,
        const char* assertion) {
    if (!func) {
        func = "FilaUtilsDebug_panic";
    }
    if (!file) {
        file = "unknown";
    }
    if (!assertion) {
        assertion = "unknown";
    }
    utils::panic(func, file, line, assertion);
}

} // extern "C"



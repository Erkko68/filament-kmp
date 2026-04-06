#include <utils/compiler.h>

#include "../../include/utils/Compiler.h"

extern "C" {

bool FilaUtilsCompiler_hasThreading(void) {
    return UTILS_HAS_THREADING != 0;
}

bool FilaUtilsCompiler_hasRtti(void) {
    return UTILS_HAS_RTTI != 0;
}

bool FilaUtilsCompiler_hasHyperThreading(void) {
    return UTILS_HAS_HYPER_THREADING != 0;
}

bool FilaUtilsCompiler_hasSanitizeThread(void) {
    return UTILS_HAS_SANITIZE_THREAD != 0;
}

bool FilaUtilsCompiler_hasSanitizeMemory(void) {
    return UTILS_HAS_SANITIZE_MEMORY != 0;
}

} // extern "C"


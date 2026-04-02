#include "utils/Log.h"

void test_headers_utils_log(void) {
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_DEBUG, "debug");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_ERROR, "error");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_WARNING, "warning");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_INFO, "info");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_VERBOSE, "verbose");
}


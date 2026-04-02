#include <stdio.h>

#include "utils/Log.h"

int main(void) {
    printf("Running functionality_utils_log...\n");

    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_DEBUG, "FilaUtilsLog debug message");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_ERROR, "FilaUtilsLog error message");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_WARNING, "FilaUtilsLog warning message");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_INFO, "FilaUtilsLog info message");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_VERBOSE, "FilaUtilsLog verbose message");
    FilaUtilsLog_write(FILA_UTILS_LOG_LEVEL_INFO, (const char*)0);

    printf("functionality_utils_log completed\n");
    return 0;
}


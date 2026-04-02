#include <utils/Log.h>

#include "../../include/utils/Log.h"

extern "C" {

void FilaUtilsLog_write(FilaUtilsLogLevel level, const char* message) {
    if (!message) {
        return;
    }
    switch (level) {
        case FILA_UTILS_LOG_LEVEL_DEBUG:
            utils::slog.d << message;
            break;
        case FILA_UTILS_LOG_LEVEL_ERROR:
            utils::slog.e << message;
            break;
        case FILA_UTILS_LOG_LEVEL_WARNING:
            utils::slog.w << message;
            break;
        case FILA_UTILS_LOG_LEVEL_INFO:
            utils::slog.i << message;
            break;
        case FILA_UTILS_LOG_LEVEL_VERBOSE:
        default:
            utils::slog.v << message;
            break;
    }
}

} // extern "C"



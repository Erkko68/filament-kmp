#ifndef FILAMENT_C_UTILS_LOG_H
#define FILAMENT_C_UTILS_LOG_H

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaUtilsLogLevel {
    FILA_UTILS_LOG_LEVEL_DEBUG = 0,
    FILA_UTILS_LOG_LEVEL_ERROR = 1,
    FILA_UTILS_LOG_LEVEL_WARNING = 2,
    FILA_UTILS_LOG_LEVEL_INFO = 3,
    FILA_UTILS_LOG_LEVEL_VERBOSE = 4,
} FilaUtilsLogLevel;

// Writes a null-terminated message to the selected utils::slog stream.
void FilaUtilsLog_write(FilaUtilsLogLevel level, const char* message);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_LOG_H


#ifndef FILAMENT_C_UTILS_STATUS_H
#define FILAMENT_C_UTILS_STATUS_H

#include <stdbool.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaStatus FilaStatus;

typedef enum FilaStatusCode {
    FILA_STATUS_CODE_OK = 0,
    FILA_STATUS_CODE_INVALID_ARGUMENT = 1,
    FILA_STATUS_CODE_INTERNAL = 2,
    FILA_STATUS_CODE_UNSUPPORTED = 3,
} FilaStatusCode;

// Creates a status with explicit code/message. NULL message is treated as empty.
FilaStatus* FilaStatus_create(FilaStatusCode code, const char* message);

// Convenience constructors.
FilaStatus* FilaStatus_createOk(void);
FilaStatus* FilaStatus_createInvalidArgument(const char* message);
FilaStatus* FilaStatus_createInternal(const char* message);
FilaStatus* FilaStatus_createUnsupported(const char* message);

// Clones an existing status object.
FilaStatus* FilaStatus_clone(const FilaStatus* status);

// Destroys a previously created status object.
void FilaStatus_destroy(FilaStatus* status);

// Returns whether the status code is OK.
bool FilaStatus_isOk(const FilaStatus* status);

// Returns the status code, defaulting to INTERNAL for null handles.
FilaStatusCode FilaStatus_getCode(const FilaStatus* status);

// Returns the message length in bytes (excluding trailing NUL).
size_t FilaStatus_getMessageLength(const FilaStatus* status);

// Copies the message into outBuffer and NUL-terminates when capacity > 0.
// Returns bytes copied excluding NUL.
size_t FilaStatus_copyMessage(const FilaStatus* status, char* outBuffer, size_t outBufferSize);

// Returns true if both statuses are equivalent.
bool FilaStatus_equals(const FilaStatus* lhs, const FilaStatus* rhs);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_STATUS_H


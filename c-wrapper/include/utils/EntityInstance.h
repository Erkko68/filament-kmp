#ifndef FILAMENT_C_UTILS_ENTITY_INSTANCE_H
#define FILAMENT_C_UTILS_ENTITY_INSTANCE_H

#include <stdbool.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef uint32_t FilaEntityInstance;

// Returns true when the instance payload is non-zero.
bool FilaEntityInstance_isValid(FilaEntityInstance instance);

// Returns the raw instance payload.
uint32_t FilaEntityInstance_asValue(FilaEntityInstance instance);

// Clears an instance payload to zero.
void FilaEntityInstance_clear(FilaEntityInstance* inOutInstance);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_ENTITY_INSTANCE_H


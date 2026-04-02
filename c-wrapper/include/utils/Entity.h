#ifndef FILAMENT_C_UTILS_ENTITY_H
#define FILAMENT_C_UTILS_ENTITY_H

#include <stdbool.h>
#include <stdint.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns true when the entity handle has no identity.
bool FilaEntity_isNull(FilaEntity entity);

// Returns true when the entity handle is non-null.
bool FilaEntity_isValid(FilaEntity entity);

// Returns the unsigned identity payload for the entity handle.
uint32_t FilaEntity_getId(FilaEntity entity);

// Clears an entity handle to the null identity.
void FilaEntity_clear(FilaEntity* inOutEntity);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_ENTITY_H


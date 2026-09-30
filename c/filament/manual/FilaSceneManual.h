// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILASCENEMANUAL_H
#define FILA_MANUAL_FILASCENEMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Scene::forEach, copying the entities it visits: C callbacks can't take an Entity. Returns the total count.
uint32_t FilaScene_forEach(const FilaScene* self, FilaEntity* out, uint32_t outCapacity);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILASCENEMANUAL_H

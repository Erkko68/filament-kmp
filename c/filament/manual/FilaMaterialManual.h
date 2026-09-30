// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAMATERIALMANUAL_H
#define FILA_MANUAL_FILAMATERIALMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Material::getParameters, copied into ParameterInfo handles C created. Returns how many it wrote.
uint32_t FilaMaterial_getParameters(const FilaMaterial* self, FilaMaterialParameterInfo* const* out, uint32_t outCapacity);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAMATERIALMANUAL_H

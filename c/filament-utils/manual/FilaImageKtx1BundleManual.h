// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAIMAGEKTX1BUNDLEMANUAL_H
#define FILA_MANUAL_FILAIMAGEKTX1BUNDLEMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Ktx1Bundle::getMetadata with a fixed-width valueSize (NULL to skip it); NULL if the key is missing.
const char* FilaImageKtx1Bundle_getMetadata(const FilaImageKtx1Bundle* self, const char* key, uint32_t* valueSize);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAIMAGEKTX1BUNDLEMANUAL_H

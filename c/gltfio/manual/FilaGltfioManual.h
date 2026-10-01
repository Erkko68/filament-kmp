// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAGLTFIOMANUAL_H
#define FILA_MANUAL_FILAGLTFIOMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// gltfio/materials/uberarchive.h's default archive (UBERARCHIVE_DEFAULT_DATA/SIZE), for FilaGltfio_createUbershaderProvider.
const void* FilaGltfio_getUberarchiveData(void);
uint32_t FilaGltfio_getUberarchiveSize(void);

// ResourceLoader::setConfiguration, with gltfPath "" instead of null (the constructor guards null; this doesn't).
void FilaGltfioResourceLoader_setConfiguration(FilaGltfioResourceLoader* self, const FilaGltfioResourceConfiguration* config);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAGLTFIOMANUAL_H

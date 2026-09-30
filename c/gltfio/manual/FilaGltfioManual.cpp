#include "FilaGltfioManual.h"

#include <gltfio/materials/uberarchive.h>

extern "C" {

const void* FilaGltfio_getUberarchiveData(void) { return UBERARCHIVE_DEFAULT_DATA; }

uint32_t FilaGltfio_getUberarchiveSize(void) { return UBERARCHIVE_DEFAULT_SIZE; }

} // extern "C"

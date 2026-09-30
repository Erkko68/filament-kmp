#include "../generated/Includes.hpp"
#include "FilaImageKtx1BundleManual.h"

extern "C" {

const char* FilaImageKtx1Bundle_getMetadata(const FilaImageKtx1Bundle* self, const char* key, uint32_t* valueSize) {
    size_t size = 0;
    const char* value = fila::cpp(self)->getMetadata(key, &size);
    if (valueSize) *valueSize = uint32_t(size);
    return value;
}

} // extern "C"

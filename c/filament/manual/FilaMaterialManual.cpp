#include "../generated/Includes.hpp"
#include "FilaMaterialManual.h"

#include <vector>

extern "C" {

uint32_t FilaMaterial_getParameters(const FilaMaterial* self, FilaMaterialParameterInfo* const* out, uint32_t outCapacity) {
    std::vector<filament::Material::ParameterInfo> infos(outCapacity);
    auto count = uint32_t(fila::cpp(self)->getParameters(infos.data(), outCapacity));
    for (uint32_t i = 0; i < count; i++) *fila::cpp(out[i]) = infos[i];
    return count;
}

} // extern "C"

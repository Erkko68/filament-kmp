#include "../generated/Includes.hpp"
#include "FilaFilamatMaterialBuilderManual.h"

#include <utils/JobSystem.h>

extern "C" {

FilaFilamatPackage* FilaFilamatMaterialBuilder_build(FilaFilamatMaterialBuilder* self) {
    utils::JobSystem jobSystem;
    jobSystem.adopt();
    auto* package = new filamat::Package(fila::cpp(self)->build(jobSystem));
    jobSystem.emancipate();
    return fila::c(package);
}

FilaFilamatMaterialBuilder* FilaFilamatMaterialBuilder_constant_int(FilaFilamatMaterialBuilder* self, const char* name, FilaConstantType type, int32_t defaultValue) {
    return fila::c(&fila::cpp(self)->constant(name, static_cast<filament::backend::ConstantType>(type), defaultValue));
}

FilaFilamatMaterialBuilder* FilaFilamatMaterialBuilder_constant_float(FilaFilamatMaterialBuilder* self, const char* name, FilaConstantType type, float defaultValue) {
    return fila::c(&fila::cpp(self)->constant(name, static_cast<filament::backend::ConstantType>(type), defaultValue));
}

FilaFilamatMaterialBuilder* FilaFilamatMaterialBuilder_constant_bool(FilaFilamatMaterialBuilder* self, const char* name, FilaConstantType type, bool defaultValue) {
    return fila::c(&fila::cpp(self)->constant(name, static_cast<filament::backend::ConstantType>(type), defaultValue));
}

} // extern "C"

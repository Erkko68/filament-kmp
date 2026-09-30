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

} // extern "C"

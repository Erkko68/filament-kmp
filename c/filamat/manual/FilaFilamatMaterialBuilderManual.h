// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAFILAMATMATERIALBUILDERMANUAL_H
#define FILA_MANUAL_FILAFILAMATMATERIALBUILDERMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Package filamat::MaterialBuilder::build(utils::JobSystem&), on a JobSystem of its own; the caller destroys the package.
FilaFilamatPackage* FilaFilamatMaterialBuilder_build(FilaFilamatMaterialBuilder* self);

// template<T> MaterialBuilder& filamat::MaterialBuilder::constant(const char*, ConstantType, T), for its T: int32_t, float, bool.
FilaFilamatMaterialBuilder* FilaFilamatMaterialBuilder_constant_int(FilaFilamatMaterialBuilder* self, const char* name, FilaConstantType type, int32_t defaultValue);
FilaFilamatMaterialBuilder* FilaFilamatMaterialBuilder_constant_float(FilaFilamatMaterialBuilder* self, const char* name, FilaConstantType type, float defaultValue);
FilaFilamatMaterialBuilder* FilaFilamatMaterialBuilder_constant_bool(FilaFilamatMaterialBuilder* self, const char* name, FilaConstantType type, bool defaultValue);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAFILAMATMATERIALBUILDERMANUAL_H

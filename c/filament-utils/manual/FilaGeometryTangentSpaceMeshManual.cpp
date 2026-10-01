#include "../generated/Includes.hpp"
#include "FilaGeometryTangentSpaceMeshManual.h"

namespace {
template<typename T, typename C>
FilaGeometryTangentSpaceMeshBuilder* aux(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const C* data, uint32_t stride) {
    using filament::geometry::TangentSpaceMesh;
    return fila::c(&fila::cpp(self)->aux(static_cast<TangentSpaceMesh::AuxAttribute>(attribute), reinterpret_cast<const T*>(data), size_t(stride)));
}
}

extern "C" {

FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_float2(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaFloat2* data, uint32_t stride) {
    return aux<filament::math::float2>(self, attribute, data, stride);
}

FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_float3(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaFloat3* data, uint32_t stride) {
    return aux<filament::math::float3>(self, attribute, data, stride);
}

FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_float4(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaFloat4* data, uint32_t stride) {
    return aux<filament::math::float4>(self, attribute, data, stride);
}

FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_ushort3(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaUshort3* data, uint32_t stride) {
    return aux<filament::math::ushort3>(self, attribute, data, stride);
}

FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_ushort4(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaUshort4* data, uint32_t stride) {
    return aux<filament::math::ushort4>(self, attribute, data, stride);
}

} // extern "C"

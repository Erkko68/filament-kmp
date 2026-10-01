// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAGEOMETRYTANGENTSPACEMESHMANUAL_H
#define FILA_MANUAL_FILAGEOMETRYTANGENTSPACEMESHMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// TangentSpaceMesh::Builder::aux, one per InData alternative. The builder keeps data until build().
FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_float2(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaFloat2* data, uint32_t stride);
FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_float3(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaFloat3* data, uint32_t stride);
FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_float4(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaFloat4* data, uint32_t stride);
FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_ushort3(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaUshort3* data, uint32_t stride);
FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_aux_ushort4(FilaGeometryTangentSpaceMeshBuilder* self, FilaGeometryTangentSpaceMeshAuxAttribute attribute, const FilaUshort4* data, uint32_t stride);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAGEOMETRYTANGENTSPACEMESHMANUAL_H

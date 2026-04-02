#ifndef FILAMENT_C_GEOMETRY_TANGENT_SPACE_MESH_H
#define FILAMENT_C_GEOMETRY_TANGENT_SPACE_MESH_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaGeometryTangentSpaceMeshAlgorithm {
    FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_DEFAULT = 0,
    FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_MIKKTSPACE = 1,
    FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_LENGYEL = 2,
    FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_HUGHES_MOLLER = 3,
    FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_FRISVAD = 4,
} FilaGeometryTangentSpaceMeshAlgorithm;

FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_create(void);
void FilaGeometryTangentSpaceMeshBuilder_destroy(FilaGeometryTangentSpaceMeshBuilder* builder);

bool FilaGeometryTangentSpaceMeshBuilder_vertexCount(FilaGeometryTangentSpaceMeshBuilder* builder, size_t vertexCount);
bool FilaGeometryTangentSpaceMeshBuilder_normals(FilaGeometryTangentSpaceMeshBuilder* builder, const float* normals3, size_t strideBytes);
bool FilaGeometryTangentSpaceMeshBuilder_tangents(FilaGeometryTangentSpaceMeshBuilder* builder, const float* tangents4, size_t strideBytes);
bool FilaGeometryTangentSpaceMeshBuilder_uvs(FilaGeometryTangentSpaceMeshBuilder* builder, const float* uvs2, size_t strideBytes);
bool FilaGeometryTangentSpaceMeshBuilder_positions(FilaGeometryTangentSpaceMeshBuilder* builder, const float* positions3, size_t strideBytes);
bool FilaGeometryTangentSpaceMeshBuilder_triangleCount(FilaGeometryTangentSpaceMeshBuilder* builder, size_t triangleCount);
bool FilaGeometryTangentSpaceMeshBuilder_trianglesUint3(FilaGeometryTangentSpaceMeshBuilder* builder, const uint32_t* triangles3u32);
bool FilaGeometryTangentSpaceMeshBuilder_trianglesUshort3(FilaGeometryTangentSpaceMeshBuilder* builder, const uint16_t* triangles3u16);
bool FilaGeometryTangentSpaceMeshBuilder_algorithm(
        FilaGeometryTangentSpaceMeshBuilder* builder,
        FilaGeometryTangentSpaceMeshAlgorithm algorithm);

FilaGeometryTangentSpaceMesh* FilaGeometryTangentSpaceMeshBuilder_build(FilaGeometryTangentSpaceMeshBuilder* builder);

void FilaGeometryTangentSpaceMesh_destroy(FilaGeometryTangentSpaceMesh* mesh);
size_t FilaGeometryTangentSpaceMesh_getVertexCount(const FilaGeometryTangentSpaceMesh* mesh);
size_t FilaGeometryTangentSpaceMesh_getTriangleCount(const FilaGeometryTangentSpaceMesh* mesh);
bool FilaGeometryTangentSpaceMesh_isRemeshed(const FilaGeometryTangentSpaceMesh* mesh);

// Copies output positions as packed float3 values.
bool FilaGeometryTangentSpaceMesh_getPositions(
        const FilaGeometryTangentSpaceMesh* mesh,
        float* outPositions3,
        size_t outVertexCount,
        size_t outStrideBytes);

// Copies output UVs as packed float2 values.
bool FilaGeometryTangentSpaceMesh_getUVs(
        const FilaGeometryTangentSpaceMesh* mesh,
        float* outUvs2,
        size_t outVertexCount,
        size_t outStrideBytes);

// Copies output triangles as uint3 indices.
bool FilaGeometryTangentSpaceMesh_getTrianglesUint3(
        const FilaGeometryTangentSpaceMesh* mesh,
        uint32_t* outTriangles3u32,
        size_t outTriangleCount);

// Copies output triangles as ushort3 indices.
bool FilaGeometryTangentSpaceMesh_getTrianglesUshort3(
        const FilaGeometryTangentSpaceMesh* mesh,
        uint16_t* outTriangles3u16,
        size_t outTriangleCount);

// outQuats4i16 receives packed xyzw quaternion components as 4 int16 values per element.
bool FilaGeometryTangentSpaceMesh_getQuatsShort4(
        const FilaGeometryTangentSpaceMesh* mesh,
        int16_t* outQuats4i16,
        size_t outQuatCount,
        size_t outStrideBytes);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GEOMETRY_TANGENT_SPACE_MESH_H


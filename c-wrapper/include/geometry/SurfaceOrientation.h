#ifndef FILAMENT_C_GEOMETRY_SURFACE_ORIENTATION_H
#define FILAMENT_C_GEOMETRY_SURFACE_ORIENTATION_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaGeometrySurfaceOrientationBuilder* FilaGeometrySurfaceOrientationBuilder_create(void);
void FilaGeometrySurfaceOrientationBuilder_destroy(FilaGeometrySurfaceOrientationBuilder* builder);

bool FilaGeometrySurfaceOrientationBuilder_vertexCount(
        FilaGeometrySurfaceOrientationBuilder* builder,
        size_t vertexCount);

// normals3 is tightly-packed float3 data unless strideBytes is non-zero.
bool FilaGeometrySurfaceOrientationBuilder_normals(
        FilaGeometrySurfaceOrientationBuilder* builder,
        const float* normals3,
        size_t strideBytes);

// tangents4 is tightly-packed float4 data unless strideBytes is non-zero.
bool FilaGeometrySurfaceOrientationBuilder_tangents(
        FilaGeometrySurfaceOrientationBuilder* builder,
        const float* tangents4,
        size_t strideBytes);

// uvs2 is tightly-packed float2 data unless strideBytes is non-zero.
bool FilaGeometrySurfaceOrientationBuilder_uvs(
        FilaGeometrySurfaceOrientationBuilder* builder,
        const float* uvs2,
        size_t strideBytes);

// positions3 is tightly-packed float3 data unless strideBytes is non-zero.
bool FilaGeometrySurfaceOrientationBuilder_positions(
        FilaGeometrySurfaceOrientationBuilder* builder,
        const float* positions3,
        size_t strideBytes);

bool FilaGeometrySurfaceOrientationBuilder_triangleCount(
        FilaGeometrySurfaceOrientationBuilder* builder,
        size_t triangleCount);

// triangles3u32 is tightly-packed uint3 index data.
bool FilaGeometrySurfaceOrientationBuilder_trianglesUint3(
        FilaGeometrySurfaceOrientationBuilder* builder,
        const uint32_t* triangles3u32);

// triangles3u16 is tightly-packed ushort3 index data.
bool FilaGeometrySurfaceOrientationBuilder_trianglesUshort3(
        FilaGeometrySurfaceOrientationBuilder* builder,
        const uint16_t* triangles3u16);

FilaGeometrySurfaceOrientation* FilaGeometrySurfaceOrientationBuilder_build(
        FilaGeometrySurfaceOrientationBuilder* builder);

void FilaGeometrySurfaceOrientation_destroy(FilaGeometrySurfaceOrientation* orientation);
size_t FilaGeometrySurfaceOrientation_getVertexCount(const FilaGeometrySurfaceOrientation* orientation);

// outQuats4i16 receives packed xyzw quaternion components as 4 int16 values per element.
bool FilaGeometrySurfaceOrientation_getQuatsShort4(const FilaGeometrySurfaceOrientation* orientation,
        int16_t* outQuats4i16,
        size_t quatCount,
        size_t outStrideBytes);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GEOMETRY_SURFACE_ORIENTATION_H


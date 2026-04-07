#ifndef FILA_SURFACE_ORIENTATION_H
#define FILA_SURFACE_ORIENTATION_H

#include <stdint.h>
#include <stdbool.h>
#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaSurfaceOrientation FilaSurfaceOrientation;
typedef struct FilaSurfaceOrientationBuilder FilaSurfaceOrientationBuilder;

// SurfaceOrientation::Builder
FilaSurfaceOrientationBuilder* FilaSurfaceOrientationBuilder_create();
void FilaSurfaceOrientationBuilder_destroy(FilaSurfaceOrientationBuilder* builder);
void FilaSurfaceOrientationBuilder_vertexCount(FilaSurfaceOrientationBuilder* builder, uint32_t vertexCount);
void FilaSurfaceOrientationBuilder_normals(FilaSurfaceOrientationBuilder* builder, const float* buffer, uint32_t stride);
void FilaSurfaceOrientationBuilder_tangents(FilaSurfaceOrientationBuilder* builder, const float* buffer, uint32_t stride);
void FilaSurfaceOrientationBuilder_uvs(FilaSurfaceOrientationBuilder* builder, const float* buffer, uint32_t stride);
void FilaSurfaceOrientationBuilder_positions(FilaSurfaceOrientationBuilder* builder, const float* buffer, uint32_t stride);
void FilaSurfaceOrientationBuilder_triangleCount(FilaSurfaceOrientationBuilder* builder, uint32_t triangleCount);
void FilaSurfaceOrientationBuilder_triangles16(FilaSurfaceOrientationBuilder* builder, const uint16_t* buffer);
void FilaSurfaceOrientationBuilder_triangles32(FilaSurfaceOrientationBuilder* builder, const uint32_t* buffer);
FilaSurfaceOrientation* FilaSurfaceOrientationBuilder_build(FilaSurfaceOrientationBuilder* builder);

// SurfaceOrientation
void FilaSurfaceOrientation_destroy(FilaSurfaceOrientation* orientation);
uint32_t FilaSurfaceOrientation_getVertexCount(const FilaSurfaceOrientation* orientation);
void FilaSurfaceOrientation_getQuatsAsFloat(const FilaSurfaceOrientation* orientation, float* buffer, uint32_t count);
void FilaSurfaceOrientation_getQuatsAsHalf(const FilaSurfaceOrientation* orientation, uint16_t* buffer, uint32_t count);
void FilaSurfaceOrientation_getQuatsAsShort(const FilaSurfaceOrientation* orientation, int16_t* buffer, uint32_t count);

#ifdef __cplusplus
}
#endif

#endif // FILA_SURFACE_ORIENTATION_H

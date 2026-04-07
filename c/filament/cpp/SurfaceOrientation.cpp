#include "SurfaceOrientation.h"
#include <filament/SurfaceOrientation.h>
#include <stddef.h>

using namespace filament;

struct FilaSurfaceOrientationBuilder {
    SurfaceOrientation::Builder builder;
};

struct FilaSurfaceOrientation {
    SurfaceOrientation* orientation;
};

extern "C" {

FilaSurfaceOrientationBuilder* FilaSurfaceOrientationBuilder_create() {
    return new FilaSurfaceOrientationBuilder();
}

void FilaSurfaceOrientationBuilder_destroy(FilaSurfaceOrientationBuilder* builder) {
    delete builder;
}

void FilaSurfaceOrientationBuilder_vertexCount(FilaSurfaceOrientationBuilder* builder, uint32_t vertexCount) {
    builder->builder.vertexCount(vertexCount);
}

void FilaSurfaceOrientationBuilder_normals(FilaSurfaceOrientationBuilder* builder, const float* buffer, uint32_t stride) {
    builder->builder.normals(buffer, stride);
}

void FilaSurfaceOrientationBuilder_tangents(FilaSurfaceOrientationBuilder* builder, const float* buffer, uint32_t stride) {
    builder->builder.tangents(buffer, stride);
}

void FilaSurfaceOrientationBuilder_uvs(FilaSurfaceOrientationBuilder* builder, const float* buffer, uint32_t stride) {
    builder->builder.uvs(buffer, stride);
}

void FilaSurfaceOrientationBuilder_positions(FilaSurfaceOrientationBuilder* builder, const float* buffer, uint32_t stride) {
    builder->builder.positions(buffer, stride);
}

void FilaSurfaceOrientationBuilder_triangleCount(FilaSurfaceOrientationBuilder* builder, uint32_t triangleCount) {
    builder->builder.triangleCount(triangleCount);
}

void FilaSurfaceOrientationBuilder_triangles16(FilaSurfaceOrientationBuilder* builder, const uint16_t* buffer) {
    builder->builder.triangles(buffer);
}

void FilaSurfaceOrientationBuilder_triangles32(FilaSurfaceOrientationBuilder* builder, const uint32_t* buffer) {
    builder->builder.triangles(buffer);
}

FilaSurfaceOrientation* FilaSurfaceOrientationBuilder_build(FilaSurfaceOrientationBuilder* builder) {
    SurfaceOrientation* orientation = builder->builder.build();
    if (orientation == nullptr) return nullptr;
    return new FilaSurfaceOrientation{orientation} ;
}

void FilaSurfaceOrientation_destroy(FilaSurfaceOrientation* orientation) {
    delete orientation->orientation;
    delete orientation;
}

uint32_t FilaSurfaceOrientation_getVertexCount(const FilaSurfaceOrientation* orientation) {
    return orientation->orientation->getVertexCount();
}

void FilaSurfaceOrientation_getQuatsAsFloat(const FilaSurfaceOrientation* orientation, float* buffer, uint32_t count) {
    orientation->orientation->getQuats(buffer, count);
}

void FilaSurfaceOrientation_getQuatsAsHalf(const FilaSurfaceOrientation* orientation, uint16_t* buffer, uint32_t count) {
    orientation->orientation->getQuats((math::half4*)buffer, count);
}

void FilaSurfaceOrientation_getQuatsAsShort(const FilaSurfaceOrientation* orientation, int16_t* buffer, uint32_t count) {
    orientation->orientation->getQuats((math::short4*)buffer, count);
}

}

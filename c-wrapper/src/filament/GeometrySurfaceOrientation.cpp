#include <geometry/SurfaceOrientation.h>

#include <math/vec3.h>
#include <math/vec4.h>

#include <new>
#include <vector>

#include "../../include/geometry/SurfaceOrientation.h"

extern "C" {

FilaGeometrySurfaceOrientationBuilder* FilaGeometrySurfaceOrientationBuilder_create(void) {
    auto* builder = new (std::nothrow) filament::geometry::SurfaceOrientation::Builder();
    return reinterpret_cast<FilaGeometrySurfaceOrientationBuilder*>(builder);
}

void FilaGeometrySurfaceOrientationBuilder_destroy(FilaGeometrySurfaceOrientationBuilder* builder) {
    if (!builder) {
        return;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::SurfaceOrientation::Builder*>(builder);
    delete cppBuilder;
}

bool FilaGeometrySurfaceOrientationBuilder_vertexCount(
        FilaGeometrySurfaceOrientationBuilder* builder,
        size_t vertexCount) {
    if (!builder || vertexCount == 0u) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::SurfaceOrientation::Builder*>(builder);
    cppBuilder->vertexCount(vertexCount);
    return true;
}

bool FilaGeometrySurfaceOrientationBuilder_normals(
        FilaGeometrySurfaceOrientationBuilder* builder,
        const float* normals3,
        size_t strideBytes) {
    if (!builder || !normals3) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::SurfaceOrientation::Builder*>(builder);
    cppBuilder->normals(reinterpret_cast<const filament::math::float3*>(normals3), strideBytes);
    return true;
}

FilaGeometrySurfaceOrientation* FilaGeometrySurfaceOrientationBuilder_build(
        FilaGeometrySurfaceOrientationBuilder* builder) {
    if (!builder) {
        return nullptr;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::SurfaceOrientation::Builder*>(builder);
    return reinterpret_cast<FilaGeometrySurfaceOrientation*>(cppBuilder->build());
}

void FilaGeometrySurfaceOrientation_destroy(FilaGeometrySurfaceOrientation* orientation) {
    if (!orientation) {
        return;
    }
    auto* cppOrientation = reinterpret_cast<filament::geometry::SurfaceOrientation*>(orientation);
    delete cppOrientation;
}

size_t FilaGeometrySurfaceOrientation_getVertexCount(const FilaGeometrySurfaceOrientation* orientation) {
    if (!orientation) {
        return 0u;
    }
    auto* cppOrientation = reinterpret_cast<const filament::geometry::SurfaceOrientation*>(orientation);
    return cppOrientation->getVertexCount();
}

bool FilaGeometrySurfaceOrientation_getQuatsShort4(const FilaGeometrySurfaceOrientation* orientation,
        int16_t* outQuats4i16,
        size_t quatCount,
        size_t outStrideBytes) {
    if (!orientation || !outQuats4i16 || quatCount == 0u) {
        return false;
    }
    auto* cppOrientation = reinterpret_cast<const filament::geometry::SurfaceOrientation*>(orientation);
    std::vector<filament::math::short4> quats(quatCount);
    cppOrientation->getQuats(quats.data(), quatCount, 0u);

    const size_t stride = outStrideBytes == 0u ? sizeof(int16_t) * 4u : outStrideBytes;
    for (size_t i = 0u; i < quatCount; ++i) {
        int16_t* dst = reinterpret_cast<int16_t*>(reinterpret_cast<uint8_t*>(outQuats4i16) + i * stride);
        dst[0] = quats[i].x;
        dst[1] = quats[i].y;
        dst[2] = quats[i].z;
        dst[3] = quats[i].w;
    }
    return true;
}

} // extern "C"


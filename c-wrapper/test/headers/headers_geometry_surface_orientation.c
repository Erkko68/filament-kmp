#include "geometry/SurfaceOrientation.h"

void test_headers_geometry_surface_orientation(void) {
    const float normals[3] = {0.0f, 1.0f, 0.0f};
    const float tangents[4] = {1.0f, 0.0f, 0.0f, 1.0f};
    const float uvs[2] = {0.0f, 0.0f};
    const float positions[3] = {0.0f, 0.0f, 0.0f};
    const uint16_t trianglesU16[3] = {0u, 0u, 0u};
    const uint32_t trianglesU32[3] = {0u, 0u, 0u};
    int16_t quats[4];

    FilaGeometrySurfaceOrientationBuilder* builder = FilaGeometrySurfaceOrientationBuilder_create();
    (void)FilaGeometrySurfaceOrientationBuilder_vertexCount(builder, 1u);
    (void)FilaGeometrySurfaceOrientationBuilder_normals(builder, normals, 0u);
    (void)FilaGeometrySurfaceOrientationBuilder_tangents(builder, tangents, 0u);
    (void)FilaGeometrySurfaceOrientationBuilder_uvs(builder, uvs, 0u);
    (void)FilaGeometrySurfaceOrientationBuilder_positions(builder, positions, 0u);
    (void)FilaGeometrySurfaceOrientationBuilder_triangleCount(builder, 1u);
    (void)FilaGeometrySurfaceOrientationBuilder_trianglesUshort3(builder, trianglesU16);
    (void)FilaGeometrySurfaceOrientationBuilder_trianglesUint3(builder, trianglesU32);

    FilaGeometrySurfaceOrientation* orientation = FilaGeometrySurfaceOrientationBuilder_build(builder);
    (void)FilaGeometrySurfaceOrientation_getVertexCount(orientation);
    (void)FilaGeometrySurfaceOrientation_getQuatsShort4(orientation, quats, 1u, 0u);

    FilaGeometrySurfaceOrientation_destroy(orientation);
    FilaGeometrySurfaceOrientationBuilder_destroy(builder);
}


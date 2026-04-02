#include <stdio.h>

#include "geometry/SurfaceOrientation.h"

int main(void) {
    printf("Running functionality_geometry_surface_orientation_null_safety...\n");

    const float normals[3] = {0.0f, 1.0f, 0.0f};
    const float tangents[4] = {1.0f, 0.0f, 0.0f, 1.0f};
    const float uvs[2] = {0.0f, 0.0f};
    const float positions[3] = {0.0f, 0.0f, 0.0f};
    const uint16_t trianglesU16[3] = {0u, 0u, 0u};
    const uint32_t trianglesU32[3] = {0u, 0u, 0u};
    int16_t quats[4] = {0, 0, 0, 0};

    if (FilaGeometrySurfaceOrientationBuilder_vertexCount((FilaGeometrySurfaceOrientationBuilder*)0, 1u) ||
            FilaGeometrySurfaceOrientationBuilder_normals((FilaGeometrySurfaceOrientationBuilder*)0, normals, 0u) ||
            FilaGeometrySurfaceOrientationBuilder_tangents((FilaGeometrySurfaceOrientationBuilder*)0, tangents, 0u) ||
            FilaGeometrySurfaceOrientationBuilder_uvs((FilaGeometrySurfaceOrientationBuilder*)0, uvs, 0u) ||
            FilaGeometrySurfaceOrientationBuilder_positions((FilaGeometrySurfaceOrientationBuilder*)0, positions, 0u) ||
            FilaGeometrySurfaceOrientationBuilder_triangleCount((FilaGeometrySurfaceOrientationBuilder*)0, 1u) ||
            FilaGeometrySurfaceOrientationBuilder_trianglesUshort3((FilaGeometrySurfaceOrientationBuilder*)0, trianglesU16) ||
            FilaGeometrySurfaceOrientationBuilder_trianglesUint3((FilaGeometrySurfaceOrientationBuilder*)0, trianglesU32) ||
            FilaGeometrySurfaceOrientationBuilder_build((FilaGeometrySurfaceOrientationBuilder*)0) != (FilaGeometrySurfaceOrientation*)0 ||
            FilaGeometrySurfaceOrientation_getVertexCount((const FilaGeometrySurfaceOrientation*)0) != 0u ||
            FilaGeometrySurfaceOrientation_getQuatsShort4((const FilaGeometrySurfaceOrientation*)0, quats, 1u, 0u)) {
        printf("SurfaceOrientation null defaults mismatch\n");
        return 1;
    }

    FilaGeometrySurfaceOrientationBuilder* builder = FilaGeometrySurfaceOrientationBuilder_create();
    if (!builder ||
            !FilaGeometrySurfaceOrientationBuilder_vertexCount(builder, 1u) ||
            !FilaGeometrySurfaceOrientationBuilder_normals(builder, normals, 0u) ||
            !FilaGeometrySurfaceOrientationBuilder_tangents(builder, tangents, 0u) ||
            !FilaGeometrySurfaceOrientationBuilder_uvs(builder, uvs, 0u) ||
            !FilaGeometrySurfaceOrientationBuilder_positions(builder, positions, 0u) ||
            !FilaGeometrySurfaceOrientationBuilder_triangleCount(builder, 1u) ||
            !FilaGeometrySurfaceOrientationBuilder_trianglesUshort3(builder, trianglesU16)) {
        printf("SurfaceOrientation builder setup failed\n");
        FilaGeometrySurfaceOrientationBuilder_destroy(builder);
        return 1;
    }

    FilaGeometrySurfaceOrientation* orientation = FilaGeometrySurfaceOrientationBuilder_build(builder);
    if (!orientation ||
            FilaGeometrySurfaceOrientation_getVertexCount(orientation) != 1u ||
            !FilaGeometrySurfaceOrientation_getQuatsShort4(orientation, quats, 1u, 0u)) {
        printf("SurfaceOrientation build/getQuats failed\n");
        FilaGeometrySurfaceOrientation_destroy(orientation);
        FilaGeometrySurfaceOrientationBuilder_destroy(builder);
        return 1;
    }

    FilaGeometrySurfaceOrientation_destroy(orientation);
    FilaGeometrySurfaceOrientationBuilder_destroy(builder);

    // Validate the uint3 entrypoint independently (cannot mix uint3 and ushort3 on one builder).
    builder = FilaGeometrySurfaceOrientationBuilder_create();
    if (!builder ||
            !FilaGeometrySurfaceOrientationBuilder_vertexCount(builder, 1u) ||
            !FilaGeometrySurfaceOrientationBuilder_normals(builder, normals, 0u) ||
            !FilaGeometrySurfaceOrientationBuilder_positions(builder, positions, 0u) ||
            !FilaGeometrySurfaceOrientationBuilder_triangleCount(builder, 1u) ||
            !FilaGeometrySurfaceOrientationBuilder_trianglesUint3(builder, trianglesU32)) {
        printf("SurfaceOrientation uint3 builder setup failed\n");
        FilaGeometrySurfaceOrientationBuilder_destroy(builder);
        return 1;
    }
    orientation = FilaGeometrySurfaceOrientationBuilder_build(builder);
    FilaGeometrySurfaceOrientation_destroy(orientation);
    FilaGeometrySurfaceOrientationBuilder_destroy(builder);

    printf("functionality_geometry_surface_orientation_null_safety completed\n");
    return 0;
}


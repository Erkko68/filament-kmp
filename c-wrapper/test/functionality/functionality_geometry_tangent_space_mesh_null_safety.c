#include <stdio.h>

#include "geometry/TangentSpaceMesh.h"

int main(void) {
    printf("Running functionality_geometry_tangent_space_mesh_null_safety...\n");

    const float positions[3] = {0.0f, 0.0f, 0.0f};
    const float uvs[2] = {0.0f, 0.0f};
    const uint16_t trianglesU16[3] = {0u, 0u, 0u};
    const float normals[3] = {0.0f, 1.0f, 0.0f};
    float outPositions[3] = {0.0f, 0.0f, 0.0f};
    float outUvs[2] = {0.0f, 0.0f};
    uint32_t outTrianglesU32[3] = {0u, 0u, 0u};
    uint16_t outTrianglesU16[3] = {0u, 0u, 0u};
    int16_t quats[4] = {0, 0, 0, 0};

    if (FilaGeometryTangentSpaceMeshBuilder_vertexCount((FilaGeometryTangentSpaceMeshBuilder*)0, 1u) ||
            FilaGeometryTangentSpaceMeshBuilder_normals((FilaGeometryTangentSpaceMeshBuilder*)0, normals, 0u) ||
            FilaGeometryTangentSpaceMeshBuilder_algorithm((FilaGeometryTangentSpaceMeshBuilder*)0, FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_DEFAULT) ||
            FilaGeometryTangentSpaceMeshBuilder_build((FilaGeometryTangentSpaceMeshBuilder*)0) != (FilaGeometryTangentSpaceMesh*)0 ||
            FilaGeometryTangentSpaceMesh_getVertexCount((const FilaGeometryTangentSpaceMesh*)0) != 0u ||
            FilaGeometryTangentSpaceMesh_getTriangleCount((const FilaGeometryTangentSpaceMesh*)0) != 0u ||
            FilaGeometryTangentSpaceMesh_isRemeshed((const FilaGeometryTangentSpaceMesh*)0) ||
            FilaGeometryTangentSpaceMesh_getPositions((const FilaGeometryTangentSpaceMesh*)0, outPositions, 1u, 0u) ||
            FilaGeometryTangentSpaceMesh_getUVs((const FilaGeometryTangentSpaceMesh*)0, outUvs, 1u, 0u) ||
            FilaGeometryTangentSpaceMesh_getTrianglesUint3((const FilaGeometryTangentSpaceMesh*)0, outTrianglesU32, 1u) ||
            FilaGeometryTangentSpaceMesh_getTrianglesUshort3((const FilaGeometryTangentSpaceMesh*)0, outTrianglesU16, 1u) ||
            FilaGeometryTangentSpaceMesh_getQuatsShort4((const FilaGeometryTangentSpaceMesh*)0, quats, 1u, 0u)) {
        printf("TangentSpaceMesh null defaults mismatch\n");
        return 1;
    }

    FilaGeometryTangentSpaceMeshBuilder* builder = FilaGeometryTangentSpaceMeshBuilder_create();
    if (!builder ||
            !FilaGeometryTangentSpaceMeshBuilder_vertexCount(builder, 1u) ||
            !FilaGeometryTangentSpaceMeshBuilder_normals(builder, normals, 0u) ||
            !FilaGeometryTangentSpaceMeshBuilder_algorithm(builder, FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_DEFAULT)) {
        printf("TangentSpaceMesh builder setup failed\n");
        FilaGeometryTangentSpaceMeshBuilder_destroy(builder);
        return 1;
    }

    FilaGeometryTangentSpaceMesh* mesh = FilaGeometryTangentSpaceMeshBuilder_build(builder);
    if (!mesh || FilaGeometryTangentSpaceMesh_getVertexCount(mesh) == 0u ||
            !FilaGeometryTangentSpaceMesh_getQuatsShort4(mesh, quats, 1u, 0u)) {
        printf("TangentSpaceMesh build/getQuats failed\n");
        FilaGeometryTangentSpaceMesh_destroy(mesh);
        FilaGeometryTangentSpaceMeshBuilder_destroy(builder);
        return 1;
    }

    FilaGeometryTangentSpaceMesh_destroy(mesh);
    FilaGeometryTangentSpaceMeshBuilder_destroy(builder);

    // Build with a full mesh payload so positions/uvs/triangles getters are expected to succeed.
    builder = FilaGeometryTangentSpaceMeshBuilder_create();
    if (!builder ||
            !FilaGeometryTangentSpaceMeshBuilder_vertexCount(builder, 1u) ||
            !FilaGeometryTangentSpaceMeshBuilder_normals(builder, normals, 0u) ||
            !FilaGeometryTangentSpaceMeshBuilder_positions(builder, positions, 0u) ||
            !FilaGeometryTangentSpaceMeshBuilder_uvs(builder, uvs, 0u) ||
            !FilaGeometryTangentSpaceMeshBuilder_triangleCount(builder, 1u) ||
            !FilaGeometryTangentSpaceMeshBuilder_trianglesUshort3(builder, trianglesU16)) {
        printf("TangentSpaceMesh full payload setup failed\n");
        FilaGeometryTangentSpaceMeshBuilder_destroy(builder);
        return 1;
    }

    mesh = FilaGeometryTangentSpaceMeshBuilder_build(builder);
    if (!mesh ||
            !FilaGeometryTangentSpaceMesh_getPositions(mesh, outPositions, 1u, 0u) ||
            !FilaGeometryTangentSpaceMesh_getUVs(mesh, outUvs, 1u, 0u) ||
            !FilaGeometryTangentSpaceMesh_getTrianglesUint3(mesh, outTrianglesU32, 1u) ||
            !FilaGeometryTangentSpaceMesh_getTrianglesUshort3(mesh, outTrianglesU16, 1u)) {
        printf("TangentSpaceMesh output getters failed\n");
        FilaGeometryTangentSpaceMesh_destroy(mesh);
        FilaGeometryTangentSpaceMeshBuilder_destroy(builder);
        return 1;
    }

    FilaGeometryTangentSpaceMesh_destroy(mesh);
    FilaGeometryTangentSpaceMeshBuilder_destroy(builder);

    printf("functionality_geometry_tangent_space_mesh_null_safety completed\n");
    return 0;
}


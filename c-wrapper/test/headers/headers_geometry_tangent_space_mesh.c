#include "geometry/TangentSpaceMesh.h"

void test_headers_geometry_tangent_space_mesh(void) {
    float positions[3] = {0.0f, 0.0f, 0.0f};
    float uvs[2] = {0.0f, 0.0f};
    uint32_t trianglesU32[3] = {0u, 0u, 0u};
    const float normals[3] = {0.0f, 1.0f, 0.0f};
    uint16_t trianglesU16[3] = {0u, 0u, 0u};
    int16_t quats[4] = {0};

    FilaGeometryTangentSpaceMeshBuilder* builder = FilaGeometryTangentSpaceMeshBuilder_create();
    (void)FilaGeometryTangentSpaceMeshBuilder_vertexCount(builder, 1u);
    (void)FilaGeometryTangentSpaceMeshBuilder_normals(builder, normals, 0u);
    (void)FilaGeometryTangentSpaceMeshBuilder_algorithm(builder, FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_DEFAULT);
    (void)FilaGeometryTangentSpaceMeshBuilder_triangleCount(builder, 1u);
    (void)FilaGeometryTangentSpaceMeshBuilder_trianglesUshort3(builder, trianglesU16);

    FilaGeometryTangentSpaceMesh* mesh = FilaGeometryTangentSpaceMeshBuilder_build(builder);
    (void)FilaGeometryTangentSpaceMesh_getVertexCount(mesh);
    (void)FilaGeometryTangentSpaceMesh_getTriangleCount(mesh);
    (void)FilaGeometryTangentSpaceMesh_isRemeshed(mesh);
    (void)FilaGeometryTangentSpaceMesh_getPositions(mesh, positions, 1u, 0u);
    (void)FilaGeometryTangentSpaceMesh_getUVs(mesh, uvs, 1u, 0u);
    (void)FilaGeometryTangentSpaceMesh_getTrianglesUint3(mesh, trianglesU32, 1u);
    (void)FilaGeometryTangentSpaceMesh_getTrianglesUshort3(mesh, trianglesU16, 1u);
    (void)FilaGeometryTangentSpaceMesh_getQuatsShort4(mesh, quats, 1u, 0u);

    FilaGeometryTangentSpaceMesh_destroy(mesh);
    FilaGeometryTangentSpaceMeshBuilder_destroy(builder);
}


#include <geometry/TangentSpaceMesh.h>

#include <math/vec2.h>
#include <math/vec3.h>
#include <math/vec4.h>

#include <new>
#include <vector>

#include "../../include/geometry/TangentSpaceMesh.h"

namespace {

filament::geometry::TangentSpaceMesh::Algorithm toAlgorithm(FilaGeometryTangentSpaceMeshAlgorithm algorithm) {
    switch (algorithm) {
        case FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_MIKKTSPACE:
            return filament::geometry::TangentSpaceMesh::Algorithm::MIKKTSPACE;
        case FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_LENGYEL:
            return filament::geometry::TangentSpaceMesh::Algorithm::LENGYEL;
        case FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_HUGHES_MOLLER:
            return filament::geometry::TangentSpaceMesh::Algorithm::HUGHES_MOLLER;
        case FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_FRISVAD:
            return filament::geometry::TangentSpaceMesh::Algorithm::FRISVAD;
        case FILA_GEOMETRY_TANGENT_SPACE_MESH_ALGORITHM_DEFAULT:
        default:
            return filament::geometry::TangentSpaceMesh::Algorithm::DEFAULT;
    }
}

} // namespace

extern "C" {

FilaGeometryTangentSpaceMeshBuilder* FilaGeometryTangentSpaceMeshBuilder_create(void) {
    auto* builder = new (std::nothrow) filament::geometry::TangentSpaceMesh::Builder();
    return reinterpret_cast<FilaGeometryTangentSpaceMeshBuilder*>(builder);
}

void FilaGeometryTangentSpaceMeshBuilder_destroy(FilaGeometryTangentSpaceMeshBuilder* builder) {
    if (!builder) {
        return;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    delete cppBuilder;
}

bool FilaGeometryTangentSpaceMeshBuilder_vertexCount(FilaGeometryTangentSpaceMeshBuilder* builder, size_t vertexCount) {
    if (!builder || vertexCount == 0u) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->vertexCount(vertexCount);
    return true;
}

bool FilaGeometryTangentSpaceMeshBuilder_normals(FilaGeometryTangentSpaceMeshBuilder* builder, const float* normals3, size_t strideBytes) {
    if (!builder || !normals3) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->normals(reinterpret_cast<const filament::math::float3*>(normals3), strideBytes);
    return true;
}

bool FilaGeometryTangentSpaceMeshBuilder_tangents(FilaGeometryTangentSpaceMeshBuilder* builder, const float* tangents4, size_t strideBytes) {
    if (!builder || !tangents4) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->tangents(reinterpret_cast<const filament::math::float4*>(tangents4), strideBytes);
    return true;
}

bool FilaGeometryTangentSpaceMeshBuilder_uvs(FilaGeometryTangentSpaceMeshBuilder* builder, const float* uvs2, size_t strideBytes) {
    if (!builder || !uvs2) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->uvs(reinterpret_cast<const filament::math::float2*>(uvs2), strideBytes);
    return true;
}

bool FilaGeometryTangentSpaceMeshBuilder_positions(FilaGeometryTangentSpaceMeshBuilder* builder, const float* positions3, size_t strideBytes) {
    if (!builder || !positions3) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->positions(reinterpret_cast<const filament::math::float3*>(positions3), strideBytes);
    return true;
}

bool FilaGeometryTangentSpaceMeshBuilder_triangleCount(FilaGeometryTangentSpaceMeshBuilder* builder, size_t triangleCount) {
    if (!builder) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->triangleCount(triangleCount);
    return true;
}

bool FilaGeometryTangentSpaceMeshBuilder_trianglesUint3(FilaGeometryTangentSpaceMeshBuilder* builder, const uint32_t* triangles3u32) {
    if (!builder || !triangles3u32) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->triangles(reinterpret_cast<const filament::math::uint3*>(triangles3u32));
    return true;
}

bool FilaGeometryTangentSpaceMeshBuilder_trianglesUshort3(FilaGeometryTangentSpaceMeshBuilder* builder, const uint16_t* triangles3u16) {
    if (!builder || !triangles3u16) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->triangles(reinterpret_cast<const filament::math::ushort3*>(triangles3u16));
    return true;
}

bool FilaGeometryTangentSpaceMeshBuilder_algorithm(
        FilaGeometryTangentSpaceMeshBuilder* builder,
        FilaGeometryTangentSpaceMeshAlgorithm algorithm) {
    if (!builder) {
        return false;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    cppBuilder->algorithm(toAlgorithm(algorithm));
    return true;
}

FilaGeometryTangentSpaceMesh* FilaGeometryTangentSpaceMeshBuilder_build(FilaGeometryTangentSpaceMeshBuilder* builder) {
    if (!builder) {
        return nullptr;
    }
    auto* cppBuilder = reinterpret_cast<filament::geometry::TangentSpaceMesh::Builder*>(builder);
    try {
        return reinterpret_cast<FilaGeometryTangentSpaceMesh*>(cppBuilder->build());
    } catch (...) {
        return nullptr;
    }
}

void FilaGeometryTangentSpaceMesh_destroy(FilaGeometryTangentSpaceMesh* mesh) {
    if (!mesh) {
        return;
    }
    filament::geometry::TangentSpaceMesh::destroy(reinterpret_cast<filament::geometry::TangentSpaceMesh*>(mesh));
}

size_t FilaGeometryTangentSpaceMesh_getVertexCount(const FilaGeometryTangentSpaceMesh* mesh) {
    if (!mesh) {
        return 0u;
    }
    auto* cppMesh = reinterpret_cast<const filament::geometry::TangentSpaceMesh*>(mesh);
    return cppMesh->getVertexCount();
}

size_t FilaGeometryTangentSpaceMesh_getTriangleCount(const FilaGeometryTangentSpaceMesh* mesh) {
    if (!mesh) {
        return 0u;
    }
    auto* cppMesh = reinterpret_cast<const filament::geometry::TangentSpaceMesh*>(mesh);
    return cppMesh->getTriangleCount();
}

bool FilaGeometryTangentSpaceMesh_isRemeshed(const FilaGeometryTangentSpaceMesh* mesh) {
    if (!mesh) {
        return false;
    }
    auto* cppMesh = reinterpret_cast<const filament::geometry::TangentSpaceMesh*>(mesh);
    return cppMesh->remeshed();
}

bool FilaGeometryTangentSpaceMesh_getQuatsShort4(
        const FilaGeometryTangentSpaceMesh* mesh,
        int16_t* outQuats4i16,
        size_t outQuatCount,
        size_t outStrideBytes) {
    if (!mesh || !outQuats4i16 || outQuatCount == 0u) {
        return false;
    }
    auto* cppMesh = reinterpret_cast<const filament::geometry::TangentSpaceMesh*>(mesh);
    std::vector<filament::math::short4> quats(outQuatCount);
    try {
        cppMesh->getQuats(quats.data(), 0u);
    } catch (...) {
        return false;
    }

    const size_t stride = outStrideBytes == 0u ? sizeof(int16_t) * 4u : outStrideBytes;
    for (size_t i = 0u; i < outQuatCount; ++i) {
        int16_t* dst = reinterpret_cast<int16_t*>(reinterpret_cast<uint8_t*>(outQuats4i16) + i * stride);
        dst[0] = quats[i].x;
        dst[1] = quats[i].y;
        dst[2] = quats[i].z;
        dst[3] = quats[i].w;
    }
    return true;
}

} // extern "C"


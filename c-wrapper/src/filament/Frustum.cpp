#include <filament/Frustum.h>

#include <math/mat4.h>
#include <math/vec3.h>
#include <math/vec4.h>

#include <cmath>

#include "../../include/filament/Frustum.h"

namespace {
filament::math::mat4f toMat4f(const float m[16]) {
    return filament::math::mat4f(
            m[0], m[1], m[2], m[3],
            m[4], m[5], m[6], m[7],
            m[8], m[9], m[10], m[11],
            m[12], m[13], m[14], m[15]);
}

void copyPlanesFromCpp(const filament::Frustum& frustum, FilaFrustum* outFrustum) {
    filament::math::float4 planes[6];
    frustum.getNormalizedPlanes(planes);
    for (size_t i = 0; i < 6; ++i) {
        outFrustum->planes[i][0] = planes[i].x;
        outFrustum->planes[i][1] = planes[i].y;
        outFrustum->planes[i][2] = planes[i].z;
        outFrustum->planes[i][3] = planes[i].w;
    }
}

bool isValidPlaneIndex(FilaFrustumPlane plane) {
    return plane >= FILA_FRUSTUM_PLANE_LEFT && plane <= FILA_FRUSTUM_PLANE_NEAR;
}
} // namespace

extern "C" {

bool FilaFrustum_fromProjection(const float projection[16], FilaFrustum* outFrustum) {
    if (!projection || !outFrustum) {
        return false;
    }
    const filament::Frustum frustum(toMat4f(projection));
    copyPlanesFromCpp(frustum, outFrustum);
    return true;
}

bool FilaFrustum_setProjection(FilaFrustum* frustum, const float projection[16]) {
    if (!frustum || !projection) {
        return false;
    }
    const filament::Frustum cppFrustum(toMat4f(projection));
    copyPlanesFromCpp(cppFrustum, frustum);
    return true;
}

bool FilaFrustum_getNormalizedPlane(
        const FilaFrustum* frustum,
        FilaFrustumPlane plane,
        float outPlane4[4]) {
    if (!frustum || !outPlane4 || !isValidPlaneIndex(plane)) {
        return false;
    }
    const size_t index = static_cast<size_t>(plane);
    outPlane4[0] = frustum->planes[index][0];
    outPlane4[1] = frustum->planes[index][1];
    outPlane4[2] = frustum->planes[index][2];
    outPlane4[3] = frustum->planes[index][3];
    return true;
}

bool FilaFrustum_getNormalizedPlanes(const FilaFrustum* frustum, float outPlanes6x4[6][4]) {
    if (!frustum || !outPlanes6x4) {
        return false;
    }
    for (size_t i = 0; i < 6; ++i) {
        outPlanes6x4[i][0] = frustum->planes[i][0];
        outPlanes6x4[i][1] = frustum->planes[i][1];
        outPlanes6x4[i][2] = frustum->planes[i][2];
        outPlanes6x4[i][3] = frustum->planes[i][3];
    }
    return true;
}

bool FilaFrustum_intersectsBox(const FilaFrustum* frustum, const FilaBox* box) {
    if (!frustum || !box) {
        return false;
    }
    for (size_t i = 0; i < 6; ++i) {
        const float nx = frustum->planes[i][0];
        const float ny = frustum->planes[i][1];
        const float nz = frustum->planes[i][2];
        const float d = frustum->planes[i][3];

        const float centerDistance =
                nx * box->center[0] + ny * box->center[1] + nz * box->center[2] + d;
        const float radius =
                std::fabs(nx) * box->halfExtent[0] +
                std::fabs(ny) * box->halfExtent[1] +
                std::fabs(nz) * box->halfExtent[2];

        if (centerDistance > radius) {
            return false;
        }
    }
    return true;
}

bool FilaFrustum_intersectsSphere(const FilaFrustum* frustum, const float sphere4[4]) {
    if (!frustum || !sphere4) {
        return false;
    }
    const float x = sphere4[0];
    const float y = sphere4[1];
    const float z = sphere4[2];
    const float r = sphere4[3];

    for (size_t i = 0; i < 6; ++i) {
        const float distance =
                frustum->planes[i][0] * x +
                frustum->planes[i][1] * y +
                frustum->planes[i][2] * z +
                frustum->planes[i][3];
        if (distance > r) {
            return false;
        }
    }
    return true;
}

float FilaFrustum_containsPoint(const FilaFrustum* frustum, const float point3[3]) {
    if (!frustum || !point3) {
        return 0.0f;
    }
    float maxDistance =
            frustum->planes[0][0] * point3[0] +
            frustum->planes[0][1] * point3[1] +
            frustum->planes[0][2] * point3[2] +
            frustum->planes[0][3];
    for (size_t i = 1; i < 6; ++i) {
        const float distance =
                frustum->planes[i][0] * point3[0] +
                frustum->planes[i][1] * point3[1] +
                frustum->planes[i][2] * point3[2] +
                frustum->planes[i][3];
        if (distance > maxDistance) {
            maxDistance = distance;
        }
    }
    return maxDistance;
}

} // extern "C"


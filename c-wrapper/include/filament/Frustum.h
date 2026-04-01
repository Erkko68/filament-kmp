#ifndef FILAMENT_C_FRUSTUM_H
#define FILAMENT_C_FRUSTUM_H

#include <stdbool.h>

#include "Box.h"

#ifdef __cplusplus
extern "C" {
#endif

// Represents a 3D frustum (filament::Frustum)
typedef struct FilaFrustum {
    float planes[6][4]; // 6 planes, each represented by a float4 (x, y, z, w)
} FilaFrustum;

typedef enum FilaFrustumPlane {
    FILA_FRUSTUM_PLANE_LEFT = 0,
    FILA_FRUSTUM_PLANE_RIGHT = 1,
    FILA_FRUSTUM_PLANE_BOTTOM = 2,
    FILA_FRUSTUM_PLANE_TOP = 3,
    FILA_FRUSTUM_PLANE_FAR = 4,
    FILA_FRUSTUM_PLANE_NEAR = 5,
} FilaFrustumPlane;

// Builds a frustum from a projection matrix in GL convention.
bool FilaFrustum_fromProjection(const float projection[16], FilaFrustum* outFrustum);

// Updates an existing frustum from a projection matrix in GL convention.
bool FilaFrustum_setProjection(FilaFrustum* frustum, const float projection[16]);

// Returns one normalized plane from the frustum.
bool FilaFrustum_getNormalizedPlane(
    const FilaFrustum* frustum,
    FilaFrustumPlane plane,
    float outPlane4[4]);

// Copies all planes in left, right, bottom, top, far, near order.
bool FilaFrustum_getNormalizedPlanes(const FilaFrustum* frustum, float outPlanes6x4[6][4]);

// Returns whether a box may intersect the frustum.
bool FilaFrustum_intersectsBox(const FilaFrustum* frustum, const FilaBox* box);

// Returns whether a sphere (x,y,z,r) may intersect the frustum.
bool FilaFrustum_intersectsSphere(const FilaFrustum* frustum, const float sphere4[4]);

// Returns max signed distance to frustum planes (negative means inside).
float FilaFrustum_containsPoint(const FilaFrustum* frustum, const float point3[3]);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_FRUSTUM_H
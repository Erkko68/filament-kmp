#include "filament/Frustum.h"

// Verifies Frustum API is consumable from C.
void fila_frustum_module_compile_only(void) {
    float projection[16] = {
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f,
    };
    float plane[4];
    float planes[6][4];
    float sphere[4] = {0.0f, 0.0f, 0.0f, 1.0f};
    float point[3] = {0.0f, 0.0f, 0.0f};
    FilaBox box = {
        {0.0f, 0.0f, 0.0f},
        {1.0f, 1.0f, 1.0f},
    };
    FilaFrustum frustum;

    (void)FilaFrustum_fromProjection(projection, &frustum);
    (void)FilaFrustum_setProjection(&frustum, projection);
    (void)FilaFrustum_getNormalizedPlane(&frustum, FILA_FRUSTUM_PLANE_LEFT, plane);
    (void)FilaFrustum_getNormalizedPlanes(&frustum, planes);
    (void)FilaFrustum_intersectsBox(&frustum, &box);
    (void)FilaFrustum_intersectsSphere(&frustum, sphere);
    (void)FilaFrustum_containsPoint(&frustum, point);
}


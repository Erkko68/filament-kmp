#ifndef FILAMENT_C_FRUSTUM_H
#define FILAMENT_C_FRUSTUM_H

#ifdef __cplusplus
extern "C" {
#endif

// Represents a 3D frustum (filament::Frustum)
typedef struct FilaFrustum {
    float planes[6][4]; // 6 planes, each represented by a float4 (x, y, z, w)
} FilaFrustum;

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_FRUSTUM_H
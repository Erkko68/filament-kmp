#ifndef FILAMENT_C_BOX_H
#define FILAMENT_C_BOX_H

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

// Represents a 3D box (filament::Box)
typedef struct FilaBox {
    float center[3];
    float halfExtent[3];
} FilaBox;

// Represents an axis-aligned bounding box (filament::Aabb)
typedef struct FilaAabb {
    float min[3];
    float max[3];
} FilaAabb;

// Box utility helpers.
bool FilaBox_isEmpty(const FilaBox* box);
bool FilaBox_set(FilaBox* inOutBox, const float min3[3], const float max3[3]);
bool FilaBox_getMin(const FilaBox* box, float outMin3[3]);
bool FilaBox_getMax(const FilaBox* box, float outMax3[3]);
bool FilaBox_translateTo(const FilaBox* box, const float center3[3], FilaBox* outBox);
bool FilaBox_getBoundingSphere(const FilaBox* box, float outCenterRadius4[4]);

// AABB utility helpers.
bool FilaAabb_isEmpty(const FilaAabb* aabb);
bool FilaAabb_center(const FilaAabb* aabb, float outCenter3[3]);
bool FilaAabb_extent(const FilaAabb* aabb, float outExtent3[3]);
float FilaAabb_contains(const FilaAabb* aabb, const float point3[3]);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BOX_H
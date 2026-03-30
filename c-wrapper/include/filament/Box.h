#ifndef FILAMENT_C_BOX_H
#define FILAMENT_C_BOX_H

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

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BOX_H
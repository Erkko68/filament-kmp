#ifndef FILAMENT_C_VIEWPORT_H
#define FILAMENT_C_VIEWPORT_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Represents a 2D viewport (filament::Viewport)
typedef struct FilaViewport {
    int32_t left;
    int32_t bottom;
    uint32_t width;
    uint32_t height;
} FilaViewport;

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_VIEWPORT_H
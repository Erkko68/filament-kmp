#ifndef FILAMENT_C_TYPES_H
#define FILAMENT_C_TYPES_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque types representing Filament C++ classes
typedef struct FilaEngine FilaEngine;
typedef struct FilaRenderer FilaRenderer;
typedef struct FilaSwapChain FilaSwapChain;
typedef struct FilaView FilaView;
typedef struct FilaScene FilaScene;
typedef struct FilaCamera FilaCamera;

// C representation of utils::Entity identity.
typedef int32_t FilaEntity;

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TYPES_H

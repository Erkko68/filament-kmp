#ifndef FILAMENT_C_CAMERA_H
#define FILAMENT_C_CAMERA_H

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns the entity associated with this camera.
FilaEntity FilaCamera_getEntity(const FilaCamera* camera);

// Sets physically-based exposure values.
void FilaCamera_setExposure(FilaCamera* camera, float aperture, float shutterSpeed, float sensitivity);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_CAMERA_H


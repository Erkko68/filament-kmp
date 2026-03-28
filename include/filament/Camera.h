#ifndef FILAMENT_C_CAMERA_H
#define FILAMENT_C_CAMERA_H

#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns the entity associated with this camera.
FilaEntity FilaCamera_getEntity(const FilaCamera* camera);

// Sets physically-based exposure values.
void FilaCamera_setExposure(FilaCamera* camera, float aperture, float shutterSpeed, float sensitivity);


// Sets perspective projection using field-of-view parameters.
void FilaCamera_setProjectionFov(FilaCamera* camera, double fovInDegrees, double aspect,
		double nearPlane, double farPlane, FilaCameraFov direction);

// Sets camera transform from eye/center/up vectors.
void FilaCamera_lookAt(FilaCamera* camera,
		double eyeX, double eyeY, double eyeZ,
		double centerX, double centerY, double centerZ,
		double upX, double upY, double upZ);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_CAMERA_H


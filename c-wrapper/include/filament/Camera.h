#ifndef FILAMENT_C_CAMERA_H
#define FILAMENT_C_CAMERA_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaCameraProjection {
    FILA_CAMERA_PROJECTION_PERSPECTIVE = 0,
    FILA_CAMERA_PROJECTION_ORTHO = 1,
} FilaCameraProjection;

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

// Sets projection using explicit frustum planes.
void FilaCamera_setProjection(
    FilaCamera* camera,
    FilaCameraProjection projection,
    double left,
    double right,
    double bottom,
    double top,
    double nearPlane,
    double farPlane);

// Sets projection from focal length.
void FilaCamera_setLensProjection(
    FilaCamera* camera,
    double focalLengthInMillimeters,
    double aspect,
    double nearPlane,
    double farPlane);

// Sets custom projection matrix for both render and culling.
void FilaCamera_setCustomProjection(FilaCamera* camera, const double projection[16], double nearPlane, double farPlane);

// Sets custom render and culling projection matrices.
void FilaCamera_setCustomProjectionWithCulling(
    FilaCamera* camera,
    const double projection[16],
    const double projectionForCulling[16],
    double nearPlane,
    double farPlane);

// Sets camera model matrix.
void FilaCamera_setModelMatrix(FilaCamera* camera, const double modelMatrix[16]);

// Returns projection matrix for the requested eye.
bool FilaCamera_getProjectionMatrix(
    const FilaCamera* camera,
    uint8_t eyeId,
    double outProjection[16]);

// Returns culling projection matrix.
bool FilaCamera_getCullingProjectionMatrix(const FilaCamera* camera, double outProjection[16]);

// Returns camera model matrix.
bool FilaCamera_getModelMatrix(const FilaCamera* camera, double outModelMatrix[16]);

// Returns camera view matrix.
bool FilaCamera_getViewMatrix(const FilaCamera* camera, double outViewMatrix[16]);

// Returns near/far clipping values.
double FilaCamera_getNear(const FilaCamera* camera);
double FilaCamera_getCullingFar(const FilaCamera* camera);

// Returns camera basis and position.
bool FilaCamera_getPosition(const FilaCamera* camera, double outPosition3[3]);
bool FilaCamera_getLeftVector(const FilaCamera* camera, float outLeft3[3]);
bool FilaCamera_getUpVector(const FilaCamera* camera, float outUp3[3]);
bool FilaCamera_getForwardVector(const FilaCamera* camera, float outForward3[3]);

// Returns effective field of view in degrees.
float FilaCamera_getFieldOfViewInDegrees(const FilaCamera* camera, FilaCameraFov direction);

// Returns physically-based camera exposure values.
float FilaCamera_getAperture(const FilaCamera* camera);
float FilaCamera_getShutterSpeed(const FilaCamera* camera);
float FilaCamera_getSensitivity(const FilaCamera* camera);
double FilaCamera_getFocalLength(const FilaCamera* camera);

// Sets and gets focus distance.
void FilaCamera_setFocusDistance(FilaCamera* camera, float distance);
float FilaCamera_getFocusDistance(const FilaCamera* camera);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_CAMERA_H


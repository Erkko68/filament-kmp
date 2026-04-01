#include "filament/Camera.h"
#include "filament/Engine.h"
#include "utils/EntityManager.h"
#include "filament/Frustum.h"

// Verifies Camera API is consumable from C and composes with Engine camera lifecycle.
void test_headers_camera(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaEntity entity = FilaEntityManager_create();

    FilaCamera* camera = FilaEngine_createCamera(engine, entity);
    (void)FilaEngine_getCameraComponent(engine, entity);
    (void)FilaCamera_getEntity(camera);
    FilaCamera_setExposure(camera, 16.0f, 1.0f / 60.0f, 100.0f);
    FilaCamera_setExposureValue(camera, 1.0f);
    FilaCamera_setProjectionFov(camera, 45.0, 16.0 / 9.0, 0.1, 1000.0, FILA_CAMERA_FOV_VERTICAL);
    FilaCamera_lookAt(camera, 0.0, 1.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    double mat[16] = {
        1.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0,
    };
    double v3[3];
    float f3[3];
    double v2[2];
    double v4[4];
    FilaFrustum frustum;

    FilaCamera_setProjection(
        camera,
        FILA_CAMERA_PROJECTION_PERSPECTIVE,
        -1.0,
        1.0,
        -1.0,
        1.0,
        0.1,
        1000.0);
    FilaCamera_setLensProjection(camera, 35.0, 16.0 / 9.0, 0.1, 1000.0);
    FilaCamera_setCustomProjection(camera, mat, 0.1, 1000.0);
    FilaCamera_setCustomProjectionWithCulling(camera, mat, mat, 0.1, 1000.0);
    FilaCamera_setCustomEyeProjection(camera, mat, 1u, mat, 0.1, 1000.0);
    FilaCamera_setModelMatrix(camera, mat);
    FilaCamera_setEyeModelMatrix(camera, 0u, mat);
    FilaCamera_setScaling(camera, 1.0, 1.0);
    FilaCamera_setShift(camera, 0.0, 0.0);

    (void)FilaCamera_projectionFov(45.0, 16.0 / 9.0, 0.1, 1000.0, FILA_CAMERA_FOV_VERTICAL, mat);
    (void)FilaCamera_projectionLens(35.0, 16.0 / 9.0, 0.1, 1000.0, mat);

    (void)FilaCamera_getProjectionMatrix(camera, 0u, mat);
    (void)FilaCamera_getCullingProjectionMatrix(camera, mat);
    (void)FilaCamera_getModelMatrix(camera, mat);
    (void)FilaCamera_getViewMatrix(camera, mat);
    (void)FilaCamera_getScaling(camera, v4);
    (void)FilaCamera_getShift(camera, v2);
    (void)FilaCamera_getNear(camera);
    (void)FilaCamera_getCullingFar(camera);
    (void)FilaCamera_getPosition(camera, v3);
    (void)FilaCamera_getLeftVector(camera, f3);
    (void)FilaCamera_getUpVector(camera, f3);
    (void)FilaCamera_getForwardVector(camera, f3);
    (void)FilaCamera_getFieldOfViewInDegrees(camera, FILA_CAMERA_FOV_VERTICAL);
    (void)FilaCamera_getFrustum(camera, &frustum);
    (void)FilaCamera_getAperture(camera);
    (void)FilaCamera_getShutterSpeed(camera);
    (void)FilaCamera_getSensitivity(camera);
    (void)FilaCamera_getFocalLength(camera);
    FilaCamera_setFocusDistance(camera, 5.0f);
    (void)FilaCamera_getFocusDistance(camera);
    (void)FilaCamera_inverseProjection(mat, mat);
    (void)FilaCamera_computeEffectiveFocalLength(35.0, 5.0);
    (void)FilaCamera_computeEffectiveFov(45.0, 5.0);

    FilaEngine_destroyCameraComponent(engine, entity);
    FilaEntityManager_destroy(entity);
}

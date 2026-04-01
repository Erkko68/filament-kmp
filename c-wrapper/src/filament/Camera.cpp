#include <filament/Camera.h>
#include <filament/Frustum.h>

#include <math/mat4.h>
#include <math/vec2.h>
#include <math/vec3.h>
#include <math/vec4.h>

#include <vector>

#include <utils/Entity.h>

// C wrapper headers
#include "../../include/filament/Camera.h"

namespace {
filament::Camera::Fov toFov(FilaCameraFov fov) {
    switch (fov) {
        case FILA_CAMERA_FOV_HORIZONTAL:
            return filament::Camera::Fov::HORIZONTAL;
        case FILA_CAMERA_FOV_VERTICAL:
        default:
            return filament::Camera::Fov::VERTICAL;
    }
}

filament::Camera::Projection toProjection(FilaCameraProjection projection) {
    switch (projection) {
        case FILA_CAMERA_PROJECTION_ORTHO:
            return filament::Camera::Projection::ORTHO;
        case FILA_CAMERA_PROJECTION_PERSPECTIVE:
        default:
            return filament::Camera::Projection::PERSPECTIVE;
    }
}

filament::math::mat4 toMat4(const double m[16]) {
    return filament::math::mat4(
            m[0], m[1], m[2], m[3],
            m[4], m[5], m[6], m[7],
            m[8], m[9], m[10], m[11],
            m[12], m[13], m[14], m[15]);
}

void fromMat4(const filament::math::mat4& mat, double out[16]) {
    for (size_t c = 0; c < 4; ++c) {
        for (size_t r = 0; r < 4; ++r) {
            out[c * 4 + r] = mat[c][r];
        }
    }
}
} // namespace

extern "C" {

FilaEntity FilaCamera_getEntity(const FilaCamera* camera) {
    if (!camera) {
        return 0;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return utils::Entity::smuggle(cppCamera->getEntity());
}

void FilaCamera_setExposure(FilaCamera* camera, float aperture, float shutterSpeed, float sensitivity) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setExposure(aperture, shutterSpeed, sensitivity);
}

void FilaCamera_setExposureValue(FilaCamera* camera, float exposure) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setExposure(exposure);
}

void FilaCamera_setProjectionFov(FilaCamera* camera, double fovInDegrees, double aspect,
        double nearPlane, double farPlane, FilaCameraFov direction) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setProjection(fovInDegrees, aspect, nearPlane, farPlane, toFov(direction));
}

void FilaCamera_lookAt(FilaCamera* camera,
        double eyeX, double eyeY, double eyeZ,
        double centerX, double centerY, double centerZ,
        double upX, double upY, double upZ) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->lookAt(
            filament::math::double3{eyeX, eyeY, eyeZ},
            filament::math::double3{centerX, centerY, centerZ},
            filament::math::double3{upX, upY, upZ});
}

void FilaCamera_setProjection(
        FilaCamera* camera, FilaCameraProjection projection, double left, double right,
        double bottom, double top, double nearPlane, double farPlane) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setProjection(
            toProjection(projection), left, right, bottom, top, nearPlane, farPlane);
}

void FilaCamera_setLensProjection(
        FilaCamera* camera, double focalLengthInMillimeters, double aspect,
        double nearPlane, double farPlane) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setLensProjection(focalLengthInMillimeters, aspect, nearPlane, farPlane);
}

bool FilaCamera_projectionFov(
        double fovInDegrees, double aspect, double nearPlane, double farPlane,
        FilaCameraFov direction, double outProjection[16]) {
    if (!outProjection) {
        return false;
    }
    fromMat4(
            filament::Camera::projection(
                    toFov(direction), fovInDegrees, aspect, nearPlane, farPlane),
            outProjection);
    return true;
}

bool FilaCamera_projectionLens(
        double focalLengthInMillimeters, double aspect, double nearPlane, double farPlane,
        double outProjection[16]) {
    if (!outProjection) {
        return false;
    }
    fromMat4(
            filament::Camera::projection(
                    focalLengthInMillimeters, aspect, nearPlane, farPlane),
            outProjection);
    return true;
}

void FilaCamera_setCustomProjection(
        FilaCamera* camera, const double projection[16], double nearPlane, double farPlane) {
    if (!camera || !projection) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setCustomProjection(toMat4(projection), nearPlane, farPlane);
}

void FilaCamera_setCustomProjectionWithCulling(
        FilaCamera* camera, const double projection[16],
        const double projectionForCulling[16], double nearPlane, double farPlane) {
    if (!camera || !projection || !projectionForCulling) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setCustomProjection(
            toMat4(projection), toMat4(projectionForCulling), nearPlane, farPlane);
}

void FilaCamera_setCustomEyeProjection(
        FilaCamera* camera, const double* projectionMatrices, size_t count,
        const double projectionForCulling[16], double nearPlane, double farPlane) {
    if (!camera || !projectionMatrices || !projectionForCulling || count == 0) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    std::vector<filament::math::mat4> eyeProjections(count);
    for (size_t i = 0; i < count; ++i) {
        eyeProjections[i] = toMat4(projectionMatrices + (i * 16));
    }
    cppCamera->setCustomEyeProjection(
            eyeProjections.data(), count, toMat4(projectionForCulling), nearPlane, farPlane);
}

void FilaCamera_setModelMatrix(FilaCamera* camera, const double modelMatrix[16]) {
    if (!camera || !modelMatrix) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setModelMatrix(toMat4(modelMatrix));
}

void FilaCamera_setEyeModelMatrix(FilaCamera* camera, uint8_t eyeId, const double modelMatrix[16]) {
    if (!camera || !modelMatrix) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setEyeModelMatrix(eyeId, toMat4(modelMatrix));
}

void FilaCamera_setScaling(FilaCamera* camera, double x, double y) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setScaling(filament::math::double2{x, y});
}

void FilaCamera_setShift(FilaCamera* camera, double x, double y) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setShift(filament::math::double2{x, y});
}

bool FilaCamera_getProjectionMatrix(
        const FilaCamera* camera, uint8_t eyeId, double outProjection[16]) {
    if (!camera || !outProjection) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    fromMat4(cppCamera->getProjectionMatrix(eyeId), outProjection);
    return true;
}

bool FilaCamera_getCullingProjectionMatrix(const FilaCamera* camera, double outProjection[16]) {
    if (!camera || !outProjection) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    fromMat4(cppCamera->getCullingProjectionMatrix(), outProjection);
    return true;
}

bool FilaCamera_getModelMatrix(const FilaCamera* camera, double outModelMatrix[16]) {
    if (!camera || !outModelMatrix) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    fromMat4(cppCamera->getModelMatrix(), outModelMatrix);
    return true;
}

bool FilaCamera_getViewMatrix(const FilaCamera* camera, double outViewMatrix[16]) {
    if (!camera || !outViewMatrix) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    fromMat4(cppCamera->getViewMatrix(), outViewMatrix);
    return true;
}

bool FilaCamera_getScaling(const FilaCamera* camera, double outScaling4[4]) {
    if (!camera || !outScaling4) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    const auto v = cppCamera->getScaling();
    outScaling4[0] = v.x;
    outScaling4[1] = v.y;
    outScaling4[2] = v.z;
    outScaling4[3] = v.w;
    return true;
}

bool FilaCamera_getShift(const FilaCamera* camera, double outShift2[2]) {
    if (!camera || !outShift2) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    const auto v = cppCamera->getShift();
    outShift2[0] = v.x;
    outShift2[1] = v.y;
    return true;
}

double FilaCamera_getNear(const FilaCamera* camera) {
    if (!camera) {
        return 0.0;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return cppCamera->getNear();
}

double FilaCamera_getCullingFar(const FilaCamera* camera) {
    if (!camera) {
        return 0.0;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return cppCamera->getCullingFar();
}

bool FilaCamera_getPosition(const FilaCamera* camera, double outPosition3[3]) {
    if (!camera || !outPosition3) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    const auto p = cppCamera->getPosition();
    outPosition3[0] = p.x;
    outPosition3[1] = p.y;
    outPosition3[2] = p.z;
    return true;
}

bool FilaCamera_getLeftVector(const FilaCamera* camera, float outLeft3[3]) {
    if (!camera || !outLeft3) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    const auto v = cppCamera->getLeftVector();
    outLeft3[0] = v.x;
    outLeft3[1] = v.y;
    outLeft3[2] = v.z;
    return true;
}

bool FilaCamera_getUpVector(const FilaCamera* camera, float outUp3[3]) {
    if (!camera || !outUp3) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    const auto v = cppCamera->getUpVector();
    outUp3[0] = v.x;
    outUp3[1] = v.y;
    outUp3[2] = v.z;
    return true;
}

bool FilaCamera_getForwardVector(const FilaCamera* camera, float outForward3[3]) {
    if (!camera || !outForward3) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    const auto v = cppCamera->getForwardVector();
    outForward3[0] = v.x;
    outForward3[1] = v.y;
    outForward3[2] = v.z;
    return true;
}

float FilaCamera_getFieldOfViewInDegrees(const FilaCamera* camera, FilaCameraFov direction) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return cppCamera->getFieldOfViewInDegrees(toFov(direction));
}

bool FilaCamera_getFrustum(const FilaCamera* camera, FilaFrustum* outFrustum) {
    if (!camera || !outFrustum) {
        return false;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    const auto frustum = cppCamera->getFrustum();
    filament::math::float4 planes[6];
    frustum.getNormalizedPlanes(planes);
    for (size_t i = 0; i < 6; ++i) {
        outFrustum->planes[i][0] = planes[i].x;
        outFrustum->planes[i][1] = planes[i].y;
        outFrustum->planes[i][2] = planes[i].z;
        outFrustum->planes[i][3] = planes[i].w;
    }
    return true;
}

float FilaCamera_getAperture(const FilaCamera* camera) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return cppCamera->getAperture();
}

float FilaCamera_getShutterSpeed(const FilaCamera* camera) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return cppCamera->getShutterSpeed();
}

float FilaCamera_getSensitivity(const FilaCamera* camera) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return cppCamera->getSensitivity();
}

double FilaCamera_getFocalLength(const FilaCamera* camera) {
    if (!camera) {
        return 0.0;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return cppCamera->getFocalLength();
}

void FilaCamera_setFocusDistance(FilaCamera* camera, float distance) {
    if (!camera) {
        return;
    }
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppCamera->setFocusDistance(distance);
}

float FilaCamera_getFocusDistance(const FilaCamera* camera) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return cppCamera->getFocusDistance();
}

bool FilaCamera_inverseProjection(const double projection[16], double outInverseProjection[16]) {
    if (!projection || !outInverseProjection) {
        return false;
    }
    fromMat4(
            filament::Camera::inverseProjection(toMat4(projection)),
            outInverseProjection);
    return true;
}

double FilaCamera_computeEffectiveFocalLength(double focalLength, double focusDistance) {
    return filament::Camera::computeEffectiveFocalLength(focalLength, focusDistance);
}

double FilaCamera_computeEffectiveFov(double fovInDegrees, double focusDistance) {
    return filament::Camera::computeEffectiveFov(fovInDegrees, focusDistance);
}

} // extern "C"


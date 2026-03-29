#include "../../../filament-prebuilts/include/filament/Camera.h"

#include "../../../filament-prebuilts/include/math/vec3.h"
#include "../../../filament-prebuilts/include/utils/Entity.h"

// C wrapper headers
#include "filament/Types.h"

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

} // extern "C"



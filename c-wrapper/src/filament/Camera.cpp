#include <filament/Camera.h>

#include <utils/Entity.h>

#include "filament/Types.h"

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

} // extern "C"



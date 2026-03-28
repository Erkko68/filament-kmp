#include <filament/Camera.h>
#include <filament/Scene.h>
#include <filament/View.h>

#include "filament/Types.h"

extern "C" {

void FilaView_setScene(FilaView* view, FilaScene* scene) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppView->setScene(cppScene);
}

FilaScene* FilaView_getScene(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    return reinterpret_cast<FilaScene*>(cppView->getScene());
}

void FilaView_setCamera(FilaView* view, FilaCamera* camera) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppCamera = reinterpret_cast<filament::Camera*>(camera);
    cppView->setCamera(cppCamera);
}

bool FilaView_hasCamera(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->hasCamera();
}

FilaCamera* FilaView_getCamera(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    if (!cppView->hasCamera()) {
        return nullptr;
    }
    return reinterpret_cast<FilaCamera*>(&cppView->getCamera());
}

} // extern "C"


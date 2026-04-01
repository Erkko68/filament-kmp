#include <filament/Camera.h>
#include <filament/ColorGrading.h>
#include <filament/RenderTarget.h>
#include <filament/Scene.h>
#include <filament/View.h>
#include <filament/Viewport.h>

#include "../../include/filament/View.h"

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

void FilaView_setViewport(FilaView* view, FilaViewport viewport) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setViewport(filament::Viewport(viewport.left, viewport.bottom, viewport.width, viewport.height));
}

FilaViewport FilaView_getViewport(const FilaView* view) {
    FilaViewport viewport = {0, 0, 0, 0};
    if (!view) {
        return viewport;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    const auto& cppViewport = cppView->getViewport();
    viewport.left = cppViewport.left;
    viewport.bottom = cppViewport.bottom;
    viewport.width = cppViewport.width;
    viewport.height = cppViewport.height;
    return viewport;
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
    return reinterpret_cast<FilaCamera*>(&cppView->getCamera());
}

void FilaView_setColorGrading(FilaView* view, FilaColorGrading* colorGrading) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppColorGrading = reinterpret_cast<filament::ColorGrading*>(colorGrading);
    cppView->setColorGrading(cppColorGrading);
}

FilaColorGrading* FilaView_getColorGrading(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    return reinterpret_cast<FilaColorGrading*>(const_cast<filament::ColorGrading*>(cppView->getColorGrading()));
}

void FilaView_setRenderTarget(FilaView* view, FilaRenderTarget* renderTarget) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppRenderTarget = reinterpret_cast<filament::RenderTarget*>(renderTarget);
    cppView->setRenderTarget(cppRenderTarget);
}

FilaRenderTarget* FilaView_getRenderTarget(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    return reinterpret_cast<FilaRenderTarget*>(cppView->getRenderTarget());
}

} // extern "C"


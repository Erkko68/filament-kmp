#include <filament/Camera.h>
#include <filament/ColorGrading.h>
#include <filament/RenderTarget.h>
#include <filament/Scene.h>
#include <filament/View.h>
#include <filament/Viewport.h>
#include <utils/Entity.h>

#include "../../include/filament/View.h"

namespace {
filament::BlendMode toBlendMode(FilaViewBlendMode blendMode) {
    return static_cast<filament::BlendMode>(blendMode);
}

FilaViewBlendMode fromBlendMode(filament::BlendMode blendMode) {
    return static_cast<FilaViewBlendMode>(blendMode);
}

filament::AntiAliasing toAntiAliasing(FilaViewAntiAliasing antiAliasing) {
    return static_cast<filament::AntiAliasing>(antiAliasing);
}

FilaViewAntiAliasing fromAntiAliasing(filament::AntiAliasing antiAliasing) {
    return static_cast<FilaViewAntiAliasing>(antiAliasing);
}

filament::Dithering toDithering(FilaViewDithering dithering) {
    return static_cast<filament::Dithering>(dithering);
}

FilaViewDithering fromDithering(filament::Dithering dithering) {
    return static_cast<FilaViewDithering>(dithering);
}

filament::ShadowType toShadowType(FilaViewShadowType shadowType) {
    return static_cast<filament::ShadowType>(shadowType);
}

FilaViewShadowType fromShadowType(filament::ShadowType shadowType) {
    return static_cast<FilaViewShadowType>(shadowType);
}
} // namespace

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

void FilaView_setName(FilaView* view, const char* name) {
    if (!view || !name) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setName(name);
}

const char* FilaView_getName(const FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->getName();
}

void FilaView_setVisibleLayers(FilaView* view, uint8_t select, uint8_t values) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setVisibleLayers(select, values);
}

uint8_t FilaView_getVisibleLayers(const FilaView* view) {
    if (!view) {
        return 0u;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->getVisibleLayers();
}

void FilaView_setBlendMode(FilaView* view, FilaViewBlendMode blendMode) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setBlendMode(toBlendMode(blendMode));
}

FilaViewBlendMode FilaView_getBlendMode(const FilaView* view) {
    if (!view) {
        return FILA_VIEW_BLEND_MODE_OPAQUE;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return fromBlendMode(cppView->getBlendMode());
}

void FilaView_setAntiAliasing(FilaView* view, FilaViewAntiAliasing antiAliasing) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setAntiAliasing(toAntiAliasing(antiAliasing));
}

FilaViewAntiAliasing FilaView_getAntiAliasing(const FilaView* view) {
    if (!view) {
        return FILA_VIEW_ANTI_ALIASING_NONE;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return fromAntiAliasing(cppView->getAntiAliasing());
}

void FilaView_setDithering(FilaView* view, FilaViewDithering dithering) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setDithering(toDithering(dithering));
}

FilaViewDithering FilaView_getDithering(const FilaView* view) {
    if (!view) {
        return FILA_VIEW_DITHERING_NONE;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return fromDithering(cppView->getDithering());
}

void FilaView_setShadowType(FilaView* view, FilaViewShadowType shadowType) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setShadowType(toShadowType(shadowType));
}

FilaViewShadowType FilaView_getShadowType(const FilaView* view) {
    if (!view) {
        return FILA_VIEW_SHADOW_TYPE_PCF;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return fromShadowType(cppView->getShadowType());
}

void FilaView_setShadowingEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setShadowingEnabled(enabled);
}

bool FilaView_isShadowingEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isShadowingEnabled();
}

void FilaView_setScreenSpaceRefractionEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setScreenSpaceRefractionEnabled(enabled);
}

bool FilaView_isScreenSpaceRefractionEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isScreenSpaceRefractionEnabled();
}

void FilaView_setPostProcessingEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setPostProcessingEnabled(enabled);
}

bool FilaView_isPostProcessingEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isPostProcessingEnabled();
}

void FilaView_setFrontFaceWindingInverted(FilaView* view, bool inverted) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setFrontFaceWindingInverted(inverted);
}

bool FilaView_isFrontFaceWindingInverted(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isFrontFaceWindingInverted();
}

void FilaView_setTransparentPickingEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setTransparentPickingEnabled(enabled);
}

bool FilaView_isTransparentPickingEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isTransparentPickingEnabled();
}

void FilaView_setStencilBufferEnabled(FilaView* view, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setStencilBufferEnabled(enabled);
}

bool FilaView_isStencilBufferEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isStencilBufferEnabled();
}

void FilaView_setChannelDepthClearEnabled(FilaView* view, uint8_t channel, bool enabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setChannelDepthClearEnabled(channel, enabled);
}

bool FilaView_isChannelDepthClearEnabled(const FilaView* view, uint8_t channel) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isChannelDepthClearEnabled(channel);
}

void FilaView_setDynamicLightingOptions(FilaView* view, float zLightNear, float zLightFar) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setDynamicLightingOptions(zLightNear, zLightFar);
}

void FilaView_setFrustumCullingEnabled(FilaView* view, bool cullingEnabled) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppView->setFrustumCullingEnabled(cullingEnabled);
}

bool FilaView_isFrustumCullingEnabled(const FilaView* view) {
    if (!view) {
        return false;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return cppView->isFrustumCullingEnabled();
}

FilaEntity FilaView_getFogEntity(const FilaView* view) {
    if (!view) {
        return 0;
    }
    auto cppView = reinterpret_cast<const filament::View*>(view);
    return utils::Entity::smuggle(cppView->getFogEntity());
}

} // extern "C"


#include <filament/Engine.h>
#include <filament/Skybox.h>
#include <filament/Texture.h>

#include <math/vec4.h>

#include "filament/Skybox.h"

namespace {
using SkyboxBuilder = filament::Skybox::Builder;
} // namespace

extern "C" {

FilaSkyboxBuilder* FilaSkyboxBuilder_create(void) {
    auto builder = new SkyboxBuilder();
    return reinterpret_cast<FilaSkyboxBuilder*>(builder);
}

void FilaSkyboxBuilder_destroy(FilaSkyboxBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkyboxBuilder*>(builder);
    delete cppBuilder;
}

void FilaSkyboxBuilder_environment(FilaSkyboxBuilder* builder, FilaTexture* cubemap) {
    if (!builder || !cubemap) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkyboxBuilder*>(builder);
    auto cppCubemap = reinterpret_cast<filament::Texture*>(cubemap);
    cppBuilder->environment(cppCubemap);
}

void FilaSkyboxBuilder_showSun(FilaSkyboxBuilder* builder, bool show) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkyboxBuilder*>(builder);
    cppBuilder->showSun(show);
}

void FilaSkyboxBuilder_intensity(FilaSkyboxBuilder* builder, float intensity) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkyboxBuilder*>(builder);
    cppBuilder->intensity(intensity);
}

void FilaSkyboxBuilder_color(FilaSkyboxBuilder* builder, float r, float g, float b, float a) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkyboxBuilder*>(builder);
    cppBuilder->color(filament::math::float4(r, g, b, a));
}

void FilaSkyboxBuilder_priority(FilaSkyboxBuilder* builder, uint8_t priority) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkyboxBuilder*>(builder);
    cppBuilder->priority(priority);
}

FilaSkybox* FilaSkyboxBuilder_build(FilaSkyboxBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<SkyboxBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaSkybox*>(cppBuilder->build(*cppEngine));
}

void FilaSkybox_setLayerMask(FilaSkybox* skybox, uint8_t select, uint8_t values) {
    if (!skybox) {
        return;
    }
    auto cppSkybox = reinterpret_cast<filament::Skybox*>(skybox);
    cppSkybox->setLayerMask(select, values);
}

uint8_t FilaSkybox_getLayerMask(const FilaSkybox* skybox) {
    if (!skybox) {
        return 0;
    }
    auto cppSkybox = reinterpret_cast<const filament::Skybox*>(skybox);
    return cppSkybox->getLayerMask();
}

float FilaSkybox_getIntensity(const FilaSkybox* skybox) {
    if (!skybox) {
        return 0.0f;
    }
    auto cppSkybox = reinterpret_cast<const filament::Skybox*>(skybox);
    return cppSkybox->getIntensity();
}

const FilaTexture* FilaSkybox_getTexture(const FilaSkybox* skybox) {
    if (!skybox) {
        return nullptr;
    }
    auto cppSkybox = reinterpret_cast<const filament::Skybox*>(skybox);
    return reinterpret_cast<const FilaTexture*>(cppSkybox->getTexture());
}

} // extern "C"


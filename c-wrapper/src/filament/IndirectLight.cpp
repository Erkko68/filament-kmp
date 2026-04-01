#include <filament/Engine.h>
#include <filament/IndirectLight.h>
#include <filament/Texture.h>

#include "../../include/filament/IndirectLight.h"

namespace {
using IndirectLightBuilder = filament::IndirectLight::Builder;
} // namespace

extern "C" {

FilaIndirectLightBuilder* FilaIndirectLightBuilder_create(void) {
    auto builder = new IndirectLightBuilder();
    return reinterpret_cast<FilaIndirectLightBuilder*>(builder);
}

void FilaIndirectLightBuilder_destroy(FilaIndirectLightBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<IndirectLightBuilder*>(builder);
    delete cppBuilder;
}

void FilaIndirectLightBuilder_reflections(FilaIndirectLightBuilder* builder, const FilaTexture* cubemap) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<IndirectLightBuilder*>(builder);
    auto cppCubemap = reinterpret_cast<const filament::Texture*>(cubemap);
    cppBuilder->reflections(cppCubemap);
}

void FilaIndirectLightBuilder_intensity(FilaIndirectLightBuilder* builder, float intensity) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<IndirectLightBuilder*>(builder);
    cppBuilder->intensity(intensity);
}

FilaIndirectLight* FilaIndirectLightBuilder_build(FilaIndirectLightBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<IndirectLightBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaIndirectLight*>(cppBuilder->build(*cppEngine));
}

void FilaIndirectLight_setIntensity(FilaIndirectLight* indirectLight, float intensity) {
    if (!indirectLight) {
        return;
    }
    auto cppIndirectLight = reinterpret_cast<filament::IndirectLight*>(indirectLight);
    cppIndirectLight->setIntensity(intensity);
}

float FilaIndirectLight_getIntensity(const FilaIndirectLight* indirectLight) {
    if (!indirectLight) {
        return 0.0f;
    }
    auto cppIndirectLight = reinterpret_cast<const filament::IndirectLight*>(indirectLight);
    return cppIndirectLight->getIntensity();
}

const FilaTexture* FilaIndirectLight_getReflectionsTexture(const FilaIndirectLight* indirectLight) {
    if (!indirectLight) {
        return nullptr;
    }
    auto cppIndirectLight = reinterpret_cast<const filament::IndirectLight*>(indirectLight);
    return reinterpret_cast<const FilaTexture*>(cppIndirectLight->getReflectionsTexture());
}

const FilaTexture* FilaIndirectLight_getIrradianceTexture(const FilaIndirectLight* indirectLight) {
    if (!indirectLight) {
        return nullptr;
    }
    auto cppIndirectLight = reinterpret_cast<const filament::IndirectLight*>(indirectLight);
    return reinterpret_cast<const FilaTexture*>(cppIndirectLight->getIrradianceTexture());
}

} // extern "C"


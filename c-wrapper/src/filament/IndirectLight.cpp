#include <filament/Engine.h>
#include <filament/IndirectLight.h>
#include <filament/Texture.h>

#include <math/mat3.h>

#include <cstddef>

#include "../../include/filament/IndirectLight.h"

namespace {
using IndirectLightBuilder = filament::IndirectLight::Builder;

filament::math::mat3f toMat3f(const float values[9]) {
    if (!values) {
        return filament::math::mat3f();
    }
    const filament::math::float3 c0(values[0], values[1], values[2]);
    const filament::math::float3 c1(values[3], values[4], values[5]);
    const filament::math::float3 c2(values[6], values[7], values[8]);
    return filament::math::mat3f(c0, c1, c2);
}

void fromMat3f(const filament::math::mat3f& matrix, float outValues[9]) {
    if (!outValues) {
        return;
    }
    for (size_t c = 0; c < 3; ++c) {
        for (size_t r = 0; r < 3; ++r) {
            outValues[c * 3 + r] = matrix[c][r];
        }
    }
}
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

void FilaIndirectLightBuilder_radiance(FilaIndirectLightBuilder* builder, uint8_t bands, const float* sh3) {
    if (!builder || !sh3) {
        return;
    }
    auto cppBuilder = reinterpret_cast<IndirectLightBuilder*>(builder);
    cppBuilder->radiance(bands, reinterpret_cast<const filament::math::float3*>(sh3));
}

void FilaIndirectLightBuilder_rotationMat3f(FilaIndirectLightBuilder* builder, const float rotation3x3[9]) {
    if (!builder || !rotation3x3) {
        return;
    }
    auto cppBuilder = reinterpret_cast<IndirectLightBuilder*>(builder);
    cppBuilder->rotation(toMat3f(rotation3x3));
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

void FilaIndirectLight_setRotationMat3f(FilaIndirectLight* indirectLight, const float rotation3x3[9]) {
    if (!indirectLight || !rotation3x3) {
        return;
    }
    auto cppIndirectLight = reinterpret_cast<filament::IndirectLight*>(indirectLight);
    cppIndirectLight->setRotation(toMat3f(rotation3x3));
}

bool FilaIndirectLight_getRotationMat3f(const FilaIndirectLight* indirectLight, float outRotation3x3[9]) {
    if (!indirectLight || !outRotation3x3) {
        return false;
    }
    auto cppIndirectLight = reinterpret_cast<const filament::IndirectLight*>(indirectLight);
    fromMat3f(cppIndirectLight->getRotation(), outRotation3x3);
    return true;
}

bool FilaIndirectLight_getDirectionEstimate(const FilaIndirectLight* indirectLight, float outDirection3[3]) {
    if (!indirectLight || !outDirection3) {
        return false;
    }
    auto cppIndirectLight = reinterpret_cast<const filament::IndirectLight*>(indirectLight);
    const auto direction = cppIndirectLight->getDirectionEstimate();
    outDirection3[0] = direction.x;
    outDirection3[1] = direction.y;
    outDirection3[2] = direction.z;
    return true;
}

bool FilaIndirectLight_getColorEstimate(
        const FilaIndirectLight* indirectLight,
        const float direction3[3],
        float outColor4[4]) {
    if (!indirectLight || !direction3 || !outColor4) {
        return false;
    }
    auto cppIndirectLight = reinterpret_cast<const filament::IndirectLight*>(indirectLight);
    const auto color = cppIndirectLight->getColorEstimate(
            filament::math::float3(direction3[0], direction3[1], direction3[2]));
    outColor4[0] = color.r;
    outColor4[1] = color.g;
    outColor4[2] = color.b;
    outColor4[3] = color.a;
    return true;
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


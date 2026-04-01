#include <filament/TextureSampler.h>

#include "../../include/filament/TextureSampler.h"

namespace {
using TextureSampler = filament::TextureSampler;

filament::backend::SamplerMinFilter toMinFilter(FilaSamplerMinFilter filter) {
    return static_cast<filament::backend::SamplerMinFilter>(filter);
}

filament::backend::SamplerMagFilter toMagFilter(FilaSamplerMagFilter filter) {
    return static_cast<filament::backend::SamplerMagFilter>(filter);
}

filament::backend::SamplerWrapMode toWrapMode(FilaSamplerWrapMode mode) {
    return static_cast<filament::backend::SamplerWrapMode>(mode);
}

filament::backend::SamplerCompareMode toCompareMode(FilaSamplerCompareMode mode) {
    return static_cast<filament::backend::SamplerCompareMode>(mode);
}

filament::backend::SamplerCompareFunc toCompareFunc(FilaSamplerCompareFunc func) {
    return static_cast<filament::backend::SamplerCompareFunc>(func);
}

FilaSamplerMinFilter fromMinFilter(filament::backend::SamplerMinFilter filter) {
    return static_cast<FilaSamplerMinFilter>(filter);
}

FilaSamplerMagFilter fromMagFilter(filament::backend::SamplerMagFilter filter) {
    return static_cast<FilaSamplerMagFilter>(filter);
}

FilaSamplerWrapMode fromWrapMode(filament::backend::SamplerWrapMode mode) {
    return static_cast<FilaSamplerWrapMode>(mode);
}

FilaSamplerCompareMode fromCompareMode(filament::backend::SamplerCompareMode mode) {
    return static_cast<FilaSamplerCompareMode>(mode);
}

FilaSamplerCompareFunc fromCompareFunc(filament::backend::SamplerCompareFunc func) {
    return static_cast<FilaSamplerCompareFunc>(func);
}
} // namespace

extern "C" {

FilaTextureParams* FilaTextureParams_create(void) {
    auto sampler = new TextureSampler();
    return reinterpret_cast<FilaTextureParams*>(sampler);
}

void FilaTextureParams_destroy(FilaTextureParams* sampler) {
    if (!sampler) return;
    auto cppSampler = reinterpret_cast<TextureSampler*>(sampler);
    delete cppSampler;
}

void FilaTextureParams_setMinFilter(FilaTextureParams* sampler, FilaSamplerMinFilter filter) {
    if (!sampler) return;
    auto cppSampler = reinterpret_cast<TextureSampler*>(sampler);
    cppSampler->setMinFilter(toMinFilter(filter));
}

void FilaTextureParams_setMagFilter(FilaTextureParams* sampler, FilaSamplerMagFilter filter) {
    if (!sampler) return;
    auto cppSampler = reinterpret_cast<TextureSampler*>(sampler);
    cppSampler->setMagFilter(toMagFilter(filter));
}

void FilaTextureParams_setWrapModeS(FilaTextureParams* sampler, FilaSamplerWrapMode mode) {
    if (!sampler) return;
    auto cppSampler = reinterpret_cast<TextureSampler*>(sampler);
    cppSampler->setWrapModeS(toWrapMode(mode));
}

void FilaTextureParams_setWrapModeT(FilaTextureParams* sampler, FilaSamplerWrapMode mode) {
    if (!sampler) return;
    auto cppSampler = reinterpret_cast<TextureSampler*>(sampler);
    cppSampler->setWrapModeT(toWrapMode(mode));
}

void FilaTextureParams_setWrapModeR(FilaTextureParams* sampler, FilaSamplerWrapMode mode) {
    if (!sampler) return;
    auto cppSampler = reinterpret_cast<TextureSampler*>(sampler);
    cppSampler->setWrapModeR(toWrapMode(mode));
}

void FilaTextureParams_setAnisotropy(FilaTextureParams* sampler, float anisotropy) {
    if (!sampler) return;
    auto cppSampler = reinterpret_cast<TextureSampler*>(sampler);
    cppSampler->setAnisotropy(anisotropy);
}

void FilaTextureParams_setCompareMode(FilaTextureParams* sampler, FilaSamplerCompareMode mode, FilaSamplerCompareFunc func) {
    if (!sampler) return;
    auto cppSampler = reinterpret_cast<TextureSampler*>(sampler);
    cppSampler->setCompareMode(toCompareMode(mode), toCompareFunc(func));
}

FilaSamplerMinFilter FilaTextureParams_getMinFilter(const FilaTextureParams* sampler) {
    if (!sampler) return FILA_SAMPLER_MIN_NEAREST;
    auto cppSampler = reinterpret_cast<const TextureSampler*>(sampler);
    return fromMinFilter(cppSampler->getMinFilter());
}

FilaSamplerMagFilter FilaTextureParams_getMagFilter(const FilaTextureParams* sampler) {
    if (!sampler) return FILA_SAMPLER_MAG_NEAREST;
    auto cppSampler = reinterpret_cast<const TextureSampler*>(sampler);
    return fromMagFilter(cppSampler->getMagFilter());
}

FilaSamplerWrapMode FilaTextureParams_getWrapModeS(const FilaTextureParams* sampler) {
    if (!sampler) return FILA_SAMPLER_WRAP_CLAMP_TO_EDGE;
    auto cppSampler = reinterpret_cast<const TextureSampler*>(sampler);
    return fromWrapMode(cppSampler->getWrapModeS());
}

FilaSamplerWrapMode FilaTextureParams_getWrapModeT(const FilaTextureParams* sampler) {
    if (!sampler) return FILA_SAMPLER_WRAP_CLAMP_TO_EDGE;
    auto cppSampler = reinterpret_cast<const TextureSampler*>(sampler);
    return fromWrapMode(cppSampler->getWrapModeT());
}

FilaSamplerWrapMode FilaTextureParams_getWrapModeR(const FilaTextureParams* sampler) {
    if (!sampler) return FILA_SAMPLER_WRAP_CLAMP_TO_EDGE;
    auto cppSampler = reinterpret_cast<const TextureSampler*>(sampler);
    return fromWrapMode(cppSampler->getWrapModeR());
}

float FilaTextureParams_getAnisotropy(const FilaTextureParams* sampler) {
    if (!sampler) return 1.0f;
    auto cppSampler = reinterpret_cast<const TextureSampler*>(sampler);
    return cppSampler->getAnisotropy();
}

FilaSamplerCompareMode FilaTextureParams_getCompareMode(const FilaTextureParams* sampler) {
    if (!sampler) return FILA_SAMPLER_COMPARE_NONE;
    auto cppSampler = reinterpret_cast<const TextureSampler*>(sampler);
    return fromCompareMode(cppSampler->getCompareMode());
}

FilaSamplerCompareFunc FilaTextureParams_getCompareFunc(const FilaTextureParams* sampler) {
    if (!sampler) return FILA_SAMPLER_COMPARE_LE;
    auto cppSampler = reinterpret_cast<const TextureSampler*>(sampler);
    return fromCompareFunc(cppSampler->getCompareFunc());
}

} // extern "C"


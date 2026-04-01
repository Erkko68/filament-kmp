#include <filament/Engine.h>
#include <filament/RenderTarget.h>
#include <filament/Texture.h>

#include "../../include/filament/RenderTarget.h"

namespace {
using RenderTargetBuilder = filament::RenderTarget::Builder;

filament::RenderTarget::AttachmentPoint toAttachmentPoint(FilaRenderTargetAttachmentPoint attachment) {
    return static_cast<filament::RenderTarget::AttachmentPoint>(attachment);
}

filament::RenderTarget::CubemapFace toCubemapFace(FilaCubemapFace face) {
    return static_cast<filament::RenderTarget::CubemapFace>(face);
}

FilaCubemapFace fromCubemapFace(filament::RenderTarget::CubemapFace face) {
    return static_cast<FilaCubemapFace>(face);
}
} // namespace

extern "C" {

FilaRenderTargetBuilder* FilaRenderTargetBuilder_create(void) {
    auto builder = new RenderTargetBuilder();
    return reinterpret_cast<FilaRenderTargetBuilder*>(builder);
}

void FilaRenderTargetBuilder_destroy(FilaRenderTargetBuilder* builder) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<RenderTargetBuilder*>(builder);
    delete cppBuilder;
}

void FilaRenderTargetBuilder_texture(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        FilaTexture* texture) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<RenderTargetBuilder*>(builder);
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    cppBuilder->texture(toAttachmentPoint(attachment), cppTexture);
}

void FilaRenderTargetBuilder_mipLevel(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        uint8_t level) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<RenderTargetBuilder*>(builder);
    cppBuilder->mipLevel(toAttachmentPoint(attachment), level);
}

void FilaRenderTargetBuilder_face(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        FilaCubemapFace face) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<RenderTargetBuilder*>(builder);
    cppBuilder->face(toAttachmentPoint(attachment), toCubemapFace(face));
}

void FilaRenderTargetBuilder_layer(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        uint32_t layer) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<RenderTargetBuilder*>(builder);
    cppBuilder->layer(toAttachmentPoint(attachment), layer);
}

void FilaRenderTargetBuilder_samples(FilaRenderTargetBuilder* builder, uint8_t samples) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<RenderTargetBuilder*>(builder);
    cppBuilder->samples(samples);
}

void FilaRenderTargetBuilder_multiview(FilaRenderTargetBuilder* builder,
        FilaRenderTargetAttachmentPoint attachment,
        uint8_t layerCount,
        uint8_t baseLayer) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<RenderTargetBuilder*>(builder);
    cppBuilder->multiview(toAttachmentPoint(attachment), layerCount, baseLayer);
}

FilaRenderTarget* FilaRenderTargetBuilder_build(FilaRenderTargetBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) return nullptr;
    auto cppBuilder = reinterpret_cast<RenderTargetBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaRenderTarget*>(cppBuilder->build(*cppEngine));
}

FilaTexture* FilaRenderTarget_getTexture(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment) {
    if (!renderTarget) return nullptr;
    auto cppRenderTarget = reinterpret_cast<const filament::RenderTarget*>(renderTarget);
    return reinterpret_cast<FilaTexture*>(cppRenderTarget->getTexture(toAttachmentPoint(attachment)));
}

uint8_t FilaRenderTarget_getMipLevel(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment) {
    if (!renderTarget) return 0;
    auto cppRenderTarget = reinterpret_cast<const filament::RenderTarget*>(renderTarget);
    return cppRenderTarget->getMipLevel(toAttachmentPoint(attachment));
}

FilaCubemapFace FilaRenderTarget_getFace(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment) {
    if (!renderTarget) return FILA_CUBEMAP_FACE_POSITIVE_X;
    auto cppRenderTarget = reinterpret_cast<const filament::RenderTarget*>(renderTarget);
    return fromCubemapFace(cppRenderTarget->getFace(toAttachmentPoint(attachment)));
}

uint32_t FilaRenderTarget_getLayer(const FilaRenderTarget* renderTarget,
        FilaRenderTargetAttachmentPoint attachment) {
    if (!renderTarget) return 0;
    auto cppRenderTarget = reinterpret_cast<const filament::RenderTarget*>(renderTarget);
    return cppRenderTarget->getLayer(toAttachmentPoint(attachment));
}

uint8_t FilaRenderTarget_getSupportedColorAttachmentsCount(const FilaRenderTarget* renderTarget) {
    if (!renderTarget) return filament::RenderTarget::MIN_SUPPORTED_COLOR_ATTACHMENTS_COUNT;
    auto cppRenderTarget = reinterpret_cast<const filament::RenderTarget*>(renderTarget);
    return cppRenderTarget->getSupportedColorAttachmentsCount();
}

} // extern "C"


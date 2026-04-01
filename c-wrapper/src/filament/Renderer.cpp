#include <backend/PixelBufferDescriptor.h>
#include <filament/RenderTarget.h>
#include <filament/Renderer.h>
#include <filament/SwapChain.h>
#include <filament/View.h>
#include <filament/Viewport.h>

#include "../../include/filament/Renderer.h"

namespace {
filament::Viewport toViewport(FilaViewport viewport) {
    return filament::Viewport{viewport.left, viewport.bottom, viewport.width, viewport.height};
}

void consumePixelBuffer(FilaPixelBufferDescriptor* buffer) {
    buffer->impl = nullptr;
    buffer->callback = nullptr;
    buffer->user = nullptr;
    buffer->handler = nullptr;
    buffer->consumed = true;
}
}

extern "C" {

bool FilaRenderer_beginFrame(FilaRenderer* renderer, FilaSwapChain* swapChain,
        uint64_t vsyncSteadyClockTimeNano) {
    if (!renderer || !swapChain) {
        return false;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    auto cppSwapChain = reinterpret_cast<filament::SwapChain*>(swapChain);
    return cppRenderer->beginFrame(cppSwapChain, vsyncSteadyClockTimeNano);
}

void FilaRenderer_render(FilaRenderer* renderer, const FilaView* view) {
    if (!renderer || !view) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    auto cppView = reinterpret_cast<const filament::View*>(view);
    cppRenderer->render(cppView);
}

void FilaRenderer_endFrame(FilaRenderer* renderer) {
    if (!renderer) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppRenderer->endFrame();
}

void FilaRenderer_copyFrame(
        FilaRenderer* renderer, FilaSwapChain* dstSwapChain, FilaViewport dstViewport,
        FilaViewport srcViewport, uint32_t flags) {
    if (!renderer || !dstSwapChain) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    auto cppSwapChain = reinterpret_cast<filament::SwapChain*>(dstSwapChain);
    cppRenderer->copyFrame(cppSwapChain, toViewport(dstViewport), toViewport(srcViewport), flags);
}

void FilaRenderer_readPixels(
        FilaRenderer* renderer, uint32_t xoffset, uint32_t yoffset, uint32_t width,
        uint32_t height, FilaPixelBufferDescriptor* buffer) {
    if (!renderer || !buffer || !buffer->impl) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    auto cppBuffer = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(buffer->impl);
    cppRenderer->readPixels(xoffset, yoffset, width, height, std::move(*cppBuffer));
    delete cppBuffer;
    consumePixelBuffer(buffer);
}

void FilaRenderer_readPixelsRenderTarget(
        FilaRenderer* renderer, FilaRenderTarget* renderTarget, uint32_t xoffset,
        uint32_t yoffset, uint32_t width, uint32_t height, FilaPixelBufferDescriptor* buffer) {
    if (!renderer || !renderTarget || !buffer || !buffer->impl) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    auto cppRenderTarget = reinterpret_cast<filament::RenderTarget*>(renderTarget);
    auto cppBuffer = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(buffer->impl);
    cppRenderer->readPixels(cppRenderTarget, xoffset, yoffset, width, height, std::move(*cppBuffer));
    delete cppBuffer;
    consumePixelBuffer(buffer);
}

void FilaRenderer_renderStandaloneView(FilaRenderer* renderer, const FilaView* view) {
    if (!renderer || !view) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    auto cppView = reinterpret_cast<const filament::View*>(view);
    cppRenderer->renderStandaloneView(cppView);
}

} // extern "C"


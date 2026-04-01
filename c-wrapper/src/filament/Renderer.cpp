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

filament::Renderer::FrameRateOptions toFrameRateOptions(const FilaRendererFrameRateOptions* options) {
    filament::Renderer::FrameRateOptions out;
    if (!options) {
        return out;
    }
    out.headRoomRatio = options->headRoomRatio;
    out.scaleRate = options->scaleRate;
    out.history = options->history;
    out.interval = options->interval;
    return out;
}

filament::Renderer::ClearOptions toClearOptions(const FilaRendererClearOptions* options) {
    filament::Renderer::ClearOptions out;
    if (!options) {
        return out;
    }
    out.clearColor.x = options->clearColor[0];
    out.clearColor.y = options->clearColor[1];
    out.clearColor.z = options->clearColor[2];
    out.clearColor.w = options->clearColor[3];
    out.clearStencil = options->clearStencil;
    out.clear = options->clear;
    out.discard = options->discard;
    return out;
}

void fromClearOptions(const filament::Renderer::ClearOptions& in, FilaRendererClearOptions* out) {
    if (!out) {
        return;
    }
    out->clearColor[0] = in.clearColor.x;
    out->clearColor[1] = in.clearColor.y;
    out->clearColor[2] = in.clearColor.z;
    out->clearColor[3] = in.clearColor.w;
    out->clearStencil = in.clearStencil;
    out->clear = in.clear;
    out->discard = in.discard;
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

void FilaRenderer_setDisplayInfo(FilaRenderer* renderer, const FilaRendererDisplayInfo* info) {
    if (!renderer) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppRenderer->setDisplayInfo(filament::Renderer::DisplayInfo{
            info ? info->refreshRate : 60.0f
    });
}

void FilaRenderer_setFrameRateOptions(FilaRenderer* renderer, const FilaRendererFrameRateOptions* options) {
    if (!renderer) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppRenderer->setFrameRateOptions(toFrameRateOptions(options));
}

void FilaRenderer_setClearOptions(FilaRenderer* renderer, const FilaRendererClearOptions* options) {
    if (!renderer) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppRenderer->setClearOptions(toClearOptions(options));
}

bool FilaRenderer_getClearOptions(const FilaRenderer* renderer, FilaRendererClearOptions* outOptions) {
    if (!renderer || !outOptions) {
        return false;
    }
    auto cppRenderer = reinterpret_cast<const filament::Renderer*>(renderer);
    fromClearOptions(cppRenderer->getClearOptions(), outOptions);
    return true;
}

void FilaRenderer_setVsyncTime(FilaRenderer* renderer, uint64_t steadyClockTimeNano) {
    if (!renderer) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppRenderer->setVsyncTime(steadyClockTimeNano);
}

void FilaRenderer_skipFrame(FilaRenderer* renderer, uint64_t vsyncSteadyClockTimeNano) {
    if (!renderer) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppRenderer->skipFrame(vsyncSteadyClockTimeNano);
}

bool FilaRenderer_shouldRenderFrame(const FilaRenderer* renderer) {
    if (!renderer) {
        return false;
    }
    auto cppRenderer = reinterpret_cast<const filament::Renderer*>(renderer);
    return cppRenderer->shouldRenderFrame();
}

double FilaRenderer_getUserTime(const FilaRenderer* renderer) {
    if (!renderer) {
        return 0.0;
    }
    auto cppRenderer = reinterpret_cast<const filament::Renderer*>(renderer);
    return cppRenderer->getUserTime();
}

void FilaRenderer_resetUserTime(FilaRenderer* renderer) {
    if (!renderer) {
        return;
    }
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppRenderer->resetUserTime();
}

} // extern "C"


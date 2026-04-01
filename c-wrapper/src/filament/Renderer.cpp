#include <filament/Renderer.h>
#include <filament/SwapChain.h>
#include <filament/View.h>

#include "../../include/filament/Types.h"

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

} // extern "C"


#ifndef FILAMENT_C_RENDERER_H
#define FILAMENT_C_RENDERER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "Viewport.h"
#include "BufferDescriptor.h"
#include "Options.h"

#ifdef __cplusplus
extern "C" {
#endif

#define FILA_RENDERER_COPY_FRAME_COMMIT 0x1u
#define FILA_RENDERER_COPY_FRAME_SET_PRESENTATION_TIME 0x2u
#define FILA_RENDERER_COPY_FRAME_CLEAR 0x4u

#define FILA_RENDERER_FRAME_INFO_INVALID ((int64_t)-1)
#define FILA_RENDERER_FRAME_INFO_PENDING ((int64_t)-2)

typedef struct FilaRendererFrameInfo {
    uint32_t frameId;
    int64_t gpuFrameDuration;
    int64_t denoisedGpuFrameDuration;
    int64_t beginFrame;
    int64_t endFrame;
    int64_t backendBeginFrame;
    int64_t backendEndFrame;
    int64_t gpuFrameComplete;
    int64_t vsync;
    int64_t displayPresent;
    int64_t presentDeadline;
    int64_t displayPresentInterval;
    int64_t compositionToPresentLatency;
    int64_t expectedPresentLatency;
} FilaRendererFrameInfo;

// Begins a frame on the given swap chain.
// Returns true when rendering should proceed, false when the frame should be skipped.
bool FilaRenderer_beginFrame(FilaRenderer* renderer, FilaSwapChain* swapChain, uint64_t vsyncSteadyClockTimeNano);

// Renders a view between beginFrame and endFrame.
void FilaRenderer_render(FilaRenderer* renderer, const FilaView* view);

// Ends the current frame.
void FilaRenderer_endFrame(FilaRenderer* renderer);

// Copies the current frame to another swap chain region.
void FilaRenderer_copyFrame(
    FilaRenderer* renderer,
    FilaSwapChain* dstSwapChain,
    FilaViewport dstViewport,
    FilaViewport srcViewport,
    uint32_t flags);

// Reads pixels from the renderer's current output.
void FilaRenderer_readPixels(
    FilaRenderer* renderer,
    uint32_t xoffset,
    uint32_t yoffset,
    uint32_t width,
    uint32_t height,
    FilaPixelBufferDescriptor* buffer);

// Reads pixels from a render target.
void FilaRenderer_readPixelsRenderTarget(
    FilaRenderer* renderer,
    FilaRenderTarget* renderTarget,
    uint32_t xoffset,
    uint32_t yoffset,
    uint32_t width,
    uint32_t height,
    FilaPixelBufferDescriptor* buffer);

// Renders a standalone view outside begin/end frame.
void FilaRenderer_renderStandaloneView(FilaRenderer* renderer, const FilaView* view);

// Returns the engine associated with this renderer.
FilaEngine* FilaRenderer_getEngine(FilaRenderer* renderer);
const FilaEngine* FilaRenderer_getEngineConst(const FilaRenderer* renderer);

// Configures renderer/display/frame pacing options.
void FilaRenderer_setDisplayInfo(FilaRenderer* renderer, const FilaRendererDisplayInfo* info);
void FilaRenderer_setFrameRateOptions(FilaRenderer* renderer, const FilaRendererFrameRateOptions* options);
void FilaRenderer_setClearOptions(FilaRenderer* renderer, const FilaRendererClearOptions* options);
bool FilaRenderer_getClearOptions(const FilaRenderer* renderer, FilaRendererClearOptions* outOptions);

// Frame pacing and timing controls.
void FilaRenderer_setVsyncTime(FilaRenderer* renderer, uint64_t steadyClockTimeNano);
void FilaRenderer_skipFrame(FilaRenderer* renderer, uint64_t vsyncSteadyClockTimeNano);
bool FilaRenderer_shouldRenderFrame(const FilaRenderer* renderer);
void FilaRenderer_setPresentationTime(FilaRenderer* renderer, int64_t monotonicClockNanos);
void FilaRenderer_skipNextFrames(FilaRenderer* renderer, size_t frameCount);
size_t FilaRenderer_getFrameToSkipCount(const FilaRenderer* renderer);

// Renderer user-time helpers.
double FilaRenderer_getUserTime(const FilaRenderer* renderer);
void FilaRenderer_resetUserTime(FilaRenderer* renderer);

// Frame-history timing introspection.
size_t FilaRenderer_getMaxFrameHistorySize(const FilaRenderer* renderer);
size_t FilaRenderer_getFrameInfoHistory(
        const FilaRenderer* renderer,
        size_t historySize,
        FilaRendererFrameInfo* outFrameInfos,
        size_t outFrameInfosCount);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_RENDERER_H


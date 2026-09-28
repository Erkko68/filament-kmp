#ifndef FILAMENT_C_RENDERER_H
#define FILAMENT_C_RENDERER_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaRendererDisplayInfo {
    float refreshRate;
} FilaRendererDisplayInfo;

typedef struct FilaRendererFrameRateOptions {
    float headRoomRatio;
    float scaleRate;
    uint8_t history;
    float interval;
} FilaRendererFrameRateOptions;

typedef struct FilaRendererClearOptions {
    double clearColor[4];
    bool clear;
    bool discard;
} FilaRendererClearOptions;

// Renderer
void FilaRenderer_skipFrame(FilaRenderer* renderer, uint64_t vsyncSteadyClockTimeNano);
bool FilaRenderer_shouldRenderFrame(const FilaRenderer* renderer);
bool FilaRenderer_beginFrame(FilaRenderer* renderer, FilaSwapChain* swapChain, uint64_t frameTimeNanos);
void FilaRenderer_endFrame(FilaRenderer* renderer);

void FilaRenderer_render(FilaRenderer* renderer, FilaView* view);
void FilaRenderer_renderStandaloneView(FilaRenderer* renderer, FilaView* view);

void FilaRenderer_copyFrame(FilaRenderer* renderer, FilaSwapChain* dstSwapChain,
        int dstLeft, int dstBottom, int dstWidth, int dstHeight,
        int srcLeft, int srcBottom, int srcWidth, int srcHeight,
        uint32_t flags);

void FilaRenderer_readPixels(FilaRenderer* renderer,
        uint32_t xoffset, uint32_t yoffset, uint32_t width, uint32_t height,
        void* buffer, uint32_t sizeInBytes,
        FilaPixelDataFormat format, FilaPixelDataType type,
        uint32_t alignment, uint32_t left, uint32_t top, uint32_t stride,
        FilaCallbackHandler* handler, FilaBufferCallback callback, void* userData);

void FilaRenderer_readPixelsRenderTarget(FilaRenderer* renderer, FilaRenderTarget* renderTarget,
        uint32_t xoffset, uint32_t yoffset, uint32_t width, uint32_t height,
        void* buffer, uint32_t sizeInBytes,
        FilaPixelDataFormat format, FilaPixelDataType type,
        uint32_t alignment, uint32_t left, uint32_t top, uint32_t stride,
        FilaCallbackHandler* handler, FilaBufferCallback callback, void* userData);

double FilaRenderer_getUserTime(const FilaRenderer* renderer);
void FilaRenderer_resetUserTime(FilaRenderer* renderer);

double FilaRenderer_getMaterialTime(const FilaRenderer* renderer);
void FilaRenderer_setMaterialTimeEpoch(FilaRenderer* renderer, int64_t timeEpochInNs);
void FilaRenderer_pauseRenderThread(FilaRenderer* renderer, uint64_t timeNs);

void FilaRenderer_setDisplayInfo(FilaRenderer* renderer, float refreshRate);
void FilaRenderer_setFrameRateOptions(FilaRenderer* renderer, float headRoomRatio, float scaleRate, uint32_t history, float interval);
void FilaRenderer_setClearOptions(FilaRenderer* renderer, double clearColor_0, double clearColor_1, double clearColor_2, double clearColor_3, bool clear, bool discard);
void FilaRenderer_getClearOptions(const FilaRenderer* renderer, int32_t* ints, double* doubles);

void FilaRenderer_setPresentationTime(FilaRenderer* renderer, uint64_t monotonicClockNanos);
void FilaRenderer_setDesiredPresentationTime(FilaRenderer* renderer, int64_t monotonicClockNanos);
void FilaRenderer_setRenderingDeadline(FilaRenderer* renderer, int64_t monotonicClockNanos);
void FilaRenderer_setVsyncTime(FilaRenderer* renderer, uint64_t steadyClockTimeNano);

void FilaRenderer_skipNextFrames(FilaRenderer* renderer, uint32_t frameCount);
uint32_t FilaRenderer_getFrameToSkipCount(const FilaRenderer* renderer);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_RENDERER_H

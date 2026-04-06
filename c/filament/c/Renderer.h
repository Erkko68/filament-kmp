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
    uint8_t interval;
} FilaRendererFrameRateOptions;

typedef struct FilaRendererClearOptions {
    float clearColor[4];
    bool clear;
    bool discard;
} FilaRendererClearOptions;

typedef enum FilaPixelDataFormat {
    FILA_PIXEL_DATA_FORMAT_R = 0,
    FILA_PIXEL_DATA_FORMAT_R_INTEGER = 1,
    FILA_PIXEL_DATA_FORMAT_RG = 2,
    FILA_PIXEL_DATA_FORMAT_RG_INTEGER = 3,
    FILA_PIXEL_DATA_FORMAT_RGB = 4,
    FILA_PIXEL_DATA_FORMAT_RGB_INTEGER = 5,
    FILA_PIXEL_DATA_FORMAT_RGBA = 6,
    FILA_PIXEL_DATA_FORMAT_RGBA_INTEGER = 7,
    FILA_PIXEL_DATA_FORMAT_UNUSED = 8,
    FILA_PIXEL_DATA_FORMAT_LUMINANCE = 9,
    FILA_PIXEL_DATA_FORMAT_ALPHA = 10,
    FILA_PIXEL_DATA_FORMAT_DEPTH_COMPONENT = 11,
    FILA_PIXEL_DATA_FORMAT_DEPTH_STENCIL = 12,
} FilaPixelDataFormat;

typedef enum FilaPixelDataType {
    FILA_PIXEL_DATA_TYPE_UBYTE = 0,
    FILA_PIXEL_DATA_TYPE_BYTE = 1,
    FILA_PIXEL_DATA_TYPE_USHORT = 2,
    FILA_PIXEL_DATA_TYPE_SHORT = 3,
    FILA_PIXEL_DATA_TYPE_UINT = 4,
    FILA_PIXEL_DATA_TYPE_INT = 5,
    FILA_PIXEL_DATA_TYPE_HALF = 6,
    FILA_PIXEL_DATA_TYPE_FLOAT = 7,
    FILA_PIXEL_DATA_TYPE_COMPRESSED = 8,
    FILA_PIXEL_DATA_TYPE_USHORT_565 = 9,
    FILA_PIXEL_DATA_TYPE_UINT_2_10_10_10_REV = 10,
    FILA_PIXEL_DATA_TYPE_UINT_10F_11F_11F_REV = 11,
} FilaPixelDataType;

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
        void* buffer, size_t sizeInBytes,
        FilaPixelDataFormat format, FilaPixelDataType type,
        uint8_t alignment, uint32_t left, uint32_t top, uint32_t stride,
        FilaCallbackHandler* handler, FilaBufferCallback callback, void* userData);

void FilaRenderer_readPixelsRenderTarget(FilaRenderer* renderer, FilaRenderTarget* renderTarget,
        uint32_t xoffset, uint32_t yoffset, uint32_t width, uint32_t height,
        void* buffer, size_t sizeInBytes,
        FilaPixelDataFormat format, FilaPixelDataType type,
        uint8_t alignment, uint32_t left, uint32_t top, uint32_t stride,
        FilaCallbackHandler* handler, FilaBufferCallback callback, void* userData);

double FilaRenderer_getUserTime(const FilaRenderer* renderer);
void FilaRenderer_resetUserTime(FilaRenderer* renderer);

void FilaRenderer_setDisplayInfo(FilaRenderer* renderer, const FilaRendererDisplayInfo* info);
void FilaRenderer_setFrameRateOptions(FilaRenderer* renderer, const FilaRendererFrameRateOptions* options);
void FilaRenderer_setClearOptions(FilaRenderer* renderer, const FilaRendererClearOptions* options);

void FilaRenderer_setPresentationTime(FilaRenderer* renderer, uint64_t monotonicClockNanos);
void FilaRenderer_setVsyncTime(FilaRenderer* renderer, uint64_t steadyClockTimeNano);

void FilaRenderer_skipNextFrames(FilaRenderer* renderer, uint32_t frameCount);
uint32_t FilaRenderer_getFrameToSkipCount(const FilaRenderer* renderer);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_RENDERER_H

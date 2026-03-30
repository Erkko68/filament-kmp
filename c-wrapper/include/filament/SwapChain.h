#ifndef FILAMENT_C_SWAP_CHAIN_H
#define FILAMENT_C_SWAP_CHAIN_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Configuration flags for FilaEngine_createSwapChain*.
static const uint64_t FILA_SWAP_CHAIN_CONFIG_TRANSPARENT = 0x1;
static const uint64_t FILA_SWAP_CHAIN_CONFIG_READABLE = 0x2;
static const uint64_t FILA_SWAP_CHAIN_CONFIG_ENABLE_XCB = 0x4;
static const uint64_t FILA_SWAP_CHAIN_CONFIG_APPLE_CVPIXELBUFFER = 0x8;
static const uint64_t FILA_SWAP_CHAIN_CONFIG_SRGB_COLORSPACE = 0x10;
static const uint64_t FILA_SWAP_CHAIN_CONFIG_HAS_STENCIL_BUFFER = 0x20;
static const uint64_t FILA_SWAP_CHAIN_CONFIG_PROTECTED_CONTENT = 0x40;
static const uint64_t FILA_SWAP_CHAIN_CONFIG_MSAA_4_SAMPLES = 0x80;

// Returns whether protected-content swap chains are supported.
bool FilaSwapChain_isProtectedContentSupported(FilaEngine* engine);

// Returns whether sRGB swap chains are supported.
bool FilaSwapChain_isSRGBSwapChainSupported(FilaEngine* engine);

// Returns whether an MSAA sample count is supported for swap chains.
bool FilaSwapChain_isMSAASwapChainSupported(FilaEngine* engine, uint32_t samples);

// Returns the native window handle used by this swap chain.
void* FilaSwapChain_getNativeWindow(const FilaSwapChain* swapChain);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SWAP_CHAIN_H


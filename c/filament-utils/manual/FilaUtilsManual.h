// Hand-written: decoders over stb that Filament's headers don't declare (Android's filament-utils has them).
#ifndef FILA_MANUAL_FILAUTILSMANUAL_H
#define FILA_MANUAL_FILAUTILSMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Decodes Radiance HDR bytes into a mipmapped 2D texture of internalFormat; NULL if they don't decode.
FilaTexture* FilaHDRLoader_createTexture(FilaEngine* engine, const void* buffer, uint32_t size, FilaTextureFormat internalFormat);

// Decodes PNG/JPEG/... bytes into a mipmapped RGBA8 (or SRGB8_A8) 2D texture; NULL if they don't decode.
FilaTexture* FilaTextureLoader_loadTexture(FilaEngine* engine, const void* buffer, uint32_t size, bool srgb);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAUTILSMANUAL_H

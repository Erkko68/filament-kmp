// Hand-written: decoders over stb that Filament's headers don't declare (Android's filament-utils has them).
#ifndef FILA_MANUAL_FILAUTILSMANUAL_H
#define FILA_MANUAL_FILAUTILSMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Decodes into a FilaDecodedImage, which needs no Engine: these can run on any thread. NULL if the bytes don't decode.
void* FilaTextureLoader_decode(const void* buffer, uint32_t size);
void* FilaHDRLoader_decode(const void* buffer, uint32_t size);

// Uploads a decoded image into a mipmapped 2D texture of internalFormat, and frees the image; NULL if that fails.
FilaTexture* FilaDecodedImage_createTexture(void* image, FilaEngine* engine, FilaTextureFormat internalFormat);

void FilaDecodedImage_destroy(void* image);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAUTILSMANUAL_H

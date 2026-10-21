// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILACOLORGRADINGMANUAL_H
#define FILA_MANUAL_FILACOLORGRADINGMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// The LUT the export callback gets, for the duration of the call.
typedef struct FilaColorGradingLut FilaColorGradingLut;

// Builder::exportLut through C's callback: build() calls it once with a FilaColorGradingLut, then frees the binding.
// Set it right before build(): a second call or a builder never built leaks it.
void FilaColorGradingBuilder_exportLut(FilaColorGradingBuilder* self, FilaArgCallback callback, void* callbackUser);
// The LUT's bytes; outInfo gets its size in bytes, PixelDataFormat, PixelDataType, width, height and depth.
const void* FilaColorGradingLut_getData(const FilaColorGradingLut* lut, uint32_t* outInfo);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILACOLORGRADINGMANUAL_H

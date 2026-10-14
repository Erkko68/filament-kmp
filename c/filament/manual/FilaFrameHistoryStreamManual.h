// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAFRAMEHISTORYSTREAMMANUAL_H
#define FILA_MANUAL_FILAFRAMEHISTORYSTREAMMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// FrameHistoryStream::getNewFrames' frames, oldest first, into up to outCapacity (the renderer's max frame history size)
// FrameInfo handles; missingBefore[i] counts the missing frame IDs just before out[i].
uint32_t FilaFrameHistoryStream_getNewFrames(FilaFrameHistoryStream* self, FilaRendererFrameInfo* const* out, uint32_t* missingBefore, uint32_t outCapacity);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAFRAMEHISTORYSTREAMMANUAL_H

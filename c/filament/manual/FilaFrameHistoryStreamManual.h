// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAFRAMEHISTORYSTREAMMANUAL_H
#define FILA_MANUAL_FILAFRAMEHISTORYSTREAMMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// FrameHistoryStream::getNewFrames, oldest first, into up to outCapacity (> 0) FrameInfo handles; a missing
// frame sets missing[i] and only out[i]'s frameId. Frames past outCapacity stay new for the next call.
uint32_t FilaFrameHistoryStream_getNewFrames(FilaFrameHistoryStream* self, FilaRendererFrameInfo* const* out, bool* missing, uint32_t outCapacity);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAFRAMEHISTORYSTREAMMANUAL_H

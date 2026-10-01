// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILAFRAMEPACERMANUAL_H
#define FILA_MANUAL_FILAFRAMEPACERMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// VsyncTick::timelines from `count` (expectedPresentationTime, deadline) pairs in steady-clock ns.
// The tick borrows `times`: keep it alive until FramePacer::setupFrame returns.
void FilaFramePacerVsyncTick_setTimelines(FilaFramePacerVsyncTick* self, const int64_t* times, uint32_t count);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILAFRAMEPACERMANUAL_H

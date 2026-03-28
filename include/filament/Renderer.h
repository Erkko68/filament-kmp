#ifndef FILAMENT_C_RENDERER_H
#define FILAMENT_C_RENDERER_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Begins a frame on the given swap chain.
// Returns true when rendering should proceed, false when the frame should be skipped.
bool FilaRenderer_beginFrame(FilaRenderer* renderer, FilaSwapChain* swapChain, uint64_t vsyncSteadyClockTimeNano);

// Renders a view between beginFrame and endFrame.
void FilaRenderer_render(FilaRenderer* renderer, const FilaView* view);

// Ends the current frame.
void FilaRenderer_endFrame(FilaRenderer* renderer);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_RENDERER_H


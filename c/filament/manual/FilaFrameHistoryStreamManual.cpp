#include "../generated/Includes.hpp"
#include "FilaFrameHistoryStreamManual.h"

extern "C" {

uint32_t FilaFrameHistoryStream_getNewFrames(FilaFrameHistoryStream* self, FilaRendererFrameInfo* const* out,
        bool* missing, uint32_t outCapacity) {
    auto const range = fila::cpp(self)->getNewFrames();
    uint32_t count = 0;
    // Advancing marks the next frame processed, so stop on the last one that fits rather than step past it.
    for (auto it = range.begin(); it != range.end(); ++it) {
        missing[count] = !*it;
        *fila::cpp(out[count]) = **it;
        if (++count == outCapacity) break;
    }
    return count;
}

} // extern "C"

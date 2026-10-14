#include "../generated/Includes.hpp"
#include "FilaFrameHistoryStreamManual.h"

extern "C" {

uint32_t FilaFrameHistoryStream_getNewFrames(FilaFrameHistoryStream* self, FilaRendererFrameInfo* const* out,
        uint32_t* missingBefore, uint32_t outCapacity) {
    // The range is consumed when getNewFrames returns, so drain it all; it holds at most outCapacity frames.
    uint32_t count = 0, missing = 0;
    for (auto const& result : fila::cpp(self)->getNewFrames()) {
        if (!result) { ++missing; continue; }
        if (count == outCapacity) break;
        missingBefore[count] = missing;
        *fila::cpp(out[count++]) = *result;
        missing = 0;
    }
    return count;
}

} // extern "C"

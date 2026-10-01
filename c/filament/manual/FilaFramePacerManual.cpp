#include "../generated/Includes.hpp"
#include "FilaFramePacerManual.h"

using HardwareTimeline = filament::FramePacer::HardwareTimeline;
static_assert(std::is_same_v<std::chrono::steady_clock::duration, std::chrono::nanoseconds>);
static_assert(sizeof(HardwareTimeline) == 2 * sizeof(int64_t));

extern "C" {

void FilaFramePacerVsyncTick_setTimelines(FilaFramePacerVsyncTick* self, const int64_t* times, uint32_t count) {
    fila::cpp(self)->timelines = { reinterpret_cast<const HardwareTimeline*>(times), count };
}

} // extern "C"

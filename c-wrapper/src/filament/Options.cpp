#include <filament/Options.h>

#include "../../include/filament/Options.h"

extern "C" {

void FilaRenderQuality_setDefaults(FilaRenderQuality* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::RenderQuality defaults;
    outOptions->hdrColorBuffer = static_cast<FilaQualityLevel>(defaults.hdrColorBuffer);
}

void FilaGuardBandOptions_setDefaults(FilaGuardBandOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::GuardBandOptions defaults;
    outOptions->enabled = defaults.enabled;
}

void FilaStereoscopicOptions_setDefaults(FilaStereoscopicOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    filament::StereoscopicOptions defaults;
    outOptions->enabled = defaults.enabled;
}

} // extern "C"


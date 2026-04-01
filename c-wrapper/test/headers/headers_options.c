#include "filament/Options.h"

void test_headers_options(void) {
    FilaRenderQuality renderQuality;
    FilaGuardBandOptions guardBand;
    FilaStereoscopicOptions stereo;
    FilaDynamicResolutionOptions dynamicResolution;
    FilaMultiSampleAntiAliasingOptions msaa;
    FilaTemporalAntiAliasingOptions taa;
    FilaScreenSpaceReflectionsOptions ssr;
    FilaRendererDisplayInfo displayInfo;
    FilaRendererFrameRateOptions frameRate;
    FilaRendererClearOptions clearOptions;
    FilaAmbientOcclusionOptions ao;
    FilaBloomOptions bloom;
    FilaFogOptions fog;
    FilaDepthOfFieldOptions dof;
    FilaVignetteOptions vignette;

    FilaRenderQuality_setDefaults(&renderQuality);
    FilaGuardBandOptions_setDefaults(&guardBand);
    FilaStereoscopicOptions_setDefaults(&stereo);
    FilaDynamicResolutionOptions_setDefaults(&dynamicResolution);
    FilaMultiSampleAntiAliasingOptions_setDefaults(&msaa);
    FilaTemporalAntiAliasingOptions_setDefaults(&taa);
    FilaScreenSpaceReflectionsOptions_setDefaults(&ssr);
    FilaRendererDisplayInfo_setDefaults(&displayInfo);
    FilaRendererFrameRateOptions_setDefaults(&frameRate);
    FilaRendererClearOptions_setDefaults(&clearOptions);
    FilaAmbientOcclusionOptions_setDefaults(&ao);
    FilaBloomOptions_setDefaults(&bloom);
    FilaFogOptions_setDefaults(&fog);
    FilaDepthOfFieldOptions_setDefaults(&dof);
    FilaVignetteOptions_setDefaults(&vignette);
}


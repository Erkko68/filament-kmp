#include "filament/Engine.h"
#include "filament/View.h"
#include "filament/ColorGrading.h"
#include "filament/RenderTarget.h"
#include "filament/Texture.h"

// Verifies View API is consumable from C and composes with Engine + Scene handles.
void test_headers_view(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaView* view = FilaEngine_createView(engine);
    FilaScene* scene = (FilaScene*)0;
    FilaEntity entity = 2;
    FilaCamera* camera = FilaEngine_createCamera(engine, entity);
    FilaViewport viewport = {0, 0, 640u, 480u};

    FilaView_setScene(view, scene);
    (void)FilaView_getScene(view);
    FilaView_setViewport(view, viewport);
    (void)FilaView_getViewport(view);
    FilaView_setCamera(view, camera);
    (void)FilaView_hasCamera(view);
    (void)FilaView_getCamera(view);

    // Test ColorGrading binding
    FilaColorGrading* colorGrading = (FilaColorGrading*)0;
    FilaView_setColorGrading(view, colorGrading);
    (void)FilaView_getColorGrading(view);

    // Test RenderTarget binding
    FilaRenderTarget* renderTarget = (FilaRenderTarget*)0;
    FilaView_setRenderTarget(view, renderTarget);
    (void)FilaView_getRenderTarget(view);

    FilaView_setName(view, "main-view");
    (void)FilaView_getName(view);
    FilaView_setVisibleLayers(view, 0xFFu, 0x03u);
    (void)FilaView_getVisibleLayers(view);
    FilaView_setBlendMode(view, FILA_VIEW_BLEND_MODE_TRANSLUCENT);
    (void)FilaView_getBlendMode(view);
    FilaView_setAntiAliasing(view, FILA_VIEW_ANTI_ALIASING_FXAA);
    (void)FilaView_getAntiAliasing(view);
    FilaView_setDithering(view, FILA_VIEW_DITHERING_TEMPORAL);
    (void)FilaView_getDithering(view);
    FilaView_setShadowType(view, FILA_VIEW_SHADOW_TYPE_VSM);
    (void)FilaView_getShadowType(view);
    FilaView_setShadowingEnabled(view, true);
    (void)FilaView_isShadowingEnabled(view);
    FilaView_setScreenSpaceRefractionEnabled(view, false);
    (void)FilaView_isScreenSpaceRefractionEnabled(view);
    FilaView_setPostProcessingEnabled(view, true);
    (void)FilaView_isPostProcessingEnabled(view);
    FilaView_setFrontFaceWindingInverted(view, false);
    (void)FilaView_isFrontFaceWindingInverted(view);
    FilaView_setTransparentPickingEnabled(view, false);
    (void)FilaView_isTransparentPickingEnabled(view);
    FilaView_setStencilBufferEnabled(view, false);
    (void)FilaView_isStencilBufferEnabled(view);

    FilaDynamicResolutionOptions dynamicResolution;
    FilaRenderQuality renderQuality;
    FilaMultiSampleAntiAliasingOptions msaa;
    FilaTemporalAntiAliasingOptions taa;
    FilaScreenSpaceReflectionsOptions ssr;
    FilaVsmShadowOptions vsmShadow;
    FilaSoftShadowOptions softShadow;
    FilaAmbientOcclusionOptions ao;
    FilaBloomOptions bloom;
    FilaFogOptions fog;
    FilaDepthOfFieldOptions dof;
    FilaVignetteOptions vignette;
    FilaGuardBandOptions guardBand;
    FilaStereoscopicOptions stereo;
    float lastScale[2] = {0.0f, 0.0f};

    FilaDynamicResolutionOptions_setDefaults(&dynamicResolution);
    FilaRenderQuality_setDefaults(&renderQuality);
    FilaMultiSampleAntiAliasingOptions_setDefaults(&msaa);
    FilaTemporalAntiAliasingOptions_setDefaults(&taa);
    FilaScreenSpaceReflectionsOptions_setDefaults(&ssr);
    FilaVsmShadowOptions_setDefaults(&vsmShadow);
    FilaSoftShadowOptions_setDefaults(&softShadow);
    FilaAmbientOcclusionOptions_setDefaults(&ao);
    FilaBloomOptions_setDefaults(&bloom);
    FilaFogOptions_setDefaults(&fog);
    FilaDepthOfFieldOptions_setDefaults(&dof);
    FilaVignetteOptions_setDefaults(&vignette);
    FilaGuardBandOptions_setDefaults(&guardBand);
    FilaStereoscopicOptions_setDefaults(&stereo);

    FilaView_setDynamicResolutionOptions(view, &dynamicResolution);
    (void)FilaView_getDynamicResolutionOptions(view, &dynamicResolution);
    (void)FilaView_getLastDynamicResolutionScale(view, lastScale);
    FilaView_setRenderQuality(view, &renderQuality);
    (void)FilaView_getRenderQuality(view, &renderQuality);
    FilaView_setMultiSampleAntiAliasingOptions(view, &msaa);
    (void)FilaView_getMultiSampleAntiAliasingOptions(view, &msaa);
    FilaView_setTemporalAntiAliasingOptions(view, &taa);
    (void)FilaView_getTemporalAntiAliasingOptions(view, &taa);
    FilaView_setScreenSpaceReflectionsOptions(view, &ssr);
    (void)FilaView_getScreenSpaceReflectionsOptions(view, &ssr);
    FilaView_setVsmShadowOptions(view, &vsmShadow);
    (void)FilaView_getVsmShadowOptions(view, &vsmShadow);
    FilaView_setSoftShadowOptions(view, &softShadow);
    (void)FilaView_getSoftShadowOptions(view, &softShadow);
    FilaView_setAmbientOcclusionOptions(view, &ao);
    (void)FilaView_getAmbientOcclusionOptions(view, &ao);
    FilaView_setBloomOptions(view, &bloom);
    (void)FilaView_getBloomOptions(view, &bloom);
    FilaView_setFogOptions(view, &fog);
    (void)FilaView_getFogOptions(view, &fog);
    FilaView_setDepthOfFieldOptions(view, &dof);
    (void)FilaView_getDepthOfFieldOptions(view, &dof);
    FilaView_setVignetteOptions(view, &vignette);
    (void)FilaView_getVignetteOptions(view, &vignette);
    FilaView_setGuardBandOptions(view, &guardBand);
    (void)FilaView_getGuardBandOptions(view, &guardBand);
    FilaView_setStereoscopicOptions(view, &stereo);
    (void)FilaView_getStereoscopicOptions(view, &stereo);
    FilaView_clearFrameHistory(view, engine);

    FilaView_setDebugCamera(view, camera);
    (void)FilaView_getDirectionalShadowCameraCount(view);
    {
        const FilaCamera* shadowCameras[2] = {(const FilaCamera*)0, (const FilaCamera*)0};
        (void)FilaView_getDirectionalShadowCameras(view, shadowCameras, 2u);
    }
    FilaView_setFroxelVizEnabled(view, false);
    {
        FilaViewFroxelConfigurationInfoWithAge froxelInfo;
        (void)FilaView_getFroxelConfigurationInfo(view, &froxelInfo);
    }
    {
        float materialGlobal[4] = {0.0f, 0.0f, 0.0f, 1.0f};
        FilaView_setMaterialGlobal(view, 0u, materialGlobal);
        (void)FilaView_getMaterialGlobal(view, 0u, materialGlobal);
    }

    FilaEngine_destroyCameraComponent(engine, entity);
    FilaEngine_destroyView(engine, view);
}

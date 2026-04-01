#include <stdio.h>

#include "filament/Camera.h"
#include "filament/Engine.h"
#include "filament/Frustum.h"
#include "filament/Scene.h"
#include "filament/View.h"

int main(void) {
    printf("Running engine+scene+view functionality program...\n");

    FilaEngineConfig config;
    FilaEngineConfig_setDefaults(&config);
    config.stereoscopicType = FILA_ENGINE_STEREOSCOPIC_TYPE_NONE;
    config.stereoscopicEyeCount = 2u;

    FilaEngine* engine = FilaEngine_createWithConfig(&config);
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    if (FilaEngine_getStereoscopicEyeCount(engine) < 2u) {
        printf("Engine eye count mismatch\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    {
        FilaEngineConfig readback;
        if (!FilaEngine_getConfig(engine, &readback) || readback.stereoscopicEyeCount < 2u) {
            printf("Engine config readback mismatch\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
        (void)FilaEngine_isStereoSupported(engine, FILA_ENGINE_STEREOSCOPIC_TYPE_NONE);
    }

    FilaScene* scene = FilaEngine_createScene(engine);
    if (!scene) {
        printf("Scene creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView* view = FilaEngine_createView(engine);
    if (!view) {
        printf("View creation failed\n");
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setScene(view, scene);
    if (FilaView_getScene(view) != scene) {
        printf("View scene mismatch\n");
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity cameraEntity = 100;
    FilaCamera* camera = FilaEngine_createCamera(engine, cameraEntity);
    if (!camera) {
        printf("Camera creation failed\n");
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setCamera(view, camera);
    if (!FilaView_hasCamera(view)) {
        printf("View camera expected but missing\n");
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaView_getCamera(view) != camera) {
        printf("View camera mismatch\n");
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaCamera_getEntity(camera) != cameraEntity) {
        printf("Camera entity mismatch\n");
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setName(view, "scene-view");
    if (FilaView_getName(view) == (const char*)0) {
        printf("View name query failed\n");
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setVisibleLayers(view, 0xFFu, 0x03u);
    if (FilaView_getVisibleLayers(view) != 0x03u) {
        printf("View visible layers mismatch\n");
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setBlendMode(view, FILA_VIEW_BLEND_MODE_TRANSLUCENT);
    FilaView_setAntiAliasing(view, FILA_VIEW_ANTI_ALIASING_FXAA);
    FilaView_setDithering(view, FILA_VIEW_DITHERING_TEMPORAL);
    FilaView_setShadowType(view, FILA_VIEW_SHADOW_TYPE_VSM);
    FilaView_setShadowingEnabled(view, false);
    FilaView_setScreenSpaceRefractionEnabled(view, false);
    FilaView_setPostProcessingEnabled(view, true);
    FilaView_setFrontFaceWindingInverted(view, false);
    FilaView_setTransparentPickingEnabled(view, false);
    FilaView_setStencilBufferEnabled(view, false);

    if (FilaView_getBlendMode(view) != FILA_VIEW_BLEND_MODE_TRANSLUCENT ||
            FilaView_getAntiAliasing(view) != FILA_VIEW_ANTI_ALIASING_FXAA ||
            FilaView_getDithering(view) != FILA_VIEW_DITHERING_TEMPORAL ||
            FilaView_getShadowType(view) != FILA_VIEW_SHADOW_TYPE_VSM) {
        printf("View enum state mismatch\n");
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaView_isShadowingEnabled(view) ||
            FilaView_isScreenSpaceRefractionEnabled(view) ||
            !FilaView_isPostProcessingEnabled(view) ||
            FilaView_isFrontFaceWindingInverted(view) ||
            FilaView_isTransparentPickingEnabled(view) ||
            FilaView_isStencilBufferEnabled(view)) {
        printf("View boolean state mismatch\n");
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    {
        FilaDynamicResolutionOptions dynamicResolution;
        FilaRenderQuality renderQuality;
        FilaMultiSampleAntiAliasingOptions msaa;
        FilaTemporalAntiAliasingOptions taa;
        FilaScreenSpaceReflectionsOptions ssr;
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
        FilaAmbientOcclusionOptions_setDefaults(&ao);
        FilaBloomOptions_setDefaults(&bloom);
        FilaFogOptions_setDefaults(&fog);
        FilaDepthOfFieldOptions_setDefaults(&dof);
        FilaVignetteOptions_setDefaults(&vignette);
        FilaGuardBandOptions_setDefaults(&guardBand);
        FilaStereoscopicOptions_setDefaults(&stereo);

        dynamicResolution.enabled = true;
        dynamicResolution.quality = FILA_QUALITY_LEVEL_MEDIUM;
        renderQuality.hdrColorBuffer = FILA_QUALITY_LEVEL_HIGH;
        msaa.enabled = true;
        msaa.sampleCount = 4u;
        taa.enabled = true;
        taa.feedback = 0.15f;
        ssr.enabled = true;
        ssr.maxDistance = 2.0f;
        ao.enabled = true;
        ao.aoType = FILA_AMBIENT_OCCLUSION_TYPE_GTAO;
        bloom.enabled = true;
        bloom.strength = 0.2f;
        fog.enabled = true;
        fog.density = 0.15f;
        dof.enabled = true;
        dof.filter = FILA_DEPTH_OF_FIELD_FILTER_MEDIAN;
        vignette.enabled = true;
        vignette.roundness = 0.6f;
        guardBand.enabled = true;
        stereo.enabled = true;

        FilaView_setDynamicResolutionOptions(view, &dynamicResolution);
        FilaView_setRenderQuality(view, &renderQuality);
        FilaView_setMultiSampleAntiAliasingOptions(view, &msaa);
        FilaView_setTemporalAntiAliasingOptions(view, &taa);
        FilaView_setScreenSpaceReflectionsOptions(view, &ssr);
        FilaView_setAmbientOcclusionOptions(view, &ao);
        FilaView_setBloomOptions(view, &bloom);
        FilaView_setFogOptions(view, &fog);
        FilaView_setDepthOfFieldOptions(view, &dof);
        FilaView_setVignetteOptions(view, &vignette);
        FilaView_setGuardBandOptions(view, &guardBand);
        FilaView_setStereoscopicOptions(view, &stereo);

        if (!FilaView_getDynamicResolutionOptions(view, &dynamicResolution) ||
                !FilaView_getRenderQuality(view, &renderQuality) ||
                !FilaView_getMultiSampleAntiAliasingOptions(view, &msaa) ||
                !FilaView_getTemporalAntiAliasingOptions(view, &taa) ||
                !FilaView_getScreenSpaceReflectionsOptions(view, &ssr) ||
                !FilaView_getAmbientOcclusionOptions(view, &ao) ||
                !FilaView_getBloomOptions(view, &bloom) ||
                !FilaView_getFogOptions(view, &fog) ||
                !FilaView_getDepthOfFieldOptions(view, &dof) ||
                !FilaView_getVignetteOptions(view, &vignette) ||
                !FilaView_getGuardBandOptions(view, &guardBand) ||
                !FilaView_getStereoscopicOptions(view, &stereo)) {
            printf("View options readback failed\n");
            FilaEngine_destroyCameraComponent(engine, cameraEntity);
            FilaEngine_destroyView(engine, view);
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroy(&engine);
            return 1;
        }

        if (!FilaView_getLastDynamicResolutionScale(view, lastScale)) {
            printf("Dynamic resolution scale readback failed\n");
            FilaEngine_destroyCameraComponent(engine, cameraEntity);
            FilaEngine_destroyView(engine, view);
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaView_clearFrameHistory(view, engine);
    }

    {
        const size_t shadowCameraCount = FilaView_getDirectionalShadowCameraCount(view);
        const FilaCamera* shadowCameras[2] = {(const FilaCamera*)0, (const FilaCamera*)0};
        const size_t copiedShadowCameras = FilaView_getDirectionalShadowCameras(view, shadowCameras, 2u);
        if (copiedShadowCameras > 2u || copiedShadowCameras > shadowCameraCount) {
            printf("Directional shadow camera contract mismatch\n");
            FilaEngine_destroyCameraComponent(engine, cameraEntity);
            FilaEngine_destroyView(engine, view);
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaView_setDebugCamera(view, camera);
        FilaView_setFroxelVizEnabled(view, false);

        if (FilaView_getDirectionalShadowCameraCount((const FilaView*)0) != 0u ||
                FilaView_getDirectionalShadowCameras((const FilaView*)0, shadowCameras, 2u) != 0u ||
                FilaView_getDirectionalShadowCameras(view, (const FilaCamera**)0, 2u) != 0u ||
                FilaView_getDirectionalShadowCameras(view, shadowCameras, 0u) != 0u) {
            printf("Directional shadow camera null-safety mismatch\n");
            FilaEngine_destroyCameraComponent(engine, cameraEntity);
            FilaEngine_destroyView(engine, view);
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroy(&engine);
            return 1;
        }

        {
            FilaViewFroxelConfigurationInfoWithAge froxelInfo;
            if (!FilaView_getFroxelConfigurationInfo(view, &froxelInfo)) {
                printf("Froxel configuration readback failed\n");
                FilaEngine_destroyCameraComponent(engine, cameraEntity);
                FilaEngine_destroyView(engine, view);
                FilaEngine_destroyScene(engine, scene);
                FilaEngine_destroy(&engine);
                return 1;
            }
        }

        if (FilaView_getFroxelConfigurationInfo((const FilaView*)0, (FilaViewFroxelConfigurationInfoWithAge*)0) ||
                FilaView_getFroxelConfigurationInfo(view, (FilaViewFroxelConfigurationInfoWithAge*)0)) {
            printf("Froxel configuration null-safety mismatch\n");
            FilaEngine_destroyCameraComponent(engine, cameraEntity);
            FilaEngine_destroyView(engine, view);
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroy(&engine);
            return 1;
        }
    }

    {
        double identity[16] = {
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0,
        };
        double outM[16];
        double eyeMatrices[32] = {
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0,

            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0,
        };
        double pos[3];
        double shift[2];
        double scaling[4];
        float axis[3];
        FilaFrustum frustum;

        FilaCamera_setProjection(
            camera,
            FILA_CAMERA_PROJECTION_PERSPECTIVE,
            -1.0,
            1.0,
            -1.0,
            1.0,
            0.1,
            1000.0);
        FilaCamera_setLensProjection(camera, 35.0, 16.0 / 9.0, 0.1, 1000.0);
        FilaCamera_setCustomProjection(camera, identity, 0.1, 1000.0);
        FilaCamera_setCustomProjectionWithCulling(camera, identity, identity, 0.1, 1000.0);
        FilaCamera_setCustomEyeProjection(camera, eyeMatrices, 2u, identity, 0.1, 1000.0);
        FilaCamera_setModelMatrix(camera, identity);
        FilaCamera_setEyeModelMatrix(camera, 0u, identity);
        FilaCamera_setScaling(camera, 1.0, 1.0);
        FilaCamera_setShift(camera, 0.0, 0.0);
        FilaCamera_setFocusDistance(camera, 4.0f);
        FilaCamera_setExposureValue(camera, 1.0f);

        if (!FilaCamera_getProjectionMatrix(camera, 0u, outM) ||
                !FilaCamera_getProjectionMatrix(camera, 1u, outM) ||
                !FilaCamera_getCullingProjectionMatrix(camera, outM) ||
                !FilaCamera_getModelMatrix(camera, outM) ||
                !FilaCamera_getViewMatrix(camera, outM) ||
                !FilaCamera_getScaling(camera, scaling) ||
                !FilaCamera_getShift(camera, shift) ||
                !FilaCamera_getPosition(camera, pos) ||
                !FilaCamera_getLeftVector(camera, axis) ||
                !FilaCamera_getUpVector(camera, axis) ||
                !FilaCamera_getForwardVector(camera, axis) ||
                !FilaCamera_getFrustum(camera, &frustum)) {
            printf("Camera matrix/vector query failed\n");
            FilaEngine_destroyCameraComponent(engine, cameraEntity);
            FilaEngine_destroyView(engine, view);
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroy(&engine);
            return 1;
        }

        (void)FilaCamera_getNear(camera);
        (void)FilaCamera_getCullingFar(camera);
        (void)FilaCamera_getFieldOfViewInDegrees(camera, FILA_CAMERA_FOV_VERTICAL);
        (void)FilaCamera_getAperture(camera);
        (void)FilaCamera_getShutterSpeed(camera);
        (void)FilaCamera_getSensitivity(camera);
        (void)FilaCamera_getFocalLength(camera);
        (void)FilaCamera_getFocusDistance(camera);
        (void)FilaCamera_projectionFov(45.0, 16.0 / 9.0, 0.1, 1000.0, FILA_CAMERA_FOV_VERTICAL, outM);
        (void)FilaCamera_projectionLens(35.0, 16.0 / 9.0, 0.1, 1000.0, outM);
        (void)FilaCamera_inverseProjection(outM, outM);
        (void)FilaCamera_computeEffectiveFocalLength(35.0, 4.0);
        (void)FilaCamera_computeEffectiveFov(45.0, 4.0);
    }

    FilaEngine_destroyCameraComponent(engine, cameraEntity);

    FilaEngine_destroyView(engine, view);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroy(&engine);

    printf("Engine+scene+view functionality program completed\n");
    return 0;
}

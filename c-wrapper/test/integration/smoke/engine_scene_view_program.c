#include <stdio.h>

#include "filament/Camera.h"
#include "filament/Engine.h"
#include "filament/Scene.h"
#include "filament/View.h"

int main(void) {
    printf("Running engine+scene+view smoke program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
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
        double identity[16] = {
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0,
        };
        double outM[16];
        double pos[3];
        float axis[3];

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
        FilaCamera_setModelMatrix(camera, identity);
        FilaCamera_setFocusDistance(camera, 4.0f);

        if (!FilaCamera_getProjectionMatrix(camera, 0u, outM) ||
                !FilaCamera_getCullingProjectionMatrix(camera, outM) ||
                !FilaCamera_getModelMatrix(camera, outM) ||
                !FilaCamera_getViewMatrix(camera, outM) ||
                !FilaCamera_getPosition(camera, pos) ||
                !FilaCamera_getLeftVector(camera, axis) ||
                !FilaCamera_getUpVector(camera, axis) ||
                !FilaCamera_getForwardVector(camera, axis)) {
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
    }

    FilaEngine_destroyCameraComponent(engine, cameraEntity);

    FilaEngine_destroyView(engine, view);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroy(&engine);

    printf("Engine+scene+view smoke program completed\n");
    return 0;
}


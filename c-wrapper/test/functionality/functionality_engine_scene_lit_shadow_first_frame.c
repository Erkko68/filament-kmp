#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>

#include "backend/BufferDescriptor.h"
#include "filament/Camera.h"
#include "filament/Engine.h"
#include "filament/IndexBuffer.h"
#include "filament/LightManager.h"
#include "filament/Material.h"
#include "filament/MaterialInstance.h"
#include "filament/RenderableManager.h"
#include "filament/Renderer.h"
#include "filament/Scene.h"
#include "filament/View.h"
#include "filament/VertexBuffer.h"
#include "utils/EntityManager.h"

static void release_heap_buffer(void* buffer, size_t size, void* user) {
    (void)size;
    (void)user;
    free(buffer);
}

static int read_binary_file(const char* path, unsigned char** outData, size_t* outSize) {
    FILE* fp = fopen(path, "rb");
    if (!fp) {
        return 0;
    }
    if (fseek(fp, 0, SEEK_END) != 0) {
        fclose(fp);
        return 0;
    }
    long end = ftell(fp);
    if (end <= 0) {
        fclose(fp);
        return 0;
    }
    if (fseek(fp, 0, SEEK_SET) != 0) {
        fclose(fp);
        return 0;
    }

    unsigned char* data = (unsigned char*)malloc((size_t)end);
    if (!data) {
        fclose(fp);
        return 0;
    }
    if (fread(data, 1u, (size_t)end, fp) != (size_t)end) {
        free(data);
        fclose(fp);
        return 0;
    }
    fclose(fp);
    *outData = data;
    *outSize = (size_t)end;
    return 1;
}

int main(void) {
    printf("Running engine+scene+lit_shadow first-frame functionality program...\n");

    int result = 1;
    FilaEngine* engine = FilaEngine_create();
    FilaRenderer* renderer = (FilaRenderer*)0;
    FilaSwapChain* swapChain = (FilaSwapChain*)0;
    FilaScene* scene = (FilaScene*)0;
    FilaView* view = (FilaView*)0;
    FilaCamera* camera = (FilaCamera*)0;
    FilaVertexBuffer* vb = (FilaVertexBuffer*)0;
    FilaIndexBuffer* ib = (FilaIndexBuffer*)0;
    FilaMaterial* sceneMaterial = (FilaMaterial*)0;
    FilaMaterialInstance* sceneMi = (FilaMaterialInstance*)0;
    FilaRenderableManager* rm = (FilaRenderableManager*)0;
    FilaLightManager* lm = (FilaLightManager*)0;

    FilaEntity cameraEntity = 0;
    FilaEntity renderableEntity = 0;
    FilaEntity lightEntity = 0;

    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    renderer = FilaEngine_createRenderer(engine);
    swapChain = FilaEngine_createSwapChainHeadless(engine, 640u, 480u, 0u);
    scene = FilaEngine_createScene(engine);
    view = FilaEngine_createView(engine);
    if (!renderer || !swapChain || !scene || !view) {
        printf("Core render objects creation failed\n");
        goto cleanup;
    }

    cameraEntity = FilaEntityManager_create();
    renderableEntity = FilaEntityManager_create();
    lightEntity = FilaEntityManager_create();
    if (!cameraEntity || !renderableEntity || !lightEntity) {
        printf("Entity creation failed\n");
        goto cleanup;
    }

    camera = FilaEngine_createCamera(engine, cameraEntity);
    if (!camera) {
        printf("Camera creation failed\n");
        goto cleanup;
    }

    FilaView_setScene(view, scene);
    FilaView_setViewport(view, (FilaViewport){0, 0, 640u, 480u});
    FilaView_setCamera(view, camera);
    FilaView_setShadowType(view, FILA_VIEW_SHADOW_TYPE_PCF);
    FilaView_setShadowingEnabled(view, true);
    FilaView_setPostProcessingEnabled(view, false);
    if (!FilaView_isShadowingEnabled(view)) {
        printf("Shadowing should be enabled on view\n");
        goto cleanup;
    }

    FilaCamera_setProjectionFov(camera, 45.0, 640.0 / 480.0, 0.1, 100.0, FILA_CAMERA_FOV_VERTICAL);
    FilaCamera_lookAt(camera, 0.0, 1.2, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    {
        FilaVertexBufferBuilder* vbb = FilaVertexBufferBuilder_create();
        FilaIndexBufferBuilder* ibb = FilaIndexBufferBuilder_create();
        if (!vbb || !ibb) {
            FilaVertexBufferBuilder_destroy(vbb);
            FilaIndexBufferBuilder_destroy(ibb);
            printf("Geometry builder creation failed\n");
            goto cleanup;
        }

        FilaVertexBufferBuilder_bufferCount(vbb, 1u);
        FilaVertexBufferBuilder_vertexCount(vbb, 3u);
        FilaVertexBufferBuilder_attribute(vbb, FILA_VERTEX_ATTRIBUTE_POSITION, 0u, FILA_VERTEX_ATTRIBUTE_TYPE_FLOAT3, 0u, 12u);
        vb = FilaVertexBufferBuilder_build(vbb, engine);
        FilaVertexBufferBuilder_destroy(vbb);

        FilaIndexBufferBuilder_indexCount(ibb, 3u);
        FilaIndexBufferBuilder_bufferType(ibb, FILA_INDEX_TYPE_USHORT);
        ib = FilaIndexBufferBuilder_build(ibb, engine);
        FilaIndexBufferBuilder_destroy(ibb);

        if (!vb || !ib) {
            printf("Geometry resource build failed\n");
            goto cleanup;
        }
    }

    {
        static const float kTriangleVertices[9] = {
            -0.8f, 0.0f, 0.0f,
             0.8f, 0.0f, 0.0f,
             0.0f, 0.8f, 0.0f,
        };
        static const uint16_t kTriangleIndices[3] = {0u, 1u, 2u};

        float* vertices = (float*)malloc(sizeof(kTriangleVertices));
        uint16_t* indices = (uint16_t*)malloc(sizeof(kTriangleIndices));
        if (!vertices || !indices) {
            free(vertices);
            free(indices);
            printf("Geometry buffer allocation failed\n");
            goto cleanup;
        }

        memcpy(vertices, kTriangleVertices, sizeof(kTriangleVertices));
        memcpy(indices, kTriangleIndices, sizeof(kTriangleIndices));

        FilaBufferDescriptor* vbDesc = FilaBufferDescriptor_create(vertices, sizeof(kTriangleVertices), release_heap_buffer, (void*)0);
        FilaBufferDescriptor* ibDesc = FilaBufferDescriptor_create(indices, sizeof(kTriangleIndices), release_heap_buffer, (void*)0);
        if (!vbDesc || !ibDesc) {
            FilaBufferDescriptor_destroy(vbDesc);
            FilaBufferDescriptor_destroy(ibDesc);
            printf("Buffer descriptor creation failed\n");
            goto cleanup;
        }

        FilaVertexBuffer_setBufferAt(vb, engine, 0u, vbDesc, 0u);
        FilaIndexBuffer_setBuffer(ib, engine, ibDesc, 0u);
    }

    {
        const char* litFixturePath = getenv("FILA_TEST_LIT_MATERIAL_PACKAGE");
        if (litFixturePath && litFixturePath[0] != '\0') {
            unsigned char* packageData = NULL;
            size_t packageSize = 0u;
            if (!read_binary_file(litFixturePath, &packageData, &packageSize)) {
                printf("Failed to read FILA_TEST_LIT_MATERIAL_PACKAGE: %s\n", litFixturePath);
                goto cleanup;
            }

            FilaMaterialBuilder* mb = FilaMaterialBuilder_create();
            if (!mb) {
                free(packageData);
                printf("MaterialBuilder creation failed for lit fixture\n");
                goto cleanup;
            }
            FilaMaterialBuilder_package(mb, packageData, packageSize);
            sceneMaterial = FilaMaterialBuilder_build(mb, engine);
            FilaMaterialBuilder_destroy(mb);
            free(packageData);
            if (!sceneMaterial) {
                printf("Failed to build material from FILA_TEST_LIT_MATERIAL_PACKAGE\n");
                goto cleanup;
            }
            sceneMi = FilaMaterial_createInstance((const FilaMaterial*)sceneMaterial);
            if (!sceneMi) {
                printf("Lit fixture material instance creation failed\n");
                goto cleanup;
            }
        } else {
            const FilaMaterial* defaultMaterial = FilaEngine_getDefaultMaterial(engine);
            if (!defaultMaterial) {
                printf("Default material lookup failed\n");
                goto cleanup;
            }

            sceneMi = FilaMaterial_createInstance(defaultMaterial);
            if (!sceneMi) {
                printf("Default material instance creation failed\n");
                goto cleanup;
            }
        }
    }

    {
        FilaRenderableManagerBuilder* rb = FilaRenderableManagerBuilder_create(1u);
        if (!rb) {
            printf("Renderable builder creation failed\n");
            goto cleanup;
        }

        FilaRenderableManagerBuilder_geometry(rb, 0u, FILA_RENDERABLE_PRIMITIVE_TRIANGLES, vb, ib, 0u, 3u);
        FilaRenderableManagerBuilder_material(rb, 0u, sceneMi);
        FilaRenderableManagerBuilder_castShadows(rb, true);
        FilaRenderableManagerBuilder_receiveShadows(rb, true);
        FilaRenderableManagerBuilder_boundingBox(rb, &(FilaBox){{0.0f, 0.4f, 0.0f}, {1.0f, 1.0f, 0.2f}});
        if (!FilaRenderableManagerBuilder_build(rb, engine, renderableEntity)) {
            FilaRenderableManagerBuilder_destroy(rb);
            printf("Renderable build failed\n");
            goto cleanup;
        }
        FilaRenderableManagerBuilder_destroy(rb);
    }

    {
        FilaLightManagerBuilder* lb = FilaLightManagerBuilder_create(FILA_LIGHT_TYPE_DIRECTIONAL);
        if (!lb) {
            printf("Light builder creation failed\n");
            goto cleanup;
        }

        FilaLightManagerBuilder_direction(lb, -0.6f, -1.0f, -0.4f);
        FilaLightManagerBuilder_intensity(lb, 80000.0f);
        FilaLightManagerBuilder_castShadows(lb, true);

        if (!FilaLightManagerBuilder_build(lb, engine, lightEntity)) {
            FilaLightManagerBuilder_destroy(lb);
            printf("Light build failed\n");
            goto cleanup;
        }
        FilaLightManagerBuilder_destroy(lb);
    }

    FilaScene_addEntity(scene, renderableEntity);
    FilaScene_addEntity(scene, lightEntity);
    if (FilaScene_getRenderableCount(scene) == 0u || FilaScene_getLightCount(scene) == 0u) {
        printf("Scene component count mismatch\n");
        goto cleanup;
    }

    lm = FilaEngine_getLightManager(engine);
    rm = FilaEngine_getRenderableManager(engine);
    if (!lm || !rm) {
        printf("Manager lookup failed\n");
        goto cleanup;
    }

    {
        const FilaLightManagerInstance li = FilaLightManager_getInstance(lm, lightEntity);
        if (!li || !FilaLightManager_isShadowCaster(lm, li)) {
            printf("Shadow-casting light validation failed\n");
            goto cleanup;
        }
    }

    {
        const FilaRenderableManagerInstance ri = FilaRenderableManager_getInstance(rm, renderableEntity);
        if (!ri || !FilaRenderableManager_isShadowCaster(rm, ri) || !FilaRenderableManager_isShadowReceiver(rm, ri)) {
            printf("Shadow-ready renderable validation failed\n");
            goto cleanup;
        }
    }

    {
        bool rendered = false;
        for (int i = 0; i < 3; ++i) {
            if (FilaRenderer_beginFrame(renderer, swapChain, 0u)) {
                FilaRenderer_render(renderer, view);
                FilaRenderer_endFrame(renderer);
                rendered = true;
                break;
            }
        }

        if (!rendered) {
            printf("No frame rendered after retries\n");
            goto cleanup;
        }
    }

    result = 0;

cleanup:
    if (lm && lightEntity) {
        FilaLightManager_destroy(lm, lightEntity);
    }
    if (rm && renderableEntity) {
        FilaRenderableManager_destroy(rm, renderableEntity);
    }

    if (scene && lightEntity) {
        FilaScene_removeEntity(scene, lightEntity);
    }
    if (scene && renderableEntity) {
        FilaScene_removeEntity(scene, renderableEntity);
    }

    if (sceneMi) {
        FilaEngine_destroyMaterialInstance(engine, sceneMi);
    }
    if (sceneMaterial) {
        FilaEngine_destroyMaterial(engine, sceneMaterial);
    }
    if (ib) {
        FilaEngine_destroyIndexBuffer(engine, ib);
    }
    if (vb) {
        FilaEngine_destroyVertexBuffer(engine, vb);
    }

    if (cameraEntity) {
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
    }

    if (lightEntity) {
        FilaEntityManager_destroy(lightEntity);
    }
    if (renderableEntity) {
        FilaEntityManager_destroy(renderableEntity);
    }
    if (cameraEntity) {
        FilaEntityManager_destroy(cameraEntity);
    }

    if (view) {
        FilaEngine_destroyView(engine, view);
    }
    if (scene) {
        FilaEngine_destroyScene(engine, scene);
    }
    if (swapChain) {
        FilaEngine_destroySwapChain(engine, swapChain);
    }
    if (renderer) {
        FilaEngine_destroyRenderer(engine, renderer);
    }
    if (engine) {
        FilaEngine_destroy(&engine);
    }

    if (result == 0) {
        printf("Engine+scene+lit_shadow first-frame functionality program completed\n");
    }
    return result;
}


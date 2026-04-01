#include <stdio.h>

#include "backend/PipelineState.h"

int main(void) {
    FilaBackendPipelineState* state = FilaBackendPipelineState_create();
    if (!state) {
        printf("PipelineState create failed\n");
        return 1;
    }

    FilaBackendRasterStateData rs = {
        .culling = FILA_BACKEND_CULLING_MODE_BACK,
        .blendEquationRGB = FILA_BACKEND_BLEND_EQUATION_ADD,
        .blendEquationAlpha = FILA_BACKEND_BLEND_EQUATION_ADD,
        .blendFunctionSrcRGB = FILA_BACKEND_BLEND_FUNCTION_ONE,
        .blendFunctionSrcAlpha = FILA_BACKEND_BLEND_FUNCTION_ONE,
        .blendFunctionDstRGB = FILA_BACKEND_BLEND_FUNCTION_ZERO,
        .blendFunctionDstAlpha = FILA_BACKEND_BLEND_FUNCTION_ZERO,
        .depthWrite = true,
        .depthFunc = FILA_BACKEND_SAMPLER_COMPARE_FUNC_LE,
        .colorWrite = true,
        .alphaToCoverage = false,
        .inverseFrontFaces = false,
        .depthClamp = false,
    };

    FilaBackendPipelineState_setRasterState(state, &rs);
    FilaBackendPipelineState_setBlendState(
        state,
        FILA_BACKEND_BLEND_EQUATION_ADD,
        FILA_BACKEND_BLEND_EQUATION_ADD,
        FILA_BACKEND_BLEND_FUNCTION_ONE,
        FILA_BACKEND_BLEND_FUNCTION_ONE,
        FILA_BACKEND_BLEND_FUNCTION_ZERO,
        FILA_BACKEND_BLEND_FUNCTION_ZERO);
    FilaBackendPipelineState_setCullingMode(state, FILA_BACKEND_CULLING_MODE_BACK);
    FilaBackendPipelineState_setDepthWrite(state, true);
    FilaBackendPipelineState_setColorWrite(state, true);
    FilaBackendPipelineState_setAlphaToCoverage(state, false);
    if (!FilaBackendPipelineState_getRasterState(state, &rs)) {
        printf("PipelineState raster roundtrip failed\n");
        FilaBackendPipelineState_destroy(state);
        return 1;
    }
    if (!FilaBackendPipelineState_getDepthWrite(state)) {
        printf("PipelineState depthWrite mismatch\n");
        FilaBackendPipelineState_destroy(state);
        return 1;
    }

    {
        FilaBackendDescriptorSetLayoutHandleId handles[4] = { 1u, 2u, 3u, 4u };
        FilaBackendPipelineState_setDescriptorSetLayoutHandles(state, handles, 4);
        {
            FilaBackendDescriptorSetLayoutHandleId out[4] = { 0 };
            uint32_t count = FilaBackendPipelineState_getDescriptorSetLayoutHandles(state, out, 4);
            if (count == 0 || out[0] != 1u) {
                printf("PipelineState descriptor bulk readback failed\n");
                FilaBackendPipelineState_destroy(state);
                return 1;
            }
        }
    }

    FilaBackendStencilOperationsData ops = {
        .stencilFunc = FILA_BACKEND_SAMPLER_COMPARE_FUNC_A,
        .stencilOpStencilFail = FILA_BACKEND_STENCIL_OPERATION_KEEP,
        .stencilOpDepthFail = FILA_BACKEND_STENCIL_OPERATION_KEEP,
        .stencilOpDepthStencilPass = FILA_BACKEND_STENCIL_OPERATION_REPLACE,
        .ref = 3,
        .readMask = 0xFF,
        .writeMask = 0xFF,
    };
    if (!FilaBackendPipelineState_setStencilOperationsForFace(
            state, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK, &ops)) {
        printf("PipelineState set stencil ops failed\n");
        FilaBackendPipelineState_destroy(state);
        return 1;
    }
    FilaBackendPipelineState_setStencilWrite(state, true);
    if (!FilaBackendPipelineState_getStencilWrite(state)) {
        printf("PipelineState stencilWrite mismatch\n");
        FilaBackendPipelineState_destroy(state);
        return 1;
    }
    if (!FilaBackendPipelineState_setStencilRefMaskForFace(
            state, FILA_BACKEND_STENCIL_FACE_FRONT, 7, 0xAA, 0x55)) {
        printf("PipelineState set stencil ref/mask failed\n");
        FilaBackendPipelineState_destroy(state);
        return 1;
    }

    FilaBackendPolygonOffsetData po = { .slope = 0.5f, .constant = 1.0f };
    FilaBackendPipelineState_setPolygonOffset(state, &po);
    if (!FilaBackendPipelineState_getPolygonOffset(state, &po)) {
        printf("PipelineState polygon offset roundtrip failed\n");
        FilaBackendPipelineState_destroy(state);
        return 1;
    }

    {
        FilaBackendRenderPassParams* params = FilaBackendRenderPassParams_create();
        if (!params) {
            printf("RenderPassParams create failed\n");
            FilaBackendPipelineState_destroy(state);
            return 1;
        }

        FilaBackendRenderPassParams_setClearColor(params, 0.25f, 0.5f, 0.75f, 1.0f);
        FilaBackendRenderPassParams_setClearTargets(params, FILA_BACKEND_TARGET_BUFFER_ALL);
        FilaBackendRenderPassParams_setClearDepth(params, 0.9);
        FilaBackendRenderPassParams_setClearStencil(params, 5u);
        FilaBackendRenderPassParams_setReadOnlyDepthStencilFlags(params, true, true);

        {
            float color[4] = {0};
            if (!FilaBackendRenderPassParams_getClearColor(params, color)) {
                printf("RenderPassParams clear color read failed\n");
                FilaBackendRenderPassParams_destroy(params);
                FilaBackendPipelineState_destroy(state);
                return 1;
            }
            if (color[3] != 1.0f) {
                printf("RenderPassParams clear color mismatch\n");
                FilaBackendRenderPassParams_destroy(params);
                FilaBackendPipelineState_destroy(state);
                return 1;
            }
            if (FilaBackendRenderPassParams_getClearTargets(params) != FILA_BACKEND_TARGET_BUFFER_ALL) {
                printf("RenderPassParams clear targets mismatch\n");
                FilaBackendRenderPassParams_destroy(params);
                FilaBackendPipelineState_destroy(state);
                return 1;
            }
        }

        {
            FilaBackendRenderPassParamsData data;
            if (!FilaBackendRenderPassParams_get(params, &data)) {
                printf("RenderPassParams roundtrip failed\n");
                FilaBackendRenderPassParams_destroy(params);
                FilaBackendPipelineState_destroy(state);
                return 1;
            }
            if ((data.readOnlyDepthStencil & FILA_BACKEND_RENDER_PASS_READONLY_DEPTH) == 0) {
                printf("RenderPassParams readonly depth flag missing\n");
                FilaBackendRenderPassParams_destroy(params);
                FilaBackendPipelineState_destroy(state);
                return 1;
            }
        }

        FilaBackendRenderPassParams_destroy(params);
    }

    FilaBackendPipelineState_destroy(state);
    return 0;
}


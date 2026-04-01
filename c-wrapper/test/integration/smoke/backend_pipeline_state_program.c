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
    if (!FilaBackendPipelineState_getRasterState(state, &rs)) {
        printf("PipelineState raster roundtrip failed\n");
        FilaBackendPipelineState_destroy(state);
        return 1;
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

    FilaBackendPolygonOffsetData po = { .slope = 0.5f, .constant = 1.0f };
    FilaBackendPipelineState_setPolygonOffset(state, &po);
    if (!FilaBackendPipelineState_getPolygonOffset(state, &po)) {
        printf("PipelineState polygon offset roundtrip failed\n");
        FilaBackendPipelineState_destroy(state);
        return 1;
    }

    FilaBackendPipelineState_destroy(state);
    return 0;
}


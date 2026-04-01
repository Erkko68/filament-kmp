#include "backend/PipelineState.h"

void backend_pipeline_state_test(void) {
    FilaBackendPipelineState* state = FilaBackendPipelineState_create();

    FilaBackendPipelineState_setProgramHandle(state, 7u);
    FilaBackendPipelineState_getProgramHandle(state);

    FilaBackendPipelineState_setVertexBufferInfoHandle(state, 9u);
    FilaBackendPipelineState_getVertexBufferInfoHandle(state);

    FilaBackendPipelineState_setPrimitiveType(state, FILA_BACKEND_PRIMITIVE_TYPE_LINE_STRIP);
    FilaBackendPipelineState_getPrimitiveType(state);

    FilaBackendPipelineState_setDescriptorSetLayoutHandleAt(state, 0, 11u);
    {
        FilaBackendDescriptorSetLayoutHandleId out = FILA_BACKEND_HANDLE_NULL;
        FilaBackendPipelineState_getDescriptorSetLayoutHandleAt(state, 0, &out);
    }

    {
        FilaBackendRasterStateData rs = {
            .culling = FILA_BACKEND_CULLING_MODE_FRONT,
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
        FilaBackendPipelineState_getRasterState(state, &rs);
    }

    {
        FilaBackendStencilOperationsData ops = {
            .stencilFunc = FILA_BACKEND_SAMPLER_COMPARE_FUNC_A,
            .stencilOpStencilFail = FILA_BACKEND_STENCIL_OPERATION_KEEP,
            .stencilOpDepthFail = FILA_BACKEND_STENCIL_OPERATION_KEEP,
            .stencilOpDepthStencilPass = FILA_BACKEND_STENCIL_OPERATION_REPLACE,
            .ref = 1,
            .readMask = 0xFF,
            .writeMask = 0xFF,
        };
        FilaBackendStencilStateData ss = {
            .front = ops,
            .back = ops,
            .stencilWrite = true,
        };
        FilaBackendPipelineState_setStencilState(state, &ss);
        FilaBackendPipelineState_getStencilState(state, &ss);
        FilaBackendPipelineState_setStencilOperationsForFace(state, FILA_BACKEND_STENCIL_FACE_FRONT, &ops);
        FilaBackendPipelineState_getStencilOperationsForFace(state, FILA_BACKEND_STENCIL_FACE_FRONT, &ops);
    }

    {
        FilaBackendPolygonOffsetData po = { .slope = 1.0f, .constant = 2.0f };
        FilaBackendPipelineState_setPolygonOffset(state, &po);
        FilaBackendPipelineState_getPolygonOffset(state, &po);
    }

    FilaBackendPipelineState_destroy(state);
}


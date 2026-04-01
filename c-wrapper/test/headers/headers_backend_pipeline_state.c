#include "backend/PipelineState.h"

void test_headers_backend_pipeline_state(void) {
    FilaBackendPipelineState* state = FilaBackendPipelineState_create();

    FilaBackendPipelineState_setProgramHandle(state, 7u);
    FilaBackendPipelineState_getProgramHandle(state);

    FilaBackendPipelineState_setVertexBufferInfoHandle(state, 9u);
    FilaBackendPipelineState_getVertexBufferInfoHandle(state);

    FilaBackendPipelineState_setPrimitiveType(state, FILA_BACKEND_PRIMITIVE_TYPE_LINE_STRIP);
    FilaBackendPipelineState_getPrimitiveType(state);
    FilaBackendPipelineState_setDefaults(state);

    FilaBackendPipelineState_setCullingMode(state, FILA_BACKEND_CULLING_MODE_FRONT);
    FilaBackendPipelineState_getCullingMode(state);
    FilaBackendPipelineState_setDepthFunc(state, FILA_BACKEND_SAMPLER_COMPARE_FUNC_GE);
    FilaBackendPipelineState_getDepthFunc(state);
    FilaBackendPipelineState_setDepthWrite(state, true);
    FilaBackendPipelineState_getDepthWrite(state);
    FilaBackendPipelineState_setColorWrite(state, true);
    FilaBackendPipelineState_getColorWrite(state);
    FilaBackendPipelineState_setAlphaToCoverage(state, true);
    FilaBackendPipelineState_getAlphaToCoverage(state);
    FilaBackendPipelineState_setInverseFrontFaces(state, true);
    FilaBackendPipelineState_getInverseFrontFaces(state);
    FilaBackendPipelineState_setDepthClamp(state, true);
    FilaBackendPipelineState_getDepthClamp(state);
    {
        FilaBackendBlendEquation eqRgb = FILA_BACKEND_BLEND_EQUATION_ADD;
        FilaBackendBlendEquation eqA = FILA_BACKEND_BLEND_EQUATION_ADD;
        FilaBackendBlendFunction srcRgb = FILA_BACKEND_BLEND_FUNCTION_ONE;
        FilaBackendBlendFunction srcA = FILA_BACKEND_BLEND_FUNCTION_ONE;
        FilaBackendBlendFunction dstRgb = FILA_BACKEND_BLEND_FUNCTION_ZERO;
        FilaBackendBlendFunction dstA = FILA_BACKEND_BLEND_FUNCTION_ZERO;
        FilaBackendPipelineState_setBlendState(
            state,
            FILA_BACKEND_BLEND_EQUATION_ADD,
            FILA_BACKEND_BLEND_EQUATION_ADD,
            FILA_BACKEND_BLEND_FUNCTION_ONE,
            FILA_BACKEND_BLEND_FUNCTION_ONE,
            FILA_BACKEND_BLEND_FUNCTION_ZERO,
            FILA_BACKEND_BLEND_FUNCTION_ZERO);
        FilaBackendPipelineState_getBlendState(
            state,
            &eqRgb,
            &eqA,
            &srcRgb,
            &srcA,
            &dstRgb,
            &dstA);
    }
    FilaBackendPipelineState_hasBlending(state);

    FilaBackendPipelineState_setDescriptorSetLayoutHandleAt(state, 0, 11u);
    {
        FilaBackendDescriptorSetLayoutHandleId out = FILA_BACKEND_HANDLE_NULL;
        FilaBackendPipelineState_getDescriptorSetLayoutHandleAt(state, 0, &out);
    }
    {
        FilaBackendDescriptorSetLayoutHandleId handles[4] = { 1u, 2u, 3u, 4u };
        FilaBackendPipelineState_setDescriptorSetLayoutHandles(state, handles, 4);
        FilaBackendPipelineState_getDescriptorSetLayoutHandles(state, handles, 4);
        FilaBackendPipelineState_clearDescriptorSetLayoutHandles(state);
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
        FilaBackendPipelineState_setStencilWrite(state, true);
        FilaBackendPipelineState_getStencilWrite(state);
        FilaBackendPipelineState_setStencilOperationsForFace(state, FILA_BACKEND_STENCIL_FACE_FRONT, &ops);
        FilaBackendPipelineState_getStencilOperationsForFace(state, FILA_BACKEND_STENCIL_FACE_FRONT, &ops);
        FilaBackendPipelineState_setStencilRefMaskForFace(state, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK, 5, 0xF0, 0x0F);
        {
            uint8_t ref = 0;
            uint8_t readMask = 0;
            uint8_t writeMask = 0;
            FilaBackendPipelineState_getStencilRefMaskForFace(
                state,
                FILA_BACKEND_STENCIL_FACE_FRONT,
                &ref,
                &readMask,
                &writeMask);
        }
    }

    {
        FilaBackendPolygonOffsetData po = { .slope = 1.0f, .constant = 2.0f };
        FilaBackendPipelineState_setPolygonOffset(state, &po);
        FilaBackendPipelineState_getPolygonOffset(state, &po);
    }

    {
        FilaBackendRenderPassParams* params = FilaBackendRenderPassParams_create();
        FilaBackendRenderPassParamsData rp = {
            .flags = {
                .clear = FILA_BACKEND_TARGET_BUFFER_ALL,
                .discardStart = FILA_BACKEND_TARGET_BUFFER_NONE,
                .discardEnd = FILA_BACKEND_TARGET_BUFFER_NONE,
            },
            .viewport = { .left = 1, .bottom = 2, .width = 10, .height = 20 },
            .depthRange = { .near = 0.0f, .far = 1.0f },
            .clearColor = { 0.1f, 0.2f, 0.3f, 1.0f },
            .clearDepth = 1.0,
            .clearStencil = 0,
            .subpassMask = 0,
            .readOnlyDepthStencil = 0,
        };
        FilaBackendRenderPassParamsData_setDefaults(&rp);
        FilaBackendRenderPassParamsData_setReadOnlyDepthStencilFlags(&rp, true, false);
        FilaBackendRenderPassFlagsData flags = rp.flags;
        FilaBackendViewportData viewport = rp.viewport;
        FilaBackendDepthRangeData depthRange = rp.depthRange;
        float color[4] = {0};

        FilaBackendRenderPassParams_set(params, &rp);
        FilaBackendRenderPassParams_get(params, &rp);
        FilaBackendRenderPassParams_setFlags(params, &flags);
        FilaBackendRenderPassParams_getFlags(params, &flags);
        FilaBackendRenderPassParams_setViewport(params, &viewport);
        FilaBackendRenderPassParams_getViewport(params, &viewport);
        FilaBackendRenderPassParams_setDepthRange(params, &depthRange);
        FilaBackendRenderPassParams_getDepthRange(params, &depthRange);
        FilaBackendRenderPassParams_setClearColor(params, 1.0f, 0.0f, 0.0f, 1.0f);
        FilaBackendRenderPassParams_getClearColor(params, color);
        FilaBackendRenderPassParams_setClearTargets(params, FILA_BACKEND_TARGET_BUFFER_DEPTH);
        FilaBackendRenderPassParams_getClearTargets(params);
        FilaBackendRenderPassParams_setClearDepth(params, 0.5);
        FilaBackendRenderPassParams_getClearDepth(params);
        FilaBackendRenderPassParams_setClearStencil(params, 7u);
        FilaBackendRenderPassParams_getClearStencil(params);
        FilaBackendRenderPassParams_setSubpassMask(params, 3u);
        FilaBackendRenderPassParams_getSubpassMask(params);
        FilaBackendRenderPassParams_setReadOnlyDepthStencil(
            params,
            FILA_BACKEND_RENDER_PASS_READONLY_DEPTH | FILA_BACKEND_RENDER_PASS_READONLY_STENCIL);
        FilaBackendRenderPassParams_setReadOnlyDepthStencilFlags(params, true, false);
        FilaBackendRenderPassParams_getReadOnlyDepthStencil(params);

        FilaBackendViewport_right(&viewport);
        FilaBackendViewport_top(&viewport);
        FilaBackendRenderPassParams_destroy(params);
    }

    FilaBackendPipelineState_destroy(state);
}


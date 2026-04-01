#include "../../include/backend/PipelineState.h"

#include <backend/Handle.h>
#include <backend/PipelineState.h>

struct FilaBackendPipelineState {
    filament::backend::PipelineState impl;
};

namespace {
template <typename HwType>
filament::backend::Handle<HwType> toHandle(uint32_t id) {
    if (id == FILA_BACKEND_HANDLE_NULL) {
        return {};
    }
    return filament::backend::Handle<HwType>(id);
}

FilaBackendRasterStateData toRasterStateData(const filament::backend::RasterState& rs) {
    FilaBackendRasterStateData out{};
    out.culling = static_cast<FilaBackendCullingMode>(rs.culling);
    out.blendEquationRGB = static_cast<FilaBackendBlendEquation>(rs.blendEquationRGB);
    out.blendEquationAlpha = static_cast<FilaBackendBlendEquation>(rs.blendEquationAlpha);
    out.blendFunctionSrcRGB = static_cast<FilaBackendBlendFunction>(rs.blendFunctionSrcRGB);
    out.blendFunctionSrcAlpha = static_cast<FilaBackendBlendFunction>(rs.blendFunctionSrcAlpha);
    out.blendFunctionDstRGB = static_cast<FilaBackendBlendFunction>(rs.blendFunctionDstRGB);
    out.blendFunctionDstAlpha = static_cast<FilaBackendBlendFunction>(rs.blendFunctionDstAlpha);
    out.depthWrite = rs.depthWrite;
    out.depthFunc = static_cast<FilaBackendSamplerCompareFunc>(rs.depthFunc);
    out.colorWrite = rs.colorWrite;
    out.alphaToCoverage = rs.alphaToCoverage;
    out.inverseFrontFaces = rs.inverseFrontFaces;
    out.depthClamp = rs.depthClamp;
    return out;
}

void applyRasterStateData(filament::backend::RasterState& rs, const FilaBackendRasterStateData& in) {
    rs.culling = static_cast<filament::backend::CullingMode>(in.culling);
    rs.blendEquationRGB = static_cast<filament::backend::BlendEquation>(in.blendEquationRGB);
    rs.blendEquationAlpha = static_cast<filament::backend::BlendEquation>(in.blendEquationAlpha);
    rs.blendFunctionSrcRGB = static_cast<filament::backend::BlendFunction>(in.blendFunctionSrcRGB);
    rs.blendFunctionSrcAlpha = static_cast<filament::backend::BlendFunction>(in.blendFunctionSrcAlpha);
    rs.blendFunctionDstRGB = static_cast<filament::backend::BlendFunction>(in.blendFunctionDstRGB);
    rs.blendFunctionDstAlpha = static_cast<filament::backend::BlendFunction>(in.blendFunctionDstAlpha);
    rs.depthWrite = in.depthWrite;
    rs.depthFunc = static_cast<filament::backend::SamplerCompareFunc>(in.depthFunc);
    rs.colorWrite = in.colorWrite;
    rs.alphaToCoverage = in.alphaToCoverage;
    rs.inverseFrontFaces = in.inverseFrontFaces;
    rs.depthClamp = in.depthClamp;
}

FilaBackendStencilOperationsData toStencilOpsData(
        const filament::backend::StencilState::StencilOperations& ops) {
    FilaBackendStencilOperationsData out{};
    out.stencilFunc = static_cast<FilaBackendSamplerCompareFunc>(ops.stencilFunc);
    out.stencilOpStencilFail = static_cast<FilaBackendStencilOperation>(ops.stencilOpStencilFail);
    out.stencilOpDepthFail = static_cast<FilaBackendStencilOperation>(ops.stencilOpDepthFail);
    out.stencilOpDepthStencilPass =
        static_cast<FilaBackendStencilOperation>(ops.stencilOpDepthStencilPass);
    out.ref = ops.ref;
    out.readMask = ops.readMask;
    out.writeMask = ops.writeMask;
    return out;
}

void applyStencilOpsData(filament::backend::StencilState::StencilOperations& ops,
        const FilaBackendStencilOperationsData& in) {
    ops.stencilFunc = static_cast<filament::backend::SamplerCompareFunc>(in.stencilFunc);
    ops.stencilOpStencilFail = static_cast<filament::backend::StencilOperation>(in.stencilOpStencilFail);
    ops.stencilOpDepthFail = static_cast<filament::backend::StencilOperation>(in.stencilOpDepthFail);
    ops.stencilOpDepthStencilPass =
        static_cast<filament::backend::StencilOperation>(in.stencilOpDepthStencilPass);
    ops.ref = in.ref;
    ops.readMask = in.readMask;
    ops.writeMask = in.writeMask;
}

FilaBackendStencilStateData toStencilStateData(const filament::backend::StencilState& ss) {
    FilaBackendStencilStateData out{};
    out.front = toStencilOpsData(ss.front);
    out.back = toStencilOpsData(ss.back);
    out.stencilWrite = ss.stencilWrite;
    return out;
}

void applyStencilStateData(filament::backend::StencilState& ss, const FilaBackendStencilStateData& in) {
    applyStencilOpsData(ss.front, in.front);
    applyStencilOpsData(ss.back, in.back);
    ss.stencilWrite = in.stencilWrite;
}
} // namespace

extern "C" {

FilaBackendPipelineState* FilaBackendPipelineState_create(void) {
    return new FilaBackendPipelineState;
}

void FilaBackendPipelineState_destroy(FilaBackendPipelineState* pipelineState) {
    delete pipelineState;
}

void FilaBackendPipelineState_setProgramHandle(
        FilaBackendPipelineState* pipelineState, FilaBackendProgramHandleId programHandle) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.program = toHandle<filament::backend::HwProgram>(programHandle);
}

FilaBackendProgramHandleId FilaBackendPipelineState_getProgramHandle(
        const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return FILA_BACKEND_HANDLE_NULL;
    }
    return pipelineState->impl.program.getId();
}

void FilaBackendPipelineState_setVertexBufferInfoHandle(
        FilaBackendPipelineState* pipelineState,
        FilaBackendVertexBufferInfoHandleId vertexBufferInfoHandle) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.vertexBufferInfo =
        toHandle<filament::backend::HwVertexBufferInfo>(vertexBufferInfoHandle);
}

FilaBackendVertexBufferInfoHandleId FilaBackendPipelineState_getVertexBufferInfoHandle(
        const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return FILA_BACKEND_HANDLE_NULL;
    }
    return pipelineState->impl.vertexBufferInfo.getId();
}

bool FilaBackendPipelineState_setDescriptorSetLayoutHandleAt(
        FilaBackendPipelineState* pipelineState, size_t index,
        FilaBackendDescriptorSetLayoutHandleId handle) {
    if (!pipelineState || index >= filament::backend::MAX_DESCRIPTOR_SET_COUNT) {
        return false;
    }
    pipelineState->impl.pipelineLayout.setLayout[index] =
        toHandle<filament::backend::HwDescriptorSetLayout>(handle);
    return true;
}

bool FilaBackendPipelineState_getDescriptorSetLayoutHandleAt(
        const FilaBackendPipelineState* pipelineState, size_t index,
        FilaBackendDescriptorSetLayoutHandleId* outHandle) {
    if (!pipelineState || !outHandle || index >= filament::backend::MAX_DESCRIPTOR_SET_COUNT) {
        return false;
    }
    *outHandle = pipelineState->impl.pipelineLayout.setLayout[index].getId();
    return true;
}

void FilaBackendPipelineState_setPrimitiveType(
        FilaBackendPipelineState* pipelineState, FilaBackendPrimitiveType primitiveType) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.primitiveType = static_cast<filament::backend::PrimitiveType>(primitiveType);
}

FilaBackendPrimitiveType FilaBackendPipelineState_getPrimitiveType(
        const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return FILA_BACKEND_PRIMITIVE_TYPE_TRIANGLES;
    }
    return static_cast<FilaBackendPrimitiveType>(pipelineState->impl.primitiveType);
}

void FilaBackendPipelineState_setRasterState(
        FilaBackendPipelineState* pipelineState, const FilaBackendRasterStateData* rasterState) {
    if (!pipelineState || !rasterState) {
        return;
    }
    applyRasterStateData(pipelineState->impl.rasterState, *rasterState);
}

bool FilaBackendPipelineState_getRasterState(
        const FilaBackendPipelineState* pipelineState, FilaBackendRasterStateData* outRasterState) {
    if (!pipelineState || !outRasterState) {
        return false;
    }
    *outRasterState = toRasterStateData(pipelineState->impl.rasterState);
    return true;
}

void FilaBackendPipelineState_setStencilState(
        FilaBackendPipelineState* pipelineState, const FilaBackendStencilStateData* stencilState) {
    if (!pipelineState || !stencilState) {
        return;
    }
    applyStencilStateData(pipelineState->impl.stencilState, *stencilState);
}

bool FilaBackendPipelineState_getStencilState(
        const FilaBackendPipelineState* pipelineState, FilaBackendStencilStateData* outStencilState) {
    if (!pipelineState || !outStencilState) {
        return false;
    }
    *outStencilState = toStencilStateData(pipelineState->impl.stencilState);
    return true;
}

bool FilaBackendPipelineState_setStencilOperationsForFace(
        FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
        const FilaBackendStencilOperationsData* operations) {
    if (!pipelineState || !operations) {
        return false;
    }

    bool updated = false;
    if (face & FILA_BACKEND_STENCIL_FACE_FRONT) {
        applyStencilOpsData(pipelineState->impl.stencilState.front, *operations);
        updated = true;
    }
    if (face & FILA_BACKEND_STENCIL_FACE_BACK) {
        applyStencilOpsData(pipelineState->impl.stencilState.back, *operations);
        updated = true;
    }
    return updated;
}

bool FilaBackendPipelineState_getStencilOperationsForFace(
        const FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
        FilaBackendStencilOperationsData* outOperations) {
    if (!pipelineState || !outOperations) {
        return false;
    }
    if (face == FILA_BACKEND_STENCIL_FACE_FRONT) {
        *outOperations = toStencilOpsData(pipelineState->impl.stencilState.front);
        return true;
    }
    if (face == FILA_BACKEND_STENCIL_FACE_BACK) {
        *outOperations = toStencilOpsData(pipelineState->impl.stencilState.back);
        return true;
    }
    return false;
}

void FilaBackendPipelineState_setPolygonOffset(
        FilaBackendPipelineState* pipelineState, const FilaBackendPolygonOffsetData* polygonOffset) {
    if (!pipelineState || !polygonOffset) {
        return;
    }
    pipelineState->impl.polygonOffset.slope = polygonOffset->slope;
    pipelineState->impl.polygonOffset.constant = polygonOffset->constant;
}

bool FilaBackendPipelineState_getPolygonOffset(
        const FilaBackendPipelineState* pipelineState, FilaBackendPolygonOffsetData* outPolygonOffset) {
    if (!pipelineState || !outPolygonOffset) {
        return false;
    }
    outPolygonOffset->slope = pipelineState->impl.polygonOffset.slope;
    outPolygonOffset->constant = pipelineState->impl.polygonOffset.constant;
    return true;
}

}


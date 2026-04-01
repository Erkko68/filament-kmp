#include "../../include/backend/PipelineState.h"

#include <backend/Handle.h>
#include <backend/PipelineState.h>

struct FilaBackendPipelineState {
    filament::backend::PipelineState impl;
};

struct FilaBackendRenderPassParams {
    filament::backend::RenderPassParams impl;
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

FilaBackendViewportData toViewportData(const filament::backend::Viewport& vp) {
    FilaBackendViewportData out{};
    out.left = vp.left;
    out.bottom = vp.bottom;
    out.width = vp.width;
    out.height = vp.height;
    return out;
}

filament::backend::Viewport toViewport(const FilaBackendViewportData& vp) {
    filament::backend::Viewport out{};
    out.left = vp.left;
    out.bottom = vp.bottom;
    out.width = vp.width;
    out.height = vp.height;
    return out;
}

FilaBackendDepthRangeData toDepthRangeData(const filament::backend::DepthRange& dr) {
    FilaBackendDepthRangeData out{};
    out.near = dr.near;
    out.far = dr.far;
    return out;
}

filament::backend::DepthRange toDepthRange(const FilaBackendDepthRangeData& dr) {
    filament::backend::DepthRange out{};
    out.near = dr.near;
    out.far = dr.far;
    return out;
}

FilaBackendRenderPassFlagsData toRenderPassFlagsData(const filament::backend::RenderPassFlags& flags) {
    FilaBackendRenderPassFlagsData out{};
    out.clear = static_cast<FilaBackendTargetBufferFlags>(flags.clear);
    out.discardStart = static_cast<FilaBackendTargetBufferFlags>(flags.discardStart);
    out.discardEnd = static_cast<FilaBackendTargetBufferFlags>(flags.discardEnd);
    return out;
}

filament::backend::RenderPassFlags toRenderPassFlags(const FilaBackendRenderPassFlagsData& flags) {
    filament::backend::RenderPassFlags out{};
    out.clear = static_cast<filament::backend::TargetBufferFlags>(flags.clear);
    out.discardStart = static_cast<filament::backend::TargetBufferFlags>(flags.discardStart);
    out.discardEnd = static_cast<filament::backend::TargetBufferFlags>(flags.discardEnd);
    return out;
}

FilaBackendRenderPassParamsData toRenderPassParamsData(const filament::backend::RenderPassParams& params) {
    FilaBackendRenderPassParamsData out{};
    out.flags = toRenderPassFlagsData(params.flags);
    out.viewport = toViewportData(params.viewport);
    out.depthRange = toDepthRangeData(params.depthRange);
    out.clearColor[0] = params.clearColor[0];
    out.clearColor[1] = params.clearColor[1];
    out.clearColor[2] = params.clearColor[2];
    out.clearColor[3] = params.clearColor[3];
    out.clearDepth = params.clearDepth;
    out.clearStencil = params.clearStencil;
    out.subpassMask = params.subpassMask;
    out.readOnlyDepthStencil = params.readOnlyDepthStencil;
    return out;
}

void applyRenderPassParamsData(
        filament::backend::RenderPassParams& params, const FilaBackendRenderPassParamsData& in) {
    params.flags = toRenderPassFlags(in.flags);
    params.viewport = toViewport(in.viewport);
    params.depthRange = toDepthRange(in.depthRange);
    params.clearColor[0] = in.clearColor[0];
    params.clearColor[1] = in.clearColor[1];
    params.clearColor[2] = in.clearColor[2];
    params.clearColor[3] = in.clearColor[3];
    params.clearDepth = in.clearDepth;
    params.clearStencil = in.clearStencil;
    params.subpassMask = in.subpassMask;
    params.readOnlyDepthStencil = in.readOnlyDepthStencil;
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

uint32_t FilaBackendPipelineState_getDescriptorSetLayoutHandles(
        const FilaBackendPipelineState* pipelineState, FilaBackendDescriptorSetLayoutHandleId* outHandles,
        uint32_t outCount) {
    if (!pipelineState || !outHandles || outCount == 0) {
        return 0;
    }
    const uint32_t maxCount = static_cast<uint32_t>(filament::backend::MAX_DESCRIPTOR_SET_COUNT);
    const uint32_t copied = outCount < maxCount ? outCount : maxCount;
    for (uint32_t i = 0; i < copied; i++) {
        outHandles[i] = pipelineState->impl.pipelineLayout.setLayout[i].getId();
    }
    return copied;
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

void FilaBackendPipelineState_setDefaults(FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl = filament::backend::PipelineState{};
}

void FilaBackendPipelineState_setCullingMode(
        FilaBackendPipelineState* pipelineState, FilaBackendCullingMode cullingMode) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.rasterState.culling = static_cast<filament::backend::CullingMode>(cullingMode);
}

FilaBackendCullingMode FilaBackendPipelineState_getCullingMode(
        const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return FILA_BACKEND_CULLING_MODE_BACK;
    }
    return static_cast<FilaBackendCullingMode>(pipelineState->impl.rasterState.culling);
}

void FilaBackendPipelineState_setDepthFunc(
        FilaBackendPipelineState* pipelineState, FilaBackendSamplerCompareFunc depthFunc) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.rasterState.depthFunc = static_cast<filament::backend::SamplerCompareFunc>(depthFunc);
}

FilaBackendSamplerCompareFunc FilaBackendPipelineState_getDepthFunc(
        const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return FILA_BACKEND_SAMPLER_COMPARE_FUNC_LE;
    }
    return static_cast<FilaBackendSamplerCompareFunc>(pipelineState->impl.rasterState.depthFunc);
}

void FilaBackendPipelineState_setDepthWrite(FilaBackendPipelineState* pipelineState, bool depthWrite) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.rasterState.depthWrite = depthWrite;
}

bool FilaBackendPipelineState_getDepthWrite(const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return false;
    }
    return pipelineState->impl.rasterState.depthWrite;
}

void FilaBackendPipelineState_setColorWrite(FilaBackendPipelineState* pipelineState, bool colorWrite) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.rasterState.colorWrite = colorWrite;
}

bool FilaBackendPipelineState_getColorWrite(const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return false;
    }
    return pipelineState->impl.rasterState.colorWrite;
}

void FilaBackendPipelineState_setAlphaToCoverage(
        FilaBackendPipelineState* pipelineState, bool alphaToCoverage) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.rasterState.alphaToCoverage = alphaToCoverage;
}

bool FilaBackendPipelineState_getAlphaToCoverage(const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return false;
    }
    return pipelineState->impl.rasterState.alphaToCoverage;
}

void FilaBackendPipelineState_setInverseFrontFaces(
        FilaBackendPipelineState* pipelineState, bool inverseFrontFaces) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.rasterState.inverseFrontFaces = inverseFrontFaces;
}

bool FilaBackendPipelineState_getInverseFrontFaces(const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return false;
    }
    return pipelineState->impl.rasterState.inverseFrontFaces;
}

void FilaBackendPipelineState_setDepthClamp(FilaBackendPipelineState* pipelineState, bool depthClamp) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.rasterState.depthClamp = depthClamp;
}

bool FilaBackendPipelineState_getDepthClamp(const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return false;
    }
    return pipelineState->impl.rasterState.depthClamp;
}

void FilaBackendPipelineState_setBlendState(
        FilaBackendPipelineState* pipelineState,
        FilaBackendBlendEquation blendEquationRGB,
        FilaBackendBlendEquation blendEquationAlpha,
        FilaBackendBlendFunction blendFunctionSrcRGB,
        FilaBackendBlendFunction blendFunctionSrcAlpha,
        FilaBackendBlendFunction blendFunctionDstRGB,
        FilaBackendBlendFunction blendFunctionDstAlpha) {
    if (!pipelineState) {
        return;
    }
    auto& rs = pipelineState->impl.rasterState;
    rs.blendEquationRGB = static_cast<filament::backend::BlendEquation>(blendEquationRGB);
    rs.blendEquationAlpha = static_cast<filament::backend::BlendEquation>(blendEquationAlpha);
    rs.blendFunctionSrcRGB = static_cast<filament::backend::BlendFunction>(blendFunctionSrcRGB);
    rs.blendFunctionSrcAlpha = static_cast<filament::backend::BlendFunction>(blendFunctionSrcAlpha);
    rs.blendFunctionDstRGB = static_cast<filament::backend::BlendFunction>(blendFunctionDstRGB);
    rs.blendFunctionDstAlpha = static_cast<filament::backend::BlendFunction>(blendFunctionDstAlpha);
}

bool FilaBackendPipelineState_getBlendState(
        const FilaBackendPipelineState* pipelineState,
        FilaBackendBlendEquation* outBlendEquationRGB,
        FilaBackendBlendEquation* outBlendEquationAlpha,
        FilaBackendBlendFunction* outBlendFunctionSrcRGB,
        FilaBackendBlendFunction* outBlendFunctionSrcAlpha,
        FilaBackendBlendFunction* outBlendFunctionDstRGB,
        FilaBackendBlendFunction* outBlendFunctionDstAlpha) {
    if (!pipelineState || !outBlendEquationRGB || !outBlendEquationAlpha || !outBlendFunctionSrcRGB ||
            !outBlendFunctionSrcAlpha || !outBlendFunctionDstRGB || !outBlendFunctionDstAlpha) {
        return false;
    }
    const auto& rs = pipelineState->impl.rasterState;
    *outBlendEquationRGB = static_cast<FilaBackendBlendEquation>(rs.blendEquationRGB);
    *outBlendEquationAlpha = static_cast<FilaBackendBlendEquation>(rs.blendEquationAlpha);
    *outBlendFunctionSrcRGB = static_cast<FilaBackendBlendFunction>(rs.blendFunctionSrcRGB);
    *outBlendFunctionSrcAlpha = static_cast<FilaBackendBlendFunction>(rs.blendFunctionSrcAlpha);
    *outBlendFunctionDstRGB = static_cast<FilaBackendBlendFunction>(rs.blendFunctionDstRGB);
    *outBlendFunctionDstAlpha = static_cast<FilaBackendBlendFunction>(rs.blendFunctionDstAlpha);
    return true;
}

bool FilaBackendPipelineState_hasBlending(const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return false;
    }
    return pipelineState->impl.rasterState.hasBlending();
}

void FilaBackendPipelineState_clearDescriptorSetLayoutHandles(FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return;
    }
    for (size_t i = 0; i < filament::backend::MAX_DESCRIPTOR_SET_COUNT; i++) {
        pipelineState->impl.pipelineLayout.setLayout[i].clear();
    }
}

uint32_t FilaBackendPipelineState_setDescriptorSetLayoutHandles(
        FilaBackendPipelineState* pipelineState, const FilaBackendDescriptorSetLayoutHandleId* handles,
        uint32_t count) {
    if (!pipelineState || !handles) {
        return 0;
    }
    const uint32_t maxCount = static_cast<uint32_t>(filament::backend::MAX_DESCRIPTOR_SET_COUNT);
    const uint32_t applied = count < maxCount ? count : maxCount;
    for (uint32_t i = 0; i < applied; i++) {
        pipelineState->impl.pipelineLayout.setLayout[i] =
            toHandle<filament::backend::HwDescriptorSetLayout>(handles[i]);
    }
    return applied;
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

void FilaBackendPipelineState_setStencilWrite(FilaBackendPipelineState* pipelineState, bool stencilWrite) {
    if (!pipelineState) {
        return;
    }
    pipelineState->impl.stencilState.stencilWrite = stencilWrite;
}

bool FilaBackendPipelineState_getStencilWrite(const FilaBackendPipelineState* pipelineState) {
    if (!pipelineState) {
        return false;
    }
    return pipelineState->impl.stencilState.stencilWrite;
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

bool FilaBackendPipelineState_setStencilRefMaskForFace(
        FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
        uint8_t ref, uint8_t readMask, uint8_t writeMask) {
    if (!pipelineState) {
        return false;
    }
    bool updated = false;
    if (face & FILA_BACKEND_STENCIL_FACE_FRONT) {
        pipelineState->impl.stencilState.front.ref = ref;
        pipelineState->impl.stencilState.front.readMask = readMask;
        pipelineState->impl.stencilState.front.writeMask = writeMask;
        updated = true;
    }
    if (face & FILA_BACKEND_STENCIL_FACE_BACK) {
        pipelineState->impl.stencilState.back.ref = ref;
        pipelineState->impl.stencilState.back.readMask = readMask;
        pipelineState->impl.stencilState.back.writeMask = writeMask;
        updated = true;
    }
    return updated;
}

bool FilaBackendPipelineState_getStencilRefMaskForFace(
        const FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
        uint8_t* outRef, uint8_t* outReadMask, uint8_t* outWriteMask) {
    if (!pipelineState || !outRef || !outReadMask || !outWriteMask) {
        return false;
    }
    if (face == FILA_BACKEND_STENCIL_FACE_FRONT) {
        *outRef = pipelineState->impl.stencilState.front.ref;
        *outReadMask = pipelineState->impl.stencilState.front.readMask;
        *outWriteMask = pipelineState->impl.stencilState.front.writeMask;
        return true;
    }
    if (face == FILA_BACKEND_STENCIL_FACE_BACK) {
        *outRef = pipelineState->impl.stencilState.back.ref;
        *outReadMask = pipelineState->impl.stencilState.back.readMask;
        *outWriteMask = pipelineState->impl.stencilState.back.writeMask;
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

int32_t FilaBackendViewport_right(const FilaBackendViewportData* viewport) {
    if (!viewport) {
        return 0;
    }
    return viewport->left + static_cast<int32_t>(viewport->width);
}

int32_t FilaBackendViewport_top(const FilaBackendViewportData* viewport) {
    if (!viewport) {
        return 0;
    }
    return viewport->bottom + static_cast<int32_t>(viewport->height);
}

FilaBackendRenderPassParams* FilaBackendRenderPassParams_create(void) {
    return new FilaBackendRenderPassParams;
}

void FilaBackendRenderPassParams_destroy(FilaBackendRenderPassParams* params) {
    delete params;
}

void FilaBackendRenderPassParams_set(
        FilaBackendRenderPassParams* params, const FilaBackendRenderPassParamsData* data) {
    if (!params || !data) {
        return;
    }
    applyRenderPassParamsData(params->impl, *data);
}

bool FilaBackendRenderPassParams_get(
        const FilaBackendRenderPassParams* params, FilaBackendRenderPassParamsData* outData) {
    if (!params || !outData) {
        return false;
    }
    *outData = toRenderPassParamsData(params->impl);
    return true;
}

void FilaBackendRenderPassParams_setFlags(
        FilaBackendRenderPassParams* params, const FilaBackendRenderPassFlagsData* flags) {
    if (!params || !flags) {
        return;
    }
    params->impl.flags = toRenderPassFlags(*flags);
}

bool FilaBackendRenderPassParams_getFlags(
        const FilaBackendRenderPassParams* params, FilaBackendRenderPassFlagsData* outFlags) {
    if (!params || !outFlags) {
        return false;
    }
    *outFlags = toRenderPassFlagsData(params->impl.flags);
    return true;
}

void FilaBackendRenderPassParams_setViewport(
        FilaBackendRenderPassParams* params, const FilaBackendViewportData* viewport) {
    if (!params || !viewport) {
        return;
    }
    params->impl.viewport = toViewport(*viewport);
}

bool FilaBackendRenderPassParams_getViewport(
        const FilaBackendRenderPassParams* params, FilaBackendViewportData* outViewport) {
    if (!params || !outViewport) {
        return false;
    }
    *outViewport = toViewportData(params->impl.viewport);
    return true;
}

void FilaBackendRenderPassParams_setDepthRange(
        FilaBackendRenderPassParams* params, const FilaBackendDepthRangeData* depthRange) {
    if (!params || !depthRange) {
        return;
    }
    params->impl.depthRange = toDepthRange(*depthRange);
}

bool FilaBackendRenderPassParams_getDepthRange(
        const FilaBackendRenderPassParams* params, FilaBackendDepthRangeData* outDepthRange) {
    if (!params || !outDepthRange) {
        return false;
    }
    *outDepthRange = toDepthRangeData(params->impl.depthRange);
    return true;
}

void FilaBackendRenderPassParams_setClearColor(
        FilaBackendRenderPassParams* params, float r, float g, float b, float a) {
    if (!params) {
        return;
    }
    params->impl.clearColor[0] = r;
    params->impl.clearColor[1] = g;
    params->impl.clearColor[2] = b;
    params->impl.clearColor[3] = a;
}

bool FilaBackendRenderPassParams_getClearColor(
        const FilaBackendRenderPassParams* params, float outColor[4]) {
    if (!params || !outColor) {
        return false;
    }
    outColor[0] = params->impl.clearColor[0];
    outColor[1] = params->impl.clearColor[1];
    outColor[2] = params->impl.clearColor[2];
    outColor[3] = params->impl.clearColor[3];
    return true;
}

void FilaBackendRenderPassParams_setClearDepth(FilaBackendRenderPassParams* params, double clearDepth) {
    if (!params) {
        return;
    }
    params->impl.clearDepth = clearDepth;
}

double FilaBackendRenderPassParams_getClearDepth(const FilaBackendRenderPassParams* params) {
    if (!params) {
        return 0.0;
    }
    return params->impl.clearDepth;
}

void FilaBackendRenderPassParams_setClearStencil(
        FilaBackendRenderPassParams* params, uint32_t clearStencil) {
    if (!params) {
        return;
    }
    params->impl.clearStencil = clearStencil;
}

uint32_t FilaBackendRenderPassParams_getClearStencil(const FilaBackendRenderPassParams* params) {
    if (!params) {
        return 0;
    }
    return params->impl.clearStencil;
}

void FilaBackendRenderPassParams_setSubpassMask(FilaBackendRenderPassParams* params, uint16_t subpassMask) {
    if (!params) {
        return;
    }
    params->impl.subpassMask = subpassMask;
}

uint16_t FilaBackendRenderPassParams_getSubpassMask(const FilaBackendRenderPassParams* params) {
    if (!params) {
        return 0;
    }
    return params->impl.subpassMask;
}

void FilaBackendRenderPassParams_setReadOnlyDepthStencil(
        FilaBackendRenderPassParams* params, uint16_t readOnlyDepthStencil) {
    if (!params) {
        return;
    }
    params->impl.readOnlyDepthStencil = readOnlyDepthStencil;
}

void FilaBackendRenderPassParams_setReadOnlyDepthStencilFlags(
        FilaBackendRenderPassParams* params, bool readOnlyDepth, bool readOnlyStencil) {
    if (!params) {
        return;
    }
    uint16_t mask = 0;
    if (readOnlyDepth) {
        mask |= FILA_BACKEND_RENDER_PASS_READONLY_DEPTH;
    }
    if (readOnlyStencil) {
        mask |= FILA_BACKEND_RENDER_PASS_READONLY_STENCIL;
    }
    params->impl.readOnlyDepthStencil = mask;
}

uint16_t FilaBackendRenderPassParams_getReadOnlyDepthStencil(const FilaBackendRenderPassParams* params) {
    if (!params) {
        return 0;
    }
    return params->impl.readOnlyDepthStencil;
}

void FilaBackendRenderPassParamsData_setDefaults(FilaBackendRenderPassParamsData* data) {
    if (!data) {
        return;
    }
    *data = toRenderPassParamsData(filament::backend::RenderPassParams{});
}

void FilaBackendRenderPassParamsData_setReadOnlyDepthStencilFlags(
        FilaBackendRenderPassParamsData* data, bool readOnlyDepth, bool readOnlyStencil) {
    if (!data) {
        return;
    }
    uint16_t mask = 0;
    if (readOnlyDepth) {
        mask |= FILA_BACKEND_RENDER_PASS_READONLY_DEPTH;
    }
    if (readOnlyStencil) {
        mask |= FILA_BACKEND_RENDER_PASS_READONLY_STENCIL;
    }
    data->readOnlyDepthStencil = mask;
}

void FilaBackendRenderPassParams_setClearTargets(
        FilaBackendRenderPassParams* params, FilaBackendTargetBufferFlags clearTargets) {
    if (!params) {
        return;
    }
    params->impl.flags.clear = static_cast<filament::backend::TargetBufferFlags>(clearTargets);
}

FilaBackendTargetBufferFlags FilaBackendRenderPassParams_getClearTargets(
        const FilaBackendRenderPassParams* params) {
    if (!params) {
        return FILA_BACKEND_TARGET_BUFFER_NONE;
    }
    return static_cast<FilaBackendTargetBufferFlags>(params->impl.flags.clear);
}

}


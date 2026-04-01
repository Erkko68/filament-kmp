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

}


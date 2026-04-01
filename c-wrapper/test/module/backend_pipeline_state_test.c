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

    FilaBackendPipelineState_destroy(state);
}


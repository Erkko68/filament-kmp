#ifndef FILAMENT_C_BACKEND_PIPELINESTATE_H
#define FILAMENT_C_BACKEND_PIPELINESTATE_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "DriverEnums.h"
#include "Handle.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaBackendPipelineState FilaBackendPipelineState;

FilaBackendPipelineState* FilaBackendPipelineState_create(void);
void FilaBackendPipelineState_destroy(FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setProgramHandle(
    FilaBackendPipelineState* pipelineState, FilaBackendProgramHandleId programHandle);
FilaBackendProgramHandleId FilaBackendPipelineState_getProgramHandle(
    const FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setVertexBufferInfoHandle(
    FilaBackendPipelineState* pipelineState,
    FilaBackendVertexBufferInfoHandleId vertexBufferInfoHandle);
FilaBackendVertexBufferInfoHandleId FilaBackendPipelineState_getVertexBufferInfoHandle(
    const FilaBackendPipelineState* pipelineState);

bool FilaBackendPipelineState_setDescriptorSetLayoutHandleAt(
    FilaBackendPipelineState* pipelineState, size_t index,
    FilaBackendDescriptorSetLayoutHandleId handle);
bool FilaBackendPipelineState_getDescriptorSetLayoutHandleAt(
    const FilaBackendPipelineState* pipelineState, size_t index,
    FilaBackendDescriptorSetLayoutHandleId* outHandle);

void FilaBackendPipelineState_setPrimitiveType(
    FilaBackendPipelineState* pipelineState, FilaBackendPrimitiveType primitiveType);
FilaBackendPrimitiveType FilaBackendPipelineState_getPrimitiveType(
    const FilaBackendPipelineState* pipelineState);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PIPELINESTATE_H


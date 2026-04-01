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

typedef struct FilaBackendRasterStateData {
    FilaBackendCullingMode culling;
    FilaBackendBlendEquation blendEquationRGB;
    FilaBackendBlendEquation blendEquationAlpha;
    FilaBackendBlendFunction blendFunctionSrcRGB;
    FilaBackendBlendFunction blendFunctionSrcAlpha;
    FilaBackendBlendFunction blendFunctionDstRGB;
    FilaBackendBlendFunction blendFunctionDstAlpha;
    bool depthWrite;
    FilaBackendSamplerCompareFunc depthFunc;
    bool colorWrite;
    bool alphaToCoverage;
    bool inverseFrontFaces;
    bool depthClamp;
} FilaBackendRasterStateData;

typedef struct FilaBackendStencilOperationsData {
    FilaBackendSamplerCompareFunc stencilFunc;
    FilaBackendStencilOperation stencilOpStencilFail;
    FilaBackendStencilOperation stencilOpDepthFail;
    FilaBackendStencilOperation stencilOpDepthStencilPass;
    uint8_t ref;
    uint8_t readMask;
    uint8_t writeMask;
} FilaBackendStencilOperationsData;

typedef struct FilaBackendStencilStateData {
    FilaBackendStencilOperationsData front;
    FilaBackendStencilOperationsData back;
    bool stencilWrite;
} FilaBackendStencilStateData;

typedef struct FilaBackendPolygonOffsetData {
    float slope;
    float constant;
} FilaBackendPolygonOffsetData;

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

void FilaBackendPipelineState_setRasterState(
    FilaBackendPipelineState* pipelineState, const FilaBackendRasterStateData* rasterState);
bool FilaBackendPipelineState_getRasterState(
    const FilaBackendPipelineState* pipelineState, FilaBackendRasterStateData* outRasterState);

void FilaBackendPipelineState_setStencilState(
    FilaBackendPipelineState* pipelineState, const FilaBackendStencilStateData* stencilState);
bool FilaBackendPipelineState_getStencilState(
    const FilaBackendPipelineState* pipelineState, FilaBackendStencilStateData* outStencilState);

bool FilaBackendPipelineState_setStencilOperationsForFace(
    FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
    const FilaBackendStencilOperationsData* operations);
bool FilaBackendPipelineState_getStencilOperationsForFace(
    const FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
    FilaBackendStencilOperationsData* outOperations);

void FilaBackendPipelineState_setPolygonOffset(
    FilaBackendPipelineState* pipelineState, const FilaBackendPolygonOffsetData* polygonOffset);
bool FilaBackendPipelineState_getPolygonOffset(
    const FilaBackendPipelineState* pipelineState, FilaBackendPolygonOffsetData* outPolygonOffset);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PIPELINESTATE_H


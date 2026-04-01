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
typedef struct FilaBackendRenderPassParams FilaBackendRenderPassParams;

#define FILA_BACKEND_RENDER_PASS_READONLY_DEPTH (1u << 0)
#define FILA_BACKEND_RENDER_PASS_READONLY_STENCIL (1u << 1)

typedef struct FilaBackendViewportData {
    int32_t left;
    int32_t bottom;
    uint32_t width;
    uint32_t height;
} FilaBackendViewportData;

typedef struct FilaBackendDepthRangeData {
    float near;
    float far;
} FilaBackendDepthRangeData;

typedef struct FilaBackendRenderPassFlagsData {
    FilaBackendTargetBufferFlags clear;
    FilaBackendTargetBufferFlags discardStart;
    FilaBackendTargetBufferFlags discardEnd;
} FilaBackendRenderPassFlagsData;

typedef struct FilaBackendRenderPassParamsData {
    FilaBackendRenderPassFlagsData flags;
    FilaBackendViewportData viewport;
    FilaBackendDepthRangeData depthRange;
    float clearColor[4];
    double clearDepth;
    uint32_t clearStencil;
    uint16_t subpassMask;
    uint16_t readOnlyDepthStencil;
} FilaBackendRenderPassParamsData;

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
uint32_t FilaBackendPipelineState_getDescriptorSetLayoutHandles(
    const FilaBackendPipelineState* pipelineState, FilaBackendDescriptorSetLayoutHandleId* outHandles,
    uint32_t outCount);

void FilaBackendPipelineState_setPrimitiveType(
    FilaBackendPipelineState* pipelineState, FilaBackendPrimitiveType primitiveType);
FilaBackendPrimitiveType FilaBackendPipelineState_getPrimitiveType(
    const FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setDefaults(FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setCullingMode(
    FilaBackendPipelineState* pipelineState, FilaBackendCullingMode cullingMode);
FilaBackendCullingMode FilaBackendPipelineState_getCullingMode(
    const FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setDepthFunc(
    FilaBackendPipelineState* pipelineState, FilaBackendSamplerCompareFunc depthFunc);
FilaBackendSamplerCompareFunc FilaBackendPipelineState_getDepthFunc(
    const FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setDepthWrite(FilaBackendPipelineState* pipelineState, bool depthWrite);
bool FilaBackendPipelineState_getDepthWrite(const FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setColorWrite(FilaBackendPipelineState* pipelineState, bool colorWrite);
bool FilaBackendPipelineState_getColorWrite(const FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setAlphaToCoverage(
    FilaBackendPipelineState* pipelineState, bool alphaToCoverage);
bool FilaBackendPipelineState_getAlphaToCoverage(const FilaBackendPipelineState* pipelineState);
bool FilaBackendPipelineState_hasBlending(const FilaBackendPipelineState* pipelineState);
void FilaBackendPipelineState_setInverseFrontFaces(
    FilaBackendPipelineState* pipelineState, bool inverseFrontFaces);
bool FilaBackendPipelineState_getInverseFrontFaces(const FilaBackendPipelineState* pipelineState);
void FilaBackendPipelineState_setDepthClamp(FilaBackendPipelineState* pipelineState, bool depthClamp);
bool FilaBackendPipelineState_getDepthClamp(const FilaBackendPipelineState* pipelineState);

void FilaBackendPipelineState_setBlendState(
    FilaBackendPipelineState* pipelineState,
    FilaBackendBlendEquation blendEquationRGB,
    FilaBackendBlendEquation blendEquationAlpha,
    FilaBackendBlendFunction blendFunctionSrcRGB,
    FilaBackendBlendFunction blendFunctionSrcAlpha,
    FilaBackendBlendFunction blendFunctionDstRGB,
    FilaBackendBlendFunction blendFunctionDstAlpha);
bool FilaBackendPipelineState_getBlendState(
    const FilaBackendPipelineState* pipelineState,
    FilaBackendBlendEquation* outBlendEquationRGB,
    FilaBackendBlendEquation* outBlendEquationAlpha,
    FilaBackendBlendFunction* outBlendFunctionSrcRGB,
    FilaBackendBlendFunction* outBlendFunctionSrcAlpha,
    FilaBackendBlendFunction* outBlendFunctionDstRGB,
    FilaBackendBlendFunction* outBlendFunctionDstAlpha);

void FilaBackendPipelineState_clearDescriptorSetLayoutHandles(FilaBackendPipelineState* pipelineState);
uint32_t FilaBackendPipelineState_setDescriptorSetLayoutHandles(
    FilaBackendPipelineState* pipelineState, const FilaBackendDescriptorSetLayoutHandleId* handles,
    uint32_t count);

void FilaBackendPipelineState_setRasterState(
    FilaBackendPipelineState* pipelineState, const FilaBackendRasterStateData* rasterState);
bool FilaBackendPipelineState_getRasterState(
    const FilaBackendPipelineState* pipelineState, FilaBackendRasterStateData* outRasterState);

void FilaBackendPipelineState_setStencilState(
    FilaBackendPipelineState* pipelineState, const FilaBackendStencilStateData* stencilState);
bool FilaBackendPipelineState_getStencilState(
    const FilaBackendPipelineState* pipelineState, FilaBackendStencilStateData* outStencilState);

void FilaBackendPipelineState_setStencilWrite(FilaBackendPipelineState* pipelineState, bool stencilWrite);
bool FilaBackendPipelineState_getStencilWrite(const FilaBackendPipelineState* pipelineState);

bool FilaBackendPipelineState_setStencilOperationsForFace(
    FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
    const FilaBackendStencilOperationsData* operations);
bool FilaBackendPipelineState_getStencilOperationsForFace(
    const FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
    FilaBackendStencilOperationsData* outOperations);
bool FilaBackendPipelineState_setStencilRefMaskForFace(
    FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
    uint8_t ref, uint8_t readMask, uint8_t writeMask);
bool FilaBackendPipelineState_getStencilRefMaskForFace(
    const FilaBackendPipelineState* pipelineState, FilaBackendStencilFace face,
    uint8_t* outRef, uint8_t* outReadMask, uint8_t* outWriteMask);

void FilaBackendPipelineState_setPolygonOffset(
    FilaBackendPipelineState* pipelineState, const FilaBackendPolygonOffsetData* polygonOffset);
bool FilaBackendPipelineState_getPolygonOffset(
    const FilaBackendPipelineState* pipelineState, FilaBackendPolygonOffsetData* outPolygonOffset);

int32_t FilaBackendViewport_right(const FilaBackendViewportData* viewport);
int32_t FilaBackendViewport_top(const FilaBackendViewportData* viewport);

FilaBackendRenderPassParams* FilaBackendRenderPassParams_create(void);
void FilaBackendRenderPassParams_destroy(FilaBackendRenderPassParams* params);

void FilaBackendRenderPassParams_set(
    FilaBackendRenderPassParams* params, const FilaBackendRenderPassParamsData* data);
bool FilaBackendRenderPassParams_get(
    const FilaBackendRenderPassParams* params, FilaBackendRenderPassParamsData* outData);

void FilaBackendRenderPassParams_setFlags(
    FilaBackendRenderPassParams* params, const FilaBackendRenderPassFlagsData* flags);
bool FilaBackendRenderPassParams_getFlags(
    const FilaBackendRenderPassParams* params, FilaBackendRenderPassFlagsData* outFlags);

void FilaBackendRenderPassParams_setViewport(
    FilaBackendRenderPassParams* params, const FilaBackendViewportData* viewport);
bool FilaBackendRenderPassParams_getViewport(
    const FilaBackendRenderPassParams* params, FilaBackendViewportData* outViewport);

void FilaBackendRenderPassParams_setDepthRange(
    FilaBackendRenderPassParams* params, const FilaBackendDepthRangeData* depthRange);
bool FilaBackendRenderPassParams_getDepthRange(
    const FilaBackendRenderPassParams* params, FilaBackendDepthRangeData* outDepthRange);

void FilaBackendRenderPassParams_setClearColor(
    FilaBackendRenderPassParams* params, float r, float g, float b, float a);
bool FilaBackendRenderPassParams_getClearColor(
    const FilaBackendRenderPassParams* params, float outColor[4]);

void FilaBackendRenderPassParams_setClearDepth(FilaBackendRenderPassParams* params, double clearDepth);
double FilaBackendRenderPassParams_getClearDepth(const FilaBackendRenderPassParams* params);

void FilaBackendRenderPassParams_setClearStencil(
    FilaBackendRenderPassParams* params, uint32_t clearStencil);
uint32_t FilaBackendRenderPassParams_getClearStencil(const FilaBackendRenderPassParams* params);

void FilaBackendRenderPassParams_setSubpassMask(FilaBackendRenderPassParams* params, uint16_t subpassMask);
uint16_t FilaBackendRenderPassParams_getSubpassMask(const FilaBackendRenderPassParams* params);

void FilaBackendRenderPassParams_setReadOnlyDepthStencil(
    FilaBackendRenderPassParams* params, uint16_t readOnlyDepthStencil);
void FilaBackendRenderPassParams_setReadOnlyDepthStencilFlags(
    FilaBackendRenderPassParams* params, bool readOnlyDepth, bool readOnlyStencil);
uint16_t FilaBackendRenderPassParams_getReadOnlyDepthStencil(const FilaBackendRenderPassParams* params);

void FilaBackendRenderPassParamsData_setDefaults(FilaBackendRenderPassParamsData* data);
void FilaBackendRenderPassParamsData_setReadOnlyDepthStencilFlags(
    FilaBackendRenderPassParamsData* data, bool readOnlyDepth, bool readOnlyStencil);

void FilaBackendRenderPassParams_setClearTargets(
    FilaBackendRenderPassParams* params, FilaBackendTargetBufferFlags clearTargets);
FilaBackendTargetBufferFlags FilaBackendRenderPassParams_getClearTargets(
    const FilaBackendRenderPassParams* params);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PIPELINESTATE_H


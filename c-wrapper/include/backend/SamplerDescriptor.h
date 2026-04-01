#ifndef FILAMENT_C_BACKEND_SAMPLERDESCRIPTOR_H
#define FILAMENT_C_BACKEND_SAMPLERDESCRIPTOR_H

#include <stdbool.h>
#include <stdint.h>

#include "DriverEnums.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaSamplerDescriptor FilaSamplerDescriptor;


typedef enum FilaBackendSamplerWrapMode {
    FILA_BACKEND_SAMPLER_WRAP_CLAMP_TO_EDGE = 0,
    FILA_BACKEND_SAMPLER_WRAP_REPEAT = 1,
    FILA_BACKEND_SAMPLER_WRAP_MIRRORED_REPEAT = 2,
} FilaBackendSamplerWrapMode;

typedef enum FilaBackendSamplerMinFilter {
    FILA_BACKEND_SAMPLER_MIN_NEAREST = 0,
    FILA_BACKEND_SAMPLER_MIN_LINEAR = 1,
    FILA_BACKEND_SAMPLER_MIN_NEAREST_MIPMAP_NEAREST = 2,
    FILA_BACKEND_SAMPLER_MIN_LINEAR_MIPMAP_NEAREST = 3,
    FILA_BACKEND_SAMPLER_MIN_NEAREST_MIPMAP_LINEAR = 4,
    FILA_BACKEND_SAMPLER_MIN_LINEAR_MIPMAP_LINEAR = 5,
} FilaBackendSamplerMinFilter;

typedef enum FilaBackendSamplerMagFilter {
    FILA_BACKEND_SAMPLER_MAG_NEAREST = 0,
    FILA_BACKEND_SAMPLER_MAG_LINEAR = 1,
} FilaBackendSamplerMagFilter;

typedef enum FilaBackendSamplerCompareMode {
    FILA_BACKEND_SAMPLER_COMPARE_NONE = 0,
    FILA_BACKEND_SAMPLER_COMPARE_TO_TEXTURE = 1,
} FilaBackendSamplerCompareMode;

typedef enum FilaBackendSamplerCompareFunc {
    FILA_BACKEND_SAMPLER_COMPARE_LE = 0,
    FILA_BACKEND_SAMPLER_COMPARE_GE = 1,
    FILA_BACKEND_SAMPLER_COMPARE_L = 2,
    FILA_BACKEND_SAMPLER_COMPARE_G = 3,
    FILA_BACKEND_SAMPLER_COMPARE_E = 4,
    FILA_BACKEND_SAMPLER_COMPARE_NE = 5,
    FILA_BACKEND_SAMPLER_COMPARE_A = 6,
    FILA_BACKEND_SAMPLER_COMPARE_N = 7,
} FilaBackendSamplerCompareFunc;

typedef struct FilaBackendSamplerParams {
    FilaBackendSamplerMagFilter filterMag;
    FilaBackendSamplerMinFilter filterMin;
    FilaBackendSamplerWrapMode wrapS;
    FilaBackendSamplerWrapMode wrapT;
    FilaBackendSamplerWrapMode wrapR;
    uint8_t anisotropyLog2;
    FilaBackendSamplerCompareMode compareMode;
    FilaBackendSamplerCompareFunc compareFunc;
} FilaBackendSamplerParams;

FilaSamplerDescriptor* FilaSamplerDescriptor_create(void);
void FilaSamplerDescriptor_destroy(FilaSamplerDescriptor* desc);

void FilaSamplerDescriptor_setTextureHandleId(FilaSamplerDescriptor* desc, uint32_t handleId);
uint32_t FilaSamplerDescriptor_getTextureHandleId(const FilaSamplerDescriptor* desc);
bool FilaSamplerDescriptor_hasTextureHandle(const FilaSamplerDescriptor* desc);
void FilaSamplerDescriptor_clearTextureHandle(FilaSamplerDescriptor* desc);

void FilaSamplerParams_setDefaults(FilaBackendSamplerParams* params);
bool FilaSamplerParams_isFiltered(const FilaBackendSamplerParams* params);

void FilaSamplerDescriptor_setParams(FilaSamplerDescriptor* desc, const FilaBackendSamplerParams* params);
bool FilaSamplerDescriptor_getParams(const FilaSamplerDescriptor* desc, FilaBackendSamplerParams* outParams);
bool FilaSamplerDescriptor_isFiltered(const FilaSamplerDescriptor* desc);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_SAMPLERDESCRIPTOR_H


#ifndef FILAMENT_C_BACKEND_TARGETBUFFERINFO_H
#define FILAMENT_C_BACKEND_TARGETBUFFERINFO_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "DriverEnums.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaBackendTargetBufferInfo FilaBackendTargetBufferInfo;
typedef struct FilaBackendMRT FilaBackendMRT;

typedef struct FilaBackendTargetBufferInfoData {
    uint32_t textureHandleId;
    uint8_t level;
    uint16_t layer;
} FilaBackendTargetBufferInfoData;

FilaBackendTargetBufferInfo* FilaBackendTargetBufferInfo_create(void);
FilaBackendTargetBufferInfo* FilaBackendTargetBufferInfo_createWithHandle(uint32_t textureHandleId);
FilaBackendTargetBufferInfo* FilaBackendTargetBufferInfo_createWithHandleLevel(
    uint32_t textureHandleId, uint8_t level);
FilaBackendTargetBufferInfo* FilaBackendTargetBufferInfo_createWithHandleLevelLayer(
    uint32_t textureHandleId, uint8_t level, uint16_t layer);
void FilaBackendTargetBufferInfo_destroy(FilaBackendTargetBufferInfo* info);

void FilaBackendTargetBufferInfo_set(FilaBackendTargetBufferInfo* info,
    const FilaBackendTargetBufferInfoData* data);
bool FilaBackendTargetBufferInfo_get(const FilaBackendTargetBufferInfo* info,
    FilaBackendTargetBufferInfoData* outData);

FilaBackendMRT* FilaBackendMRT_create(void);
void FilaBackendMRT_destroy(FilaBackendMRT* mrt);

uint8_t FilaBackendMRT_getMinSupportedRenderTargetCount(void);
uint8_t FilaBackendMRT_getMaxSupportedRenderTargetCount(void);

bool FilaBackendMRT_setTargetBufferAt(FilaBackendMRT* mrt, size_t index,
    const FilaBackendTargetBufferInfoData* data);
bool FilaBackendMRT_getTargetBufferAt(const FilaBackendMRT* mrt, size_t index,
    FilaBackendTargetBufferInfoData* outData);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_TARGETBUFFERINFO_H


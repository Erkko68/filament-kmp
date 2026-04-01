#ifndef FILAMENT_C_BACKEND_HANDLE_H
#define FILAMENT_C_BACKEND_HANDLE_H

#include <stdbool.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Matches filament::backend::HandleBase::nullid.
#define FILA_BACKEND_HANDLE_NULL 0xFFFFFFFFu

typedef uint32_t FilaBackendHandleId;

typedef FilaBackendHandleId FilaBackendBufferObjectHandleId;
typedef FilaBackendHandleId FilaBackendFenceHandleId;
typedef FilaBackendHandleId FilaBackendIndexBufferHandleId;
typedef FilaBackendHandleId FilaBackendProgramHandleId;
typedef FilaBackendHandleId FilaBackendRenderPrimitiveHandleId;
typedef FilaBackendHandleId FilaBackendRenderTargetHandleId;
typedef FilaBackendHandleId FilaBackendStreamHandleId;
typedef FilaBackendHandleId FilaBackendSwapChainHandleId;
typedef FilaBackendHandleId FilaBackendSyncHandleId;
typedef FilaBackendHandleId FilaBackendTextureHandleId;
typedef FilaBackendHandleId FilaBackendTimerQueryHandleId;
typedef FilaBackendHandleId FilaBackendVertexBufferHandleId;
typedef FilaBackendHandleId FilaBackendVertexBufferInfoHandleId;
typedef FilaBackendHandleId FilaBackendDescriptorSetLayoutHandleId;
typedef FilaBackendHandleId FilaBackendDescriptorSetHandleId;
typedef FilaBackendHandleId FilaBackendMemoryMappedBufferHandleId;

bool FilaBackendHandle_isValid(FilaBackendHandleId handleId);
void FilaBackendHandle_clear(FilaBackendHandleId* handleId);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_HANDLE_H


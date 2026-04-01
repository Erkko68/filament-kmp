#ifndef FILAMENT_C_BACKEND_ACQUIREDIMAGE_H
#define FILAMENT_C_BACKEND_ACQUIREDIMAGE_H

#include <stdbool.h>

#include "CallbackHandler.h"
#include "DriverEnums.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaBackendAcquiredImage FilaBackendAcquiredImage;


typedef struct FilaBackendAcquiredImageData {
    void* image;
    FilaBackendStreamCallback callback;
    void* userData;
    FilaCallbackHandler* handler;
} FilaBackendAcquiredImageData;

FilaBackendAcquiredImage* FilaBackendAcquiredImage_create(void);
void FilaBackendAcquiredImage_destroy(FilaBackendAcquiredImage* acquiredImage);

bool FilaBackendAcquiredImage_set(
    FilaBackendAcquiredImage* acquiredImage, const FilaBackendAcquiredImageData* data);
bool FilaBackendAcquiredImage_get(
    const FilaBackendAcquiredImage* acquiredImage, FilaBackendAcquiredImageData* outData);
void FilaBackendAcquiredImage_reset(FilaBackendAcquiredImage* acquiredImage);

bool FilaBackendAcquiredImage_invokeReleaseCallback(const FilaBackendAcquiredImage* acquiredImage);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_ACQUIREDIMAGE_H


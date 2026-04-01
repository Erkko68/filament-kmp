#include "backend/AcquiredImage.h"

#include <stddef.h>

static void backend_acquired_image_release(void* image, void* userData) {
    (void) image;
    (void) userData;
}

void backend_acquired_image_test(void) {
    FilaBackendAcquiredImage* acquiredImage = FilaBackendAcquiredImage_create();

    FilaBackendAcquiredImageData data = {
        .image = (void*) 0x1,
        .callback = backend_acquired_image_release,
        .userData = (void*) 0x2,
        .handler = NULL,
    };

    FilaBackendAcquiredImage_set(acquiredImage, &data);

    FilaBackendAcquiredImageData outData;
    FilaBackendAcquiredImage_get(acquiredImage, &outData);

    FilaBackendAcquiredImage_invokeReleaseCallback(acquiredImage);
    FilaBackendAcquiredImage_reset(acquiredImage);

    FilaBackendAcquiredImage_destroy(acquiredImage);
}


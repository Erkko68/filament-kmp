#include "../../include/backend/AcquiredImage.h"

#include <backend/AcquiredImage.h>
#include <backend/CallbackHandler.h>

struct FilaBackendAcquiredImage {
    filament::backend::AcquiredImage impl;
};

namespace {
filament::backend::AcquiredImage toCpp(const FilaBackendAcquiredImageData& in) {
    filament::backend::AcquiredImage out;
    out.image = in.image;
    out.callback = static_cast<filament::backend::StreamCallback>(in.callback);
    out.userData = in.userData;
    out.handler = reinterpret_cast<filament::backend::CallbackHandler*>(in.handler);
    return out;
}

FilaBackendAcquiredImageData toC(const filament::backend::AcquiredImage& in) {
    FilaBackendAcquiredImageData out;
    out.image = in.image;
    out.callback = static_cast<FilaBackendStreamCallback>(in.callback);
    out.userData = in.userData;
    out.handler = reinterpret_cast<FilaCallbackHandler*>(in.handler);
    return out;
}
} // namespace

extern "C" {

FilaBackendAcquiredImage* FilaBackendAcquiredImage_create(void) {
    return new FilaBackendAcquiredImage;
}

void FilaBackendAcquiredImage_destroy(FilaBackendAcquiredImage* acquiredImage) {
    delete acquiredImage;
}

bool FilaBackendAcquiredImage_set(
        FilaBackendAcquiredImage* acquiredImage, const FilaBackendAcquiredImageData* data) {
    if (!acquiredImage || !data) {
        return false;
    }
    acquiredImage->impl = toCpp(*data);
    return true;
}

bool FilaBackendAcquiredImage_get(
        const FilaBackendAcquiredImage* acquiredImage, FilaBackendAcquiredImageData* outData) {
    if (!acquiredImage || !outData) {
        return false;
    }
    *outData = toC(acquiredImage->impl);
    return true;
}

void FilaBackendAcquiredImage_reset(FilaBackendAcquiredImage* acquiredImage) {
    if (!acquiredImage) {
        return;
    }
    acquiredImage->impl = filament::backend::AcquiredImage{};
}

bool FilaBackendAcquiredImage_invokeReleaseCallback(const FilaBackendAcquiredImage* acquiredImage) {
    if (!acquiredImage || !acquiredImage->impl.callback) {
        return false;
    }
    acquiredImage->impl.callback(acquiredImage->impl.image, acquiredImage->impl.userData);
    return true;
}

}


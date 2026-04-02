#include <filament/Sync.h>

#include <memory>
#include <new>

#include "../../include/filament/Sync.h"

namespace {

struct SyncCallbackPayload {
    FilaSyncCallback callback;
    FilaSyncTokenCallback tokenCallback;
    void* userData;
    uintptr_t userToken;
};

void onExternalHandleReady(filament::backend::Platform::Sync* sync, void* userData) {
    std::unique_ptr<SyncCallbackPayload> payload(static_cast<SyncCallbackPayload*>(userData));
    if (!payload || !payload->callback) {
        if (payload && payload->tokenCallback) {
            payload->tokenCallback(reinterpret_cast<FilaSync*>(sync), payload->userToken);
        }
        return;
    }
    payload->callback(reinterpret_cast<FilaSync*>(sync), payload->userData);
}

} // namespace

extern "C" {

bool FilaSync_getExternalHandle(FilaSync* sync, FilaSyncCallback callback, void* userData) {
    if (!sync || !callback) {
        return false;
    }
    auto* payload = new (std::nothrow) SyncCallbackPayload{callback, nullptr, userData, 0u};
    if (!payload) {
        return false;
    }
    auto cppSync = reinterpret_cast<filament::Sync*>(sync);
    cppSync->getExternalHandle(nullptr, onExternalHandleReady, payload);
    return true;
}

bool FilaSync_getExternalHandleWithToken(FilaSync* sync, FilaSyncTokenCallback callback, uintptr_t userToken) {
    if (!sync || !callback) {
        return false;
    }
    auto* payload = new (std::nothrow) SyncCallbackPayload{nullptr, callback, nullptr, userToken};
    if (!payload) {
        return false;
    }
    auto cppSync = reinterpret_cast<filament::Sync*>(sync);
    cppSync->getExternalHandle(nullptr, onExternalHandleReady, payload);
    return true;
}

} // extern "C"


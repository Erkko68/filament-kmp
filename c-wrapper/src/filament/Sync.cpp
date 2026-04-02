#include <filament/Sync.h>

#include <memory>
#include <new>

#include "../../include/filament/Sync.h"

namespace {

struct SyncCallbackPayload {
    FilaSyncCallback callback;
    void* userData;
};

void onExternalHandleReady(filament::backend::Platform::Sync* sync, void* userData) {
    std::unique_ptr<SyncCallbackPayload> payload(static_cast<SyncCallbackPayload*>(userData));
    if (!payload || !payload->callback) {
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
    auto* payload = new (std::nothrow) SyncCallbackPayload{callback, userData};
    if (!payload) {
        return false;
    }
    auto cppSync = reinterpret_cast<filament::Sync*>(sync);
    cppSync->getExternalHandle(nullptr, onExternalHandleReady, payload);
    return true;
}

} // extern "C"


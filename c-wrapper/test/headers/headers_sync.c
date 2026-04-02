#include "filament/Sync.h"

void test_headers_sync(void) {
    FilaSync* sync = (FilaSync*)0;
    FilaSyncCallback callback = (FilaSyncCallback)0;
    FilaSyncTokenCallback tokenCallback = (FilaSyncTokenCallback)0;
    (void)FilaSync_getExternalHandle(sync, callback, (void*)0);
    (void)FilaSync_getExternalHandleWithToken(sync, tokenCallback, (uintptr_t)0);
}


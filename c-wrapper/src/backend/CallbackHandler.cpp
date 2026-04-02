#include "filament/BufferDescriptor.h"

#include <backend/CallbackHandler.h>

namespace {
class CCallbackHandler final : public filament::backend::CallbackHandler {
public:
    CCallbackHandler(FilaCallbackHandlerDispatcher dispatcher, void* handlerUser)
            : mDispatcher(dispatcher), mTokenDispatcher(nullptr),
              mHandlerUser(handlerUser), mHandlerUserToken(0u) {
    }

    CCallbackHandler(FilaCallbackHandlerDispatcherToken dispatcher, uintptr_t handlerUserToken)
            : mDispatcher(nullptr), mTokenDispatcher(dispatcher),
              mHandlerUser(nullptr), mHandlerUserToken(handlerUserToken) {
    }

    void post(void* user, Callback callback) override {
        if (!callback) {
            return;
        }
        if (mDispatcher) {
            mDispatcher(user, callback, mHandlerUser);
            return;
        }
        if (mTokenDispatcher) {
            mTokenDispatcher(reinterpret_cast<uintptr_t>(user), callback, mHandlerUserToken);
        }
    }

private:
    FilaCallbackHandlerDispatcher mDispatcher;
    FilaCallbackHandlerDispatcherToken mTokenDispatcher;
    void* mHandlerUser;
    uintptr_t mHandlerUserToken;
};
} // namespace

extern "C" {

FilaCallbackHandler* FilaCallbackHandler_create(
        FilaCallbackHandlerDispatcher dispatcher,
        void* handlerUser) {
    if (!dispatcher) {
        return nullptr;
    }
    auto handler = new FilaCallbackHandler;
    handler->impl = new CCallbackHandler(dispatcher, handlerUser);
    return handler;
}

FilaCallbackHandler* FilaCallbackHandler_createWithToken(
        FilaCallbackHandlerDispatcherToken dispatcher,
        uintptr_t handlerUserToken) {
    if (!dispatcher) {
        return nullptr;
    }
    auto handler = new FilaCallbackHandler;
    handler->impl = new CCallbackHandler(dispatcher, handlerUserToken);
    return handler;
}

void FilaCallbackHandler_destroy(FilaCallbackHandler* handler) {
    if (!handler) {
        return;
    }
    auto impl = static_cast<CCallbackHandler*>(handler->impl);
    delete impl;
    delete handler;
}

}


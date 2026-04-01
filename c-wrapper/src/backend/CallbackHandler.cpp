#include "filament/BufferDescriptor.h"

#include <backend/CallbackHandler.h>

namespace {
class CCallbackHandler final : public filament::backend::CallbackHandler {
public:
    CCallbackHandler(FilaCallbackHandlerDispatcher dispatcher, void* handlerUser)
            : mDispatcher(dispatcher), mHandlerUser(handlerUser) {
    }

    void post(void* user, Callback callback) override {
        if (!mDispatcher || !callback) {
            return;
        }
        mDispatcher(user, callback, mHandlerUser);
    }

private:
    FilaCallbackHandlerDispatcher mDispatcher;
    void* mHandlerUser;
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

void FilaCallbackHandler_destroy(FilaCallbackHandler* handler) {
    if (!handler) {
        return;
    }
    auto impl = static_cast<CCallbackHandler*>(handler->impl);
    delete impl;
    delete handler;
}

}


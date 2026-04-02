#include "filament/BufferDescriptor.h"
#include <backend/BufferDescriptor.h>

namespace {
struct CallbackPayload {
    FilaBufferReleaseCallback callback;
    FilaBufferReleaseTokenCallback tokenCallback;
    void* user;
    uintptr_t userToken;
};

void releasePayload(CallbackPayload* payload) {
    delete payload;
}

void buffer_release_trampoline(void* buffer, size_t size, void* user) {
    auto payload = static_cast<CallbackPayload*>(user);
    if (!payload) {
        return;
    }
    if (payload->callback) {
        payload->callback(buffer, size, payload->user);
    }
    if (payload->tokenCallback) {
        payload->tokenCallback(buffer, size, payload->userToken);
    }
    releasePayload(payload);
}

CallbackPayload* makePayload(
        FilaBufferReleaseCallback callback,
        void* user,
        FilaBufferReleaseTokenCallback tokenCallback,
        uintptr_t userToken) {
    if (!callback && !tokenCallback) {
        return nullptr;
    }
    auto payload = new CallbackPayload;
    payload->callback = callback;
    payload->tokenCallback = tokenCallback;
    payload->user = user;
    payload->userToken = userToken;
    return payload;
}
} // namespace


extern "C" {

FilaBufferDescriptor* FilaBufferDescriptor_create(const void* buffer, size_t size, FilaBufferReleaseCallback callback, void* user) {
    auto payload = makePayload(callback, user, nullptr, 0u);
    auto desc = new FilaBufferDescriptor;
    desc->impl = new filament::backend::BufferDescriptor(
        buffer, size, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = callback;
    desc->tokenCallback = nullptr;
    desc->user = user;
    desc->userToken = 0u;
    desc->handler = nullptr;
    desc->consumed = false;
    return desc;
}

FilaBufferDescriptor* FilaBufferDescriptor_createWithToken(
        const void* buffer, size_t size, FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    auto payload = makePayload(nullptr, nullptr, callback, userToken);
    auto desc = new FilaBufferDescriptor;
    desc->impl = new filament::backend::BufferDescriptor(
        buffer, size, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = nullptr;
    desc->tokenCallback = callback;
    desc->user = nullptr;
    desc->userToken = userToken;
    desc->handler = nullptr;
    desc->consumed = false;
    return desc;
}

FilaBufferDescriptor* FilaBufferDescriptor_createWithHandler(
        const void* buffer, size_t size, FilaCallbackHandler* handler,
        FilaBufferReleaseCallback callback, void* user) {
    auto payload = makePayload(callback, user, nullptr, 0u);
    auto desc = new FilaBufferDescriptor;
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    desc->impl = new filament::backend::BufferDescriptor(
        buffer, size, cppHandler, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = callback;
    desc->tokenCallback = nullptr;
    desc->user = user;
    desc->userToken = 0u;
    desc->handler = handler;
    desc->consumed = false;
    return desc;
}

FilaBufferDescriptor* FilaBufferDescriptor_createWithHandlerAndToken(
        const void* buffer, size_t size, FilaCallbackHandler* handler,
        FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    auto payload = makePayload(nullptr, nullptr, callback, userToken);
    auto desc = new FilaBufferDescriptor;
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    desc->impl = new filament::backend::BufferDescriptor(
        buffer, size, cppHandler, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = nullptr;
    desc->tokenCallback = callback;
    desc->user = nullptr;
    desc->userToken = userToken;
    desc->handler = handler;
    desc->consumed = false;
    return desc;
}

void FilaBufferDescriptor_setCallback(FilaBufferDescriptor* desc, FilaBufferReleaseCallback callback, void* user) {
    if (!desc || !desc->impl) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
    if (impl->hasCallback()) {
        auto oldPayload = static_cast<CallbackPayload*>(impl->getUser());
        releasePayload(oldPayload);
    }
    auto payload = makePayload(callback, user, nullptr, 0u);
    impl->setCallback(callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = callback;
    desc->tokenCallback = nullptr;
    desc->user = user;
    desc->userToken = 0u;
    desc->handler = nullptr;
}

void FilaBufferDescriptor_setCallbackWithToken(
        FilaBufferDescriptor* desc, FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    if (!desc || !desc->impl) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
    if (impl->hasCallback()) {
        auto oldPayload = static_cast<CallbackPayload*>(impl->getUser());
        releasePayload(oldPayload);
    }
    auto payload = makePayload(nullptr, nullptr, callback, userToken);
    impl->setCallback(callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = nullptr;
    desc->tokenCallback = callback;
    desc->user = nullptr;
    desc->userToken = userToken;
    desc->handler = nullptr;
}

void FilaBufferDescriptor_setCallbackWithHandler(
        FilaBufferDescriptor* desc, FilaCallbackHandler* handler,
        FilaBufferReleaseCallback callback, void* user) {
    if (!desc || !desc->impl) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
    if (impl->hasCallback()) {
        auto oldPayload = static_cast<CallbackPayload*>(impl->getUser());
        releasePayload(oldPayload);
    }
    auto payload = makePayload(callback, user, nullptr, 0u);
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    impl->setCallback(cppHandler, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = callback;
    desc->tokenCallback = nullptr;
    desc->user = user;
    desc->userToken = 0u;
    desc->handler = handler;
}

void FilaBufferDescriptor_setCallbackWithHandlerAndToken(
        FilaBufferDescriptor* desc, FilaCallbackHandler* handler,
        FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    if (!desc || !desc->impl) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
    if (impl->hasCallback()) {
        auto oldPayload = static_cast<CallbackPayload*>(impl->getUser());
        releasePayload(oldPayload);
    }
    auto payload = makePayload(nullptr, nullptr, callback, userToken);
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    impl->setCallback(cppHandler, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = nullptr;
    desc->tokenCallback = callback;
    desc->user = nullptr;
    desc->userToken = userToken;
    desc->handler = handler;
}

bool FilaBufferDescriptor_hasCallback(const FilaBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return false;
    }
    auto impl = reinterpret_cast<const filament::backend::BufferDescriptor*>(desc->impl);
    return impl->hasCallback();
}

FilaBufferReleaseCallback FilaBufferDescriptor_getCallback(const FilaBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return nullptr;
    }
    return desc->callback;
}

void* FilaBufferDescriptor_getUser(const FilaBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return nullptr;
    }
    return desc->user;
}

uintptr_t FilaBufferDescriptor_getUserToken(const FilaBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return 0u;
    }
    return desc->userToken;
}

FilaCallbackHandler* FilaBufferDescriptor_getHandler(const FilaBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return nullptr;
    }
    return desc->handler;
}

void FilaBufferDescriptor_destroy(FilaBufferDescriptor* desc) {
    if (desc) {
        if (desc->impl) {
            auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
            delete impl;
        }
        delete desc;
    }
}

const void* FilaBufferDescriptor_getBuffer(const FilaBufferDescriptor* desc) {
    if (!desc || !desc->impl) return nullptr;
    auto impl = reinterpret_cast<const filament::backend::BufferDescriptor*>(desc->impl);
    return impl->buffer;
}

size_t FilaBufferDescriptor_getSize(const FilaBufferDescriptor* desc) {
    if (!desc || !desc->impl) return 0;
    auto impl = reinterpret_cast<const filament::backend::BufferDescriptor*>(desc->impl);
    return impl->size;
}

} // extern "C"

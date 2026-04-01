#include "filament/BufferDescriptor.h"
#include <backend/BufferDescriptor.h>

namespace {
struct CallbackPayload {
    FilaBufferReleaseCallback callback;
    void* user;
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
    releasePayload(payload);
}

CallbackPayload* makePayload(FilaBufferReleaseCallback callback, void* user) {
    if (!callback) {
        return nullptr;
    }
    auto payload = new CallbackPayload;
    payload->callback = callback;
    payload->user = user;
    return payload;
}
} // namespace


extern "C" {

FilaBufferDescriptor* FilaBufferDescriptor_create(const void* buffer, size_t size, FilaBufferReleaseCallback callback, void* user) {
    auto payload = makePayload(callback, user);
    auto desc = new FilaBufferDescriptor;
    desc->impl = new filament::backend::BufferDescriptor(
        buffer, size, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = callback;
    desc->user = user;
    desc->handler = nullptr;
    return desc;
}

FilaBufferDescriptor* FilaBufferDescriptor_createWithHandler(
        const void* buffer, size_t size, FilaCallbackHandler* handler,
        FilaBufferReleaseCallback callback, void* user) {
    auto payload = makePayload(callback, user);
    auto desc = new FilaBufferDescriptor;
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    desc->impl = new filament::backend::BufferDescriptor(
        buffer, size, cppHandler, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = callback;
    desc->user = user;
    desc->handler = handler;
    return desc;
}

void FilaBufferDescriptor_setCallback(FilaBufferDescriptor* desc, FilaBufferReleaseCallback callback, void* user) {
    if (!desc) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
    if (impl->hasCallback()) {
        auto oldPayload = static_cast<CallbackPayload*>(impl->getUser());
        releasePayload(oldPayload);
    }
    auto payload = makePayload(callback, user);
    impl->setCallback(callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = callback;
    desc->user = user;
    desc->handler = nullptr;
}

void FilaBufferDescriptor_setCallbackWithHandler(
        FilaBufferDescriptor* desc, FilaCallbackHandler* handler,
        FilaBufferReleaseCallback callback, void* user) {
    if (!desc) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
    if (impl->hasCallback()) {
        auto oldPayload = static_cast<CallbackPayload*>(impl->getUser());
        releasePayload(oldPayload);
    }
    auto payload = makePayload(callback, user);
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    impl->setCallback(cppHandler, callback ? buffer_release_trampoline : nullptr, payload);
    desc->callback = callback;
    desc->user = user;
    desc->handler = handler;
}

bool FilaBufferDescriptor_hasCallback(const FilaBufferDescriptor* desc) {
    if (!desc) {
        return false;
    }
    auto impl = reinterpret_cast<const filament::backend::BufferDescriptor*>(desc->impl);
    return impl->hasCallback();
}

FilaBufferReleaseCallback FilaBufferDescriptor_getCallback(const FilaBufferDescriptor* desc) {
    if (!desc) {
        return nullptr;
    }
    return desc->callback;
}

void* FilaBufferDescriptor_getUser(const FilaBufferDescriptor* desc) {
    if (!desc) {
        return nullptr;
    }
    return desc->user;
}

FilaCallbackHandler* FilaBufferDescriptor_getHandler(const FilaBufferDescriptor* desc) {
    if (!desc) {
        return nullptr;
    }
    return desc->handler;
}

void FilaBufferDescriptor_destroy(FilaBufferDescriptor* desc) {
    if (desc) {
        auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
        delete impl;
        delete desc;
    }
}

const void* FilaBufferDescriptor_getBuffer(const FilaBufferDescriptor* desc) {
    if (!desc) return nullptr;
    auto impl = reinterpret_cast<const filament::backend::BufferDescriptor*>(desc->impl);
    return impl->buffer;
}

size_t FilaBufferDescriptor_getSize(const FilaBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<const filament::backend::BufferDescriptor*>(desc->impl);
    return impl->size;
}

} // extern "C"

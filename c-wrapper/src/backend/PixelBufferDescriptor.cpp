#include "filament/BufferDescriptor.h"
#include <backend/DriverEnums.h>
#include <backend/PixelBufferDescriptor.h>

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

static_assert((int)filament::backend::PixelDataFormat::R == FILA_PIXEL_DATA_FORMAT_R,
    "FilaPixelDataFormat must stay aligned with filament::backend::PixelDataFormat");
static_assert((int)filament::backend::PixelDataType::UBYTE == FILA_PIXEL_DATA_TYPE_UBYTE,
    "FilaPixelDataType must stay aligned with filament::backend::PixelDataType");
static_assert((int)filament::backend::CompressedPixelDataType::EAC_R11 ==
        FILA_COMPRESSED_PIXEL_DATA_TYPE_EAC_R11,
    "FilaCompressedPixelDataType must stay aligned with filament::backend::CompressedPixelDataType");
} // namespace


extern "C" {

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_create(const void* buffer, size_t size,
        FilaPixelDataFormat format, FilaPixelDataType type, uint8_t alignment,
        FilaBufferReleaseCallback callback, void* user) {
    return FilaPixelBufferDescriptor_createWithLayoutAndHandler(
        buffer, size, format, type, alignment, 0, 0, 0, nullptr, callback, user);
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithToken(const void* buffer, size_t size,
        FilaPixelDataFormat format, FilaPixelDataType type, uint8_t alignment,
        FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    return FilaPixelBufferDescriptor_createWithLayoutHandlerAndToken(
        buffer, size, format, type, alignment, 0, 0, 0, nullptr, callback, userToken);
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithLayout(const void* buffer, size_t size,
        FilaPixelDataFormat format, FilaPixelDataType type, uint8_t alignment,
        uint32_t left, uint32_t top, uint32_t stride,
        FilaBufferReleaseCallback callback, void* user) {
    return FilaPixelBufferDescriptor_createWithLayoutAndHandler(
        buffer, size, format, type, alignment, left, top, stride, nullptr, callback, user);
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithLayoutAndToken(const void* buffer, size_t size,
        FilaPixelDataFormat format, FilaPixelDataType type, uint8_t alignment,
        uint32_t left, uint32_t top, uint32_t stride,
        FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    return FilaPixelBufferDescriptor_createWithLayoutHandlerAndToken(
        buffer, size, format, type, alignment, left, top, stride, nullptr, callback, userToken);
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithHandler(const void* buffer, size_t size,
        FilaPixelDataFormat format, FilaPixelDataType type, uint8_t alignment,
        FilaCallbackHandler* handler, FilaBufferReleaseCallback callback, void* user) {
    return FilaPixelBufferDescriptor_createWithLayoutAndHandler(
        buffer, size, format, type, alignment, 0, 0, 0, handler, callback, user);
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithHandlerAndToken(const void* buffer, size_t size,
        FilaPixelDataFormat format, FilaPixelDataType type, uint8_t alignment,
        FilaCallbackHandler* handler, FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    return FilaPixelBufferDescriptor_createWithLayoutHandlerAndToken(
        buffer, size, format, type, alignment, 0, 0, 0, handler, callback, userToken);
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithLayoutAndHandler(
        const void* buffer, size_t size, FilaPixelDataFormat format, FilaPixelDataType type,
        uint8_t alignment, uint32_t left, uint32_t top, uint32_t stride,
        FilaCallbackHandler* handler, FilaBufferReleaseCallback callback, void* user) {
    auto payload = makePayload(callback, user, nullptr, 0u);
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    auto desc = new FilaPixelBufferDescriptor;
    desc->impl = new filament::backend::PixelBufferDescriptor(
        buffer, size,
        static_cast<filament::backend::PixelDataFormat>(format),
        static_cast<filament::backend::PixelDataType>(type),
        alignment, left, top, stride,
        cppHandler,
        callback ? buffer_release_trampoline : nullptr,
        payload);
    desc->callback = callback;
    desc->tokenCallback = nullptr;
    desc->user = user;
    desc->userToken = 0u;
    desc->handler = handler;
    desc->consumed = false;
    return desc;
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithLayoutHandlerAndToken(
        const void* buffer, size_t size, FilaPixelDataFormat format, FilaPixelDataType type,
        uint8_t alignment, uint32_t left, uint32_t top, uint32_t stride,
        FilaCallbackHandler* handler, FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    auto payload = makePayload(nullptr, nullptr, callback, userToken);
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    auto desc = new FilaPixelBufferDescriptor;
    desc->impl = new filament::backend::PixelBufferDescriptor(
        buffer, size,
        static_cast<filament::backend::PixelDataFormat>(format),
        static_cast<filament::backend::PixelDataType>(type),
        alignment, left, top, stride,
        cppHandler,
        callback ? buffer_release_trampoline : nullptr,
        payload);
    desc->callback = nullptr;
    desc->tokenCallback = callback;
    desc->user = nullptr;
    desc->userToken = userToken;
    desc->handler = handler;
    desc->consumed = false;
    return desc;
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createCompressed(const void* buffer, size_t size,
        FilaCompressedPixelDataType format, uint32_t imageSize,
        FilaBufferReleaseCallback callback, void* user) {
    return FilaPixelBufferDescriptor_createCompressedWithHandler(
        buffer, size, format, imageSize, nullptr, callback, user);
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createCompressedWithToken(const void* buffer, size_t size,
        FilaCompressedPixelDataType format, uint32_t imageSize,
        FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    return FilaPixelBufferDescriptor_createCompressedWithHandlerAndToken(
        buffer, size, format, imageSize, nullptr, callback, userToken);
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createCompressedWithHandler(
        const void* buffer, size_t size, FilaCompressedPixelDataType format, uint32_t imageSize,
        FilaCallbackHandler* handler, FilaBufferReleaseCallback callback, void* user) {
    auto payload = makePayload(callback, user, nullptr, 0u);
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    auto desc = new FilaPixelBufferDescriptor;
    desc->impl = new filament::backend::PixelBufferDescriptor(
        buffer, size,
        static_cast<filament::backend::CompressedPixelDataType>(format), imageSize,
        cppHandler,
        callback ? buffer_release_trampoline : nullptr,
        payload);
    desc->callback = callback;
    desc->tokenCallback = nullptr;
    desc->user = user;
    desc->userToken = 0u;
    desc->handler = handler;
    desc->consumed = false;
    return desc;
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createCompressedWithHandlerAndToken(
        const void* buffer, size_t size, FilaCompressedPixelDataType format, uint32_t imageSize,
        FilaCallbackHandler* handler, FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    auto payload = makePayload(nullptr, nullptr, callback, userToken);
    auto cppHandler = handler ? reinterpret_cast<filament::backend::CallbackHandler*>(handler->impl) : nullptr;
    auto desc = new FilaPixelBufferDescriptor;
    desc->impl = new filament::backend::PixelBufferDescriptor(
        buffer, size,
        static_cast<filament::backend::CompressedPixelDataType>(format), imageSize,
        cppHandler,
        callback ? buffer_release_trampoline : nullptr,
        payload);
    desc->callback = nullptr;
    desc->tokenCallback = callback;
    desc->user = nullptr;
    desc->userToken = userToken;
    desc->handler = handler;
    desc->consumed = false;
    return desc;
}

void FilaPixelBufferDescriptor_setCallback(FilaPixelBufferDescriptor* desc,
        FilaBufferReleaseCallback callback, void* user) {
    if (!desc || !desc->impl) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
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

void FilaPixelBufferDescriptor_setCallbackWithToken(FilaPixelBufferDescriptor* desc,
        FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    if (!desc || !desc->impl) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
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

void FilaPixelBufferDescriptor_setCallbackWithHandler(
        FilaPixelBufferDescriptor* desc, FilaCallbackHandler* handler,
        FilaBufferReleaseCallback callback, void* user) {
    if (!desc || !desc->impl) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
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

void FilaPixelBufferDescriptor_setCallbackWithHandlerAndToken(
        FilaPixelBufferDescriptor* desc, FilaCallbackHandler* handler,
        FilaBufferReleaseTokenCallback callback, uintptr_t userToken) {
    if (!desc || !desc->impl) {
        return;
    }
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
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

bool FilaPixelBufferDescriptor_hasCallback(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return false;
    }
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->hasCallback();
}

FilaBufferReleaseCallback FilaPixelBufferDescriptor_getCallback(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return nullptr;
    }
    return desc->callback;
}

void* FilaPixelBufferDescriptor_getUser(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return nullptr;
    }
    return desc->user;
}

uintptr_t FilaPixelBufferDescriptor_getUserToken(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return 0u;
    }
    return desc->userToken;
}

FilaCallbackHandler* FilaPixelBufferDescriptor_getHandler(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) {
        return nullptr;
    }
    return desc->handler;
}

void FilaPixelBufferDescriptor_destroy(FilaPixelBufferDescriptor* desc) {
    if (desc) {
        if (desc->impl) {
            auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
            delete impl;
        }
        delete desc;
    }
}

const void* FilaPixelBufferDescriptor_getBuffer(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return nullptr;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->buffer;
}

size_t FilaPixelBufferDescriptor_getSize(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return 0;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->size;
}

FilaPixelDataFormat FilaPixelBufferDescriptor_getFormat(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return FILA_PIXEL_DATA_FORMAT_R;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return static_cast<FilaPixelDataFormat>(impl->format);
}

FilaPixelDataType FilaPixelBufferDescriptor_getType(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return FILA_PIXEL_DATA_TYPE_UBYTE;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return static_cast<FilaPixelDataType>(impl->type);
}

uint8_t FilaPixelBufferDescriptor_getAlignment(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return 1;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->alignment;
}

uint32_t FilaPixelBufferDescriptor_getLeft(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return 0;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->left;
}

uint32_t FilaPixelBufferDescriptor_getTop(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return 0;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->top;
}

uint32_t FilaPixelBufferDescriptor_getStride(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return 0;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->stride;
}

uint32_t FilaPixelBufferDescriptor_getImageSize(const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return 0;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->imageSize;
}

FilaCompressedPixelDataType FilaPixelBufferDescriptor_getCompressedFormat(
        const FilaPixelBufferDescriptor* desc) {
    if (!desc || !desc->impl) return FILA_COMPRESSED_PIXEL_DATA_TYPE_EAC_R11;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return static_cast<FilaCompressedPixelDataType>(impl->compressedFormat);
}

size_t FilaPixelBufferDescriptor_computePixelSize(FilaPixelDataFormat format, FilaPixelDataType type) {
    return filament::backend::PixelBufferDescriptor::computePixelSize(
        static_cast<filament::backend::PixelDataFormat>(format),
        static_cast<filament::backend::PixelDataType>(type));
}

size_t FilaPixelBufferDescriptor_computeDataSize(FilaPixelDataFormat format, FilaPixelDataType type,
        size_t stride, size_t height, size_t alignment) {
    return filament::backend::PixelBufferDescriptor::computeDataSize(
        static_cast<filament::backend::PixelDataFormat>(format),
        static_cast<filament::backend::PixelDataType>(type),
        stride, height, alignment);
}

} // extern "C"

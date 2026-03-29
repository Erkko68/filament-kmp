#include "../../../include/filament/BufferDescriptor.h"
#include "../../../filament-prebuilts/include/backend/BufferDescriptor.h"
#include "../../../filament-prebuilts/include/backend/PixelBufferDescriptor.h"
#include <cstring>

extern "C" {


static void buffer_release_trampoline(void* buffer, size_t size, void* user) {
    if (user) {
        auto cb = reinterpret_cast<FilaBufferReleaseCallback*>(user);
        (*cb)(buffer, size, user);
    }
}

FilaBufferDescriptor* FilaBufferDescriptor_create(const void* buffer, size_t size, FilaBufferReleaseCallback callback, void* user) {
    auto desc = new FilaBufferDescriptor;
    desc->impl = new filament::backend::BufferDescriptor(buffer, size, callback ? buffer_release_trampoline : nullptr, user);
    return desc;
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
    auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
    return impl->buffer;
}

size_t FilaBufferDescriptor_getSize(const FilaBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<filament::backend::BufferDescriptor*>(desc->impl);
    return impl->size;
}

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_create(const void* buffer, size_t size, int format, int type, uint8_t alignment, FilaBufferReleaseCallback callback, void* user) {
    auto desc = new FilaPixelBufferDescriptor;
    desc->impl = reinterpret_cast<void*>(new filament::backend::PixelBufferDescriptor(
        buffer, size,
        (filament::backend::PixelDataFormat)format,
        (filament::backend::PixelDataType)type,
        alignment, 0, 0, 0, // left, top, stride
        nullptr, // CallbackHandler*
        callback ? buffer_release_trampoline : nullptr,
        user
    ));
    return desc;
}

void FilaPixelBufferDescriptor_destroy(FilaPixelBufferDescriptor* desc) {
    if (desc) {
        auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
        delete impl;
        delete desc;
    }
}

const void* FilaPixelBufferDescriptor_getBuffer(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return nullptr;
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->buffer;
}

size_t FilaPixelBufferDescriptor_getSize(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->size;
}

int FilaPixelBufferDescriptor_getFormat(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
    return (int)impl->format;
}

int FilaPixelBufferDescriptor_getType(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
    return (int)impl->type;
}

uint8_t FilaPixelBufferDescriptor_getAlignment(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return 1;
    auto impl = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->alignment;
}

} // extern "C"


#include "filament/BufferDescriptor.h"
#include <backend/PixelBufferDescriptor.h>


extern "C" {

static void buffer_release_trampoline(void* buffer, size_t size, void* user) {
    if (user) {
        auto cb = reinterpret_cast<FilaBufferReleaseCallback*>(user);
        (*cb)(buffer, size, user);
    }
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
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->buffer;
}

size_t FilaPixelBufferDescriptor_getSize(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->size;
}

int FilaPixelBufferDescriptor_getFormat(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return (int)impl->format;
}

int FilaPixelBufferDescriptor_getType(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return (int)impl->type;
}

uint8_t FilaPixelBufferDescriptor_getAlignment(const FilaPixelBufferDescriptor* desc) {
    if (!desc) return 1;
    auto impl = reinterpret_cast<const filament::backend::PixelBufferDescriptor*>(desc->impl);
    return impl->alignment;
}

} // extern "C"

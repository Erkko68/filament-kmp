#include "filament/BufferDescriptor.h"
#include <backend/BufferDescriptor.h>


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
    auto impl = reinterpret_cast<const filament::backend::BufferDescriptor*>(desc->impl);
    return impl->buffer;
}

size_t FilaBufferDescriptor_getSize(const FilaBufferDescriptor* desc) {
    if (!desc) return 0;
    auto impl = reinterpret_cast<const filament::backend::BufferDescriptor*>(desc->impl);
    return impl->size;
}

} // extern "C"

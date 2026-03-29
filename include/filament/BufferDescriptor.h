#ifndef FILAMENT_C_BUFFER_DESCRIPTOR_H
#define FILAMENT_C_BUFFER_DESCRIPTOR_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Concrete C-visible structs that hold an opaque pointer to the C++ implementation.
// Other C++ wrapper sources can cast the `impl` pointer to the appropriate
// filament::backend::* type.
typedef struct FilaBufferDescriptor {
	void* impl;
} FilaBufferDescriptor;

typedef struct FilaPixelBufferDescriptor {
	void* impl;
} FilaPixelBufferDescriptor;

typedef void (*FilaBufferReleaseCallback)(void* buffer, size_t size, void* user);

// BufferDescriptor creation/destruction
FilaBufferDescriptor* FilaBufferDescriptor_create(const void* buffer, size_t size, FilaBufferReleaseCallback callback, void* user);
void FilaBufferDescriptor_destroy(FilaBufferDescriptor* desc);
const void* FilaBufferDescriptor_getBuffer(const FilaBufferDescriptor* desc);
size_t FilaBufferDescriptor_getSize(const FilaBufferDescriptor* desc);

// PixelBufferDescriptor creation/destruction
FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_create(const void* buffer, size_t size, int format, int type, uint8_t alignment, FilaBufferReleaseCallback callback, void* user);
void FilaPixelBufferDescriptor_destroy(FilaPixelBufferDescriptor* desc);
const void* FilaPixelBufferDescriptor_getBuffer(const FilaPixelBufferDescriptor* desc);
size_t FilaPixelBufferDescriptor_getSize(const FilaPixelBufferDescriptor* desc);
int FilaPixelBufferDescriptor_getFormat(const FilaPixelBufferDescriptor* desc);
int FilaPixelBufferDescriptor_getType(const FilaPixelBufferDescriptor* desc);
uint8_t FilaPixelBufferDescriptor_getAlignment(const FilaPixelBufferDescriptor* desc);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BUFFER_DESCRIPTOR_H


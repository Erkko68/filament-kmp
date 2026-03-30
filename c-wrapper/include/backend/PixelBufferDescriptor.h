#ifndef FILAMENT_C_BACKEND_PIXELBUFFERDESCRIPTOR_H
#define FILAMENT_C_BACKEND_PIXELBUFFERDESCRIPTOR_H

#include "BufferDescriptor.h"
#include <stdint.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaPixelBufferDescriptor FilaPixelBufferDescriptor;

/**
 * Creates a FilaPixelBufferDescriptor referencing an image in main memory.
 *
 * @param buffer    Virtual address of the buffer containing the image.
 * @param size      Size in bytes of the buffer containing the image.
 * @param format    Format of the image pixels (filament::backend::PixelDataFormat).
 * @param type      Type of the image pixels (filament::backend::PixelDataType).
 * @param alignment Alignment in bytes of pixel rows.
 * @param callback  A callback used to release the CPU buffer.
 * @param user      An opaque user pointer passed to the callback function when it's called.
 * @return A pointer to the newly created FilaPixelBufferDescriptor.
 */
FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_create(
    const void* buffer, size_t size, int format, int type, uint8_t alignment,
    FilaBufferReleaseCallback callback, void* user);

/**
 * Destroys a FilaPixelBufferDescriptor.
 * This will trigger the release callback if one was set.
 *
 * @param desc A pointer to the FilaPixelBufferDescriptor to destroy.
 */
void FilaPixelBufferDescriptor_destroy(FilaPixelBufferDescriptor* desc);

/**
 * Returns the buffer pointer from a FilaPixelBufferDescriptor.
 *
 * @param desc A pointer to the FilaPixelBufferDescriptor.
 * @return The buffer pointer.
 */
const void* FilaPixelBufferDescriptor_getBuffer(const FilaPixelBufferDescriptor* desc);

/**
 * Returns the size of the buffer from a FilaPixelBufferDescriptor.
 *
 * @param desc A pointer to the FilaPixelBufferDescriptor.
 * @return The size of the buffer in bytes.
 */
size_t FilaPixelBufferDescriptor_getSize(const FilaPixelBufferDescriptor* desc);

/**
 * Returns the format of the pixels from a FilaPixelBufferDescriptor.
 *
 * @param desc A pointer to the FilaPixelBufferDescriptor.
 * @return The format of the pixels.
 */
int FilaPixelBufferDescriptor_getFormat(const FilaPixelBufferDescriptor* desc);

/**
 * Returns the type of the pixels from a FilaPixelBufferDescriptor.
 *
 * @param desc A pointer to the FilaPixelBufferDescriptor.
 * @return The type of the pixels.
 */
int FilaPixelBufferDescriptor_getType(const FilaPixelBufferDescriptor* desc);

/**
 * Returns the alignment of the pixel rows from a FilaPixelBufferDescriptor.
 *
 * @param desc A pointer to the FilaPixelBufferDescriptor.
 * @return The alignment of the pixel rows in bytes.
 */
uint8_t FilaPixelBufferDescriptor_getAlignment(const FilaPixelBufferDescriptor* desc);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PIXELBUFFERDESCRIPTOR_H

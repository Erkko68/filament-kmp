#ifndef FILAMENT_C_BACKEND_PIXELBUFFERDESCRIPTOR_H
#define FILAMENT_C_BACKEND_PIXELBUFFERDESCRIPTOR_H

#include "BufferDescriptor.h"
#include "CallbackHandler.h"
#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaPixelBufferDescriptor FilaPixelBufferDescriptor;

typedef enum FilaPixelDataFormat {
    FILA_PIXEL_DATA_FORMAT_R = 0,
    FILA_PIXEL_DATA_FORMAT_R_INTEGER = 1,
    FILA_PIXEL_DATA_FORMAT_RG = 2,
    FILA_PIXEL_DATA_FORMAT_RG_INTEGER = 3,
    FILA_PIXEL_DATA_FORMAT_RGB = 4,
    FILA_PIXEL_DATA_FORMAT_RGB_INTEGER = 5,
    FILA_PIXEL_DATA_FORMAT_RGBA = 6,
    FILA_PIXEL_DATA_FORMAT_RGBA_INTEGER = 7,
    FILA_PIXEL_DATA_FORMAT_UNUSED = 8,
    FILA_PIXEL_DATA_FORMAT_DEPTH_COMPONENT = 9,
    FILA_PIXEL_DATA_FORMAT_DEPTH_STENCIL = 10,
    FILA_PIXEL_DATA_FORMAT_ALPHA = 11,
} FilaPixelDataFormat;

typedef enum FilaPixelDataType {
    FILA_PIXEL_DATA_TYPE_UBYTE = 0,
    FILA_PIXEL_DATA_TYPE_BYTE = 1,
    FILA_PIXEL_DATA_TYPE_USHORT = 2,
    FILA_PIXEL_DATA_TYPE_SHORT = 3,
    FILA_PIXEL_DATA_TYPE_UINT = 4,
    FILA_PIXEL_DATA_TYPE_INT = 5,
    FILA_PIXEL_DATA_TYPE_HALF = 6,
    FILA_PIXEL_DATA_TYPE_FLOAT = 7,
    FILA_PIXEL_DATA_TYPE_COMPRESSED = 8,
    FILA_PIXEL_DATA_TYPE_UINT_10F_11F_11F_REV = 9,
    FILA_PIXEL_DATA_TYPE_USHORT_565 = 10,
    FILA_PIXEL_DATA_TYPE_UINT_2_10_10_10_REV = 11,
} FilaPixelDataType;

typedef enum FilaCompressedPixelDataType {
    FILA_COMPRESSED_PIXEL_DATA_TYPE_EAC_R11 = 0,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_EAC_R11_SIGNED = 1,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_EAC_RG11 = 2,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_EAC_RG11_SIGNED = 3,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_ETC2_RGB8 = 4,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_ETC2_SRGB8 = 5,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_ETC2_RGB8_A1 = 6,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_ETC2_SRGB8_A1 = 7,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_ETC2_EAC_RGBA8 = 8,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_ETC2_EAC_SRGBA8 = 9,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_DXT1_RGB = 10,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_DXT1_RGBA = 11,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_DXT3_RGBA = 12,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_DXT5_RGBA = 13,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_DXT1_SRGB = 14,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_DXT1_SRGBA = 15,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_DXT3_SRGBA = 16,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_DXT5_SRGBA = 17,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_4x4 = 18,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_5x4 = 19,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_5x5 = 20,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_6x5 = 21,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_6x6 = 22,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_8x5 = 23,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_8x6 = 24,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_8x8 = 25,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_10x5 = 26,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_10x6 = 27,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_10x8 = 28,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_10x10 = 29,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_12x10 = 30,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_ASTC_12x12 = 31,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_4x4 = 32,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_5x4 = 33,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_5x5 = 34,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_6x5 = 35,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_6x6 = 36,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_8x5 = 37,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_8x6 = 38,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_8x8 = 39,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_10x5 = 40,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_10x6 = 41,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_10x8 = 42,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_10x10 = 43,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_12x10 = 44,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB8_ALPHA8_ASTC_12x12 = 45,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RED_RGTC1 = 46,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SIGNED_RED_RGTC1 = 47,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RED_GREEN_RGTC2 = 48,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SIGNED_RED_GREEN_RGTC2 = 49,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGB_BPTC_SIGNED_FLOAT = 50,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGB_BPTC_UNSIGNED_FLOAT = 51,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_RGBA_BPTC_UNORM = 52,
    FILA_COMPRESSED_PIXEL_DATA_TYPE_SRGB_ALPHA_BPTC_UNORM = 53,
} FilaCompressedPixelDataType;

/**
 * Creates a FilaPixelBufferDescriptor referencing an image in main memory.
 * After a successful texture upload call consumes this descriptor, it becomes non-owning and
 * callback/query APIs return empty values.
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
    const void* buffer, size_t size, FilaPixelDataFormat format, FilaPixelDataType type,
    uint8_t alignment,
    FilaBufferReleaseCallback callback, void* user);

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithLayout(
    const void* buffer, size_t size, FilaPixelDataFormat format, FilaPixelDataType type,
    uint8_t alignment, uint32_t left, uint32_t top, uint32_t stride,
    FilaBufferReleaseCallback callback, void* user);

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithHandler(
    const void* buffer, size_t size, FilaPixelDataFormat format, FilaPixelDataType type,
    uint8_t alignment, FilaCallbackHandler* handler,
    FilaBufferReleaseCallback callback, void* user);

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createWithLayoutAndHandler(
    const void* buffer, size_t size, FilaPixelDataFormat format, FilaPixelDataType type,
    uint8_t alignment, uint32_t left, uint32_t top, uint32_t stride,
    FilaCallbackHandler* handler, FilaBufferReleaseCallback callback, void* user);

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createCompressed(
    const void* buffer, size_t size, FilaCompressedPixelDataType format, uint32_t imageSize,
    FilaBufferReleaseCallback callback, void* user);

FilaPixelBufferDescriptor* FilaPixelBufferDescriptor_createCompressedWithHandler(
    const void* buffer, size_t size, FilaCompressedPixelDataType format, uint32_t imageSize,
    FilaCallbackHandler* handler, FilaBufferReleaseCallback callback, void* user);

void FilaPixelBufferDescriptor_setCallback(
    FilaPixelBufferDescriptor* desc, FilaBufferReleaseCallback callback, void* user);

void FilaPixelBufferDescriptor_setCallbackWithHandler(
    FilaPixelBufferDescriptor* desc, FilaCallbackHandler* handler,
    FilaBufferReleaseCallback callback, void* user);

bool FilaPixelBufferDescriptor_hasCallback(const FilaPixelBufferDescriptor* desc);
FilaBufferReleaseCallback FilaPixelBufferDescriptor_getCallback(const FilaPixelBufferDescriptor* desc);
void* FilaPixelBufferDescriptor_getUser(const FilaPixelBufferDescriptor* desc);
FilaCallbackHandler* FilaPixelBufferDescriptor_getHandler(const FilaPixelBufferDescriptor* desc);

/**
 * Destroys a FilaPixelBufferDescriptor.
 * This triggers the release callback only if this descriptor still owns the image buffer.
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
FilaPixelDataFormat FilaPixelBufferDescriptor_getFormat(const FilaPixelBufferDescriptor* desc);

/**
 * Returns the type of the pixels from a FilaPixelBufferDescriptor.
 *
 * @param desc A pointer to the FilaPixelBufferDescriptor.
 * @return The type of the pixels.
 */
FilaPixelDataType FilaPixelBufferDescriptor_getType(const FilaPixelBufferDescriptor* desc);

/**
 * Returns the alignment of the pixel rows from a FilaPixelBufferDescriptor.
 *
 * @param desc A pointer to the FilaPixelBufferDescriptor.
 * @return The alignment of the pixel rows in bytes.
 */
uint8_t FilaPixelBufferDescriptor_getAlignment(const FilaPixelBufferDescriptor* desc);

uint32_t FilaPixelBufferDescriptor_getLeft(const FilaPixelBufferDescriptor* desc);
uint32_t FilaPixelBufferDescriptor_getTop(const FilaPixelBufferDescriptor* desc);
uint32_t FilaPixelBufferDescriptor_getStride(const FilaPixelBufferDescriptor* desc);

uint32_t FilaPixelBufferDescriptor_getImageSize(const FilaPixelBufferDescriptor* desc);
FilaCompressedPixelDataType FilaPixelBufferDescriptor_getCompressedFormat(
    const FilaPixelBufferDescriptor* desc);

size_t FilaPixelBufferDescriptor_computePixelSize(FilaPixelDataFormat format, FilaPixelDataType type);
size_t FilaPixelBufferDescriptor_computeDataSize(
    FilaPixelDataFormat format, FilaPixelDataType type, size_t stride, size_t height,
    size_t alignment);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PIXELBUFFERDESCRIPTOR_H

#ifndef FILAMENT_C_BACKEND_BUFFERDESCRIPTOR_H
#define FILAMENT_C_BACKEND_BUFFERDESCRIPTOR_H

#include <stddef.h>
#include <stdbool.h>
#include <stdint.h>

#include "CallbackHandler.h"

#ifdef __cplusplus
extern "C" {
#endif

// Opaque type for FilaBufferDescriptor
typedef struct FilaBufferDescriptor FilaBufferDescriptor;

// Callback for buffer release
typedef void (*FilaBufferReleaseCallback)(void* buffer, size_t size, void* user);
typedef void (*FilaBufferReleaseTokenCallback)(void* buffer, size_t size, uintptr_t userToken);

/**
 * Creates a FilaBufferDescriptor.
 * The created descriptor takes ownership of the buffer.
 * After a successful buffer upload call consumes this descriptor, it becomes non-owning and
 * callback/query APIs return empty values.
 *
 * @param buffer    Memory address of the CPU buffer to reference.
 * @param size      Size of the CPU buffer in bytes.
 * @param callback  A callback used to release the CPU buffer from this BufferDescriptor.
 * @param user      An opaque user pointer passed to the callback function when it's called.
 * @return A pointer to the newly created FilaBufferDescriptor.
 */
FilaBufferDescriptor* FilaBufferDescriptor_create(
    const void* buffer, size_t size, FilaBufferReleaseCallback callback, void* user);

FilaBufferDescriptor* FilaBufferDescriptor_createWithToken(
    const void* buffer, size_t size, FilaBufferReleaseTokenCallback callback, uintptr_t userToken);

FilaBufferDescriptor* FilaBufferDescriptor_createWithHandler(
    const void* buffer, size_t size, FilaCallbackHandler* handler,
    FilaBufferReleaseCallback callback, void* user);

FilaBufferDescriptor* FilaBufferDescriptor_createWithHandlerAndToken(
    const void* buffer, size_t size, FilaCallbackHandler* handler,
    FilaBufferReleaseTokenCallback callback, uintptr_t userToken);

// Replaces the release callback and associated user pointer.
void FilaBufferDescriptor_setCallback(
    FilaBufferDescriptor* desc, FilaBufferReleaseCallback callback, void* user);

void FilaBufferDescriptor_setCallbackWithToken(
    FilaBufferDescriptor* desc, FilaBufferReleaseTokenCallback callback, uintptr_t userToken);

void FilaBufferDescriptor_setCallbackWithHandler(
    FilaBufferDescriptor* desc, FilaCallbackHandler* handler,
    FilaBufferReleaseCallback callback, void* user);

void FilaBufferDescriptor_setCallbackWithHandlerAndToken(
    FilaBufferDescriptor* desc, FilaCallbackHandler* handler,
    FilaBufferReleaseTokenCallback callback, uintptr_t userToken);

// Returns true if a release callback is currently set.
bool FilaBufferDescriptor_hasCallback(const FilaBufferDescriptor* desc);

// Returns the currently configured release callback.
FilaBufferReleaseCallback FilaBufferDescriptor_getCallback(const FilaBufferDescriptor* desc);

// Returns the currently configured user pointer.
void* FilaBufferDescriptor_getUser(const FilaBufferDescriptor* desc);

// Returns the currently configured user token.
uintptr_t FilaBufferDescriptor_getUserToken(const FilaBufferDescriptor* desc);

// Returns the callback handler associated with this descriptor, or NULL.
FilaCallbackHandler* FilaBufferDescriptor_getHandler(const FilaBufferDescriptor* desc);

/**
 * Destroys a FilaBufferDescriptor.
 * This triggers the release callback only if this descriptor still owns the buffer.
 *
 * @param desc A pointer to the FilaBufferDescriptor to destroy.
 */
void FilaBufferDescriptor_destroy(FilaBufferDescriptor* desc);

/**
 * Returns the buffer pointer from a FilaBufferDescriptor.
 *
 * @param desc A pointer to the FilaBufferDescriptor.
 * @return The buffer pointer.
 */
const void* FilaBufferDescriptor_getBuffer(const FilaBufferDescriptor* desc);

/**
 * Returns the size of the buffer from a FilaBufferDescriptor.
 *
 * @param desc A pointer to the FilaBufferDescriptor.
 * @return The size of the buffer in bytes.
 */
size_t FilaBufferDescriptor_getSize(const FilaBufferDescriptor* desc);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_BUFFERDESCRIPTOR_H

#ifndef FILAMENT_C_FILAMENT_BUFFERDESCRIPTOR_H
#define FILAMENT_C_FILAMENT_BUFFERDESCRIPTOR_H

#include "../backend/BufferDescriptor.h"
#include "../backend/PixelBufferDescriptor.h"

#ifdef __cplusplus
// Internal bridge payload for C++ translation units.
struct FilaBufferDescriptor {
    void* impl;
    FilaBufferReleaseCallback callback;
    void* user;
    FilaCallbackHandler* handler;
};

struct FilaPixelBufferDescriptor {
    void* impl;
    FilaBufferReleaseCallback callback;
    void* user;
    FilaCallbackHandler* handler;
};

struct FilaCallbackHandler {
    void* impl;
};
#endif

#endif // FILAMENT_C_FILAMENT_BUFFERDESCRIPTOR_H


#ifndef FILAMENT_C_STREAM_H
#define FILAMENT_C_STREAM_H

#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaStreamType {
    FILA_STREAM_ACQUIRED = 0,
    FILA_STREAM_NATIVE = 1,
} FilaStreamType;

// Stream builder API
FilaStreamBuilder* FilaStreamBuilder_create(void);
void FilaStreamBuilder_destroy(FilaStreamBuilder* builder);
void FilaStreamBuilder_width(FilaStreamBuilder* builder, uint32_t width);
void FilaStreamBuilder_height(FilaStreamBuilder* builder, uint32_t height);

// Stream methods
FilaStreamType FilaStream_getStreamType(const FilaStream* stream);
void FilaStream_setDimensions(FilaStream* stream, uint32_t width, uint32_t height);
int64_t FilaStream_getTimestamp(const FilaStream* stream);

// Builder build
FilaStream* FilaStreamBuilder_build(FilaStreamBuilder* builder, FilaEngine* engine);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_STREAM_H


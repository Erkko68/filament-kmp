#ifndef FILAMENT_C_GEOMETRY_TRANSCODER_H
#define FILAMENT_C_GEOMETRY_TRANSCODER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaGeometryComponentType {
    FILA_GEOMETRY_COMPONENT_TYPE_BYTE = 0,
    FILA_GEOMETRY_COMPONENT_TYPE_UBYTE = 1,
    FILA_GEOMETRY_COMPONENT_TYPE_SHORT = 2,
    FILA_GEOMETRY_COMPONENT_TYPE_USHORT = 3,
    FILA_GEOMETRY_COMPONENT_TYPE_HALF = 4,
    FILA_GEOMETRY_COMPONENT_TYPE_FLOAT = 5,
} FilaGeometryComponentType;

typedef struct FilaGeometryTranscoderConfig {
    FilaGeometryComponentType componentType;
    bool normalized;
    uint32_t componentCount;
    uint32_t inputStrideBytes;
} FilaGeometryTranscoderConfig;

FilaGeometryTranscoder* FilaGeometryTranscoder_create(FilaGeometryTranscoderConfig config);
void FilaGeometryTranscoder_destroy(FilaGeometryTranscoder* transcoder);

// If outTarget is NULL, this returns required output byte size.
size_t FilaGeometryTranscoder_transcode(const FilaGeometryTranscoder* transcoder,
        float* outTarget,
        const void* source,
        size_t count);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GEOMETRY_TRANSCODER_H


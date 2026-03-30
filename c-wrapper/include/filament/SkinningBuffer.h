#ifndef FILAMENT_C_SKINNING_BUFFER_H
#define FILAMENT_C_SKINNING_BUFFER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaSkinningBufferBuilder* FilaSkinningBufferBuilder_create(void);
void FilaSkinningBufferBuilder_destroy(FilaSkinningBufferBuilder* builder);
void FilaSkinningBufferBuilder_boneCount(FilaSkinningBufferBuilder* builder, uint32_t boneCount);
void FilaSkinningBufferBuilder_initialize(FilaSkinningBufferBuilder* builder, bool initialize);
FilaSkinningBuffer* FilaSkinningBufferBuilder_build(FilaSkinningBufferBuilder* builder, FilaEngine* engine);

void FilaSkinningBuffer_setBonesMat4f(FilaSkinningBuffer* skinningBuffer,
        FilaEngine* engine,
        const float* transforms4x4,
        size_t count,
        size_t offset);

size_t FilaSkinningBuffer_getBoneCount(const FilaSkinningBuffer* skinningBuffer);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SKINNING_BUFFER_H


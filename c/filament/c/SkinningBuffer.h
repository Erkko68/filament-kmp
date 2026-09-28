#ifndef FILAMENT_C_SKINNING_BUFFER_H
#define FILAMENT_C_SKINNING_BUFFER_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

// Builder
typedef struct FilaSkinningBufferBuilder FilaSkinningBufferBuilder;

FilaSkinningBufferBuilder* FilaSkinningBufferBuilder_create(void);
void FilaSkinningBufferBuilder_destroy(FilaSkinningBufferBuilder* builder);
FilaSkinningBuffer* FilaSkinningBufferBuilder_build(FilaSkinningBufferBuilder* builder, FilaEngine* engine);

void FilaSkinningBufferBuilder_boneCount(FilaSkinningBufferBuilder* builder, uint32_t boneCount);
void FilaSkinningBufferBuilder_initialize(FilaSkinningBufferBuilder* builder, bool initialize);

// SkinningBuffer
uint32_t FilaSkinningBuffer_getBoneCount(const FilaSkinningBuffer* buffer);
void FilaSkinningBuffer_setBonesMat4f(FilaSkinningBuffer* buffer, FilaEngine* engine, const float* matrices, uint32_t boneCount, uint32_t offset);
void FilaSkinningBuffer_setBonesQuaternions(FilaSkinningBuffer* buffer, FilaEngine* engine, const FilaBone* bones, uint32_t boneCount, uint32_t offset);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SKINNING_BUFFER_H

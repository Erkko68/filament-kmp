#ifndef FILAMENT_C_INSTANCE_BUFFER_H
#define FILAMENT_C_INSTANCE_BUFFER_H

#include <stdbool.h>
#include <stddef.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaInstanceBufferBuilder* FilaInstanceBufferBuilder_create(size_t instanceCount);
void FilaInstanceBufferBuilder_destroy(FilaInstanceBufferBuilder* builder);
FilaInstanceBuffer* FilaInstanceBufferBuilder_build(FilaInstanceBufferBuilder* builder, FilaEngine* engine);

size_t FilaInstanceBuffer_getInstanceCount(const FilaInstanceBuffer* instanceBuffer);

void FilaInstanceBuffer_setLocalTransforms(FilaInstanceBuffer* instanceBuffer,
        const float* transforms4x4,
        size_t count,
        size_t offset);

bool FilaInstanceBuffer_getLocalTransform(FilaInstanceBuffer* instanceBuffer,
        size_t index,
        float* outTransform4x4);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_INSTANCE_BUFFER_H


#ifndef FILAMENT_C_MORPH_TARGET_BUFFER_H
#define FILAMENT_C_MORPH_TARGET_BUFFER_H

#include <stdbool.h>
#include <stdint.h>
#include <stddef.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaMorphTargetBufferBuilder* FilaMorphTargetBufferBuilder_create(void);
void FilaMorphTargetBufferBuilder_destroy(FilaMorphTargetBufferBuilder* builder);
void FilaMorphTargetBufferBuilder_vertexCount(FilaMorphTargetBufferBuilder* builder, size_t vertexCount);
void FilaMorphTargetBufferBuilder_count(FilaMorphTargetBufferBuilder* builder, size_t count);
void FilaMorphTargetBufferBuilder_withPositions(FilaMorphTargetBufferBuilder* builder, bool enable);
void FilaMorphTargetBufferBuilder_withTangents(FilaMorphTargetBufferBuilder* builder, bool enable);
void FilaMorphTargetBufferBuilder_enableCustomMorphing(FilaMorphTargetBufferBuilder* builder, bool enable);
FilaMorphTargetBuffer* FilaMorphTargetBufferBuilder_build(FilaMorphTargetBufferBuilder* builder, FilaEngine* engine);

size_t FilaMorphTargetBuffer_getVertexCount(const FilaMorphTargetBuffer* morphTargetBuffer);
size_t FilaMorphTargetBuffer_getCount(const FilaMorphTargetBuffer* morphTargetBuffer);
bool FilaMorphTargetBuffer_hasPositions(const FilaMorphTargetBuffer* morphTargetBuffer);
bool FilaMorphTargetBuffer_hasTangents(const FilaMorphTargetBuffer* morphTargetBuffer);
bool FilaMorphTargetBuffer_isCustomMorphingEnabled(const FilaMorphTargetBuffer* morphTargetBuffer);
void FilaMorphTargetBuffer_setPositionsAtFloat3(
		FilaMorphTargetBuffer* morphTargetBuffer,
		FilaEngine* engine,
		size_t targetIndex,
		const float* positions3,
		size_t count,
		size_t offset);
void FilaMorphTargetBuffer_setTangentsAt(
		FilaMorphTargetBuffer* morphTargetBuffer,
		FilaEngine* engine,
		size_t targetIndex,
		const int16_t* tangents4,
		size_t count,
		size_t offset);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_MORPH_TARGET_BUFFER_H

